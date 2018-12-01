package camunda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

/**
 * Class that is used to calculate the partitionability metrics, following the
 * definitions of Jan Mendeling's Metrics for Process Models. The metrics are
 * Separability, Depth and Structuredness, and this class contains an inner
 * class for each one of those.
 * 
 * @author PROSLabTeam
 *
 */
public class PartitionabilityMetricsExtractor {

	private BpmnBasicMetricsExtractor basicExtractor;

	public PartitionabilityMetricsExtractor(BpmnBasicMetricsExtractor basicExtractor) {
		this.basicExtractor = basicExtractor;
	}

	/**
	 * Public method to calculate the separability, using the inner class
	 * SeparabilityMetricExtractor
	 * 
	 * @return mdoel separability
	 */
	public double getSeparability() {
		SeparabilityMetricExtractor extractor = new SeparabilityMetricExtractor();
		return extractor.getSeparabilityInner();
	}

	/**
	 * Public method to calculate the depth, using the inner class
	 * DepthMetricExtractor
	 * 
	 * @return model depth
	 */
	public double getDepth() {
		DepthMetricExtractor ex = new DepthMetricExtractor();
		return ex.getDepthInner();
	}

	/**
	 * Public method to calculate the structuredness, using the inner class
	 * StructurednessMetricExtractor
	 * 
	 * @return model structuredness
	 */
	public double getStructuredness() {
		StructurednessMetricExtractor ex = new StructurednessMetricExtractor();
		return ex.getStructurednessInner();
	}

	/**
	 * Inner class the is used to calculate the model separability
	 * 
	 * @author PROSLabTeam
	 *
	 */
	private class SeparabilityMetricExtractor {

		// The collection of the Flow Nodes from the model
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
		 * Method that calculates the separability of the model. First it gets the
		 * number of nodes that have no outgoing or incoming flows. Then, it checks the
		 * model obtained by removing each one of the node, to check again the number of
		 * nodes that have no outgoing or incoming flows. If those last numbers are
		 * greater than the ones from the original model, the node in question is a
		 * cut-vertex, thus the separability gets incremented by one.
		 * 
		 * @return
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
			Collection<SequenceFlow> inFlows;
			Collection<SequenceFlow> outFlows;
			for (FlowNode node : nodes) {
				tempNoOutgoingNodes = 0;
				tempNoIncomingNodes = 0;
				for (FlowNode tempNode : nodes) {
					if (!tempNode.equals(node)) {
						inFlows = tempNode.getIncoming();
						outFlows = tempNode.getOutgoing();
						if (inFlows.size() == 0)
							tempNoIncomingNodes++;
						for (SequenceFlow in : inFlows) {
							if (in.getSource().equals(node) && (inFlows.size() - 1) == 0) {
								tempNoIncomingNodes++;
							}
						}
						if (outFlows.size() == 0)
							tempNoOutgoingNodes++;
						for (SequenceFlow out : outFlows) {
							if (out.getTarget().equals(node) && (outFlows.size() - 1) == 0) {
								tempNoOutgoingNodes++;
							}
						}
					}
				}
				// if (tempNoOutgoingNodes > initialNoOutgoingNodes && tempNoIncomingNodes >
				// initialNoIncomingNodes) {
				if (tempNoIncomingNodes > initialNoIncomingNodes) {
					cutVertices++;
				}
			}
			return (double) cutVertices;
		}
	}

	/**
	 * Inner class the is used to calculate the model depth
	 * 
	 * @author PROSLabTeam
	 *
	 */
	private class DepthMetricExtractor {

		private Collection<String> visitedNodes;
		// Map between the Flow Nodes and the relative depth-in
		private Map<FlowNode, Integer> nodesIndepth;
		// Map between the id of the Flow Nodes and the relative depth-in
		private Map<String, Integer> nodesOutdepth;

		public DepthMetricExtractor() {
			visitedNodes = new ArrayList<String>();
			nodesIndepth = new HashMap<FlowNode, Integer>();
			nodesOutdepth = new HashMap<String, Integer>();
		}

		/**
		 * Method that calculates the depth of the model. The depth of a node represents
		 * the minimun between the depth-in and the depth-out of a Node in the model.
		 * This method iterates on the start nodes of the model to calculate recursively the depth-in and the depth-out for each one of them.
		 * @return the depth of the model
		 */
		public double getDepthInner() {
			Collection<ModelElementInstance> modelStartEvent = basicExtractor
					.getCollectionOfElementType(StartEvent.class);
			Collection<ModelElementInstance> modelNodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
			ArrayList<Integer> results = new ArrayList<Integer>();
			int depthIn = 0;
			int depthOut = 0;
			int toAdd = 0;
			int toReturn = 0;
			//Iterate on every start event, to get every possibile path and, thus, every node
			for (ModelElementInstance modelStart : modelStartEvent) {
				visitedNodes.clear();
				FlowNode start = (FlowNode) modelStart;
				calculateDepthIn(start);
				visitedNodes.clear();
				calculateDepthOut(start);
			}
			//Iterate on every node to check the minimum between the depth-in and the depth-out
			for (ModelElementInstance modelNode : modelNodes) {
				FlowNode node = (FlowNode) modelNode;
				depthIn = nodesIndepth.get(node);
				depthOut = nodesOutdepth.get(node.getId());
				toAdd = depthIn < depthOut ? depthIn : depthOut;
				results.add(toAdd);
			}
			//Iterate on the array of the minumum results obtained to get the maximum
			for (int result : results) {
				toReturn = result > toReturn ? result : toReturn;
			}
			return (double) toReturn;
		}
		
		/**
		 * Recursive method that calculates the depth out of a node. The depth out of a node is based on the depth out of the successive node.
		 * The depth out is incremented by one for every join node that is on the path of the node, and decremented for every split.
		 * @param sourceNode 
		 * @return the depthOut of the node
		 */
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
			//Iterates on each outgoing flow of the source node
			for (SequenceFlow outFlow : outgoingFlows) {
				succNode = outFlow.getTarget();
				//Check if the successive node's depth-out has already been calculated
				if (nodesOutdepth.containsKey(succNode.getId())) {
					tempDepthOut = nodesOutdepth.get(succNode.getId());
				} else {
					//The successive node's depth out has not already been calculated, so the method is recursively called 
					tempDepthOut = calculateDepthOut(succNode);
				}
				//Save the greatest depth-out of the various possible successive nodes
				if (tempDepthOut > succDepthOut || succDepthOut == 0) {
					succDepthOut = tempDepthOut;
					succOutgoing = succNode.getOutgoing().size();
				}
			}
			if ((succOutgoing > 1 && incomingSize > 1)
					|| ((succOutgoing == 1 || succOutgoing == 0) && (incomingSize == 0 || incomingSize == 1))) {
				// max(depthout(curr), depthout(succ)) if pre -> Split && curr -> Join OR
				// max(depthout(curr), depthout(succ)) if pre -/> Split && curr -/> Join
				// toReturn = succDepthOut > baseDepthOut ? succDepthOut : baseDepthOut;
				toReturn = succDepthOut;
			} else if (succOutgoing > 1 && (incomingSize == 0 || incomingSize == 1)) {
				// max(depthout(curr), depthout(succ) - 1) if pre -> Split && curr -/> Join
				toReturn = (succDepthOut - 1) > baseDepthOut ? (succDepthOut - 1) : baseDepthOut;
			} else if ((succOutgoing == 1 || succOutgoing == 0) && incomingSize > 1) {
				// max(depthout(curr), depthout(succ) + 1) if pre -/> Split && curr -> Join
				// toReturn = (succDepthOut + 1) > baseDepthOut ? (succDepthOut + 1) :
				// baseDepthOut;
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
			// fare i confronti relativi
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
			if ((precNodeOutgoing > 1 && incomingSize > 1)
					|| ((precNodeOutgoing == 1 || precNodeOutgoing == 0) && (incomingSize == 0 || incomingSize == 1))) {
				// max(depthin(curr), depthin(succ)) if pre -> Split && curr -> Join OR
				// max(depthin(curr), depthin(succ)) if pre -/> Split && curr -/> Join
				// toReturn = precNodeDepthIn > baseDepthIn ? precNodeDepthIn : baseDepthIn;
				toReturn = precNodeDepthIn;
			} else if (precNodeOutgoing > 1 && (incomingSize == 0 || incomingSize == 1)) {
				// max(depthin(curr), depthin(succ) + 1) if pre -> Split && curr -/> Join
				// toReturn = (precNodeDepthIn + 1) > baseDepthIn ? (precNodeDepthIn + 1):
				// baseDepthIn;
				toReturn = (precNodeDepthIn + 1);
			} else if ((precNodeOutgoing == 1 || precNodeOutgoing == 0) && incomingSize > 1) {
				// max(depthin(curr), depthin(succ) - 1) if pre -/> Split && curr -> Join
				toReturn = (precNodeDepthIn - 1) > baseDepthIn ? (precNodeDepthIn - 1) : baseDepthIn;
			}
			// salvare solamente il minore tra i vari risultati ottenuti in relazione ai
			// nodi precedenti
			// richiamare nuovamente il metodo sui nodi successivi
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

	private class StructurednessMetricExtractor {

		private Collection<String> visitedNodes;

		public StructurednessMetricExtractor() {
			visitedNodes = new ArrayList<String>();

		}

		public double getStructurednessInner() {
			Collection<ModelElementInstance> modelNodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
			int totalNumberOfNodes = modelNodes.size();
			System.out.println("Total Number of Nodes: " + totalNumberOfNodes);
			double result = 1 - (totalNumberOfNodes / getReducedGraphNumberOfNodesMetric());
			return result;
		}

		private int getReducedGraphNumberOfNodesMetric() {
			Collection<ModelElementInstance> modelNodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
			int nodesInReducedGraph = modelNodes.size();
			// If the model can be homogeneously reduced, the only nodes present are the
			// start event and the end event
			if (homogeneousReductionResult(modelNodes))
				nodesInReducedGraph = 2;
			else {
				// Cycle through every node of the model to check if it is part of a reducible
				// construct
				int totalNumberOfNodesRemoved;
				for (ModelElementInstance modelNode : modelNodes) {
					totalNumberOfNodesRemoved = 0;
					FlowNode node = (FlowNode) modelNode;
					// Calls every reduction method and sums all the nodes that can be removed
					totalNumberOfNodesRemoved = trivialConstructsReduction(node)
							+ structuredStartAndEndComponentsReduction(node) + unstructuredAcyclicEndComponents(node)
							+ unstructuredAcyclicStartAndEndComponents1(node) + unstructuredCyclicStartAndEnd1(node)
							+ unstructuredCyclicStartAndEnd2(node) + joinConnectorMerge(node)
							+ splitConnectorsMerge(node);
					nodesInReducedGraph -= totalNumberOfNodesRemoved;
				}
			}
			System.out.println("Nodes in reduced graph: " + nodesInReducedGraph);
			return nodesInReducedGraph;
		}

		/**
		 * 
		 * @param node
		 * @return
		 */
		private int structuredStartAndEndComponentsReduction(FlowNode node) {
			boolean reducedFlag = false;
			Collection<SequenceFlow> nodeIncoming, nodeOutgoing;
			int numberOfNodesToRemove = 0;
			if (node instanceof Gateway) {
				reducedFlag = false;
				nodeIncoming = node.getIncoming();
				for (SequenceFlow flow : nodeIncoming) {
					if (flow.getSource() instanceof StartEvent && flow.getSource().getOutgoing().size() == 1) {
						// variable decreased for every start event that has a single outgoing flow
						numberOfNodesToRemove++;
						reducedFlag = true;
					}
				}
				// variable re-increased for the node that would substitute the ones that got
				// merged together
				if (reducedFlag)
					numberOfNodesToRemove--;
				// Reduction of Structured End Components
				reducedFlag = false;
				nodeOutgoing = node.getOutgoing();
				for (SequenceFlow flow : nodeOutgoing) {
					if (flow.getTarget() instanceof EndEvent && flow.getTarget().getIncoming().size() == 1) {
						// variable decreased for every end event that has a single incoming flow
						numberOfNodesToRemove++;
						reducedFlag = true;
					}
				}
				// variable re-increased for the node that would substitute the ones that got
				// merged together
				if (reducedFlag)
					numberOfNodesToRemove--;
			}
			return numberOfNodesToRemove;
		}

		private int unstructuredAcyclicEndComponents(FlowNode node) {
			// Reduction of Unstructured Acyclic End Components
			Collection<SequenceFlow> nodeIncoming;
			int numberOfNodesToRemove = 0;
			if (node instanceof EndEvent) {
				nodeIncoming = node.getIncoming();
				if (node.getOutgoing().size() == 0 && nodeIncoming.size() == 1) {
					for (SequenceFlow flow : nodeIncoming) {
						if (flow.getSource() instanceof InclusiveGateway && !isInCycle(flow.getSource())) {
							numberOfNodesToRemove++;
						}
					}
				}
			}
			return numberOfNodesToRemove;
		}

		private int unstructuredAcyclicStartAndEndComponents1(FlowNode node) {
			int numberOfNodesToRemove = 0;
			// Reduction of Unstructured Acyclic Start and End Components (b)
			if (node instanceof Gateway && !(node instanceof ParallelGateway) && !isInCycle(node)) {
				boolean firstEventFound = false;
				boolean secondEventFound = false;
				boolean gatewayConnected = false;
				FlowNode tempNode, secondEventNode, firstEventNode = null;
				Collection<SequenceFlow> nodeOutgoing = node.getOutgoing();
				Collection<SequenceFlow> tempNodeIncoming, tempNodeOutgoing;
				for (SequenceFlow outFlow : nodeOutgoing) {
					tempNode = outFlow.getTarget();
					if (!firstEventFound && (tempNode instanceof EndEvent || tempNode instanceof StartEvent)
							&& tempNode.getOutgoing().size() == 0 && tempNode.getIncoming().size() == 1) {
						firstEventNode = tempNode;
						firstEventFound = true;
					}
					if (!secondEventFound && tempNode instanceof ParallelGateway && !isInCycle(tempNode)) {
						tempNodeIncoming = tempNode.getIncoming();
						for (SequenceFlow inFlowTemp : tempNodeIncoming) {
							secondEventNode = inFlowTemp.getSource();
							if ((secondEventNode instanceof EndEvent || secondEventNode instanceof StartEvent)
									&& !secondEventNode.equals(firstEventNode)
									&& secondEventNode.getIncoming().size() == 0
									&& secondEventNode.getOutgoing().size() == 1) {
								secondEventFound = true;
							}
						}
						tempNodeOutgoing = tempNode.getOutgoing();
						for (SequenceFlow outFlowTemp : tempNodeOutgoing) {
							if (outFlowTemp.getTarget().equals(node))
								gatewayConnected = true;
						}
					}
				}
				if (!gatewayConnected && firstEventFound && secondEventFound) {
					numberOfNodesToRemove++;
				}
			}
			return numberOfNodesToRemove;
		}

		private int unstructuredCyclicStartAndEnd1(FlowNode node) {
			int numberOfNodesToRemove = 0;
			// Reduction of Unstructured Cyclic Start and End Components
			if (node instanceof Gateway && isInCycle(node)) {
				boolean firstEventFound = false;
				boolean secondEventFound = false;
				boolean gatewayConnected = false;
				FlowNode firstConnector = node;
				FlowNode tempNode, firstEventNode = null;
				Collection<SequenceFlow> nodeOutgoing = node.getOutgoing();
				Collection<SequenceFlow> tempNodeOutgoing;
				for (SequenceFlow outFlow : nodeOutgoing) {
					tempNode = outFlow.getTarget();
					if (!firstEventFound && (tempNode instanceof EndEvent || tempNode instanceof StartEvent)
							&& tempNode.getOutgoing().size() == 0 && tempNode.getIncoming().size() == 1) {
						firstEventNode = tempNode;
						firstEventFound = true;
					}
					if (tempNode instanceof Gateway && isInCycle(tempNode) && (!(tempNode instanceof ExclusiveGateway)
							|| !(firstConnector instanceof ExclusiveGateway))) {
						FlowNode secondConnector = tempNode;
						tempNodeOutgoing = secondConnector.getOutgoing();
						for (SequenceFlow tempOutFlow : tempNodeOutgoing) {
							FlowNode secondEvent = tempOutFlow.getTarget();
							if (!secondEventFound
									&& (secondEvent instanceof EndEvent || secondEvent instanceof StartEvent)
									&& !secondEvent.equals(firstEventNode) && secondEvent.getOutgoing().size() == 0
									&& secondEvent.getIncoming().size() == 1) {
								secondEventFound = true;
							}
							if (secondEvent.equals(node))
								gatewayConnected = true;
						}
					}
				}
				if (!gatewayConnected && firstEventFound && secondEventFound) {
					numberOfNodesToRemove++;
				}
			}
			return numberOfNodesToRemove;
		}

		private int unstructuredCyclicStartAndEnd2(FlowNode node) {
			int numberOfNodesToRemove = 0;
			// Reduction of Unstructured Cyclic Start and End Components (b)
			if (node instanceof Gateway && isInCycle(node)) {
				boolean firstEventFound = false;
				boolean secondEventFound = false;
				boolean gatewayConnected = false;
				FlowNode firstConnector = node;
				FlowNode tempNode, firstEventNode = null;
				Collection<SequenceFlow> nodeIncoming = node.getIncoming();
				Collection<SequenceFlow> tempNodeIncoming;
				for (SequenceFlow inFlow : nodeIncoming) {
					tempNode = inFlow.getSource();
					if (!firstEventFound && (tempNode instanceof EndEvent || tempNode instanceof StartEvent)
							&& tempNode.getOutgoing().size() == 1 && tempNode.getIncoming().size() == 0) {
						firstEventNode = tempNode;
						firstEventFound = true;
					}
					if (tempNode instanceof Gateway && isInCycle(tempNode)
							&& (tempNode instanceof ParallelGateway || firstConnector instanceof ParallelGateway)) {
						FlowNode secondConnector = tempNode;
						tempNodeIncoming = secondConnector.getIncoming();
						for (SequenceFlow tempInFlow : tempNodeIncoming) {
							FlowNode secondEvent = tempInFlow.getSource();
							if (!secondEventFound
									&& (secondEvent instanceof EndEvent || secondEvent instanceof StartEvent)
									&& !secondEvent.equals(firstEventNode) && secondEvent.getOutgoing().size() == 1
									&& secondEvent.getIncoming().size() == 0) {
								secondEventFound = true;
							}
							if (secondEvent.equals(node))
								gatewayConnected = true;
						}
					}
				}
				if (!gatewayConnected && firstEventFound && secondEventFound) {
					numberOfNodesToRemove++;
				}
			}
			return numberOfNodesToRemove;
		}

		private int joinConnectorMerge(FlowNode node) {
			// Join Connector Merge
			int numberOfNodesToRemove = 0;
			if (node instanceof Gateway && node.getIncoming().size() > 1 && node.getOutgoing().size() == 1) {
				boolean possibleMerge = false;
				boolean gatewayConnected = false;
				Collection<SequenceFlow> nodeOutgoing = node.getOutgoing();
				FlowNode tempNode;
				for (SequenceFlow outFlow : nodeOutgoing) {
					tempNode = outFlow.getTarget();
					if (!tempNode.equals(node) && tempNode.getClass().equals(node.getClass())
							&& tempNode.getIncoming().size() > 1) {
						possibleMerge = true;
					}
					if (tempNode.equals(node)) {
						gatewayConnected = true;
					}
				}
				if (!gatewayConnected && possibleMerge) {
					numberOfNodesToRemove++;
				}
			}
			return numberOfNodesToRemove;
		}

		private int splitConnectorsMerge(FlowNode node) {
			int numberOfNodesToRemove = 0;
			// Split Connector Merge
			if (node instanceof Gateway && node.getOutgoing().size() > 1) {
				boolean possibleMerge = false;
				boolean gatewayConnected = false;
				Collection<SequenceFlow> nodeOutgoing = node.getOutgoing();
				FlowNode tempNode;
				for (SequenceFlow outFlow : nodeOutgoing) {
					tempNode = outFlow.getTarget();
					if (!tempNode.equals(node) && tempNode.getClass().equals(node.getClass())
							&& tempNode.getIncoming().size() == 1 && tempNode.getOutgoing().size() > 1) {
						possibleMerge = true;
					}
					if (tempNode.equals(node)) {
						gatewayConnected = true;
					}
				}
				if (!gatewayConnected && possibleMerge) {
					numberOfNodesToRemove++;
				}
			}
			return numberOfNodesToRemove;
		}

		private int trivialConstructsReduction(FlowNode node) {
			// Reduction of Trivial Constructs
			int numberOfNodesToRemove = 0;
			if (node.getOutgoing().size() == 1 && node.getOutgoing().size() == 1) {
				numberOfNodesToRemove++;
			}
			return numberOfNodesToRemove;
		}

		private boolean homogeneousReductionResult(Collection<ModelElementInstance> modelNodes) {
			Collection<ModelElementInstance> modelGateways = basicExtractor.getCollectionOfElementType(Gateway.class);
			boolean firstCase = true;
			boolean secondCase = true;
			boolean thirdCase = true;
			for (ModelElementInstance modelGateway : modelGateways) {
				Gateway gateway = (Gateway) modelGateway;
				if (!(gateway instanceof ExclusiveGateway)) {
					firstCase = false;
				}
				if (!(gateway instanceof InclusiveGateway)) {
					secondCase = false;
				}
				if (!((gateway.getOutgoing().size() > 1 && !(gateway instanceof ParallelGateway))
						|| (gateway.getIncoming().size() > 1 && !(gateway instanceof ExclusiveGateway)))) {
					thirdCase = false;
				}
			}
			for (ModelElementInstance modelNode : modelNodes) {
				FlowNode node = (FlowNode) modelNode;
				if (isInCycle(node)) {
					secondCase = false;
					thirdCase = false;
				}
			}
			if (firstCase || secondCase || thirdCase)
				return true;
			else
				return false;
		}

		private boolean isInCycle(FlowNode node) {
			if (visitedNodes.contains(node.getId()))
				return true;
			visitedNodes.add(node.getId());
			Collection<SequenceFlow> nodeOutgoing = node.getOutgoing();
			for (SequenceFlow flow : nodeOutgoing) {
				if (isInCycle(flow.getTarget()))
					return true;
			}
			visitedNodes.remove(node.getId());
			return false;
		}
	}
}