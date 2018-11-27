package camunda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.enterprise.inject.Model;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class PartitionabilityMetricsExtractor {

	private BpmnBasicMetricsExtractor basicExtractor;

	public PartitionabilityMetricsExtractor(BpmnBasicMetricsExtractor basicExtractor) {
		this.basicExtractor = basicExtractor;
	}
	
	public double getSeparability() {
		SeparabilityMetricExtractor extractor = new SeparabilityMetricExtractor();
		return extractor.getSeparabilityInner();
	}

	private class SeparabilityMetricExtractor {

		private Collection<FlowNode> nodes;

		public SeparabilityMetricExtractor() {
			Collection<ModelElementInstance> modelNodes = new ArrayList<ModelElementInstance>(
					basicExtractor.getCollectionOfElementType(FlowNode.class));
			nodes = new ArrayList<FlowNode>();
			for (ModelElementInstance modelNode : modelNodes) {
				nodes.add((FlowNode) modelNode);
			}
		}

		/**
		 * TODO Per qualche motivo questo metodo non fa funzionare gli altri se
		 * eseguito prima dei suddetti, probabilmente qualche errore derivato da
		 * "clone()" Method that calculates the separability of the model. First
		 * it gets the number of nodes that have no outgoing or incoming flows.
		 * Then, it checks the model obtained by removing each one of the node,
		 * to check again the number of nodes that have no outgoing or incoming
		 * flows. If those last numbers are greater than the the ones from the
		 * original model, the node in question is a cut-vertex, thus the
		 * separability get incremented by one.
		 * 
		 * @return the number of cut-vertexes
		 */
		public double getSeparabilityInner() {
			int initialNoIncomingNodes = 0;
			int initialNoOutgoingNodes = 0;
			int cutVertices = 0;
			// Iterate the nodes of the model
			for (FlowNode node : nodes) {
				if (node.getOutgoing().size() == 0)
					initialNoOutgoingNodes++;
				if (node.getIncoming().size() == 0)
					initialNoIncomingNodes++;
			}
			int tempNoOutgoingNodes = 0;
			int tempNoIncomingNodes = 0;
			System.out.println("Nodes: " + nodes.size());
			for (FlowNode node : nodes) {
				ModelInstance tempModel = basicExtractor.getModelInstance().clone();
				tempModel.getModelElementById(node.getId()).getParentElement().removeChildElement(node);
				Collection<FlowNode> tempModelNodes = tempModel.getModelElementsByType(FlowNode.class);
				System.out.println("Nodes after elimination: " + tempModelNodes.size());
				tempNoOutgoingNodes = 0;
				tempNoIncomingNodes = 0;
				for (FlowNode tempNode : tempModelNodes) {
					if (tempNode.getOutgoing().size() == 0)
						tempNoOutgoingNodes++;
					if (tempNode.getIncoming().size() == 0)
						tempNoIncomingNodes++;
				}
				if (tempNoOutgoingNodes > initialNoOutgoingNodes || tempNoIncomingNodes > initialNoIncomingNodes)
					cutVertices++;
			}
			System.out.println(cutVertices);
			return (double) cutVertices;
		}
	}
	
	private class DepthMetricExtractor {
		
		private Collection<String> visitedNodes;
		private Map<String, Integer> nodesIndepth;
		private Map<String, Integer> nodesOutdepth;
		
		public DepthMetricExtractor() {
			Collection<String> visitedNodes = new ArrayList<String>();
		}
		
		public double getDepth() {
			Collection<ModelElementInstance> modelNodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
			for (ModelElementInstance modelNode : modelNodes) {
				FlowNode node = (FlowNode) modelNode;
			}
			return 0.0;
		}
		
		public int calculateDepth(FlowNode sourceNode) {
			if (!visitedNodes.contains(sourceNode)) {
				return 0;
			}
			int tempOutDepth = 0;
			int succDepth = 0;
			FlowNode succNode;
			int succOutgoing = 0;
			Collection<SequenceFlow> outgoingFlows = sourceNode.getOutgoing();
			Collection<SequenceFlow> incomingFlows = sourceNode.getIncoming();
			int incomingSize = incomingFlows.size();
			for (SequenceFlow outFlow : outgoingFlows) {
				succNode = outFlow.getTarget();
				succDepth = calculateDepth(succNode);
				if (succDepth < tempOutDepth || tempOutDepth == 0) {
					tempOutDepth = succDepth;
					succOutgoing = succNode.getOutgoing().size();
				}
			}
			if ((succOutgoing > 1 && incomingSize > 1) || ((succOutgoing == 1 || succOutgoing == 0) && (incomingSize == 0 && incomingSize == 1))) {
				//max(depthout(curr), depthout(succ)) if pre -> Split && curr -> Join OR
				//max(depthout(curr), depthout(succ)) if pre -/> Split && curr -/> Join
				tempOutDepth = succDepth > tempOutDepth ? succDepth : tempOutDepth;
			} else if (succOutgoing > 1 &&  (incomingSize == 0 || incomingSize == 1)) {
				//max(depthout(curr), depthout(succ) - 1) if pre -> Split && curr -/> Join
				tempOutDepth = (succDepth - 1) > tempOutDepth ? (succDepth - 1): tempOutDepth;
			} else if ((succOutgoing == 1 || succOutgoing == 0) &&  incomingSize > 1) {
				//max(depthout(curr), depthout(succ) + 1) if pre -/> Split && curr -> Join
				tempOutDepth = (succDepth + 1) > tempOutDepth ? (succDepth + 1) : tempOutDepth;
			}
			return tempOutDepth;
		}
		
	}
}
