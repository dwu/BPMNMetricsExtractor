package bpmnMetadataExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

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
		System.out.println("Tempo load del file: " + loadTime + "ms");
		basicExtractor.runMetrics();
		long basicTime = System.currentTimeMillis() - loadTime - startTime;
		System.out.println("Tempo calcolo metriche di base: " + basicTime + "ms");
		advExtractor.runMetrics();
		long advTime = System.currentTimeMillis() - basicTime - startTime - loadTime;
		System.out.println("Tempo calcolo metriche avanzate: " + advTime + "ms");
		jsonEncoder.exportJson();
		MySqlInterface db = new MySqlInterface();
		db.connect();
//		db.createTables(jsonEncoder);
//		db.createAndInsertMetricsInfosTable();
		db.saveMetrics(jsonEncoder);
		db.closeConnection();
	}
	
	public String getJsonMetrics(InputStream fileStream, String fileName) {
		BpmnModelInstance modelInstance = Bpmn.readModelFromStream(fileStream);
		JsonEncoder jsonEncoder = new JsonEncoder(fileName);
		BpmnBasicMetricsExtractor basicExtractor = new BpmnBasicMetricsExtractor(modelInstance, jsonEncoder);
		BpmnAdvancedMetricsExtractor advExtractor = new BpmnAdvancedMetricsExtractor(basicExtractor, jsonEncoder);
		basicExtractor.runMetrics();
		advExtractor.runMetrics();
		jsonEncoder.populateHeader(LocalDateTime.now());
		MySqlInterface db = new MySqlInterface();
		db.connect();
		db.saveMetrics(jsonEncoder);
		db.closeConnection();
		return jsonEncoder.getJson().toString();
	}

	public static void main(String[] args) throws IOException{
		BpmnFileOpener fileOpener = new BpmnFileOpener();
		BpmnModelReader modelReader = new BpmnModelReader(fileOpener.openFile());
		modelReader.test();		
	}
}
