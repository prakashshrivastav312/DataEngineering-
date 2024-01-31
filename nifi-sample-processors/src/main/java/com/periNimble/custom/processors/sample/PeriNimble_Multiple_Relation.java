package com.periNimble.custom.processors.sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.io.StreamCallback;
import org.apache.nifi.processor.util.StandardValidators;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Tags({"example"})
@CapabilityDescription("Provide a description")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class PeriNimble_Multiple_Relation extends AbstractProcessor {

    private static final double THRESHOLD_VALUE = 155.0;

    public static final PropertyDescriptor MY_PROPERTY = new PropertyDescriptor
            .Builder().name("MY_PROPERTY")
            .displayName("My property")
            .description("Example Property")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();


    public static final Relationship Success = new Relationship.Builder()
            .name("Success")
            .description("On Parsing Success relationship")
            .build();
    public static final Relationship Failure = new Relationship.Builder()
            .name("Failure")
            .description("On Parsing Failure relationship")
            .build();

    public static final Relationship HighValue = new Relationship.Builder()
            .name("HighValue")
            .description("On High Value relationship")
            .build();
    public static final Relationship LowValue = new Relationship.Builder()
            .name("LowValue")
            .description("On Low Value relationship")
            .build();

    private List<PropertyDescriptor> descriptors;
    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        descriptors = Collections.singletonList(MY_PROPERTY);
        relationships = new HashSet<>(Arrays.asList(Success, Failure, HighValue, LowValue));
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) {
        FlowFile flowFile = session.get();
        if (flowFile != null) {
            session.write(flowFile, new Callback(context, session, flowFile));
            session.transfer(flowFile,Success);
        }
    }

    class Callback implements StreamCallback {

        private final ProcessContext context;
        private final ProcessSession session;

        private final FlowFile flowFile;  // Add a member variable for the FlowFile


        Callback(ProcessContext context, ProcessSession session,FlowFile flowFile) {
            this.context = context;
            this.session = session;
            this.flowFile = flowFile;
        }

        @Override
        public void process(InputStream inputStream, OutputStream outputStream) throws IOException {
            String originalContent = new Scanner(inputStream).useDelimiter("\\A").next();
            ObjectMapper objectMapper = new ObjectMapper();

            try {

                getLogger().info("Processing FlowFile with UUID: {}", flowFile.getAttribute(CoreAttributes.UUID.key()));
                JsonNode root = objectMapper.readTree(originalContent);

                // Extract measure_value from the JSON
                double measureValue = root.path("measures").path("reads").get(0).path("measure_value").asDouble();

                // Fetch the max measure_value from the database
                double maxMeasureValue = fetchMaxMeasureValueFromDatabase();

                // Update measure_value by adding maxMeasureValue
                double updatedMeasureValue = measureValue + maxMeasureValue;

                getLogger().info("Original Measure Value: {}", measureValue);
                getLogger().info("Updated Measure Value: {}", updatedMeasureValue);
                getLogger().info("Threshold Value: {}", THRESHOLD_VALUE);


                // Create a list to store the original and updated intervals
                List<UpdatedMeasureInterval> updatedMeasureIntervalList = new ArrayList<>();

                // Add the original and updated intervals to the list
                updatedMeasureIntervalList.add(new UpdatedMeasureInterval(measureValue, updatedMeasureValue));

                // Convert the list to JSON
                ArrayNode updatedJsonArray = objectMapper.valueToTree(updatedMeasureIntervalList);

                // Write the updated JSON content to the output stream
                outputStream.write(updatedJsonArray.toString().getBytes(StandardCharsets.UTF_8));

                // Create a new FlowFile for high-value data with updated JSON content
                FlowFile highValueFlowFile = session.create(flowFile);
                updateFlowFileContent(highValueFlowFile, updatedJsonArray);

                if (updatedMeasureValue > THRESHOLD_VALUE) {
                    // Transfer the new FlowFile to the HighValue relationship
                    session.transfer(highValueFlowFile, HighValue);
                    getLogger().info("Transferred to HighValue relationship");
                } else {
                    // Create a new FlowFile for low-value data with updated JSON content
                    FlowFile lowValueFlowFile = session.create(flowFile);
                    updateFlowFileContent(lowValueFlowFile, updatedJsonArray);

                    // Transfer the new FlowFile to the LowValue relationship
                    session.transfer(lowValueFlowFile, LowValue);
                    getLogger().info("Transferred to LowValue relationship");
                }

                // Transfer the original FlowFile to the Success relationship
                session.transfer(flowFile, Success);
                getLogger().info("Transferred to Success relationship");

            } catch (Exception e) {
                getLogger().error("Failed to process FlowFile: {}", e.getMessage(), e);
                session.transfer(flowFile, Failure); // Transfer to Failure in case of an error
            }
            }

            private void updateFlowFileContent(FlowFile flowFile, ArrayNode updatedJsonArray) {
                session.write(flowFile, outputStream -> outputStream.write(updatedJsonArray.toString().getBytes(StandardCharsets.UTF_8)));
            }
        }



        private double fetchMaxMeasureValueFromDatabase() throws SQLException {
            try (Connection connection = DatabaseConnector.connect();
                 PreparedStatement statement = connection.prepareStatement("SELECT MAX(measure_value) FROM m_meter_value");
                 ResultSet resultSet = statement.executeQuery()) {

                return resultSet.next() ? resultSet.getDouble(1) : 0;

            } catch (SQLException e) {
                getLogger().error("Failed to fetch max measure_value from the database: {}", e.getMessage(), e);
                throw e;
            }
        }
    }

