package camunda;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ComplexGateway;
import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

/**
 * 
 * TODO Classe in cui andare a inserire i metodi per l'estrazione delle statistiche/metadati
 * @author PROSLabTeam
 *
 */
public class BpmnMetadataExtractor {
	
	//modello da cui estrarre le metriche
	private BpmnModelInstance modelInstance;
	
	public BpmnMetadataExtractor(BpmnModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}
	
	/**
	 * Metodo principale per runnare tutti i metodi che ottengono le metriche
	 */
	public void runMetrics() {
		System.out.println("Numero di task: " + getTasks());
		System.out.println("Numero di decisioni complesse: " + getComplexDecisions());
		System.out.println("Numero di data objects in input alle attività: " + getDataObjectsInput());
		System.out.println("Numero di data objects in output dalle attività: " + getDataObjectsOutput());
		System.out.println("Numero di decisioni inclusive: " + getInclusiveDecisions());
		System.out.println("Numero di decisioni esclusive basate sui dati (exclusive gateways): " + getExclusiveDataBasedDecisions());
	}
	
	/**
	 * Metrica: NT
	 * @return numero dei tasks
	 */
	private int getTasks() {
		return getNumberOfTypeElement(Task.class);
	}
	
	/**
	 * Metrica: NCD
	 * (numero di complex gateways)
	 * @return numero delle decisioni complesse
	 */
	private int getComplexDecisions() {
		return getNumberOfTypeElement(ComplexGateway.class)/2;
	}
	
	/**
	 * Metrica: NDOin
	 * @return numero dei data objects che sono in input delle attività
	 */
	private int getDataObjectsInput() {
		return getNumberOfTypeElement(DataInputAssociation.class);
	}
	
	/**
	 * Metrica: NDOout
	 * @return numero dei data objects che sono in output delle attività
	 */
	private int getDataObjectsOutput() {
		return getNumberOfTypeElement(DataOutputAssociation.class);
	}
	
	/**
	 * Metrica: NID
	 * (numero di inclusive gateways)
	 * @return numero delle decisioni inclusive
	 */
	private int getInclusiveDecisions() {
		return getNumberOfTypeElement(InclusiveGateway.class)/2;
	}
	
	/**
	 * Metrica: NEDDB
	 * (numero degli exclusive gateways)
	 * @return numero delle decisioni esclusive basate sui dati 
	 */
	private int getExclusiveDataBasedDecisions() {
		return getNumberOfTypeElement(ExclusiveGateway.class)/2;
	}
	
	/**
	 * Metodo che cerca nel modello tutti gli elementi del tipo "type" per ottenerne il numero complessivo
	 * @param type: la classe del tipo degli elementi di cui si vuole conoscere il numero
	 * @return il numero degli elementi del tipo "type"
	 */
	private int getNumberOfTypeElement(Class type) {
		ModelElementType modelElementType = modelInstance.getModel().getType(type);
		Collection<ModelElementInstance> modelElementInstances = modelInstance.getModelElementsByType(modelElementType);
		return modelElementInstances.size();
	}
}
