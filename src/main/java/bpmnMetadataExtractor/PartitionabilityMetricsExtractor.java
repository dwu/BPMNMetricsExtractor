package bpmnMetadataExtractor;

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

//	/**
//	 * Public method to calculate the separability, using the inner class
//	 * SeparabilityMetricExtractor
//	 * 
//	 * @return mdoel separability
//	 */
//	public double getSeparability() {
//		SeparabilityMetricExtractor extractor = new SeparabilityMetricExtractor();
//		return extractor.getSeparabilityInner();
//	}

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

//	/**
//	 * Inner class the is used to calculate the model separability
//	 * 
//	 * @author PROSLabTeam
//	 *
//	 */
//	private class SeparabilityMetricExtractor {
//
//		// The collection of the Flow Nodes from the model
//		private Collection<FlowNode> nodes;
//
//		public SeparabilityMetricExtractor() {
//			Collection<ModelElementInstance> modelNodes = new ArrayList<ModelElementInstance>(
//					basicExtractor.getCollectionOfElementType(FlowNode.class));
//			nodes = new ArrayList<FlowNode>();
//			for (ModelElementInstance modelNode : modelNodes) {
//				nodes.add((FlowNode) modelNode);
//			}
//		}
//
//		/**
//		 * Method that calculates the separability of the model. First it gets
//		 * the number of nodes that have no outgoing or incoming flows. Then, it
//		 * checks the model obtained by removing each one of the node, to check
//		 * again the number of nodes that have no outgoing or incoming flows. If
//		 * those last numbers are greater than the ones from the original model,
//		 * the node in question is a cut-vertex, thus the separability gets
//		 * incremented by one.
//		 * 
//		 * @return
//		 */
//		public double getSeparabilityInner() {
//			int initialNoIncomingNodes = 0;
//			int initialNoOutgoingNodes = 0;
//			int cutVertices = 0;
//			// Iterate the nodes of the model
//			for (FlowNode node : nodes) {
//				if (node.getOutgoing().size() == 0)
//					initialNoOutgoingNodes++;
//				if (node.getIncoming().size() == 0)
//					initialNoIncomingNodes++;
//			}
//			int tempNoOutgoingNodes = 0;
//			int tempNoIncomingNodes = 0;
//			Collection<SequenceFlow> inFlows;
//			Collection<SequenceFlow> outFlows;
//			for (FlowNode node : nodes) {
//				tempNoOutgoingNodes = 0;
//				tempNoIncomingNodes = 0;
//				for (FlowNode tempNode : nodes) {
//					if (!tempNode.equals(node)) {
//						inFlows = tempNode.getIncoming();
//						outFlows = tempNode.getOutgoing();
//						if (inFlows.size() == 0)
//							tempNoIncomingNodes++;
//						for (SequenceFlow in : inFlows) {
//							if (in.getSource().equals(node) && (inFlows.size() - 1) == 0) {
//								tempNoIncomingNodes++;
//							}
//						}
//						if (outFlows.size() == 0)
//							tempNoOutgoingNodes++;
//						for (SequenceFlow out : outFlows) {
//							if (out.getTarget().equals(node) && (outFlows.size() - 1) == 0) {
//								tempNoOutgoingNodes++;
//							}
//						}
//					}
//				}
//				// if (tempNoOutgoingNodes > initialNoOutgoingNodes &&
//				// tempNoIncomingNodes >
//				// initialNoIncomingNodes) {
//				if (tempNoIncomingNodes > initialNoIncomingNodes) {
//					cutVertices++;
//				}
//			}
//			return (double) cutVertices;
//		}
//	}

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
		 * Method that calculates the depth of the model. The depth of a node
		 * represents the minimun between the depth-in and the depth-out of a
		 * Node in the model. This method iterates on the start nodes of the
		 * model to calculate recursively the depth-in and the depth-out for
		 * each one of them.
		 * 
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
			// Iterate on every start event, to get every possibile path and,
			// thus, every node
			for (ModelElementInstance modelStart : modelStartEvent) {
				visitedNodes.clear();
				FlowNode start = (FlowNode) modelStart;
				calculateDepthIn(start);
				visitedNodes.clear();
				calculateDepthOut(start);
			}
			// Iterate on every node to check the minimum between the depth-in
			// and the depth-out
			for (ModelElementInstance modelNode : modelNodes) {
				FlowNode node = (FlowNode) modelNode;
				//Boundary events controls
				if (nodesIndepth.containsKey(node))
					depthIn = nodesIndepth.get(node);
				if (nodesOutdepth.containsKey(node.getId()))
					depthOut = nodesOutdepth.get(node.getId());
				toAdd = depthIn < depthOut ? depthIn : depthOut;
				results.add(toAdd);
			}
			// Iterate on the array of the minumum results obtained to get the
			// maximum
			for (int result : results) {
				toReturn = result > toReturn ? result : toReturn;
			}
			return (double) toReturn;
		}

		/**
		 * Recursive method that calculates the depth out of a node. The depth
		 * out of a node is based on the depth out of the successive node. The
		 * depth out is incremented by one for every join node that is found on
		 * the path of the node, and decremented for every split.
		 * 
		 * @param sourceNode
		 * @return the depthOut of the node
		 */
		private int calculateDepthOut(FlowNode sourceNode) {
			// Check if the node has already been visited to avoid an infinite
			// loop in presence of cycles in the model
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
			// Iterates on each outgoing flow of the source node
			for (SequenceFlow outFlow : outgoingFlows) {
				try {
					succNode = outFlow.getTarget();
					// Check if the successive node's depth-out has already been
					// calculated
					if (nodesOutdepth.containsKey(succNode.getId())) {
						tempDepthOut = nodesOutdepth.get(succNode.getId());
					} else {
						// The successive node's depth out has not already been
						// calculated, so the method is recursively called
						tempDepthOut = calculateDepthOut(succNode);
					}
					// Save the greatest depth-out of the various successive nodes
					if (tempDepthOut > succDepthOut || succDepthOut == 0) {
						succDepthOut = tempDepthOut;
						succOutgoing = succNode.getOutgoing().size();
					}
				}catch(Exception e) {continue;}
			}
			// Calculate the depth-out checking if the successive node is either
			// a split node or not and if the current node is a join node or not
			if ((succOutgoing > 1 && incomingSize > 1)
					|| ((succOutgoing == 1 || succOutgoing == 0) && (incomingSize == 0 || incomingSize == 1))) {
				// max(depthout(curr), depthout(succ)) if pre -> Split && curr -> Join OR
				// max(depthout(curr), depthout(succ)) if pre -/> Split && curr -/> Join
				toReturn = succDepthOut;
			} else if (succOutgoing > 1 && (incomingSize == 0 || incomingSize == 1)) {
				// max(depthout(curr), depthout(succ) - 1) if pre -> Split && curr -/> Join
				toReturn = (succDepthOut - 1) > baseDepthOut ? (succDepthOut - 1) : baseDepthOut;
			} else if ((succOutgoing == 1 || succOutgoing == 0) && incomingSize > 1) {
				// max(depthout(curr), depthout(succ) + 1) if pre -/> Split && curr -> Join
				toReturn = (succDepthOut + 1);
			}
			visitedNodes.remove(sourceNode.getId());
			// Save the depth-out of the node in the relative map
			if (!nodesOutdepth.containsKey(sourceNode.getId()))
				nodesOutdepth.put(sourceNode.getId(), toReturn);
			return toReturn;
		}

		/**
		 * Recursive method that calculates the depth in of a node. The depth in
		 * of a node is based on the depth in of the previous node. The depth in
		 * is incremented by one for every split node that is found on the path
		 * of the node, and decremented for every join.
		 * 
		 * @param sourceNode
		 * @return the depthOut of the node
		 */
		private void calculateDepthIn(FlowNode sourceNode) {
			// Check if the node has already been visited to avoid an infinite
			// loop in presence of cycles in the model
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
			// Iterates on each incoming flow of the source node
			for (SequenceFlow inFlow : incomingFlows) {
				precNode = inFlow.getSource();
				// Check if the previous node's depth-in has already been
				// calculated
				if (nodesIndepth.containsKey(precNode)) {
					tempDepthIn = nodesIndepth.get(precNode);
				}
				// Save the greatest depth-in of the various previous nodes
				if (tempDepthIn > precNodeDepthIn || precNodeDepthIn == 0) {
					precNodeDepthIn = tempDepthIn;
					precNodeOutgoing = precNode.getOutgoing().size();
				}
			}
			// Calculate the depth-in checking if the previous node is either a
			// split node or not and if the current node is a join node or not
			if ((precNodeOutgoing > 1 && incomingSize > 1)
					|| ((precNodeOutgoing == 1 || precNodeOutgoing == 0) && (incomingSize == 0 || incomingSize == 1))) {
				// max(depthin(curr), depthin(succ)) if pre -> Split && curr -> Join OR
				// max(depthin(curr), depthin(succ)) if pre -/> Split && curr -/> Join
				toReturn = precNodeDepthIn;
			} else if (precNodeOutgoing > 1 && (incomingSize == 0 || incomingSize == 1)) {
				// max(depthin(curr), depthin(succ) + 1) if pre -> Split && curr -/> Join
				toReturn = (precNodeDepthIn + 1);
			} else if ((precNodeOutgoing == 1 || precNodeOutgoing == 0) && incomingSize > 1) {
				// max(depthin(curr), depthin(succ) - 1) if pre -/> Split && curr -> Join
				toReturn = (precNodeDepthIn - 1) > baseDepthIn ? (precNodeDepthIn - 1) : baseDepthIn;
			}
			// Save the depth-out of the node in the relative map
			if (!nodesIndepth.containsKey(sourceNode))
				nodesIndepth.put(sourceNode, toReturn);
			// Call the method recursively on the successive nodes
			Collection<SequenceFlow> outgoingFlows = sourceNode.getOutgoing();
			for (SequenceFlow outFlow : outgoingFlows) {
				try {
					calculateDepthIn(outFlow.getTarget());
				}catch(Exception e) {continue;}
			}
			visitedNodes.remove(sourceNode.getId());
			return;
		}

	}

	/**
	 * Inner class the is used to calculate the model structuredness
	 * 
	 * @author PROSLabTeam
	 *
	 */
	private class StructurednessMetricExtractor {

		private Collection<String> visitedNodes;

		public StructurednessMetricExtractor() {
			visitedNodes = new ArrayList<String>();
		}

		/**
		 * Method that calculates the structuredness of the model. The
		 * structuredness of a model is given by 1 minus the division between
		 * the number of nodes in the reduced graph and the number of nodes of
		 * the current graph.
		 * 
		 * @return the structuredness of the model
		 */
		public double getStructurednessInner() {
			Collection<ModelElementInstance> modelNodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
			double totalNumberOfNodes = (double) modelNodes.size();
			double reducedGraphNodes = (double) getReducedGraphNumberOfNodes();
			if (reducedGraphNodes == 2.0) {
				return 1.0;
			} else {
				double result = 1.0 - (reducedGraphNodes / totalNumberOfNodes);
				return result;
			}
		}

		/**
		 * Method that, given the base model, determines what nodes would be
		 * removed in the construction of the reduced graph, applying only the
		 * rules that would have changed the number of nodes in the graph.
		 * 
		 * @return the number of nodes in the reduced graph
		 */
		private int getReducedGraphNumberOfNodes() {
			Collection<ModelElementInstance> modelNodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
			int nodesInReducedGraph = modelNodes.size();
			// If the model can be homogeneously reduced, the only nodes present are the
			// start event and the end event
			if (homogeneousReductionResult(modelNodes))
				nodesInReducedGraph = 2;
			else {
				// Cycle through every node of the model to check if it would be removed
				// in the reduced graph applying all the possible rules
				int totalNumberOfNodesRemoved;
				for (ModelElementInstance modelNode : modelNodes) {
					totalNumberOfNodesRemoved = 0;
					FlowNode node = (FlowNode) modelNode;
					// Calls every reduction method and sums all the nodes that
					// can be removed
					totalNumberOfNodesRemoved = trivialConstructsReduction(node)
							+ structuredStartAndEndComponentsReduction(node) + unstructuredAcyclicEndComponentsReduction(node)
							+ unstructuredAcyclicStartAndEndComponentsReduction(node) + unstructuredCyclicStartAndEndReduction1(node)
							+ unstructuredCyclicStartAndEndReduction2(node) + joinConnectorMerge(node)
							+ splitConnectorsMerge(node);
					nodesInReducedGraph -= totalNumberOfNodesRemoved;
				}
			}
			return nodesInReducedGraph;
		}

		/**
		 * Method that applies the structured start and end components reduction and calculates the number of 
		 * nodes that it would remove. 
		 * The rule states that if two or more start nodes are linked only to the same gateway, or if two or more
		 * end nodes' only incoming flows come from the same gateway, those events can be merged.
		 * @param node: current node
		 * @return the number of nodes that would be removed applying this rule
		 */
		private int structuredStartAndEndComponentsReduction(FlowNode node) {
			// "Reduction of Structured Start Components"
			boolean reducedFlag = false;
			Collection<SequenceFlow> nodeIncoming, nodeOutgoing;
			int numberOfNodesToRemove = 0;
			//Check if node is a gateway 
			if (node instanceof Gateway) {
				reducedFlag = false;
				nodeIncoming = node.getIncoming();
				//Iterate on the incoming flows and check if the nodes that generated them are Start Events with 
				// only one outgoing flow
				for (SequenceFlow flow : nodeIncoming) {
					if (flow.getSource() instanceof StartEvent && flow.getSource().getOutgoing().size() == 1) {
						numberOfNodesToRemove++;
						reducedFlag = true;
					}
				}
				//The number of the nodes to remove is decreased by one, to add the node that would be the 
				// sum of the ones that got removed
				if (reducedFlag)
					numberOfNodesToRemove--;
				// "Reduction of Structured End Components"
				reducedFlag = false;
				nodeOutgoing = node.getOutgoing();
				//Iterate on the outgoing flows and check if the nodes that are linked to them are End Events with 
				// only one incoming flow
				for (SequenceFlow flow : nodeOutgoing) {
					if (flow.getTarget() instanceof EndEvent && flow.getTarget().getIncoming().size() == 1) {
						numberOfNodesToRemove++;
						reducedFlag = true;
					}
				}
				//The number of the nodes to remove is decreased by one, to add the node that would be the 
				// sum of the ones that got removed
				if (reducedFlag)
					numberOfNodesToRemove--;
			}
			return numberOfNodesToRemove;
		}
		
		/**
		 * 
		 * @param node
		 * @return
		 */
		private int unstructuredAcyclicEndComponentsReduction(FlowNode node) {
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

		private int unstructuredAcyclicStartAndEndComponentsReduction(FlowNode node) {
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

		private int unstructuredCyclicStartAndEndReduction1(FlowNode node) {
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

		private int unstructuredCyclicStartAndEndReduction2(FlowNode node) {
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
		
		/**
		 * Checks if the node is part of a trivial constructs and returns 1 in a positive case, and 0 otherwise.
		 * A trivial construct is a node of the graph with only one incoming flow and one outgoing flow.
		 * @param node
		 * @return the number of nodes that would be removed by the trivial constructs reduction if applied on the current node
		 */
		private int trivialConstructsReduction(FlowNode node) {
			int numberOfNodesToRemove = 0;
			if (node.getOutgoing().size() == 1 && node.getOutgoing().size() == 1) {
				numberOfNodesToRemove++;
			}
			return numberOfNodesToRemove;
		}
		
		/**
		 * Checks the three possible cases in which the homogeneous reduction is possible on the model.
		 * In the first case, every gateway of the model must be a XOR-node (exclusive gateway)
		 * In the second case, every gateway of the model must be an OR-node (inclusive gateway)
		 * In the third case, every split gateway mustn't be an AND-node (parallel gateway) and 
		 *  every join gateway mustn't be a XOR-node.
		 * For both the second and the third node, every node of the cycle mustn't be part of a cycle.
		 * The homogeneous reduction reduces the graph to just the start and the end node.
		 * @param modelNodes
		 * @return true if the homogeneous reduction is applicable, false otherwise
		 */
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
		
		/**
		 * Checks if the node is part of a cycle through recursion
		 * @param node
		 * @return true if the node is part of a cycle, false otherwise
		 */
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