package bpmnMetadataExtractor;

import java.util.ArrayList;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class SizeMetricsExtractor {
	
	private ArrayList<String> visitedNodes;
	private BpmnBasicMetricsExtractor basicExtractor;
	
	public SizeMetricsExtractor(BpmnBasicMetricsExtractor basicExtractor) {
		visitedNodes = new ArrayList<String>();
		this.basicExtractor = basicExtractor;
	}

	public double getDiam() {
		Collection<ModelElementInstance> startNodes = basicExtractor.getCollectionOfElementType(StartEvent.class);
		ArrayList<Integer> results = new ArrayList<Integer>();
		for (ModelElementInstance startNodeModel : startNodes) {
			visitedNodes.clear();
			FlowNode startNode = (FlowNode) startNodeModel;
			results.add(calculateMaxDiam(startNode, 0));
		}
		int toReturn = 0;
		for (int result : results) {
			toReturn = result > toReturn ? result : toReturn;
		}
		return (double) toReturn;
	}
	
	private int calculateMaxDiam(FlowNode sourceNode, int pathValue) {
		if (!visitedNodes.contains(sourceNode.getId())) {
			visitedNodes.add(sourceNode.getId());
		} else {
			return pathValue;
		}
		int toReturn = 0;
		int tempDiamValue = 0;
		Collection<SequenceFlow> flows = sourceNode.getOutgoing();
		for (SequenceFlow flow : flows) {
			try {
				tempDiamValue = calculateMaxDiam(flow.getTarget(), pathValue + 1);
				if (tempDiamValue > toReturn) {
					toReturn = tempDiamValue;
				}
			}catch(Exception e) {continue;}
		}
		if (flows.size() == 0) {
			toReturn = pathValue;
		}
		visitedNodes.remove(sourceNode.getId());
		return toReturn;
	}
	
}
