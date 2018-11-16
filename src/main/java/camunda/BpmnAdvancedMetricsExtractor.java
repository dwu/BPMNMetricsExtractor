package camunda;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class BpmnAdvancedMetricsExtractor {
	
	//modello da cui estrarre le metriche
	private BpmnModelInstance modelInstance;
	private BpmnBasicMetricsExtractor basicMetricsExtractor;
	private JsonEncoder json;
	
	public BpmnAdvancedMetricsExtractor(BpmnModelInstance modelInstance, BpmnBasicMetricsExtractor basicMetricsExtractor, JsonEncoder jsonEncoder) {
		this.modelInstance = modelInstance;
		this.basicMetricsExtractor = basicMetricsExtractor;
		this.json = jsonEncoder;
	}
	
	public void runMetrics() {
		json.addAdvancedMetric("CLA", getConnectivityLevelBetweenActivities());
		json.addAdvancedMetric("CLP", getConnectivityLevelBetweenPartecipants());
		json.addAdvancedMetric("PDOPin", getProportionOfIncomingDataObjectsAndTotalDataObjects());
		json.addAdvancedMetric("PDOPout", getProportionOfOutgoingDataObjectsAndTotalDataObjects());
		json.addAdvancedMetric("TNT", getTotalNumberOfTasks());
		json.addAdvancedMetric("PDOTout", getProportionOfDataObjectsAsOutgoingProducts());
		json.addAdvancedMetric("PLT", getProportionOfPoolsOrLanesAndActivities());
		json.addAdvancedMetric("TNCS", getNumberOfCollapsedSubProcesses());
		json.addAdvancedMetric("TNA", getTotalNumberOfActivities());
		json.addAdvancedMetric("TNDO", getTotalNumberOfDataObjects());
		json.addAdvancedMetric("TNG", getTotalNumberOfGateways());
		json.addAdvancedMetric("TNEE", getTotalNumberOfEndEvents());
		json.addAdvancedMetric("TNIE", getTotalNumberOfIntermediateEvents());
		json.addAdvancedMetric("TNSE", getTotalNumberOfStartEvents());
		json.addAdvancedMetric("TNE", getTotalNumberOfEvents());
		this.json.exportJson();
		System.out.println("JSON adv: " + this.json.print());
	}
	
	/**
	 * Metrica: CLA
	 * Total Number of Activities / Number of Sequence Flows between these Activities 
	 * (CLA = TNA / NSFA)
	 * @return 
	 */
	public float getConnectivityLevelBetweenActivities() {
		try {
			return getTotalNumberOfActivities() /  basicMetricsExtractor.getSequenceFlowsBetweenActivities();
		} catch (ArithmeticException e) {
			return 0;
		}
	}
	
	/**
	 * Metrica: CLP
	 * Number of message flows / Number of Pools 
	 * (CLP = NMF/NP)
	 * @return
	 */
	public float getConnectivityLevelBetweenPartecipants() {
		try {
			return basicMetricsExtractor.getMessageFlows() / basicMetricsExtractor.getPools();
		} catch (ArithmeticException e) {
			return 0;
		}
	}

	/**
	 * Metrica: PDOPin
	 * PDOPin Proportion of data objects as incoming products and total data objects
	 * Number of Data Objects which are input of activities / Total number of Data Objects in the model (PDOPIn = NDOIn/TNDO)
	 * @return 
	 */
	public float getProportionOfIncomingDataObjectsAndTotalDataObjects() {
		try {
			return basicMetricsExtractor.getDataObjectsInput() / getTotalNumberOfDataObjects();
		} catch (ArithmeticException e) {
			return 0;
		}
	}
	
	/**
	 * Metrica: PDOPout
	 * PDOPout Proportion of data objects as outgoing products and total data objects
	 * Number of Data Objects which are output of activities / Total number of Data Objects in the model (PDOPOut = NDOOut/TNDO)
	 * @return 
	 */
	public float getProportionOfOutgoingDataObjectsAndTotalDataObjects() {
		try {
			return basicMetricsExtractor.getDataObjectsOutput() / getTotalNumberOfDataObjects();
		} catch (ArithmeticException e) {
			return 0;
		}
	}
	
	/**
	 * Metrica: TNT
	 * Total number of task 
	 * Number of Tasks + Number of Task Looping + Number of Task Multiple Instances + Number of Task Compensation (TNT = NT + NTL + NTMI + NTC)
	 * @return
	 */
	public int getTotalNumberOfTasks() {
		return basicMetricsExtractor.getTasks();
	}
	
	/**
	 * Metrica: PDOTout
	 * Proportion of data objects as outgoing product of activities of the model
	 * Number of data objects which are outputs of activities / Total number of Tasks (PDOTOut = NDOOut/TNT)
	 * @return
	 */
	public float getProportionOfDataObjectsAsOutgoingProducts() {
		try {
			return basicMetricsExtractor.getDataObjectsOutput() / getTotalNumberOfTasks();
		} catch (ArithmeticException e) {
			return 0;
		}
	}
	
	/**
	 * Metrica: PLT
	 * Proportion of pools/lanes and activities
	 * Number of Lanes / Total number of Tasks (PLT = NL/TNT)
	 * @return
	 */
	public float getProportionOfPoolsOrLanesAndActivities() {
		try {
			return basicMetricsExtractor.getLanes() / getTotalNumberOfTasks();
		} catch (ArithmeticException e) {
			return 0;
		}
	}
	
	/**
	 * Metrica: TNCS
	 * Total number of collapsed sub-processes
	 * Number of Collapsed Sub-Processes + Number of Collapsed Sub-Processes Looping + Number of Collapsed Sub-Processes Multiple Instance
	 *    + Number of Collapsed Sub-Processes Compensation + Number of Collapsed Sub-Processes Ad-Hoc
	 * (TNCS = NCS + NCSL + NCSMI + NCSC + NCSA)
	 * @return
	 */
	public int getNumberOfCollapsedSubProcesses() {
		return basicMetricsExtractor.getSubprocesses();
	}
	
	/**
	 * Metrica: TNA
	 * Total number of activities
	 * Total number of Tasks + Total number of Collapsed Sub-Processes(TNA = TNT + TNCS)
	 * @return
	 */
	public int getTotalNumberOfActivities() {
		return getTotalNumberOfTasks() + getNumberOfCollapsedSubProcesses();
	}
	
	/**
	 * Metrica: TNDO
	 * Total number of Data Objects in the model
	 * Number of data objects which are input of activities + Number of data objects which are outputs of activities (TNDO = NDOIn + NDOOut)
	 * @return
	 */
	public int getTotalNumberOfDataObjects() {
		return basicMetricsExtractor.getDataObjectsInput() + basicMetricsExtractor.getDataObjectsOutput();
	}
	
	/**TODO
	 * Metrica: TNG
	 * Total number of gateways
	 * Number of exclusive data-based decision + Number of exclusive event-based decision + Number of inclusive decision + Number of complex decision + Number of parallel forking
	 * (TNG = NEDDB + NEDEB + NID + NCD + NPF)
	 * @return
	 */
	public int getTotalNumberOfGateways() {
		return basicMetricsExtractor.getExclusiveDataBasedDecisions() + basicMetricsExtractor.getExclusiveEventBasedDecisions() + basicMetricsExtractor.getInclusiveDecisions() + 
				basicMetricsExtractor.getComplexDecisions() + basicMetricsExtractor.getParallelGateways();
		//return basicMetricsExtractor.getGateways();
	}
	
	/**TODO i vari tipi di End Events non sono presenti 
	 * Metrica: TNEE
	 * Total number of End Events
	 * Number of End None Events + Number of End Message Events + Number of End Error Events + Number of End Cancel Events +
	 *      + Number of End Compensation Events + Number of End Link Events + Number of End Multiple Events + Number of End Terminate Events  
	 * (TNEE = NENE + NEMsE + NEEE + NECaE + NECoE + NELE + NEMuE + NETE)
	 * @return
	 */
	public int getTotalNumberOfEndEvents() {
		return basicMetricsExtractor.getEndEvents();
	}
	
	/**TODO non sono presenti None, Rule e Multiple // non sono veramente intermediate events
	 * Metrica: TNIE
	 * Total number of Intermediate Events
	 * Number of Intermediate None Events  + Number of Intermediate Timer Events + Number of Intermediate Message Events + Number of Intermediate Error Events + 
	 *     + Number of Intermediate Cancel Events + Number of Intermediate Compensation Event + Number of Intermediate Rule Events +
	 *     + Number of Intermediate Link Events + Number of Intermediate Multiple Events
	 * (TNIE = NINE + NITE + NIMsE + NIEE + NICaE + NICoE + NIRE + NILE + NIMuE)
	 * @return
	 */
	public int getTotalNumberOfIntermediateEvents() {
		return basicMetricsExtractor.getTimerEventDefinitions() + basicMetricsExtractor.getMessageEvents() + basicMetricsExtractor.getErrorEvents() 
		    + basicMetricsExtractor.getCancelEvents() + basicMetricsExtractor.getCompensateEvents() + basicMetricsExtractor.getLinkEvents();
	}
	
	/**TODO i vari tipi di Start Events non sono presenti 
	 * Metrica: TNSE
	 * Total number of Start Events
	 * Number of Start None Events + Number of Start Timer Events + Number of Start Message Events + Number of Start Rule Events + Number of Start Link Events + 
	 *     + Number of Start Multiple Events 
	 * (TNSE = NSNE + NSTE + NSMsE + NSRE + NSLE + NSMuE)
	 * @return
	 */
	public int getTotalNumberOfStartEvents() {
		return basicMetricsExtractor.getStartEvents();
	}
	
	/**
	 * Metrica: TNE
	 * Total number of Events
	 * Total number of Start Events + Total number of Intermediate Events + Total number of End Events
	 * (TNE = TNSE + TNIE + TNEE)
	 * @return
	 */
	public int getTotalNumberOfEvents() {
		return getTotalNumberOfStartEvents() + getTotalNumberOfIntermediateEvents() + getTotalNumberOfEndEvents();
	}
	
	/**
	 * Metrica: CFC
	 * Control-flow Complexity metric. It captures a weighted sum of all connectors that are used in a process model.
	 * @return
	 */
	public double getControlFlowComplexity() {
		double toReturn = 0;
		int tempSize = 0;
		Collection<ModelElementInstance> exclusiveGateways = basicMetricsExtractor.getCollectionOfElementType(ExclusiveGateway.class);
		Collection<ModelElementInstance> inclusiveGateways = basicMetricsExtractor.getCollectionOfElementType(InclusiveGateway.class);
		Collection<ModelElementInstance> parallelGateways = basicMetricsExtractor.getCollectionOfElementType(ParallelGateway.class);
		//La CFC di uno xor-split presente in un processo è data dal numero di flussi uscenti dallo split in questione
		for (ModelElementInstance exGateway : exclusiveGateways) {
			toReturn += ((FlowNode) exGateway).getOutgoing().size();
		}
		//La CFC di un or-split è data da 2^n - 1, dove n è pari al numero di flussi uscenti dallo split in questione
		for (ModelElementInstance inGateway : inclusiveGateways) {
			tempSize = ((FlowNode) inGateway).getOutgoing().size();
			toReturn = Math.pow(2, tempSize) - 1;
		}
		//La CFC di and-split è semplicemente 1
		toReturn += parallelGateways.size();
		return toReturn;
	}
	
	/**
	 * Metrica: NOAC
	 * Number of activities and control-flow elements in a process. 
	 * @return
	 */
	public int getNumberOfActivitiesAndControlFlowElements() {
		return basicMetricsExtractor.getActivities() + basicMetricsExtractor.getFlowElements();
	}
	
	/**
	 * Metrica: NOAJS
	 * Number of activities, joins, and splits in a process
	 */
	public int getNumberOfActivitiesJoinsAndsplits() {
		return basicMetricsExtractor.getActivities();
	}
	
	/**
	 * Metrica: HPC_D
	 * Hasted-based Process Complexity (process difficulty)
	 * @return
	 */
	public int getHastedBasedProcessDifficultyComplexity() {
		return 0;
	}
	
	/**
	 * Metrica: HPC_N
	 * Hasted-based Process Complexity (process length)
	 * @return
	 */
	public int getHastedBasedProcessLengthComplexity() {
		return 0;
	}
	
	/**
	 * Metrica: HPC_V
	 * Halsted-based Process Complexity (process volume)
	 * @return
	 */
	public int getHastedBasedProcessVolumeComplexity() {
		return 0;
	}
	
	/**
	 * Metrica: NoI or Fanin
	 * Number of activity inputs. The fan-in of a procedure A is the number of local flows
	 *  into procedure A plus the number of data structures from which procedure A retrieves information.
	 * @return
	 */
	public int getNumberOfActivityInputs() {
		return 0;
	}
	
	/**
	 * Metrica: NoO or Fanout
     * Number of activity outputs. The fan-out of a procedure A is the number of local flows
     *  from procedure A plus the number of data structures which procedure A updates.
	 * @return
	 */
	public int getNumberOfActivityOutputs() {
		return 0;
	}
	
	/**
	 * Metrica: Length
	 * Activity length. The length is 1 if the activity is a black box; if it is a white box,
	 *  the length can be calculated using traditional software engineering metrics
	 *  that have been previously presented, namely the LOC (line of code) and
	 *  MCC (McCabe’s cyclomatic complexity).
	 *  @return
	 */
	public int getActivityLenght() {
		return 0;
	}
	
	/**
	 * Metrica: IC
	 * Interface complexity of an activity metric. IC = Length ? (NoI ? NoO)2, where 
	 *  the length of the activity can be calculated using traditional Software Engineering metrics
	 *  such as LOC (1 if the activity source code is unknown) and NoI and NoO are 
	 *  the number of inputs and outputs.
	 * @return
	 */
	public int getInterfaceComplexityOfActivityMetric() {
		return 0;
	}
	
	/**
	 * Metrica: NOF
	 * Number of control flow connections (number of arcs)
	 * @return
	 */
	public int getNumberOfControlFlow() {
		return 0;
	}
}