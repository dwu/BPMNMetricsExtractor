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
 * Classe in cui andare a inserire i metodi per l'estrazione delle
 * statistiche/metadati
 * 
 * @author PROSLabTeam
 *
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
	 * Metodo principale per runnare tutti i metodi che ottengono le metriche
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
		this.json.addBasicMetric("NMILCH", this.getMultiInstanceLoopCharacteristicss());
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
		this.json.addBasicMetric("NFDCG", this.getFluxDividingComplexGateways());
		this.json.addBasicMetric("NFDEBG", this.getFluxDividingEventBasedGateways());
		this.json.addBasicMetric("NFDEXG", this.getFluxDividingExclusiveGateways());
		this.json.addBasicMetric("NFDIG", this.getFluxDividingInclusiveGateways());
		this.json.addBasicMetric("NFDPG", this.getFluxDividingParallelGateways());
		this.json.addBasicMetric("NFDT", this.getFluxDividingTasks());
		this.json.addBasicMetric("NFJCG", this.getFluxJoiningComplexGateways());
		this.json.addBasicMetric("NFJEBG", this.getFluxJoiningEventBasedGateways());
		this.json.addBasicMetric("NFJEXG", this.getFluxJoiningExclusiveGateways());
		this.json.addBasicMetric("NFJIG", this.getFluxJoiningInclusiveGateways());
		this.json.addBasicMetric("NFJPG", this.getFluxJoiningParallelGateways());
		this.json.addBasicMetric("NFJT", this.getFluxJoiningTasks());
		this.json.addBasicMetric("NFJCG", this.getFluxJoiningAndDividingComplexGateways());
		this.json.addBasicMetric("NFJDEBG", this.getFluxJoiningAndDividingEventBasedGateways());
		this.json.addBasicMetric("NFJDEXG", this.getFluxJoiningAndDividingExclusiveGateways());
		this.json.addBasicMetric("NFJDIG", this.getFluxJoiningAndDividingInclusiveGateways());
		this.json.addBasicMetric("NFJDPG", this.getFluxJoiningAndDividingParallelGateways());
		this.json.addBasicMetric("NFJDT", this.getFluxJoiningAndDividingTasks());
	}

	//TODO aggiungere exclusive gateways
	
	/**
	 * Metrica: NT
	 * 
	 * @return numero dei tasks
	 */
	public int getTasks() {
		return getNumberOfTypeElement(Task.class);
	}

	/**
	 * Metrica: NCD (numero di complex gateways)
	 * 
	 * @return numero delle decisioni complesse
	 */
	public int getComplexDecisions() {
		return getNumberOfTypeElement(ComplexGateway.class);
	}

	/**
	 * Metrica: NDOin
	 * 
	 * @return numero dei data objects che sono in input delle attività
	 */
	public int getDataObjectsInput() {
		return getNumberOfTypeElement(DataInput.class);
	}

	/**
	 * Metrica: NDOout
	 * 
	 * @return numero dei data objects che sono in output delle attività
	 */
	public int getDataObjectsOutput() {
		return getNumberOfTypeElement(DataOutput.class);
	}

	/**
	 * Metrica: NID (numero di inclusive gateways)
	 * 
	 * @return numero delle decisioni inclusive
	 */
	public int getInclusiveDecisions() {
		return getNumberOfTypeElement(InclusiveGateway.class);
	}

	/**
	 * Metrica: NEDDB (numero degli exclusive gateways)
	 * 
	 * @return numero delle decisioni esclusive basate sui dati
	 */
	public int getExclusiveDataBasedDecisions() {
		return getNumberOfTypeElement(ExclusiveGateway.class);
	}

	/**
	 * Metrica: NEDEB (numero degli event based gateways)
	 * 
	 * @return numero delle decisioni esclusive basate sugli eventi
	 */
	public int getExclusiveEventBasedDecisions() {
		return getNumberOfTypeElement(EventBasedGateway.class);
	}

	/**
	 * Metrica: NL (numero di lanes)
	 * 
	 * @return numero di lanes
	 */
	public int getLanes() {
		Collection<Participant> participants = this.modelInstance.getModelElementsByType(Participant.class);
//		Collection<Process> processes = this.modelInstance.getModelElementsByType(Process.class);
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
	 * Metrica: NMF (numero di message flows)
	 * 
	 * @return numero di flussi di messaggi
	 */
	public int getMessageFlows() {
		return getNumberOfTypeElement(MessageFlow.class);
	}

	/**
	 * Metrica: NP (numero di pools)
	 * 
	 * @return numero di pools
	 */
	public int getPools() {
		return getNumberOfTypeElement(Participant.class);
	}

	/**
	 * Metrica: NSFE (numero di archi uscenti dagli eventi)
	 * 
	 * @return numero di archi uscenti dagli eventi
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
	 * Metrica:NSFG (numero di archi uscenti dai gateways)
	 * 
	 * @return numero di archi uscenti dai gateways
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
	 * Metrica: NSFA (Numero di archi colleganti attività)
	 * 
	 * @return numero di archi colleganti due attività
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
	 * 
	 * @return il numero delle Activation Conditions 
	 */
	public int getActivationConditions() {
		return getNumberOfTypeElement(ActivationCondition.class);
	}
	
	/**
	 * 
	 * @return il numero delle attività
	 */
	public int getActivities() {
		return getNumberOfTypeElement(Activity.class);
	}
	
	/**
	 * 
	 * @return il numero degli Artifact
	 */
	public int getArtifacts() {
		return getNumberOfTypeElement(Artifact.class);
	}
	
	/**
	 * 
	 * @return il numero degli Assignment 
	 */
	public int getAssignments() {
		return getNumberOfTypeElement(Assignment.class);
	}
	
	/**
	 * 
	 * @return il numero delle associazioni 
	 */
	public int getAssociations() {
		return getNumberOfTypeElement(Association.class);
	}
	
	/**
	 * 
	 * @return il numero degli Auditing
	 */
	public int getAuditing() {
		return getNumberOfTypeElement(Auditing.class);
	}
	
	/**
	 * 
	 * @return il numero degli elementi di base
	 */
	public int getBaseElements() {
		return getNumberOfTypeElement(BaseElement.class);
	}
	
	/**
	 * 
	 * @return il numero dei Boundary Events
	 */
	public int getBoundaryEvents() {
		return getNumberOfTypeElement(BoundaryEvent.class);
	}
	
	/**
	 * 
	 * @return il numero dei Business Rule Task
	 */
	public int getBusinessRuleTasks() {
		return getNumberOfTypeElement(BusinessRuleTask.class);
	}
	
	/**
	 * 
	 * @return il numero dei Callable Elements
	 */
	public int getCallableElements() {
		return getNumberOfTypeElement(CallableElement.class);
	}
	
	/**
	 * 
	 * @return il numero delle Call Activities
	 */
	public int getCallActivities() {
		return getNumberOfTypeElement(CallActivity.class);
	}
	
	/**
	 * 
	 * @return il numero delle Call Conversations
	 */
	public int getCallConversations() {
		return getNumberOfTypeElement(CallConversation.class);
	}
	
	/**
	 * 
	 * @return il numero dei Cancel Events
	 */
	public int getCancelEvents() {
		return getNumberOfTypeElement(CancelEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero dei Catch Events
	 */
	public int getCatchEvents() {
		return getNumberOfTypeElement(CatchEvent.class);
	}
	
	/**
	 * 
	 * @return il numero dei Category Values
	 */
	public int getCategoryValues() {
		return getNumberOfTypeElement(CategoryValue.class);
	}
	
	/**
	 * 
	 * @return il numero delle Collaboration
	 */
	public int getCollaborations() {
		return getNumberOfTypeElement(Collaboration.class);
	}
	
	/**
	 * 
	 * @return il numero dei Compensate Events 
	 */
	public int getCompensateEvents() {
		return getNumberOfTypeElement(CompensateEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero delle Completion Conditions
	 */
	public int getCompletionConditions() {
		return getNumberOfTypeElement(CompletionCondition.class);
	}
	
	/**
	 * 
	 * @return il numero dei Complex Behavior
	 */
	public int getComplexBehaviorDefinitions() {
		return getNumberOfTypeElement(ComplexBehaviorDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero delle Conditions
	 */
	public int getConditions() {
		return getNumberOfTypeElement(Condition.class);
	}
	
	/**
	 * 
	 * @return il numero dei Conditional Event
	 */
	public int getConditionalEvent() {
		return getNumberOfTypeElement(ConditionalEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero delle Condition Expressions
	 */
	public int getConditionExpressions() {
		return getNumberOfTypeElement(ConditionExpression.class);
	}
	
	/**
	 * 
	 * @return il numero delle Conversations
	 */
	public int getConversations() {
		return getNumberOfTypeElement(Conversation.class);
	}
	
	/**
	 * 
	 * @return il numero delle Conversation Associations
	 */
	public int getConversationAssociations() {
		return getNumberOfTypeElement(ConversationAssociation.class);
	}
	
	/**
	 * 
	 * @return il numero dei Conversation Links
	 */
	public int getConversationLinks() {
		return getNumberOfTypeElement(ConversationLink.class);
	}
	
	/**
	 * 
	 * @return il numero dei ConversationNode
	 */
	public int getConversationNodes() {
		return getNumberOfTypeElement(ConversationNode.class);
	}
	
	/**
	 * 
	 * @return il numero delle Correlation Keys
	 */
	public int getCorrelationKeys() {
		return getNumberOfTypeElement(CorrelationKey.class);
	}
	
	/**
	 * 
	 * @return il numero delle Correlation Properties
	 */
	public int getCorrelationProperties() {
		return getNumberOfTypeElement(CorrelationProperty.class);
	}
	
	/**
	 * 
	 * @return il numero delle Correlation Property Bindings
	 */
	public int getCorrelationPropertyBindings() {
		return getNumberOfTypeElement(CorrelationPropertyBinding.class);
	}
	
	/**
	 * 
	 * @return il numero delle Correlation Property Retrieval Expressions
	 */
	public int getCorrelationPropertyRetrievalExpressions() {
		return getNumberOfTypeElement(CorrelationPropertyRetrievalExpression.class);
	}
	
	/**
	 * 
	 * @return il numero delle Correlation Subscriptions
	 */
	public int getCorrelationSubscriptions() {
		return getNumberOfTypeElement(CorrelationSubscription.class);
	}
	
	/**
	 * 
	 * @return il numero delle Data Associations
	 */
	public int getDataAssociations() {
		return getNumberOfTypeElement(DataAssociation.class);
	}
	
	/**
	 * 
	 * @return il numero delle Data Input Associations
	 */
	public int getDataInputAssociations() {
		return getNumberOfTypeElement(DataInputAssociation.class);
	}
	
	/**
	 * 
	 * @return il numero dei Data Objects
	 */
	public int getDataObjects() {
		return getNumberOfTypeElement(DataObject.class);
	}
	
	/**
	 * 
	 * @return il numero delle Data Object References
	 */
	public int getDataObjectReferences() {
		return getNumberOfTypeElement(DataObjectReference.class);
	}
	
	/**
	 * 
	 * @return il numero delle Data Output Associations
	 */
	public int getDataOutputAssociations() {
		return getNumberOfTypeElement(DataOutputAssociation.class);
	}
	
	/**
	 * 
	 * @return il numero dei Data States
	 */
	public int getDataStates() {
		return getNumberOfTypeElement(DataState.class);
	}
	
	/**
	 * 
	 * @return il numero dei Data Stores
	 */
	public int getDataStores() {
		return getNumberOfTypeElement(DataStore.class);
	}
	
	/**
	 * 
	 * @return il numero delle Definitions
	 */
	public int getDefinitions() {
		return getNumberOfTypeElement(Definitions.class);
	}
	
	/**
	 * 
	 * @return il numero di Documentations
	 */
	public int getDocumentations() {
		return getNumberOfTypeElement(Documentation.class);
	}
	
	/**
	 * 
	 * @return il numero degli End Events
	 */
	public int getEndEvents() {
		return getNumberOfTypeElement(EndEvent.class);
	}
	
	/**
	 * 
	 * @return il numero degli End Points
	 */
	public int getEndPoints() {
		return getNumberOfTypeElement(EndPoint.class);
	}
	
	/**
	 * 
	 * @return il numero degli Errors 
	 */
	public int getErrors() {
		return getNumberOfTypeElement(Error.class);
	}
	
	/**
	 * 
	 * @return il numero degli Error Events
	 */
	public int getErrorEvents() {
		return getNumberOfTypeElement(ErrorEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero delle Escalations 
	 */
	public int getEscalations() {
		return getNumberOfTypeElement(Escalation.class);
	}
	
	/**
	 * 
	 * @return il numero degli Escalation Events
	 */
	public int getEscalationEvents() {
		return getNumberOfTypeElement(EscalationEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero degli Events
	 */
	public int getEvents() {
		return getNumberOfTypeElement(Event.class);
	}
	
	/**
	 * 
	 * @return il numero delle Event Definitions
	 */
	public int getEventDefinitions() {
		return getNumberOfTypeElement(EventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero delle Expressions
	 */
	public int getExpressions() {
		return getNumberOfTypeElement(Expression.class);
	}
	
	/**
	 * 
	 * @return il numero delle Extensions
	 */
	public int getExtensions() {
		return getNumberOfTypeElement(Extension.class);
	}
	
	/**
	 * 
	 * @return il numero degli Extension Elements 
	 */
	public int getExtensionElements() {
		return getNumberOfTypeElement(ExtensionElements.class);
	}
	
	/**
	 * 
	 * @return il numero dei Flow Elements
	 */
	public int getFlowElements() {
		return getNumberOfTypeElement(FlowElement.class);
	}
	
	/**
	 * 
	 * @return il numero dei Flow Nodes
	 */
	public int getFlowNodes() {
		return getNumberOfTypeElement(FlowNode.class);
	}
	
	/**
	 * 
	 * @return il numero delle Formal Expressions
	 */
	public int getFormalExpressions() {
		return getNumberOfTypeElement(FormalExpression.class);
	}
	
	/**
	 * 
	 * @return il numero dei Gateway
	 */
	public int getGateways() {
		return getNumberOfTypeElement(Gateway.class);
	}
	
	/**
	 * 
	 * @return il numero delle Global Conversations
	 */
	public int getGlobalConversations() {
		return getNumberOfTypeElement(GlobalConversation.class);
	}
	
	/**
	 * 
	 * @return il numero degli Human Performers
	 */
	public int getHumanPerformers() {
		return getNumberOfTypeElement(HumanPerformer.class);
	}
	
	/**
	 * 
	 * @return il numero degli Import
	 */
	public int getImports() {
		return getNumberOfTypeElement(Import.class);
	}
	
	/**
	 * 
	 * @return il numero degli Input Data Items
	 */
	public int getInputDataItems() {
		return getNumberOfTypeElement(InputDataItem.class);
	}
	
	/**
	 * 
	 * @return il numero degli Input Sets
	 */
	public int getInputSets() {
		return getNumberOfTypeElement(InputSet.class);
	}
	
	/**
	 * 
	 * @return il numero degli Interaction Nodes
	 */
	public int getInteractionNodes() {
		return getNumberOfTypeElement(InteractionNode.class);
	}
	
	/**
	 * 
	 * @return il numero delle Interfaces
	 */
	public int getInterfaces() {
		return getNumberOfTypeElement(Interface.class);
	}
	
	/**
	 * 
	 * @return il numero degli Intermediate Catch Events
	 */
	public int getIntermediateCatchEvents() {
		return getNumberOfTypeElement(IntermediateCatchEvent.class);
	}
	
	/**
	 * 
	 * @return il numero degli Intermediate Throw Events
	 */
	public int getIntermediateThrowEvents() {
		return getNumberOfTypeElement(IntermediateThrowEvent.class);
	}
	
	/**
	 * 
	 * @return il numero degli Io Bindings
	 */
	public int getIoBindings() {
		return getNumberOfTypeElement(IoBinding.class);
	}
	
	/**
	 * 
	 * @return il numero delle Io Specifications
	 */
	public int getIoSpecifications() {
		return getNumberOfTypeElement(IoSpecification.class);
	}
	
	/**
	 * 
	 * @return il numero degli Item Aware Elements
	 */
	public int getItemAwareElements() {
		return getNumberOfTypeElement(ItemAwareElement.class);
	}
	
	/**
	 * 
	 * @return il numero delle Item Definitions
	 */
	public int getItemDefinitions() {
		return getNumberOfTypeElement(ItemDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero delle Link Event Definitions
	 */
	public int getLinkEvents() {
		return getNumberOfTypeElement(LinkEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero delle Loop Cardinalities
	 */
	public int getLoopCardinalities() {
		return getNumberOfTypeElement(LoopCardinality.class);
	}
	
	/**
	 * 
	 * @return il numero dei Task con Loop Characteristics
	 */
	public int getLoopCharacteristics() {
		return getNumberOfTypeElement(LoopCharacteristics.class);
	}
	
	/**
	 * 
	 * @return il numero dei Manual Tasks
	 */
	public int getManualTasks() {
		return getNumberOfTypeElement(ManualTask.class);
	}
	
	/**
	 * 
	 * @return il numero dei messaggi
	 */
	public int getMessages() {
		return getNumberOfTypeElement(Message.class);
	}
	
	/**
	 * 
	 * @return il numero dei Message Event 
	 */
	public int getMessageEvents() {
		return getNumberOfTypeElement(MessageEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero delle Message Flow Associations
	 */
	public int getMessageFlowAssociations() {
		return getNumberOfTypeElement(MessageFlowAssociation.class);
	}
	
	/**
	 * 
	 * @return il numero dei Monitorings 
	 */
	public int getMonitorings() {
		return getNumberOfTypeElement(Monitoring.class);
	}
	
	/**
	 * 
	 * @return il numero dei tasks con Multi Instance Loop Characteristics
	 */
	public int getMultiInstanceLoopCharacteristicss() {
		return getNumberOfTypeElement(MultiInstanceLoopCharacteristics.class);
	}
	
	/**
	 * 
	 * @return il numero delle Operazioni
	 */
	public int getOperations() {
		return getNumberOfTypeElement(Operation.class);
	}
	
	/**
	 * 
	 * @return il numero dei Output Data Items
	 */
	public int getOutputDataItems() {
		return getNumberOfTypeElement(OutputDataItem.class);
	}
	
	/**
	 * 
	 * @return il numero degli Output Sets
	 */
	public int getOutputSets() {
		return getNumberOfTypeElement(OutputSet.class);
	}
	
	/**
	 * 
	 * @return il numero di gateways paralleli
	 */
	public int getParallelGateways() {
		return getNumberOfTypeElement(ParallelGateway.class);
	}
	
	/**
	 * 
	 * @return il numero delle Participant Associations
	 */
	public int getParticipantAssociations() {
		return getNumberOfTypeElement(ParticipantAssociation.class);
	}
	
	/**
	 * 
	 * @return il numero delle Participant Multiplicities
	 */
	public int getParticipantMultiplicities() {
		return getNumberOfTypeElement(ParticipantMultiplicity.class);
	}
	
	/**
	 * 
	 * @return il numero dei Performers 
	 */
	public int getPerformers() {
		return getNumberOfTypeElement(Performer.class);
	}
	
	/**
	 * 
	 * @return il numero dei Potential Owners
	 */
	public int getPotentialOwners() {
		return getNumberOfTypeElement(PotentialOwner.class);
	}
	
	/**
	 * 
	 * @return il numero dei Processi 
	 */
	public int getProcesses() {
		return getNumberOfTypeElement(Process.class);
	}
	
	/**
	 * 
	 * @return il numero delle Proprietà
	 */
	public int getProperties() {
		return getNumberOfTypeElement(Property.class);
	}
	
	/**
	 * 
	 * @return il numero dei Receive Tasks
	 */
	public int getReceiveTasks() {
		return getNumberOfTypeElement(ReceiveTask.class);
	}
	
	/**
	 * 
	 * @return il numero delle Relazioni
	 */
	public int getRelationships() {
		return getNumberOfTypeElement(Relationship.class);
	}
	
	/**
	 * 
	 * @return il numero dei Renderings
	 */
	public int getRenderings() {
		return getNumberOfTypeElement(Rendering.class);
	}
	
	/**
	 * 
	 * @return il numero delle Resources
	 */
	public int getResources() {
		return getNumberOfTypeElement(Resource.class);
	}
	
	/**
	 * 
	 * @return il numero delle Resource Assignment Expressions
	 */
	public int getResourceAssignmentExpressions() {
		return getNumberOfTypeElement(ResourceAssignmentExpression.class);
	}
	
	/**
	 * 
	 * @return il numero dei Resource Parameters
	 */
	public int getResourceParameters() {
		return getNumberOfTypeElement(ResourceParameter.class);
	}
	
	/**
	 * 
	 * @return il numero dei Resource Parameter Bindings
	 */
	public int getResourceParameterBindings() {
		return getNumberOfTypeElement(ResourceParameterBinding.class);
	}
	
	/**
	 * 
	 * @return il numero dei Resource Roles
	 */
	public int getResourceRoles() {
		return getNumberOfTypeElement(ResourceRole.class);
	}
	
	/**
	 * 
	 * @return il numero dei Root Elements
	 */
	public int getRootElements() {
		return getNumberOfTypeElement(RootElement.class);
	}
	
	/**
	 * 
	 * @return il numero degli Scripts
	 */
	public int getScripts() {
		return getNumberOfTypeElement(Script.class);
	}
	
	/**
	 * 
	 * @return il numero degli Script Tasks
	 */
	public int getScriptTasks() {
		return getNumberOfTypeElement(ScriptTask.class);
	}
	
	/**
	 * 
	 * @return il numero dei Send Tasks 
	 */
	public int getSendTasks() {
		return getNumberOfTypeElement(SendTask.class);
	}
	
	/**
	 * 
	 * @return il numero dei Sequence Flows
	 */
	public int getSequenceFlows() {
		return getNumberOfTypeElement(SequenceFlow.class);
	}
	
	/**
	 * 
	 * @return il numero dei Service Tasks
	 */
	public int getServiceTasks() {
		return getNumberOfTypeElement(ServiceTask.class);
	}
	
	/**
	 * 
	 * @return il numero dei Signals
	 */
	public int getSignals() {
		return getNumberOfTypeElement(Signal.class);
	}
	
	/**
	 * 
	 * @return il numero dei Signal Events
	 */
	public int getSignalEvent() {
		return getNumberOfTypeElement(SignalEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero degli Start Events
	 */
	public int getStartEvents() {
		return getNumberOfTypeElement(StartEvent.class);
	}
	
	/**
	 * 
	 * @return il numero dei Sotto-Processi
	 */
	public int getSubprocesses() {
		return getNumberOfTypeElement(SubProcess.class);
	}
	
	/**
	 * 
	 * @return il numero delle Sub Conversations
	 */
	public int getSubConversations() {
		return getNumberOfTypeElement(SubConversation.class);
	}
	
	/**
	 * 
	 * @return il numero dei Terminate Events
	 */
	public int getTerminateEvents() {
		return getNumberOfTypeElement(TerminateEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero dei Texts 
	 */
	public int getTexts() {
		return getNumberOfTypeElement(Text.class);
	}
	
	/**
	 * 
	 * @return il numero delle Text Annotations
	 */
	public int getTextAnnotations() {
		return getNumberOfTypeElement(TextAnnotation.class);
	}
	
	/**
	 * 
	 * @return il numero dei Throw Events
	 */
	public int getThrowEvents() {
		return getNumberOfTypeElement(ThrowEvent.class);
	}
	
	/**
	 * 
	 * @return il numero delle Time Cycles
	 */
	public int getTimeCycles() {
		return getNumberOfTypeElement(TimeCycle.class);
	}
	
	/**
	 * 
	 * @return il numero dei Time Dates
	 */
	public int getTimeDates() {
		return getNumberOfTypeElement(TimeDate.class);
	}
	
	/**
	 * 
	 * @return il numero delle Time Durations
	 */
	public int getTimeDurations() {
		return getNumberOfTypeElement(TimeDuration.class);
	}
	
	/**
	 * 
	 * @return il numero dei Timer Events
	 */
	public int getTimerEventDefinitions() {
		return getNumberOfTypeElement(TimerEventDefinition.class);
	}
	
	/**
	 * 
	 * @return il numero delle Transactions
	 */
	public int getTransactions() {
		return getNumberOfTypeElement(Transaction.class);
	}
	
	/**
	 * 
	 * @return il numero degli User Tasks
	 */
	public int getUserTasks() {
		return getNumberOfTypeElement(UserTask.class);
	}
	
	/**
	 * 
	 * @return il numero di complex gateways che dividono il flusso
	 */
	public int getFluxDividingComplexGateways() {
		return getFluxDividingElementsOfType(ComplexGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di event based gateways che dividono il flusso
	 */
	public int getFluxDividingEventBasedGateways() {
		return getFluxDividingElementsOfType(EventBasedGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di exclusive gateways che dividono il flusso
	 */
	public int getFluxDividingExclusiveGateways() {
		return getFluxDividingElementsOfType(ExclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di inclusive gateways che dividono il flusso
	 */
	public int getFluxDividingInclusiveGateways() {
		return getFluxDividingElementsOfType(InclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di parallel gateways che dividono il flusso
	 */
	public int getFluxDividingParallelGateways() {
		return getFluxDividingElementsOfType(ParallelGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di tasks che dividono il flusso
	 */
	public int getFluxDividingTasks() {
		return getFluxDividingElementsOfType(Task.class);
	}
	
	/**
	 * 
	 * @return il numero di complex gateways che uniscono il flusso
	 */
	public int getFluxJoiningComplexGateways() {
		return getFluxJoiningElementsOfType(ComplexGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di event based gateways che uniscono il flusso
	 */
	public int getFluxJoiningEventBasedGateways() {
		return getFluxJoiningElementsOfType(EventBasedGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di exclusive gateways che uniscono il flusso
	 */
	public int getFluxJoiningExclusiveGateways() {
		return getFluxJoiningElementsOfType(ExclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di inclusive gateways che uniscono il flusso
	 */
	public int getFluxJoiningInclusiveGateways() {
		return getFluxJoiningElementsOfType(InclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di parallel gateways che uniscono il flusso
	 */
	public int getFluxJoiningParallelGateways() {
		return getFluxJoiningElementsOfType(ParallelGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di tasks che uniscono il flusso
	 */
	public int getFluxJoiningTasks() {
		return getFluxJoiningElementsOfType(Task.class);
	}
	
	/**
	 * 
	 * @return il numero di complex gateways che uniscono e dividono il flusso
	 */
	public int getFluxJoiningAndDividingComplexGateways() {
		return getFluxJoiningAndDividingElementsOfType(ComplexGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di event based gateways che uniscono e dividono il flusso
	 */
	public int getFluxJoiningAndDividingEventBasedGateways() {
		return getFluxJoiningAndDividingElementsOfType(EventBasedGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di exclusive gateways che uniscono e dividono il flusso
	 */
	public int getFluxJoiningAndDividingExclusiveGateways() {
		return getFluxJoiningAndDividingElementsOfType(ExclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di inclusive gateways che uniscono e dividono il flusso
	 */
	public int getFluxJoiningAndDividingInclusiveGateways() {
		return getFluxJoiningAndDividingElementsOfType(InclusiveGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di parallel gateways che uniscono e dividono il flusso
	 */
	public int getFluxJoiningAndDividingParallelGateways() {
		return getFluxJoiningAndDividingElementsOfType(ParallelGateway.class);
	}
	
	/**
	 * 
	 * @return il numero di tasks che uniscono e dividono il flusso
	 */
	public int getFluxJoiningAndDividingTasks() {
		return getFluxJoiningAndDividingElementsOfType(Task.class);
	}
	
	/**
	 * Metodo che cerca tutti gli elementi della classe type presenti nel modello e conta quanti dividono il flusso
	 * @param type: classe del tipo di elemento che si vuole analizzare
	 * @return il numero di elementi della classe type che dividono il flusso
	 */
	private int getFluxDividingElementsOfType(Class type) {
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
	 * Metodo che cerca tutti gli elementi della classe type presenti nel modello e conta quanti uniscono il flusso
	 * @param type: classe del tipo di elemento che si vuole analizzare
	 * @return il numero di elementi della classe type che uniscono il flusso
	 */
	private int getFluxJoiningElementsOfType(Class type) {
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
	 * Metodo che cerca tutti gli elementi della classe type presenti nel modello e conta quanti uniscono e dividono il flusso
	 * @param type: classe del tipo di elemento che si vuole analizzare
	 * @return il numero di elementi della classe type che uniscono e dividono il flusso
	 */
	private int getFluxJoiningAndDividingElementsOfType(Class type) {
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
	 * ottenerne il numero complessivo
	 * 
	 * @param type:
	 *            la classe del tipo degli elementi di cui si vuole conoscere il
	 *            numero
	 * @return il numero degli elementi del tipo "type"
	 */
	public int getNumberOfTypeElement(Class type) {
		return getCollectionOfElementType(type).size();
	}
	
	public Collection<ModelElementInstance> getCollectionOfElementType(Class type) {
		ModelElementType modelElementType = modelInstance.getModel().getType(type);
		return this.modelInstance.getModelElementsByType(modelElementType);
	}
}
