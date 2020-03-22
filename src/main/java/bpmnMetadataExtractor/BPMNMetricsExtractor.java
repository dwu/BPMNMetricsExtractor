package bpmnMetadataExtractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class BPMNMetricsExtractor  {

    boolean processApplicationStopped;

    public static void main(final String... args) throws Exception {
        if (args.length == 0) {
            System.err.println("Error: Missing filename argument(s)");
            System.exit(1);
        }

        ArrayList<String> csvHeaderElements = BpmnModelReader.getCsvHeaderElements();
        StringBuffer sb = new StringBuffer();
        String[] sa = new String[csvHeaderElements.size()];
        CSVPrinter printer = CSVFormat.DEFAULT.withHeader(csvHeaderElements.toArray(sa)).print(sb);

        for (String filename : args) {
            try {
                BpmnModelReader metricsExtractor = new BpmnModelReader();

                metricsExtractor.printCsvMetrics(printer, new FileInputStream(new File(filename)), filename);
            } catch (Exception e) {
                System.err.println("Error processing file " + filename + ": " + e.getMessage());
            }
        }

        System.out.println(sb.toString());
    }

}
