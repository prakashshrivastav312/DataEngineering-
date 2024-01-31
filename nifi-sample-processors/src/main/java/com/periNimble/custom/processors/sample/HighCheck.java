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
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.io.InputStreamCallback;
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
public class HighCheck extends AbstractProcessor {

    public static final PropertyDescriptor MY_PROPERTY = new PropertyDescriptor
            .Builder().name("HIGH_CHECK_THRESHOLD")
            .displayName("High Check Threshold")
            .description("High Check Threshold")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();


    public static final Relationship Original = new Relationship.Builder()
            .name("Original")
            .description("On Parsing Original relationship")
            .build();
    public static final Relationship Success = new Relationship.Builder()
            .name("Success")
            .description("On Parsing Success relationship")
            .build();
    public static final Relationship Exception = new Relationship.Builder()
            .name("Exception")
            .description("On Parsing Failure relationship")
            .build();



    private List<PropertyDescriptor> descriptors;
    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        descriptors = Collections.singletonList(MY_PROPERTY);
        relationships = new HashSet<>(Arrays.asList(Success, Exception, Original));
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
            session.write(flowFile, new Callback(context, flowFile, session));
            session.transfer(flowFile,Original);
        }
    }

    class Callback implements StreamCallback {

        private final ProcessContext context;
        private final ProcessSession session;

        private final FlowFile orgFlowfile;

        Callback(ProcessContext context, FlowFile orgFlowfile, ProcessSession session) {
            this.context = context;
            this.session = session;
            this.orgFlowfile = orgFlowfile;
        }

        @Override
        public void process(InputStream inputStream, OutputStream outputStream) throws IOException {
            String originalContent = new Scanner(inputStream).useDelimiter("\\A").next();
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode root = objectMapper.readTree(originalContent);

                // Extract measure_value from the JSON
                String meter_id = root.path("measures").path("device").asText();

                // Extract measure_value from the JSON
                double measureValue = root.path("measures").path("reads").get(0).path("measure_value").asDouble();
                getLogger().info("Original Measure Value: {}", measureValue);

                // Fetch the max measure_value from the database
                double dailyMeasureValue = fetchDailyAverageFromDatabase(meter_id);
                getLogger().info("Daily Measure Value: {}", measureValue);

                // Accessing the configured property value
                String MyPropertyValue = context.getProperty(MY_PROPERTY).getValue();
                getLogger().info("MyProperty Value: {}", MyPropertyValue);

                double threshold = Double.valueOf(MyPropertyValue);
                getLogger().info("Threshold Value: {}", threshold);



                // Compare the updated measure_value with the threshold
                if (threshold > 2) {
                    // Add attribute to JsonNode
                    if (root instanceof ObjectNode) {
                        ObjectNode objectNode = (ObjectNode) root;
                        objectNode.put("exception", "high");
                    }


                    // Serialize JsonNode back to JSON string
                    String modifiedJsonString = objectMapper.writeValueAsString(root);
                    FlowFile newFlowFile = session.create(orgFlowfile);
                    updateFlowFileContent(session, newFlowFile, modifiedJsonString);
                    getLogger().info("OrgFlowFile: ", orgFlowfile);
                    // Transfer the FlowFile to the success relationship
                    session.transfer(newFlowFile, Exception);
                    getLogger().info("Transferred to Success relationship");
                } else {
                    FlowFile newFlowFile1 = session.create(orgFlowfile);
                    updateFlowFileContent(session, newFlowFile1, originalContent);
                    getLogger().info("OrgFlowFile: ", orgFlowfile);
                    // Transfer the FlowFile to the success relationship
                    session.transfer(newFlowFile1, Success);
                    getLogger().info("Transferred to Exception relationship");
                }

            } catch (Exception e) {
                getLogger().error("Failed to process FlowFile: {}", e.getMessage(), e);
            }
        }

        private void updateFlowFileContent(ProcessSession session, FlowFile flowFile, String updatedJsonArray) {
            session.write(flowFile, outputStream -> outputStream.write(updatedJsonArray.getBytes(StandardCharsets.UTF_8)));
        }



        private double fetchDailyAverageFromDatabase(final String meter_id) throws SQLException {
            double dailyAvg = 0.0;
            try (Connection connection = DatabaseConnector.connect()) {
                if (connection != null) {
                    String query = "SELECT daily_avg FROM dailyaveragekwh WHERE meter_id = ? ORDER BY avg_date DESC LIMIT 1";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        // Set the parameter for the meter_id
                        statement.setString(1, meter_id);

                        try (ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {
                                dailyAvg = resultSet.getDouble("daily_avg");
                                System.out.println("Meter ID: " + meter_id + ", Daily Average: " + dailyAvg);
                            } else {
                                System.out.println("No records found for meter ID: " + meter_id);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return dailyAvg;
        }
    }
}
