package bpmnMetadataExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.Bpmn;

/**
 * Classe principale, in cui vengono richiamati il MetadataExtractor e, eventualmente, il DatabaseController, per l'estrazione delle metriche e loro salvataggio, a partire da un file bpmn
 * @author PROSLabTeam
 *
 */
public class BpmnModelReader {
	//file da cui vengono lette le metriche
	private File loadedFile;
	private ArrayList<String> csvHeaderElements = getCsvHeaderElements();

	/**
	 * Costruttore in cui viene instanziato il loadedFile tramite il suo path
	 * @param filePath
	 */
	public BpmnModelReader(String filePath) {
		loadedFile = new File(filePath);
	}
	
	public BpmnModelReader() {
		
	}
	
	/**
	 * Getter di loadedFile
	 * @return il file caricato
	 */
	public File getLoadedFile() {
		return loadedFile;
	}
	
	/**
	 * Setter di loadedFile
	 * @param filePath: il path in cui si trova il nuovo file
	 */
	public void setLoadedFile(String filePath) {
		this.loadedFile = new File(filePath);
	}
	
	/**
	 * Metodo di test che viene richiamato dal main
	 */
	private void test() {
		long startTime = System.currentTimeMillis();
		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(loadedFile);
		JsonEncoder jsonEncoder = new JsonEncoder(loadedFile.getName());
		BpmnBasicMetricsExtractor basicExtractor = new BpmnBasicMetricsExtractor(modelInstance, jsonEncoder);
		BpmnAdvancedMetricsExtractor advExtractor = new BpmnAdvancedMetricsExtractor(basicExtractor, jsonEncoder);
		long loadTime = System.currentTimeMillis() - startTime;
//		System.out.println("Tempo load del file: " + loadTime + "ms");
		basicExtractor.runMetrics();
		long basicTime = System.currentTimeMillis() - loadTime - startTime;
//		System.out.println("Tempo calcolo metriche di base: " + basicTime + "ms");
		advExtractor.runMetrics();
		long advTime = System.currentTimeMillis() - basicTime - startTime - loadTime;
//		System.out.println("Tempo calcolo metriche avanzate: " + advTime + "ms");
		jsonEncoder.exportJson();
		MySqlInterface db = new MySqlInterface();
		db.connect();
//		db.createTables(jsonEncoder);
//		db.createAndInsertMetricsInfosTable();
//		db.saveMetrics(jsonEncoder);
//		db.closeConnection();
	}
	
	public String getJsonMetrics(InputStream fileStream, String fileName) {
		BpmnModelInstance modelInstance = Bpmn.readModelFromStream(fileStream);
		JsonEncoder jsonEncoder = new JsonEncoder(fileName);
		BpmnBasicMetricsExtractor basicExtractor = new BpmnBasicMetricsExtractor(modelInstance, jsonEncoder);
		BpmnAdvancedMetricsExtractor advExtractor = new BpmnAdvancedMetricsExtractor(basicExtractor, jsonEncoder);
//		System.out.println("Start extracting Metrics\n");
		basicExtractor.runMetrics();
//		System.out.println("Basic Metrics have been extracted\n");
		advExtractor.runMetrics();
//		System.out.println("Advanced Metrics have been extracted\n");
		jsonEncoder.populateHeader(LocalDateTime.now());
//		MySqlInterface db = new MySqlInterface();
//		db.connect();
//		db.saveMetrics(jsonEncoder);
//		db.closeConnection();
		return jsonEncoder.getJson().toString();
	}

	public static ArrayList<String> getCsvHeaderElements() {
		ArrayList<String> header = new ArrayList<>();

		JsonEncoder jsonEncoder = new JsonEncoder(null);
		for (String key : jsonEncoder.getMetricsInfos().keySet()) {
			header.add(key);
		}

		Collections.sort(header);
		header.add(0, "file_name");

		return header;
	}

	public void printCsvMetrics(CSVPrinter printer, InputStream fileStream, String fileName) throws IOException {
		BpmnModelInstance modelInstance = Bpmn.readModelFromStream(fileStream);
		JsonEncoder jsonEncoder = new JsonEncoder(fileName);
		BpmnBasicMetricsExtractor basicExtractor = new BpmnBasicMetricsExtractor(modelInstance, jsonEncoder);
		BpmnAdvancedMetricsExtractor advExtractor = new BpmnAdvancedMetricsExtractor(basicExtractor, jsonEncoder);

		basicExtractor.runMetrics();
		advExtractor.runMetrics();

		jsonEncoder.populateHeader(LocalDateTime.now());

		Map<String, String> metrics = jsonEncoder.getMetrics();
		String[] row = new String[csvHeaderElements.size()];
		row[0] = fileName;
		for (int i = 1; i < row.length; ++i) {
			if (metrics.get(csvHeaderElements.get(i)) !=  null) {
				row[i] = metrics.get(csvHeaderElements.get(i)).toString();
			} else {
				row[i] = "";
			}
		}
		printer.printRecord(row);
	}

	public static void main(String[] args) throws IOException{
		BpmnFileOpener fileOpener = new BpmnFileOpener();
		BpmnModelReader modelReader = new BpmnModelReader(fileOpener.openFile());
		modelReader.test();		
	}
}
