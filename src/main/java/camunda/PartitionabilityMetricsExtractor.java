package camunda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Model;

import org.camunda.bpm.model.bpmn.impl.instance.Incoming;
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
	
	public double getDepth() {
		DepthMetricExtractor ex = new DepthMetricExtractor();
		return ex.getDepthInner();
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
		 * TODO "Fare finta" che il nodo dell'iterazione non esista, decrementando i vari tempIn e tempOut ogni volta che si trova un collegamento con lui
		 * Method that calculates the separability of the model. First
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
			for (FlowNode node : nodes) {
				ModelInstance tempModel = basicExtractor.getModelInstance().clone();
				tempModel.getModelElementById(node.getId()).getParentElement().removeChildElement(node);
				Collection<FlowNode> tempModelNodes = tempModel.getModelElementsByType(FlowNode.class);
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
			return (double) cutVertices;
		}
	}
	
	private class DepthMetricExtractor {
		
		private Collection<String> visitedNodes;
		private Map<FlowNode, Integer> nodesIndepth;
		private Map<String, Integer> nodesOutdepth;

		
		public DepthMetricExtractor() {
			visitedNodes = new ArrayList<String>();
			nodesIndepth = new HashMap<FlowNode, Integer>();
			nodesOutdepth = new HashMap<String, Integer>();
		}
		
		public double getDepthInner() {
			Collection<ModelElementInstance> modelStartEvent = basicExtractor.getCollectionOfElementType(StartEvent.class);
			Collection<ModelElementInstance> modelNodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
			ArrayList<Integer> results = new ArrayList<Integer>();
			int depthIn = 0;
			int depthOut = 0;
			int toAdd = 0;
			int toReturn = 0;
			for (ModelElementInstance modelStart : modelStartEvent) {
				visitedNodes.clear();
				FlowNode start = (FlowNode) modelStart;
				calculateDepthIn(start);
				visitedNodes.clear();
				calculateDepthOut(start);
			}
			for (ModelElementInstance modelNode : modelNodes) {
				FlowNode node = (FlowNode) modelNode;
				depthIn = nodesIndepth.get(node);
				depthOut = nodesOutdepth.get(node.getId());
				toAdd = depthIn < depthOut ? depthIn : depthOut;
				results.add(toAdd);
			}
			for (int result : results) {
				toReturn = result > toReturn ? result : toReturn; 
			}
			return (double) toReturn;
		}
		
		private int calculateDepthOut(FlowNode sourceNode) {
			if (visitedNodes.contains(sourceNode.getId())) {
				return 0;
			}
			visitedNodes.add(sourceNode.getId());
			int tempDepthOut = 0;
			int succDepthOut = 0;
			int baseDepthOut = 0;
			int toReturn = 0;
			FlowNode succNode;
			int succOutgoing = 0;
			Collection<SequenceFlow> outgoingFlows = sourceNode.getOutgoing();
			Collection<SequenceFlow> incomingFlows = sourceNode.getIncoming();
			int incomingSize = incomingFlows.size();
			for (SequenceFlow outFlow : outgoingFlows) {
				succNode = outFlow.getTarget();
				if (nodesOutdepth.containsKey(succNode.getId())) {
					tempDepthOut = nodesOutdepth.get(succNode.getId());
				} else {
					tempDepthOut = calculateDepthOut(succNode);
				}
				if (tempDepthOut > succDepthOut || succDepthOut == 0) {
					succDepthOut = tempDepthOut;
					succOutgoing = succNode.getOutgoing().size();
				}
			}
			if ((succOutgoing > 1 && incomingSize > 1) || ((succOutgoing == 1 || succOutgoing == 0) && (incomingSize == 0 || incomingSize == 1))) {
				//max(depthout(curr), depthout(succ)) if pre -> Split && curr -> Join OR
				//max(depthout(curr), depthout(succ)) if pre -/> Split && curr -/> Join
//				toReturn = succDepthOut > baseDepthOut ? succDepthOut : baseDepthOut;
				toReturn = succDepthOut;
			} else if (succOutgoing > 1 &&  (incomingSize == 0 || incomingSize == 1)) {
				//max(depthout(curr), depthout(succ) - 1) if pre -> Split && curr -/> Join
				toReturn = (succDepthOut - 1) > baseDepthOut ? (succDepthOut - 1): baseDepthOut;
			} else if ((succOutgoing == 1 || succOutgoing == 0) &&  incomingSize > 1) {
				//max(depthout(curr), depthout(succ) + 1) if pre -/> Split && curr -> Join
//				toReturn = (succDepthOut + 1) > baseDepthOut ? (succDepthOut + 1) : baseDepthOut;
				toReturn = (succDepthOut + 1);
			}
			visitedNodes.remove(sourceNode.getId());
			if (!nodesOutdepth.containsKey(sourceNode.getId()))
				nodesOutdepth.put(sourceNode.getId(), toReturn);
			return toReturn;
		}
		
		private void calculateDepthIn(FlowNode sourceNode) {
			if (visitedNodes.contains(sourceNode.getId())) {
				return;
			}
			visitedNodes.add(sourceNode.getId());
			FlowNode precNode;
			int precNodeDepthIn = 0;
			int precNodeOutgoing = 0;
			int tempDepthIn = 0;
			int baseDepthIn = 0;
			int toReturn = 0;
			Collection<SequenceFlow> incomingFlows = sourceNode.getIncoming();
			int incomingSize = incomingFlows.size();
			//fare i confronti relativi 
			for (SequenceFlow inFlow : incomingFlows) {
				precNode = inFlow.getSource();
				if (nodesIndepth.containsKey(precNode)) {
					tempDepthIn = nodesIndepth.get(precNode);
				}
				if (tempDepthIn > precNodeDepthIn || precNodeDepthIn == 0) {
					precNodeDepthIn = tempDepthIn;
					precNodeOutgoing = precNode.getOutgoing().size();
				}
			}
			if ((precNodeOutgoing > 1 && incomingSize > 1) || ((precNodeOutgoing == 1 || precNodeOutgoing == 0) && (incomingSize == 0 || incomingSize == 1))) {
				//max(depthin(curr), depthin(succ)) if pre -> Split && curr -> Join OR
				//max(depthin(curr), depthin(succ)) if pre -/> Split && curr -/> Join
//				toReturn = precNodeDepthIn > baseDepthIn ? precNodeDepthIn : baseDepthIn;
				toReturn = precNodeDepthIn;
			} else if (precNodeOutgoing > 1 &&  (incomingSize == 0 || incomingSize == 1)) {
				//max(depthin(curr), depthin(succ) + 1) if pre -> Split && curr -/> Join
//				toReturn = (precNodeDepthIn + 1) > baseDepthIn ? (precNodeDepthIn + 1): baseDepthIn;
				toReturn = (precNodeDepthIn + 1);
			} else if ((precNodeOutgoing == 1 || precNodeOutgoing == 0) &&  incomingSize > 1) {
				//max(depthin(curr), depthin(succ) - 1) if pre -/> Split && curr -> Join
				toReturn = (precNodeDepthIn - 1) > baseDepthIn ? (precNodeDepthIn - 1) : baseDepthIn;
			}
			//salvare solamente il minore tra i vari risultati ottenuti in relazione ai nodi precedenti
			//richiamare nuovamente il metodo sui nodi successivi
			if (!nodesIndepth.containsKey(sourceNode))
				nodesIndepth.put(sourceNode, toReturn);
			Collection<SequenceFlow> outgoingFlows = sourceNode.getOutgoing();
			for (SequenceFlow outFlow : outgoingFlows) {
				calculateDepthIn(outFlow.getTarget());
			}
			visitedNodes.remove(sourceNode.getId());
			return;
		}
		
	}
}
