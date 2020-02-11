package bpmnMetadataExtractor;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.MultiInstanceLoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class CognitiveWeightMetricExtractor {
	
	private BpmnBasicMetricsExtractor basicMetricsExtractor;
	private List<String> visitedNodes;
	
	public CognitiveWeightMetricExtractor(BpmnBasicMetricsExtractor basicMetricExtractor) {
		this.basicMetricsExtractor = basicMetricExtractor;
		visitedNodes = new ArrayList<String>();
	}
	
	public double getCognitiveWeight() {
		int totalWeight = 0;
		for (ModelElementInstance modelStartEvent : basicMetricsExtractor.getCollectionOfElementType(StartEvent.class)) {
			totalWeight += getSequenceNodesCognitiveWeight((FlowNode) modelStartEvent, false, false);
		}
		for (ModelElementInstance modelExGateway : basicMetricsExtractor.getCollectionOfElementType(ExclusiveGateway.class)) {
			ExclusiveGateway gateway = (ExclusiveGateway) modelExGateway;
			if (gateway.getOutgoing().size() == 2) {
				totalWeight += 2;
			} else if (gateway.getOutgoing().size() > 2) {
				totalWeight += 3;
			}
		}
		totalWeight += (basicMetricsExtractor.getFlowDividingParallelGateways() * 4);
		totalWeight += (basicMetricsExtractor.getFlowDividingInclusiveGateways()* 7);
		totalWeight += (basicMetricsExtractor.getNumberOfTypeElement(SubProcess.class) * 2);
		totalWeight += (basicMetricsExtractor.getNumberOfTypeElement(MultiInstanceLoopCharacteristics.class) * 6);
		totalWeight += basicMetricsExtractor.getCancelEvents();
		return (double) totalWeight;
	}

	/**
	 * Calculates the weight of the node, specifically a task or an event one, checking if it is part of a sequence or not, calling itself recursively on the next nodes
	 * @param sourceNode: the node to analyze
	 * @param sequence: indicates if the past nodes started a sequence or not
	 * @return the weight of the node considering only the weights of sequences
	 */
	private int getSequenceNodesCognitiveWeight(FlowNode sourceNode, boolean startSequence, boolean middleSequence) {
		int weight = 0;
		//Loop check
		if (visitedNodes.contains(sourceNode.getId())) {
			return weight;
		}
		visitedNodes.add(sourceNode.getId());
		//Check if the current node is a Task or an Event with only one incoming flow and one outgoing flow, to check if it can possibly be part of a simple sequence
		if ((sourceNode instanceof Task || sourceNode instanceof Event) && sourceNode.getIncoming().size() == 1 && sourceNode.getOutgoing().size() == 1) {
			if (!startSequence) {
				//The current node is the first node of a possible sequence
				startSequence = true;
			} else if (!middleSequence) {
				middleSequence = true;
			}
		} else if (middleSequence) {
			//The sequence has ended, so weight gets incremented by one
			weight++;
			middleSequence = false;
		}
		startSequence = false;
		//Call the method on every subsequent node, and sum all the weight
		for (SequenceFlow outFlow : sourceNode.getOutgoing()) {
			try {
			weight += getSequenceNodesCognitiveWeight(outFlow.getTarget(), startSequence, middleSequence);
			}catch(Exception e) {continue;}
		}
		return weight;
	}

}
