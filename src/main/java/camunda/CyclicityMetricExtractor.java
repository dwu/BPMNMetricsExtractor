package camunda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.apache.ibatis.reflection.SystemMetaObject;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
/**
 * 
 * @author PROSLabTeam
 *
 */
public class CyclicityMetricExtractor {
	private BpmnBasicMetricsExtractor basicMetricsExtractor;
	private ArrayList<TarjanNode> nodes = new ArrayList<TarjanNode>();
	private ArrayList<TarjanNode> nodesInCycle = new ArrayList<TarjanNode>();
	private ArrayList<ArrayList<TarjanNode>> stronglyConnectedComponents = new ArrayList<ArrayList<TarjanNode>>();
	private int index = 0;
	private Stack<TarjanNode> nodesStack;

	public CyclicityMetricExtractor(BpmnBasicMetricsExtractor bpmnBasicExtractor) {
		this.basicMetricsExtractor = bpmnBasicExtractor;
		this.nodesStack = new Stack<TarjanNode>();
		//Creates a TarjanNode for each node in the model
		for (FlowNode node: this.basicMetricsExtractor.getModelInstance().getModelElementsByType(FlowNode.class)) {
			this.nodes.add(new TarjanNode(node));
		}
		
		//Initializes the successors of all the nodes
		for (TarjanNode node: this.nodes) {
			node.setSuccessors(this.nodes);
		}

	}
	
	
	public void getModelStronglyConnectedComponents() {
		for (TarjanNode node: this.nodes) {
			if (node.getIndex() == -1) {
				this.getStronglyConnectedComponents(node);
			}
		}
		
		System.out.println("NODI NEL CICLO: " + this.nodesInCycle.size());
		System.out.println("COMPONENTI FORTEMENTE CONNESSE: " + this.stronglyConnectedComponents.size());
	}
	
	private void getStronglyConnectedComponents(TarjanNode node) {
		node.setIndex(this.index);
		node.setLowLink(this.index);
		this.index++;
		nodesStack.push(node);

		for (TarjanNode successor: node.getSuccessors()) {
			if (successor.getIndex() == -1) {
				this.getStronglyConnectedComponents(successor);
				node.setLowLink(Math.min(node.getLowLink(), successor.getLowLink()));
			} else if (nodesStack.contains(successor)) {
				node.setLowLink(Math.min(node.getLowLink(), successor.getIndex()));
			}
		}
		
		if (node.getLowLink() == node.getIndex()) {
			TarjanNode anotherNode;
			ArrayList<TarjanNode> scc = new ArrayList<TarjanNode>();
			
			do {
				anotherNode = nodesStack.pop();
				scc.add(anotherNode);
			} while (!node.equals(anotherNode));
			
			this.stronglyConnectedComponents.add(scc);
			
		}
	}
	
	private void setNodesInCycle(List<TarjanNode> scc) {
		if (scc.size() > 1) {
			this.nodesInCycle.addAll(scc);
		} else if (scc.size() == 1) {
			if (this.hasSelfLoop(scc.get(0))) {
				this.nodesInCycle.addAll(scc);
			}
		}
	}
	
	private boolean hasSelfLoop(TarjanNode node) {
		for (TarjanNode successor: node.getSuccessors()) {
			if (successor.equals(node)) {
				return true;
			}
		}
		return false;
	}
	
	private class TarjanNode {
		
		private FlowNode node;
		private int index;
		private int lowLink;
		private ArrayList<TarjanNode> successors = new ArrayList<TarjanNode>();
		
		private ArrayList<TarjanNode> predecessors = new ArrayList<TarjanNode>();
		
		
		public TarjanNode (FlowNode node) {
			this.node = node;
			this.index = -1;
			this.lowLink = -1;
		}
		
		public FlowNode getNode() {
			return node;
		}

		public void setNode(FlowNode node) {
			this.node = node;
		}

		public int getIndex() {
			return index;
		}
		
		public ArrayList<TarjanNode> getSuccessors() {
			return this.successors;
		}
		public ArrayList<TarjanNode> getPredecessors() {
			return this.predecessors;
		}
		public void setIndex(int index) {
			this.index = index;
		}

		public int getLowLink() {
			return lowLink;
		}

		public void setLowLink(int lowLink) {
			this.lowLink = lowLink;
		}
		
		public void setSuccessors(List<TarjanNode> nodes) {
			for (SequenceFlow flow: this.node.getOutgoing()) {
				for (TarjanNode node: nodes) {
					if ((flow.getTarget()).equals(node.getNode())) {
						this.successors.add(node);
					}
				}
			}
		}
		
	}
	
	
	
	
	
}