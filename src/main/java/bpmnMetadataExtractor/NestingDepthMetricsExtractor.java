package bpmnMetadataExtractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.ComplexGateway;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

/**
 * Class that contains the methods to calculate the mean and the max nesting depth of a model.
 * The nesting depth of a node is given by the minimum number of decisions nodes that have to be crossed to get to the node.
 * The decision node are represented by Complex and Exclusive gateways.
 * @author PROSLabTeam
 *
 */
public class NestingDepthMetricsExtractor {
	
	private Collection<String> visitedNodes;
	private Collection<FlowNode> nodes;
	private Map<String, Integer> nestingDepthValues;
	private BpmnBasicMetricsExtractor basicExtractor;

	public NestingDepthMetricsExtractor(BpmnBasicMetricsExtractor basicExtractor) {
		this.basicExtractor = basicExtractor;
		visitedNodes = new ArrayList<String>();
		nestingDepthValues = new HashMap<String, Integer>();
		Collection<ModelElementInstance> modelNodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
		nodes = new ArrayList<FlowNode>();
		for (ModelElementInstance modelNode : modelNodes) {
			if (!(modelNodes instanceof BoundaryEvent))
				nodes.add((FlowNode) modelNode);
		}
		initializeNestingDepthValuesMap();
	}
	
	/**
	 * Populates the map of the nesting values, iterating over the start nodes and calling the recursive method getNodeNestingDepth 
	 */
	private void initializeNestingDepthValuesMap() {
		for (ModelElementInstance modelStartEvent : basicExtractor.getCollectionOfElementType(StartEvent.class)) {
			visitedNodes.clear();
			getNodeNestingDepth((FlowNode) modelStartEvent, 0);
		}
	}
	
	/**
	 * Calculates the mean nesting depth of the nodes in the model
	 * @return 
	 */
	public double getMeanNestingDepth() {
		int meanNestingDepth = 0;
		for (FlowNode node : nodes) {
			if (nestingDepthValues.containsKey(node.getId()))
				meanNestingDepth += nestingDepthValues.get(node.getId());
		}
		return (double) meanNestingDepth / (double) nodes.size();
	}
	
	/**
	 * Calculates the max nesting depth of the nodes in the model
	 * @return
	 */
	public double getMaxNestingDepth() {
		int maxNestingDepth = 0;
		int tempNestingDepth = 0;
		for (FlowNode node : nodes) {
			//Boundary events control, as they are never reached by getNodeNestingDepth
			if (nestingDepthValues.containsKey(node.getId()))
				tempNestingDepth = nestingDepthValues.get(node.getId());
			maxNestingDepth = tempNestingDepth > maxNestingDepth ? tempNestingDepth : maxNestingDepth;
		}
		return (double) maxNestingDepth;
	}
	
	/**
	 * Method that calculates the nesting depth's value of a node, and then calls itself recursively on the subsequent nodes.
	 * Only complex and exclusive gateways increase the nesting depth value.
	 * If the nesting depth value has already been calculated for another path, the minimum between becomes the new value.
	 * @param node
	 * @param nestingDepthValue: the value of the nesting depth up to the FlowNode node
	 * @return the nesting depth of a node
	 */
	private int getNodeNestingDepth(FlowNode node, int nestingDepthValue) {
		//Check if the node has been already visited, to avoid infinite loop in presence of cycles in the model
		if (visitedNodes.contains(node.getId())) {
			return nestingDepthValue;
		}
		visitedNodes.add(node.getId());
		//Check if the node represents a decision node
		if ((node instanceof ComplexGateway || node instanceof ExclusiveGateway) && node.getOutgoing().size() > 1) {
			nestingDepthValue++;
		}
		
		try {
			//Recursive call on subsequent nodes
			for (SequenceFlow outFlow : node.getOutgoing()) {
				getNodeNestingDepth(outFlow.getTarget(), nestingDepthValue);
			}
		}catch(Exception e) {
			return nestingDepthValue;
		}
		//Add the nesting depth's value in the map
		if (nestingDepthValues.containsKey(node.getId())) {
			int precedentNDValue = nestingDepthValues.get(node.getId());
			nestingDepthValue = precedentNDValue < nestingDepthValue ? precedentNDValue : nestingDepthValue;
		}
		nestingDepthValues.put(node.getId(), nestingDepthValue);
		visitedNodes.remove(node.getId());
		return nestingDepthValue;
	}

}
