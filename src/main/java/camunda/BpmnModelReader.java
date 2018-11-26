package camunda;

import java.io.File;
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
		CrossConnectivityMetricExtractor ccExtractor = new CrossConnectivityMetricExtractor(basicExtractor);
		long loadTime = System.currentTimeMillis() - startTime;
		System.out.println("Tempo load del file: " + loadTime);
		basicExtractor.runMetrics();
		long basicTime = System.currentTimeMillis() - loadTime - startTime;
		System.out.println("Tempo calcolo metriche di base: " + basicTime);
		advExtractor.runMetrics();
		long advTime = System.currentTimeMillis() - basicTime - startTime - loadTime;
		System.out.println("Tempo calcolo metriche avanzate: " + advTime);
	}

	public static void main(String[] args){
		BpmnFileOpener fileOpener = new BpmnFileOpener();
		BpmnModelReader modelReader = new BpmnModelReader(fileOpener.openFile());
		modelReader.test();
	}
}
