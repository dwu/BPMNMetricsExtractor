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
		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(loadedFile);
		JsonEncoder jsonEncoder = new JsonEncoder();
		BpmnBasicMetricsExtractor basicExtractor = new BpmnBasicMetricsExtractor(modelInstance, jsonEncoder);
		BpmnAdvancedMetricsExtractor advExtractor = new BpmnAdvancedMetricsExtractor(modelInstance, basicExtractor, jsonEncoder);
		basicExtractor.runMetrics();
		advExtractor.runMetrics();
	}

	public static void main(String[] args){
		//BpmnModelReader modelReader = new BpmnModelReader("airline.bpmn");
		//BpmnModelReader modelReader = new BpmnModelReader("dataAssociationsTest.bpmn");
		//BpmnModelReader modelReader = new BpmnModelReader("bpmnTest01.bpmn");
		//BpmnModelReader modelReader = new BpmnModelReader("sequenceFlowsBetweenActivities.bpmn");
		//BpmnModelReader modelReader = new BpmnModelReader("lanesPools.bpmn");
		BpmnFileOpener fileOpener = new BpmnFileOpener();
		BpmnModelReader modelReader = new BpmnModelReader(fileOpener.openFile());
		modelReader.test();
	}
}
