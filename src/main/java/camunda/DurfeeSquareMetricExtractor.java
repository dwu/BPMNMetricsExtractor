package camunda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Class that calculate the DSM metric
 * It equals d if there are d types of elements which occur at least d times in the model(each),
 * and the other types occur no more than d times (each)
 * @author PROSLabTeam
 *
 */
public class DurfeeSquareMetricExtractor {
	private BpmnBasicMetricsExtractor basicMetricsExtractor;
	private ArrayList<Integer> flowNodes;
	
	public DurfeeSquareMetricExtractor(BpmnBasicMetricsExtractor basicMetricsExtractor) {
		this.basicMetricsExtractor = basicMetricsExtractor;
		this.flowNodes = new ArrayList<Integer>();
	}
	
	public double getDurfeeMetric() {
		return this.calculateDurfee(this.getFlowNodes(flowNodes));
	}
	
	/**
	 * Method that get all the flow nodes in the model and separate them into an ArrayList
	 * @param flowNodes
	 * @return ArrayList of flownodes
	 */
	public ArrayList<Integer> getFlowNodes(ArrayList<Integer> flowNodes){
		flowNodes.add(this.basicMetricsExtractor.getActivities() - this.basicMetricsExtractor.getSubprocesses() - this.basicMetricsExtractor.getTasks());
		flowNodes.add(this.basicMetricsExtractor.getBoundaryEvents());
		flowNodes.add(this.basicMetricsExtractor.getBusinessRuleTasks());
		flowNodes.add(this.basicMetricsExtractor.getCallActivities());
		flowNodes.add(this.basicMetricsExtractor.getCatchEvents() - this.basicMetricsExtractor.getBoundaryEvents() - this.basicMetricsExtractor.getIntermediateCatchEvents() - this.basicMetricsExtractor.getStartEvents());
		flowNodes.add(this.basicMetricsExtractor.getComplexDecisions());
		flowNodes.add(this.basicMetricsExtractor.getEndEvents());
		flowNodes.add(this.basicMetricsExtractor.getFlowJoiningAndDividingEventBasedGateways());
		flowNodes.add(this.basicMetricsExtractor.getEvents() - this.basicMetricsExtractor.getCatchEvents() - this.basicMetricsExtractor.getThrowEvents());
		flowNodes.add(this.basicMetricsExtractor.getExclusiveEventBasedDecisions());
		flowNodes.add(this.basicMetricsExtractor.getExclusiveDataBasedDecisions());
		flowNodes.add(this.basicMetricsExtractor.getInclusiveDecisions());
		flowNodes.add(this.basicMetricsExtractor.getIntermediateCatchEvents());
		flowNodes.add(this.basicMetricsExtractor.getIntermediateThrowEvents());
		flowNodes.add(this.basicMetricsExtractor.getManualTasks());
		flowNodes.add(this.basicMetricsExtractor.getParallelGateways());
		flowNodes.add(this.basicMetricsExtractor.getReceiveTasks());
		flowNodes.add(this.basicMetricsExtractor.getScriptTasks());
		flowNodes.add(this.basicMetricsExtractor.getSendTasks());
		flowNodes.add(this.basicMetricsExtractor.getServiceTasks());
		flowNodes.add(this.basicMetricsExtractor.getStartEvents());
		flowNodes.add(this.basicMetricsExtractor.getSubprocesses());
		flowNodes.add(this.basicMetricsExtractor.getTasks() - this.basicMetricsExtractor.getBusinessRuleTasks() - this.basicMetricsExtractor.getManualTasks() - this.basicMetricsExtractor.getReceiveTasks() - this.basicMetricsExtractor.getScriptTasks() - this.basicMetricsExtractor.getSendTasks() - this.basicMetricsExtractor.getServiceTasks() - this.basicMetricsExtractor.getUserTasks());
		flowNodes.add(this.basicMetricsExtractor.getThrowEvents() - this.basicMetricsExtractor.getEndEvents() - this.basicMetricsExtractor.getIntermediateThrowEvents());
		flowNodes.add(this.basicMetricsExtractor.getTransactions());
		flowNodes.add(this.basicMetricsExtractor.getUserTasks());
		return flowNodes;
	}
	
	/**
	 * Durfee Metric Algorithm
	 * @param flowNodes
	 * @return Durfee Square
	 */
	private int calculateDurfee(ArrayList<Integer> flowNodes){
		flowNodes.removeIf(a -> (a == 0));
		int ds = 0;
		int max = Collections.max(flowNodes);
		Collections.sort(flowNodes);
		boolean found = false;
		//check for flow nodes presence
		if(!flowNodes.isEmpty()){
			//if there's only one flow node it just return 1
			if (flowNodes.size() == 1){
				ds = 1;
			} else {
				//set ds as max/2 in order to have a starting pivot to iterate with
				ds = max/2;
				do {
					int htds = 0;
					//if pivot*2 is higher then the amount of flow nodes
					if (ds * 2 > flowNodes.size()){
						ds--; //subtract 1 to ds
					} else {
						//check in the flow nodes if there are ds flow nodes with higher value than ds (htds)
						for (int i = 0; i < flowNodes.size(); i++){
							if (flowNodes.get(i) < ds){
								htds++;
							}
						}
						//if there are more htds than ds 
						if (htds > ds)
							ds--; //subtract 1 to ds
						else
							found = true; //Durfee Square has been found
					}
				} while (found = false); //Repeat if Durfee Square has not been found
			}
		}
		return ds;
	}
	
}
