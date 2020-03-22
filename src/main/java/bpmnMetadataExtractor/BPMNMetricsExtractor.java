package bpmnMetadataExtractor;

import org.camunda.bpm.model.xml.ModelParseException;

import java.io.File;
import java.io.FileInputStream;

public class BPMNMetricsExtractor  {

    boolean processApplicationStopped;

    public static void main(final String... args) throws Exception {
        if (args.length == 0) {
            System.err.println("Error: Missing filename argument(s)");
            System.exit(1);
        }

        for (String filename : args) {
            try {
                BpmnModelReader metricsExtractor = new BpmnModelReader();
                String json = metricsExtractor.getJsonMetrics(new FileInputStream(new File(filename)), filename);
                System.out.println(json);
            } catch (ModelParseException e) {
                System.err.println("Error processing file " + filename + ": " + e.getMessage());
            }
        }
    }

}
