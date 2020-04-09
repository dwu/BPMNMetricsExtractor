package bpmnMetadataExtractor;

import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class BPMNMetricsExtractor  {

    boolean processApplicationStopped;

    public static void main(final String... args) throws Exception {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        Option inputOption = new Option("i", "input", true, "input file names");
        inputOption.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(inputOption);
        options.addOption( "f", "filelist", true, "read input file names from filelist");

        ArrayList<String> csvHeaderElements = BpmnModelReader.getCsvHeaderElements();
        StringBuffer sb = new StringBuffer();
        String[] sa = new String[csvHeaderElements.size()];
        CSVPrinter printer = CSVFormat.DEFAULT.withHeader(csvHeaderElements.toArray(sa)).print(sb);

        try {
            CommandLine commandLine = parser.parse( options, args );

            if(!(commandLine.hasOption("i") || commandLine.hasOption("f"))) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("BPMNMetricsExtractor", options);
                System.exit(1);
            }

            if (commandLine.hasOption("i")) {
                // Process files given as commandline arguments
                for (String filename : commandLine.getOptionValues("i")) {
                    try {
                        BpmnModelReader metricsExtractor = new BpmnModelReader();
                        metricsExtractor.printCsvMetrics(printer, new FileInputStream(new File(filename)), filename);
                    } catch (Exception e) {
                        System.err.println("Error processing file " + filename + ": " + e.getMessage());
                    }
                }
            } else if (commandLine.hasOption("f")) {
                // Process files given in input file list
                Stream<String> fileNames = Files.lines(Paths.get(commandLine.getOptionValue("f")), StandardCharsets.UTF_8);
                fileNames.forEach(filename -> {
                    try {
                        BpmnModelReader metricsExtractor = new BpmnModelReader();
                        metricsExtractor.printCsvMetrics(printer, new FileInputStream(new File(filename)), filename);
                    } catch (Exception e) {
                        System.err.println("Error processing file " + filename + ": " + e.getMessage());
                    }
                });
            }

            System.out.println(sb.toString());
        } catch (ParseException e) {
            System.out.println("Error parsing commandline arguments");
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("BPMNMetricsExtractor", options);
            System.exit(1);
        }
    }
}
