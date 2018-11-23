package camunda;

import java.util.Collection;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Error;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

/**
 * 
 * Classe in cui andare a inserire i metoof per l'estrazione delle
 * statistiche/metadati
 * 
 * @author PROSLabTeam
 *
 *TODO: aggiungere task con più flussi entranti o uscenti ai parallel gateways
 *		distinzione event boundary, intermediate e start e end
 */
public class BpmnBasicMetricsExtractor {

	// modello da cui estrarre le metriche
	private BpmnModelInstance modelInstance;
	private JsonEncoder json;

	public BpmnBasicMetricsExtractor(BpmnModelInstance modelInstance, JsonEncoder jsonEncoder) {
		this.modelInstance = modelInstance;
		this.json = jsonEncoder;
	}

	/**
	 * Metodo principale per runnare tutti i metoof che ottengono le metriche
	 */
	public void runMetrics() {
		this.json.addBasicMetric("NT", this.getTasks());
		this.json.addBasicMetric("NCD", this.getComplexDecisions());
		this.json.addBasicMetric("NDOin", this.getDataObjectsInput());
		this.json.addBasicMetric("NDOout", this.getDataObjectsOutput());
		this.json.addBasicMetric("NID", this.getInclusiveDecisions());
		this.json.addBasicMetric("NEDDB", this.getExclusiveDataBasedDecisions());
		this.json.addBasicMetric("NEDEB", this.getExclusiveEventBasedDecisions());
		this.json.addBasicMetric("NL", this.getLanes());
		this.json.addBasicMetric("NMF", this.getMessageFlows());
		this.json.addBasicMetric("NP", this.getPools());
		this.json.addBasicMetric("NSFE", this.getSequenceFlowsFromEvents());
		this.json.addBasicMetric("NSFG", this.getSequenceFlowsFromGateways());
		this.json.addBasicMetric("NSFA", this.getSequenceFlowsBetweenActivities());
		this.json.addBasicMetric("NAC", this.getActivationConditions());
		this.json.addBasicMetric("NACT", this.getActivities());
		this.json.addBasicMetric("NART", this.getArtifacts());
		this.json.addBasicMetric("NASI", this.getAssignments());
		this.json.addBasicMetric("NASO", this.getAssociations());
		this.json.addBasicMetric("NAUD", this.getAuditing());
		this.json.addBasicMetric("NBEL", this.getBaseElements());
		this.json.addBasicMetric("NBEV", this.getBoundaryEvents());
		this.json.addBasicMetric("NBRT", this.getBusinessRuleTasks());
		this.json.addBasicMetric("NCEL", this.getCallableElements());
		this.json.addBasicMetric("NCAC", this.getCallActivities());
		this.json.addBasicMetric("NCCO", this.getCallConversations());
		this.json.addBasicMetric("NCANEV", this.getCancelEvents());
		this.json.addBasicMetric("NCATEV", this.getCatchEvents());
		this.json.addBasicMetric("NCVAL", this.getCategoryValues());
		this.json.addBasicMetric("NCOL", this.getCollaborations());
		this.json.addBasicMetric("NCOMEV", this.getCompensateEvents());
		this.json.addBasicMetric("NCOCON", this.getCompletionConditions());
		this.json.addBasicMetric("NCBDEF", this.getComplexBehaviorDefinitions());
		this.json.addBasicMetric("NCOND", this.getConditions());
		this.json.addBasicMetric("NCONDEV", this.getConditionalEvent());
		this.json.addBasicMetric("NCONDEX", this.getConditionExpressions());
		this.json.addBasicMetric("NCONV", this.getConversations());
		this.json.addBasicMetric("NCONVAS", this.getConversationAssociations());
		this.json.addBasicMetric("NCONVL", this.getConversationLinks());
		this.json.addBasicMetric("NCONVN", this.getConversationNodes());
		this.json.addBasicMetric("NCORK", this.getCorrelationKeys());
		this.json.addBasicMetric("NCORP", this.getCorrelationProperties());
		this.json.addBasicMetric("NCORPB", this.getCorrelationPropertyBindings());
		this.json.addBasicMetric("NCORPRE", this.getCorrelationPropertyRetrievalExpressions());
		this.json.addBasicMetric("NCORS", this.getCorrelationSubscriptions());
		this.json.addBasicMetric("NDA", this.getDataAssociations());
		this.json.addBasicMetric("NDInA", this.getDataInputAssociations());
		this.json.addBasicMetric("NDO", this.getDataObjects());
		this.json.addBasicMetric("NDOR", this.getDataObjectReferences());
		this.json.addBasicMetric("NDOutA", this.getDataOutputAssociations());
		this.json.addBasicMetric("NDSTA", this.getDataStates());
		this.json.addBasicMetric("NDSTO", this.getDataStores());
		this.json.addBasicMetric("NDEF", this.getDefinitions());
		this.json.addBasicMetric("NDOC", this.getDocumentations());
		this.json.addBasicMetric("NENDEV", this.getEndEvents());
		this.json.addBasicMetric("NENDP", this.getEndPoints());
		this.json.addBasicMetric("NERR", this.getErrors());
		this.json.addBasicMetric("NERREV", this.getErrorEvents());
		this.json.addBasicMetric("NESC", this.getEscalations());
		this.json.addBasicMetric("NESCEV", this.getEscalationEvents());
		this.json.addBasicMetric("NEV", this.getEvents());
		this.json.addBasicMetric("NEVDEF", this.getEventDefinitions());
		this.json.addBasicMetric("NEXP", this.getExpressions());
		this.json.addBasicMetric("NEXT", this.getExtensions());
		this.json.addBasicMetric("NEXTEL", this.getExtensionElements());
		this.json.addBasicMetric("NFLEL", this.getFlowElements());
		this.json.addBasicMetric("NFLNO", this.getFlowNodes());
		this.json.addBasicMetric("NFOREXP", this.getFormalExpressions());
		this.json.addBasicMetric("NGA", this.getGateways());
		this.json.addBasicMetric("NGC", this.getGlobalConversations());
		this.json.addBasicMetric("NHP", this.getHumanPerformers());
		this.json.addBasicMetric("NIMP", this.getImports());
		this.json.addBasicMetric("NInDI", this.getInputDataItems());
		this.json.addBasicMetric("NInS", this.getInputSets());
		this.json.addBasicMetric("NINTNO", this.getInteractionNodes());
		this.json.addBasicMetric("NINTE", this.getInterfaces());
		this.json.addBasicMetric("NICEV", this.getIntermediateCatchEvents());
		this.json.addBasicMetric("NITEV", this.getIntermediateThrowEvents());
		this.json.addBasicMetric("NIOB", this.getIoBindings());
		this.json.addBasicMetric("NIOS", this.getIoSpecifications());
		this.json.addBasicMetric("NIAEL", this.getItemAwareElements());
		this.json.addBasicMetric("NLEV", this.getLinkEvents());
		this.json.addBasicMetric("NLOOPCA", this.getLoopCardinalities());
		this.json.addBasicMetric("NLOOPCH", this.getLoopCharacteristics());
		this.json.addBasicMetric("NMT", this.getManualTasks());
		this.json.addBasicMetric("NMES", this.getMessages());
		this.json.addBasicMetric("NMESEV", this.getMessageEvents());
		this.json.addBasicMetric("NMESFA", this.getMessageFlowAssociations());
		this.json.addBasicMetric("NMON", this.getMonitorings());
		this.json.addBasicMetric("NMILCH", this.getMultiInstanceLoopCharacteristics());
		this.json.addBasicMetric("NOP", this.getOperations());
		this.json.addBasicMetric("NOutDI", this.getOutputDataItems());
		this.json.addBasicMetric("NOutS", this.getOutputSets());
		this.json.addBasicMetric("NPG", this.getParallelGateways());
		this.json.addBasicMetric("NPAS", this.getParticipantAssociations());
		this.json.addBasicMetric("NPM", this.getParticipantMultiplicities());
		this.json.addBasicMetric("NPER", this.getPerformers());
		this.json.addBasicMetric("NPO", this.getPotentialOwners());
		this.json.addBasicMetric("NPROC", this.getProcesses());
		this.json.addBasicMetric("NPROP", this.getProperties());
		this.json.addBasicMetric("NRT", this.getReceiveTasks());
		this.json.addBasicMetric("NREL", this.getRelationships());
		this.json.addBasicMetric("NREN", this.getRenderings());
		this.json.addBasicMetric("NRES", this.getResources());
		this.json.addBasicMetric("NRESAEX", this.getResourceAssignmentExpressions());
		this.json.addBasicMetric("NRESP", this.getResourceParameters());
		this.json.addBasicMetric("NRESPB", this.getResourceParameterBindings());
		this.json.addBasicMetric("NRESR", this.getResourceRoles());
		this.json.addBasicMetric("NRE", this.getRootElements());
		this.json.addBasicMetric("NSC", this.getScripts());
		this.json.addBasicMetric("NSCT", this.getScriptTasks());
		this.json.addBasicMetric("NSENT", this.getSendTasks());
		this.json.addBasicMetric("NSEQF", this.getSequenceFlows());
		this.json.addBasicMetric("NSERT", this.getServiceTasks());
		this.json.addBasicMetric("NSI", this.getSignals());
		this.json.addBasicMetric("NSIEV", this.getSignalEvent());
		this.json.addBasicMetric("NSTEV", this.getStartEvents());
		this.json.addBasicMetric("NSC", this.getSubConversations());
		this.json.addBasicMetric("NTEV", this.getTerminateEvents());
		this.json.addBasicMetric("NTEX", this.getTexts());
		this.json.addBasicMetric("NTEXA", this.getTextAnnotations());
		this.json.addBasicMetric("NTEV", this.getThrowEvents());
		this.json.addBasicMetric("NTC", this.getTimeCycles());
		this.json.addBasicMetric("NTDA", this.getTimeDates());
		this.json.addBasicMetric("NTDU", this.getTimeDurations());
		this.json.addBasicMetric("NTEVD", this.getTimerEventDefinitions());
		this.json.addBasicMetric("NTR", this.getTransactions());
		this.json.addBasicMetric("NUT", this.getUserTasks());
		this.json.addBasicMetric("NSUB", this.getSubprocesses());
		this.json.addBasicMetric("NFDCG", this.getFlowDividingComplexGateways());
		this.json.addBasicMetric("NFDEBG", this.getFlowDividingEventBasedGateways());
		this.json.addBasicMetric("NFDEXG", this.getFlowDividingExclusiveGateways());
		this.json.addBasicMetric("NFDIG", this.getFlowDividingInclusiveGateways());
		this.json.addBasicMetric("NFDPG", this.getFlowDividingParallelGateways());
		this.json.addBasicMetric("NFDG", this.getFlowDividingGateways());
		this.json.addBasicMetric("NFDT", this.getFlowDividingTasks());
		this.json.addBasicMetric("NFJCG", this.getFlowJoiningComplexGateways());
		this.json.addBasicMetric("NFJEBG", this.getFlowJoiningEventBasedGateways());
		this.json.addBasicMetric("NFJEXG", this.getFlowJoiningExclusiveGateways());
		this.json.addBasicMetric("NFJIG", this.getFlowJoiningInclusiveGateways());
		this.json.addBasicMetric("NFJPG", this.getFlowJoiningParallelGateways());
		this.json.addBasicMetric("NFJG", this.getFlowJoiningGateways());
		this.json.addBasicMetric("NFJT", this.getFlowJoiningTasks());
		this.json.addBasicMetric("NFJCG", this.getFlowJoiningAndDividingComplexGateways());
		this.json.addBasicMetric("NFJDEBG", this.getFlowJoiningAndDividingEventBasedGateways());
		this.json.addBasicMetric("NFJDEXG", this.getFlowJoiningAndDividingExclusiveGateways());
		this.json.addBasicMetric("NFJDIG", this.getFlowJoiningAndDividingInclusiveGateways());
		this.json.addBasicMetric("NFJDPG", this.getFlowJoiningAndDividingParallelGateways());
		this.json.addBasicMetric("NFJDG", this.getFlowJoiningAndDividingGateways());
		this.json.addBasicMetric("NFJDT", this.getFlowJoiningAndDividingTasks());
	}
	
	/**
	 * Metrica: NT
	 * 
	 * @return number of tasks
	 */
	public int getTasks() {
		return getNumberOfTypeElement(Task.class);
	}

	/**
	 * Metrica: NCD (number of complex gateways)
	 * 
	 * @return Number of complex decisions 
	 */
	public int getComplexDecisions() {
		return getNumberOfTypeElement(ComplexGateway.class);
	}

	/**
	 * Metrica: NDOin
	 * 
	 * @return number of data objects that are input of activities
	 */
	public int getDataObjectsInput() {
		return getNumberOfTypeElement(DataInput.class);
	}

	/**
	 * Metrica: NDOout
	 * 
	 * @return number of data objects that are output of activities
	 */
	public int getDataObjectsOutput() {
		return getNumberOfTypeElement(DataOutput.class);
	}

	/**
	 * Metrica: NID (number of inclusive gateways)
	 * 
	 * @return number of inclusive decisions
	 */
	public int getInclusiveDecisions() {
		return getNumberOfTypeElement(InclusiveGateway.class);
	}
	
	/**
	 * Metrica: NEDDB (number of exclusive gateways)
	 * 
	 * @return number of data based decisions
	 */
	public int getExclusiveDataBasedDecisions() {
		return getNumberOfTypeElement(ExclusiveGateway.class);
	}

	/**
	 * Metrica: NEDEB (number of event based gateways)
	 * 
	 * @return number of event based decisions
	 */
	public int getExclusiveEventBasedDecisions() {
		return getNumberOfTypeElement(EventBasedGateway.class);
	}

	/**
	 * Metrica: NL 
	 * 
	 * @return number of lanes
	 */
	public int getLanes() {
		Collection<Participant> participants = this.modelInstance.getModelElementsByType(Participant.class);
		int numberOfLanes = 0;
		for (Participant p : participants) {
			Collection<LaneSet> laneSets = p.getProcess().getLaneSets();
			for (LaneSet l : laneSets) {
				Collection<Lane> lanes = l.getLanes();
				if (lanes.size() > 1) {
					numberOfLanes += lanes.size();
				}
			}
		}
		return numberOfLanes;
	}

	/**
	 * Metrica: NMF
	 * 
	 * @return number of message flows
	 */
	public int getMessageFlows() {
		return getNumberOfTypeElement(MessageFlow.class);
	}

	/**
	 * Metrica: NP
	 * 
	 * @return number of pools
	 */
	public int getPools() {
		return getNumberOfTypeElement(Participant.class);
	}

	/**
	 * Metrica: NSFE 
	 * 
	 * @return number of outgoing sequence flows from events
	 */
	public int getSequenceFlowsFromEvents() {
		Collection<Event> events = this.modelInstance.getModelElementsByType(Event.class);
		int numberOfOutgoingSequenceFlows = 0;
		for (Event e : events) {
			numberOfOutgoingSequenceFlows += e.getOutgoing().size();
		}
		return numberOfOutgoingSequenceFlows;
	}

	/**
	 * Metrica:NSFG 
	 * 
	 * @return Number of outgoing sequence flows from gateways
	 */
	public int getSequenceFlowsFromGateways() {
		Collection<Gateway> gateways = this.modelInstance.getModelElementsByType(Gateway.class);
		int numberOfOutgoingSequenceFlows = 0;
		for (Gateway g : gateways) {
			numberOfOutgoingSequenceFlows += g.getOutgoing().size();
		}
		return numberOfOutgoingSequenceFlows;
	}

	/**
	 * Metrica: NSFA 
	 * 
	 * @return Number of sequence flows between activities
	 */
	public int getSequenceFlowsBetweenActivities() {
		Collection<Activity> activities = this.modelInstance.getModelElementsByType(Activity.class);
		Collection<SequenceFlow> outgoingFlows;
		int numberOfSequenceFlows = 0;
		for (Activity a : activities) {
			outgoingFlows = a.getOutgoing();
			for (SequenceFlow s : outgoingFlows) {
				if (s.getTarget() instanceof Activity) {
					numberOfSequenceFlows++;
				}
			}
		}
		return numberOfSequenceFlows;
	}
	
	/**
	 * Metric: NAC
	 * 
	 * @return number of Activation Conditions 
	 */
	public int getActivationConditions() {
		return getNumberOfTypeElement(ActivationCondition.class);
	}
	
	/**
	 * Metric: NACT
	 * 
	 * @return number of Activities
	 */
	public int getActivities() {
		return getNumberOfTypeElement(Activity.class);
	}
	
	/**
	 * Metric: NART
	 * 
	 * @return number of Artifacts
	 */
	public int getArtifacts() {
		return getNumberOfTypeElement(Artifact.class);
	}
	
	/**
	 * Metric: NASI
	 * 
	 * @return number of Assignments
	 */
	public int getAssignments() {
		return getNumberOfTypeElement(Assignment.class);
	}
	
	/**
	 * Metric: NASO 
	 * 
	 * @return number of Associations
	 */
	public int getAssociations() {
		return getNumberOfTypeElement(Association.class);
	}
	
	/**
	 * Metric: NAUD
	 * 
	 * @return number of Auditings
	 */
	public int getAuditing() {
		return getNumberOfTypeElement(Auditing.class);
	}
	
	/**
	 * Metric: NBEL
	 * 
	 * @return number of base elements
	 */
	public int getBaseElements() {
		return getNumberOfTypeElement(BaseElement.class);
	}
	
	/**
	 * Metric: NBEV
	 * 
	 * @return number of Boundary Events
	 */
	public int getBoundaryEvents() {
		return getNumberOfTypeElement(BoundaryEvent.class);
	}
	
	/**
	 * Metric: NBRT
	 * 
	 * @return number of Business Rule Tasks
	 */
	public int getBusinessRuleTasks() {
		return getNumberOfTypeElement(BusinessRuleTask.class);
	}
	
	/**
	 * Metric: NCEL
	 * 
	 * @return number of Callable Elements
	 */
	public int getCallableElements() {
		return getNumberOfTypeElement(CallableElement.class);
	}
	
	/**
	 * Metric: NCAC
	 * 
	 * @return number of Call Activities
	 */
	public int getCallActivities() {
		return getNumberOfTypeElement(CallActivity.class);
	}
	
	/**
	 * Metric: NCCO
	 * 
	 * @return number of Call Conversations
	 */
	public int getCallConversations() {
		return getNumberOfTypeElement(CallConversation.class);
	}
	
	/**
	 * Metric: NCANEV
	 * 
	 * @return number of Cancel Events
	 */
	public int getCancelEvents() {
		return getNumberOfTypeElement(CancelEventDefinition.class);
	}
	
	/**
	 * Metric: NCATEV
	 * 
	 * @return number of Catch Events
	 */
	public int getCatchEvents() {
		return getNumberOfTypeElement(CatchEvent.class);
	}
	
	/**
	 * Metric: NCVAL
	 * 
	 * @return number of Category Values
	 */
	public int getCategoryValues() {
		return getNumberOfTypeElement(CategoryValue.class);
	}
	
	/**
	 * Metric: NCOL
	 * 
	 * @return number of Collaboration
	 */
	public int getCollaborations() {
		return getNumberOfTypeElement(Collaboration.class);
	}
	
	/**
	 * Metric: NCOMEV
	 * 
	 * @return number of Compensate Events 
	 */
	public int getCompensateEvents() {
		return getNumberOfTypeElement(CompensateEventDefinition.class);
	}
	
	/**
	 * Metric: NCOCON
	 * 
	 * @return number of Completion Conditions
	 */
	public int getCompletionConditions() {
		return getNumberOfTypeElement(CompletionCondition.class);
	}
	
	/**
	 * Metric: NCBDEF
	 * 
	 * @return number of Complex Behavior
	 */
	public int getComplexBehaviorDefinitions() {
		return getNumberOfTypeElement(ComplexBehaviorDefinition.class);
	}
	
	/**
	 * Metric: NCOND
	 * 
	 * @return number of Conditions
	 */
	public int getConditions() {
		return getNumberOfTypeElement(Condition.class);
	}
	
	/**
	 * Metric: NCONDEV
	 * 
	 * @return number of Conditional Event
	 */
	public int getConditionalEvent() {
		return getNumberOfTypeElement(ConditionalEventDefinition.class);
	}
	
	/**
	 * Metric: NCONDEX
	 * 
	 * @return number of Condition Expressions
	 */
	public int getConditionExpressions() {
		return getNumberOfTypeElement(ConditionExpression.class);
	}
	
	/**
	 * Metric: NCONV
	 * 
	 * @return number of Conversations
	 */
	public int getConversations() {
		return getNumberOfTypeElement(Conversation.class);
	}
	
	/**
	 * Metric: NCONVAS
	 * 
	 * @return number of Conversation Associations
	 */
	public int getConversationAssociations() {
		return getNumberOfTypeElement(ConversationAssociation.class);
	}
	
	/**
	 * Metric: NCONVL
	 * 
	 * @return number of Conversation Links
	 */
	public int getConversationLinks() {
		return getNumberOfTypeElement(ConversationLink.class);
	}
	
	/**
	 * Metric: NCONVN
	 * 
	 * @return number of Conversation Node
	 */
	public int getConversationNodes() {
		return getNumberOfTypeElement(ConversationNode.class);
	}
	
	/**
	 * Metric: NCORK
	 * 
	 * @return number of Correlation Keys
	 */
	public int getCorrelationKeys() {
		return getNumberOfTypeElement(CorrelationKey.class);
	}
	
	/**
	 * Metric: NCORP
	 * 
	 * @return number of Correlation Properties
	 */
	public int getCorrelationProperties() {
		return getNumberOfTypeElement(CorrelationProperty.class);
	}
	
	/**
	 * Metric: NCORPB
	 * 
	 * @return number of Correlation Property Bindings
	 */
	public int getCorrelationPropertyBindings() {
		return getNumberOfTypeElement(CorrelationPropertyBinding.class);
	}
	
	/**
	 * Metric: NCORPRE
	 * 
	 * @return number of Correlation Property Retrieval Expressions
	 */
	public int getCorrelationPropertyRetrievalExpressions() {
		return getNumberOfTypeElement(CorrelationPropertyRetrievalExpression.class);
	}
	
	/**
	 * Metric: NCORS
	 * 
	 * @return number of Correlation Subscriptions
	 */
	public int getCorrelationSubscriptions() {
		return getNumberOfTypeElement(CorrelationSubscription.class);
	}
	
	/**
	 * Metric: NDA
	 * 
	 * @return number of Data Associations
	 */
	public int getDataAssociations() {
		return getNumberOfTypeElement(DataAssociation.class);
	}
	
	/**
	 * Metric: NDInA
	 * 
	 * @return number of Data Input Associations
	 */
	public int getDataInputAssociations() {
		return getNumberOfTypeElement(DataInputAssociation.class);
	}
	
	/**
	 * Metric: NDO
	 * 
	 * @return number of Data Objects
	 */
	public int getDataObjects() {
		return getNumberOfTypeElement(DataObject.class);
	}
	
	/**
	 * Metric: NDOR
	 * 
	 * @return number of Data Object References
	 */
	public int getDataObjectReferences() {
		return getNumberOfTypeElement(DataObjectReference.class);
	}
	
	/**
	 * Metric: NDOutA
	 * 
	 * @return number of Data Output Associations
	 */
	public int getDataOutputAssociations() {
		return getNumberOfTypeElement(DataOutputAssociation.class);
	}
	
	/**
	 * Metric: NDSTA
	 * 
	 * @return number of Data States
	 */
	public int getDataStates() {
		return getNumberOfTypeElement(DataState.class);
	}
	
	/**
	 * Metric: NDSTO
	 * 
	 * @return number of Data Stores
	 */
	public int getDataStores() {
		return getNumberOfTypeElement(DataStore.class);
	}
	
	/**
	 * Metric: NDEF
	 * 
	 * @return number of Definitions
	 */
	public int getDefinitions() {
		return getNumberOfTypeElement(Definitions.class);
	}
	
	/**
	 * Metric: NDOC
	 * 
	 * @return number of Documentations
	 */
	public int getDocumentations() {
		return getNumberOfTypeElement(Documentation.class);
	}
	
	/**
	 * Metric: NENDEV
	 * 
	 * @return number of End Events
	 */
	public int getEndEvents() {
		return getNumberOfTypeElement(EndEvent.class);
	}
	
	/**
	 * Metric: NENDP
	 * 
	 * @return number of End Points
	 */
	public int getEndPoints() {
		return getNumberOfTypeElement(EndPoint.class);
	}
	
	/**
	 * Metric: NERR
	 * 
	 * @return number of Errors 
	 */
	public int getErrors() {
		return getNumberOfTypeElement(Error.class);
	}
	
	/**
	 * Metric: NERREV
	 * 
	 * @return number of Error Events
	 */
	public int getErrorEvents() {
		return getNumberOfTypeElement(ErrorEventDefinition.class);
	}
	
	/**
	 * Metric: NESC
	 * 
	 * @return number of Escalations 
	 */
	public int getEscalations() {
		return getNumberOfTypeElement(Escalation.class);
	}
	
	/**
	 * Metric: NESCEV
	 * 
	 * @return number of Escalation Events
	 */
	public int getEscalationEvents() {
		return getNumberOfTypeElement(EscalationEventDefinition.class);
	}
	
	/**
	 * Metric: NEV
	 * 
	 * @return number of Events
	 */
	public int getEvents() {
		return getNumberOfTypeElement(Event.class);
	}
	
	/**
	 * Metric: NEVDEF
	 * 
	 * @return number of Event Definitions
	 */
	public int getEventDefinitions() {
		return getNumberOfTypeElement(EventDefinition.class);
	}
	
	/**
	 * Metric: NEXP
	 * 
	 * @return number of Expressions
	 */
	public int getExpressions() {
		return getNumberOfTypeElement(Expression.class);
	}
	
	/**
	 * Metric: NEXT
	 * 
	 * @return number of Extensions
	 */
	public int getExtensions() {
		return getNumberOfTypeElement(Extension.class);
	}
	
	/**
	 * Metric: NEXTEL
	 * 
	 * @return number of Extension Elements 
	 */
	public int getExtensionElements() {
		return getNumberOfTypeElement(ExtensionElements.class);
	}
	
	/**
	 * Metric: NFLEL
	 * 
	 * @return number of Flow Elements
	 */
	public int getFlowElements() {
		return getNumberOfTypeElement(FlowElement.class);
	}
	
	/**
	 * Metric: NFLNO
	 * 
	 * @return number of Flow Nodes
	 */
	public int getFlowNodes() {
		return getNumberOfTypeElement(FlowNode.class);
	}
	
	/**
	 * Metric: NFOREXP
	 * 
	 * @return number of Formal Expressions
	 */
	public int getFormalExpressions() {
		return getNumberOfTypeElement(FormalExpression.class);
	}
	
	/**
	 * Metric: NGA
	 * 
	 * @return number of Gateway
	 */
	public int getGateways() {
		return getNumberOfTypeElement(Gateway.class);
	}
	
	/**
	 * Metric: NGC
	 * 
	 * @return number of Global Conversations
	 */
	public int getGlobalConversations() {
		return getNumberOfTypeElement(GlobalConversation.class);
	}
	
	/**
	 * Metric: NHP
	 * 
	 * @return number of Human Performers
	 */
	public int getHumanPerformers() {
		return getNumberOfTypeElement(HumanPerformer.class);
	}
	
	/**
	 * Metric: NIMP
	 * 
	 * @return number of Import
	 */
	public int getImports() {
		return getNumberOfTypeElement(Import.class);
	}
	
	/**
	 * Metric: NInDI
	 * 
	 * @return number of Input Data Items
	 */
	public int getInputDataItems() {
		return getNumberOfTypeElement(InputDataItem.class);
	}
	
	/**
	 * Metric: NInS
	 * 
	 * @return number of Input Sets
	 */
	public int getInputSets() {
		return getNumberOfTypeElement(InputSet.class);
	}
	
	/**
	 * 
	 * @return number of Interaction Nodes
	 */
	public int getInteractionNodes() {
		return getNumberOfTypeElement(InteractionNode.class);
	}
	
	/**
	 * 
	 * @return number of Interfaces
	 */
	public int getInterfaces() {
		return getNumberOfTypeElement(Interface.class);
	}
	
	/**
	 * 
	 * @return number of Intermediate Catch Events
	 */
	public int getIntermediateCatchEvents() {
		return getNumberOfTypeElement(IntermediateCatchEvent.class);
	}
	
	/**
	 * 
	 * @return number of Intermediate Throw Events
	 */
	public int getIntermediateThrowEvents() {
		return getNumberOfTypeElement(IntermediateThrowEvent.class);
	}
	
	/**
	 * 
	 * @return number of Io Bindings
	 */
	public int getIoBindings() {
		return getNumberOfTypeElement(IoBinding.class);
	}
	
	/**
	 * 
	 * @return number of Io Specifications
	 */
	public int getIoSpecifications() {
		return getNumberOfTypeElement(IoSpecification.class);
	}
	
	/**
	 * 
	 * @return number of Item Aware Elements
	 */
	public int getItemAwareElements() {
		return getNumberOfTypeElement(ItemAwareElement.class);
	}
	
	/**
	 * 
	 * @return number of Item Definitions
	 */
	public int getItemDefinitions() {
		return getNumberOfTypeElement(ItemDefinition.class);
	}
	
	/**
	 * 
	 * @return number of Link Event Definitions
	 */
	public int getLinkEvents() {
		return getNumberOfTypeElement(LinkEventDefinition.class);
	}
	
	/**
	 * 
	 * @return number of Loop Cardinalities
	 */
	public int getLoopCardinalities() {
		return getNumberOfTypeElement(LoopCardinality.class);
	}
	
	/**
	 * 
	 * @return number of Task con Loop Characteristics
	 */
	public int getLoopCharacteristics() {
		return getNumberOfTypeElement(LoopCharacteristics.class);
	}
	
	/**
	 * 
	 * @return number of Manual Tasks
	 */
	public int getManualTasks() {
		return getNumberOfTypeElement(ManualTask.class);
	}
	
	/**
	 * 
	 * @return number of messaggi
	 */
	public int getMessages() {
		return getNumberOfTypeElement(Message.class);
	}
	
	/**
	 * 
	 * @return number of Message Event 
	 */
	public int getMessageEvents() {
		return getNumberOfTypeElement(MessageEventDefinition.class);
	}
	
	/**
	 * 
	 * @return number of Message Flow Associations
	 */
	public int getMessageFlowAssociations() {
		return getNumberOfTypeElement(MessageFlowAssociation.class);
	}
	
	/**
	 * 
	 * @return number of Monitorings 
	 */
	public int getMonitorings() {
		return getNumberOfTypeElement(Monitoring.class);
	}
	
	/**
	 * 
	 * @return number of tasks con Multi Instance Loop Characteristics
	 */
	public int getMultiInstanceLoopCharacteristics() {
		return getNumberOfTypeElement(MultiInstanceLoopCharacteristics.class);
	}
	
	/**
	 * 
	 * @return number of Operazioni
	 */
	public int getOperations() {
		return getNumberOfTypeElement(Operation.class);
	}
	
	/**
	 * 
	 * @return number of Output Data Items
	 */
	public int getOutputDataItems() {
		return getNumberOfTypeElement(OutputDataItem.class);
	}
	
	/**
	 * 
	 * @return number of Output Sets
	 */
	public int getOutputSets() {
		return getNumberOfTypeElement(OutputSet.class);
	}
	
	/**
	 * 
	 * @return number of gateways paralleli
	 */
	public int getParallelGateways() {
		return getNumberOfTypeElement(ParallelGateway.class);
	}
	
	/**
	 * 
	 * @return number of Participant Associations
	 */
	public int getParticipantAssociations() {
		return getNumberOfTypeElement(ParticipantAssociation.class);
	}
	
	/**
	 * 
	 * @return number of Participant Multiplicities
	 */
	public int getParticipantMultiplicities() {
		return getNumberOfTypeElement(ParticipantMultiplicity.class);
	}
	
	/**
	 * 
	 * @return number of Performers 
	 */
	public int getPerformers() {
		return getNumberOfTypeElement(Performer.class);
	}
	
	/**
	 * 
	 * @return number of Potential Owners
	 */
	public int getPotentialOwners() {
		return getNumberOfTypeElement(PotentialOwner.class);
	}
	
	/**
	 * 
	 * @return number of Processi 
	 */
	public int getProcesses() {
		return getNumberOfTypeElement(Process.class);
	}
	
	/**
	 * 
	 * @return number of Proprietà
	 */
	public int getProperties() {
		return getNumberOfTypeElement(Property.class);
	}
	
	/**
	 * 
	 * @return number of Receive Tasks
	 */
	public int getReceiveTasks() {
		return getNumberOfTypeElement(ReceiveTask.class);
	}
	
	/**
	 * 
	 * @return number of Relazioni
	 */
	public int getRelationships() {
		return getNumberOfTypeElement(Relationship.class);
	}
	
	/**
	 * 
	 * @return number of Renderings
	 */
	public int getRenderings() {
		return getNumberOfTypeElement(Rendering.class);
	}
	
	/**
	 * 
	 * @return number of Resources
	 */
	public int getResources() {
		return getNumberOfTypeElement(Resource.class);
	}
	
	/**
	 * 
	 * @return number of Resource Assignment Expressions
	 */
	public int getResourceAssignmentExpressions() {
		return getNumberOfTypeElement(ResourceAssignmentExpression.class);
	}
	
	/**
	 * 
	 * @return number of Resource Parameters
	 */
	public int getResourceParameters() {
		return getNumberOfTypeElement(ResourceParameter.class);
	}
	
	/**
	 * 
	 * @return number of Resource Parameter Bindings
	 */
	public int getResourceParameterBindings() {
		return getNumberOfTypeElement(ResourceParameterBinding.class);
	}
	
	/**
	 * 
	 * @return number of Resource Roles
	 */
	public int getResourceRoles() {
		return getNumberOfTypeElement(ResourceRole.class);
	}
	
	/**
	 * 
	 * @return number of Root Elements
	 */
	public int getRootElements() {
		return getNumberOfTypeElement(RootElement.class);
	}
	
	/**
	 * 
	 * @return number of Scripts
	 */
	public int getScripts() {
		return getNumberOfTypeElement(Script.class);
	}
	
	/**
	 * 
	 * @return number of Script Tasks
	 */
	public int getScriptTasks() {
		return getNumberOfTypeElement(ScriptTask.class);
	}
	
	/**
	 * 
	 * @return number of Send Tasks 
	 */
	public int getSendTasks() {
		return getNumberOfTypeElement(SendTask.class);
	}
	
	/**
	 * 
	 * @return number of Sequence Flows
	 */
	public int getSequenceFlows() {
		return getNumberOfTypeElement(SequenceFlow.class);
	}
	
	/**
	 * 
	 * @return number of Service Tasks
	 */
	public int getServiceTasks() {
		return getNumberOfTypeElement(ServiceTask.class);
	}
	
	/**
	 * 
	 * @return number of Signals
	 */
	public int getSignals() {
		return getNumberOfTypeElement(Signal.class);
	}
	
	/**
	 * 
	 * @return number of Signal Events
	 */
	public int getSignalEvent() {
		return getNumberOfTypeElement(SignalEventDefinition.class);
	}
	
	/**
	 * 
	 * @return number of Start Events
	 */
	public int getStartEvents() {
		return getNumberOfTypeElement(StartEvent.class);
	}
	
	/**
	 * 
	 * @return number of Sotto-Processi
	 */
	public int getSubprocesses() {
		return getNumberOfTypeElement(SubProcess.class);
	}
	
	/**
	 * 
	 * @return number of Sub Conversations
	 */
	public int getSubConversations() {
		return getNumberOfTypeElement(SubConversation.class);
	}
	
	/**
	 * 
	 * @return number of Terminate Events
	 */
	public int getTerminateEvents() {
		return getNumberOfTypeElement(TerminateEventDefinition.class);
	}
	
	/**
	 * 
	 * @return number of Texts 
	 */
	public int getTexts() {
		return getNumberOfTypeElement(Text.class);
	}
	
	/**
	 * 
	 * @return number of Text Annotations
	 */
	public int getTextAnnotations() {
		return getNumberOfTypeElement(TextAnnotation.class);
	}
	
	/**
	 * 
	 * @return number of Throw Events
	 */
	public int getThrowEvents() {
		return getNumberOfTypeElement(ThrowEvent.class);
	}
	
	/**
	 * 
	 * @return number of Time Cycles
	 */
	public int getTimeCycles() {
		return getNumberOfTypeElement(TimeCycle.class);
	}
	
	/**
	 * 
	 * @return number of Time Dates
	 */
	public int getTimeDates() {
		return getNumberOfTypeElement(TimeDate.class);
	}
	
	/**
	 * 
	 * @return number of Time Durations
	 */
	public int getTimeDurations() {
		return getNumberOfTypeElement(TimeDuration.class);
	}
	
	/**
	 * 
	 * @return number of Timer Events
	 */
	public int getTimerEventDefinitions() {
		return getNumberOfTypeElement(TimerEventDefinition.class);
	}
	
	/**
	 * 
	 * @return number of Transactions
	 */
	public int getTransactions() {
		return getNumberOfTypeElement(Transaction.class);
	}
	
	/**
	 * 
	 * @return number of User Tasks
	 */
	public int getUserTasks() {
		return getNumberOfTypeElement(UserTask.class);
	}
	
	/**
	 * 
	 * @return number of complex gateways che dividono flusso
	 */
	public int getFlowDividingComplexGateways() {
		return getFlowDividingElementsOfType(ComplexGateway.class);
	}
	
	/**
	 * 
	 * @return number of event based gateways che dividono flusso
	 */
	public int getFlowDividingEventBasedGateways() {
		return getFlowDividingElementsOfType(EventBasedGateway.class);
	}
	
	/**
	 * 
	 * @return number of exclusive gateways che dividono flusso
	 */
	public int getFlowDividingExclusiveGateways() {
		return getFlowDividingElementsOfType(ExclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return number of inclusive gateways che dividono flusso
	 */
	public int getFlowDividingInclusiveGateways() {
		return getFlowDividingElementsOfType(InclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return number of parallel gateways che dividono flusso
	 */
	public int getFlowDividingParallelGateways() {
		return getFlowDividingElementsOfType(ParallelGateway.class);
	}
	
	/**
	 * 
	 * @return number of tasks che dividono flusso
	 */
	public int getFlowDividingTasks() {
		return getFlowDividingElementsOfType(Task.class);
	}
	
	/**
	 * 
	 * @return number of complex gateways che uniscono flusso
	 */
	public int getFlowJoiningComplexGateways() {
		return getFlowJoiningElementsOfType(ComplexGateway.class);
	}
	
	/**
	 * 
	 * @return number of event based gateways che uniscono flusso
	 */
	public int getFlowJoiningEventBasedGateways() {
		return getFlowJoiningElementsOfType(EventBasedGateway.class);
	}
	
	/**
	 * 
	 * @return number of exclusive gateways che uniscono flusso
	 */
	public int getFlowJoiningExclusiveGateways() {
		return getFlowJoiningElementsOfType(ExclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return number of inclusive gateways che uniscono flusso
	 */
	public int getFlowJoiningInclusiveGateways() {
		return getFlowJoiningElementsOfType(InclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return number of parallel gateways che uniscono flusso
	 */
	public int getFlowJoiningParallelGateways() {
		return getFlowJoiningElementsOfType(ParallelGateway.class);
	}
	
	/**
	 * 
	 * @return number of tasks che uniscono flusso
	 */
	public int getFlowJoiningTasks() {
		return getFlowJoiningElementsOfType(Task.class);
	}
	
	/**
	 * 
	 * @return number of complex gateways che uniscono e dividono flusso
	 */
	public int getFlowJoiningAndDividingComplexGateways() {
		return getFlowJoiningAndDividingElementsOfType(ComplexGateway.class);
	}
	
	/**
	 * 
	 * @return number of event based gateways che uniscono e dividono flusso
	 */
	public int getFlowJoiningAndDividingEventBasedGateways() {
		return getFlowJoiningAndDividingElementsOfType(EventBasedGateway.class);
	}
	
	/**
	 * 
	 * @return number of exclusive gateways che uniscono e dividono flusso
	 */
	public int getFlowJoiningAndDividingExclusiveGateways() {
		return getFlowJoiningAndDividingElementsOfType(ExclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return number of inclusive gateways che uniscono e dividono flusso
	 */
	public int getFlowJoiningAndDividingInclusiveGateways() {
		return getFlowJoiningAndDividingElementsOfType(InclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return number of parallel gateways che uniscono e dividono flusso
	 */
	public int getFlowJoiningAndDividingParallelGateways() {
		return getFlowJoiningAndDividingElementsOfType(ParallelGateway.class);
	}
	
	/**
	 * 
	 * @return number of tasks che uniscono e dividono flusso
	 */
	public int getFlowJoiningAndDividingTasks() {
		return getFlowJoiningAndDividingElementsOfType(Task.class);
	}
	
	/**
	 * 
	 * @return number of gateways che dividono flusso
	 */
	public int getFlowDividingGateways() {
		return getFlowDividingElementsOfType(Gateway.class);
	}
	
	/**
	 *  
	 * @return number of gateways che uniscono flusso
	 */
	public int getFlowJoiningGateways() {
		return getFlowJoiningElementsOfType(Gateway.class);
	}
	
	/**
	 * 
	 * @return number of gateways che uniscono e dividono flusso
	 */
	public int getFlowJoiningAndDividingGateways() {
		return getFlowJoiningAndDividingElementsOfType(Gateway.class);
	}
	
	/**
	 * Metodo che cerca tutti gli elementi della classe type presenti nel modello e conta quanti dividono flusso
	 * @param type: classe del tipo of elemento che si vuole analizzare
	 * @return number of elementi della classe type che dividono flusso
	 */
	private int getFlowDividingElementsOfType(Class type) {
		int toReturn = 0;
		Collection<ModelElementInstance> modelElementInstances = getCollectionOfElementType(type);
		for (ModelElementInstance instance : modelElementInstances) {
			if (((FlowNode) instance).getOutgoing().size() > 1) {
				toReturn += 1;
			}
		}
		return toReturn;
	}
	
	/**
	 * Metodo che cerca tutti gli elementi della classe type presenti nel modello e conta quanti uniscono flusso
	 * @param type: classe del tipo of elemento che si vuole analizzare
	 * @return number of elementi della classe type che uniscono flusso
	 */
	private int getFlowJoiningElementsOfType(Class type) {
		int toReturn = 0;
		Collection<ModelElementInstance> modelElementInstances = getCollectionOfElementType(type);
		for (ModelElementInstance instance : modelElementInstances) {
			if (((FlowNode) instance).getIncoming().size() > 1) {
				toReturn += 1;
			}
		}
		return toReturn;
	}
	
	/**
	 * Metodo che cerca tutti gli elementi della classe type presenti nel modello e conta quanti uniscono e dividono flusso
	 * @param type: classe del tipo of elemento che si vuole analizzare
	 * @return number of elementi della classe type che uniscono e dividono flusso
	 */
	private int getFlowJoiningAndDividingElementsOfType(Class type) {
		int toReturn = 0;
		Collection<ModelElementInstance> modelElementInstances = getCollectionOfElementType(type);
		for (ModelElementInstance instance : modelElementInstances) {
			if (((FlowNode) instance).getIncoming().size() > 1 && ((FlowNode) instance).getOutgoing().size() > 1) {
				toReturn += 1;
			}
		}
		return toReturn;
	}
	
	/**
	 * Metodo che cerca nel modello tutti gli elementi del tipo "type" per
	 * ottenerne number complessivo
	 * 
	 * @param type:
	 *            la classe del tipo of elementi of cui si vuole conoscere il
	 *            number
	 * @return number of elementi del tipo "type"
	 */
	public int getNumberOfTypeElement(Class type) {
		return getCollectionOfElementType(type).size();
	}
	
	public Collection<ModelElementInstance> getCollectionOfElementType(Class type) {
		ModelElementType modelElementType = modelInstance.getModel().getType(type);
		return this.modelInstance.getModelElementsByType(modelElementType);
	}
}
