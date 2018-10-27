package camunda;

import java.util.Collection;
import java.util.List;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.ComplexGateway;
import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
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
		System.out.println("Nuemero di decisioni esclusive basate su eventi (event based gateways): " + getExclusiveEventBasedDecisions());
		System.out.println("Numero di lanes: " + getLanes());
		System.out.println("Numero di flussi di messaggi: " + getMessageFlows());
		System.out.println("Numero di pools: " + getPools());
		System.out.println("Numero di archi uscenti dagli eventi: " + this.getSequenceFlowsFromEvents());
		System.out.println("Numero di archi uscenti dai gateways: " + this.getSequenceFlowsFromGateways());
		System.out.println("Numero di archi tra attività: " + this.getSequenceFlowsBetweenActivities());
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
	 * Metrica: NEDEB
	 * (numero degli event based gateways)
	 * @return numero delle decisioni esclusive basate sugli eventi
	 */
	private int getExclusiveEventBasedDecisions(){
		return getNumberOfTypeElement(EventBasedGateway.class);
	}
	
	/**
	 * Metrica: NL
	 * (numero di lanes)
	 * @return numero di lanes
	 */
	private int getLanes(){
		return getNumberOfTypeElement(Lane.class);
	}
	
	/**
	 * Metrica: NMF
	 * (numero di message flows)
	 * @return numero di flussi di messaggi
	 */
	private int getMessageFlows(){
		return getNumberOfTypeElement(MessageFlow.class);
	}
	
	/**
	 * Metrica: NP
	 * (numero di pools)
	 * @return numero di pools
	 */
	private int getPools(){
		return getNumberOfTypeElement(Process.class);
	}
	/**
	 * Metrica: NSFE
	 * (numero di archi uscenti dagli eventi)
	 * @return numero di archi uscenti dagli eventi
	 */
	private int getSequenceFlowsFromEvents(){
		Collection<Event> events = this.modelInstance.getModelElementsByType(Event.class);
		int numberOfOutgoingSequenceFlows = 0;
		for (Event e: events) {
			numberOfOutgoingSequenceFlows += e.getOutgoing().size();	
		}
		return numberOfOutgoingSequenceFlows;
	}
	
	/**
	 * Metrica:NSFG
	 * (numero di archi uscenti dai gateways)
	 * @return numero di archi uscenti dai gateways
	 */
	private int getSequenceFlowsFromGateways(){
		Collection<Gateway> gateways = this.modelInstance.getModelElementsByType(Gateway.class);
		int numberOfOutgoingSequenceFlows = 0;
		for (Gateway g: gateways) {
			numberOfOutgoingSequenceFlows += g.getOutgoing().size();
		}
		return numberOfOutgoingSequenceFlows; 
	}
	/**
	 * Metrica: NSFA
	 * (Numero di archi colleganti attività)
	 * 
	 * @return numero di archi colleganti due attività
	 */
	private int getSequenceFlowsBetweenActivities() {
		Collection<Activity> activities = this.modelInstance.getModelElementsByType(Activity.class);
		Collection<SequenceFlow> outgoingFlows;
		int numberOfSequenceFlows = 0;
		System.out.println(activities.size());
		for (Activity a: activities) {
			outgoingFlows = a.getOutgoing();
			for (SequenceFlow s: outgoingFlows) {
				if (s.getTarget() instanceof Activity) {
					numberOfSequenceFlows++;
				}
			}
		}
		return numberOfSequenceFlows;
	}
	
	
	/**
	 * Metodo che cerca nel modello tutti gli elementi del tipo "type" per ottenerne il numero complessivo
	 * @param type: la classe del tipo degli elementi di cui si vuole conoscere il numero
	 * @return il numero degli elementi del tipo "type"
	 */
	private int getNumberOfTypeElement(Class type) {
		ModelElementType modelElementType = modelInstance.getModel().getType(type);
		Collection<ModelElementInstance> modelElementInstances = this.modelInstance.getModelElementsByType(modelElementType);
		return modelElementInstances.size();
	}
	/**
	 * Metodo che cerca nel modello tutti gli elementi del tipo "type" passato come parametro.
	 * @param type: la classe degli elementi da ritornare
	 * @return una collection contenente gli elementi del tipo passato come parametro
	 */
	/*private Collection<Event> getElementsOfType(Class type) {
		ModelElementType modelElementType = modelInstance.getModel().getType(type);
		Collection<Event> modelElementInstances = this.modelInstance.getModelElementsByType();
		return modelElementInstances;	
	}*/
}
