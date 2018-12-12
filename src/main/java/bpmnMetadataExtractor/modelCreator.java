//package camunda;
//
//import java.io.File;
//
//import org.camunda.bpm.model.bpmn.Bpmn;
//import org.camunda.bpm.model.bpmn.BpmnModelInstance;
//import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
//import org.camunda.bpm.model.bpmn.instance.Definitions;
//import org.camunda.bpm.model.bpmn.instance.EndEvent;
//import org.camunda.bpm.model.bpmn.instance.FlowNode;
//import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
//import org.camunda.bpm.model.bpmn.instance.StartEvent;
//import org.camunda.bpm.model.bpmn.instance.UserTask;
//import org.camunda.bpm.model.xml.instance.ModelElementInstance;
//
//public class modelCreator {
//	
//	private BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
//	
//
//	
//	public void createModel(){
//		// create an empty model
//		BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
//		Definitions definitions = modelInstance.newInstance(Definitions.class);
//		definitions.setTargetNamespace("http://camunda.org/examples");
//		modelInstance.setDefinitions(definitions);
//
//		// create the process
//		Process process = createElement(definitions, "process-with-one-task", Process.class);
//
//		// create start event, user task and end event
//		StartEvent startEvent = createElement(process, "start", StartEvent.class);
//		UserTask task1 = createElement(process, "task1", UserTask.class);
//		task1.setName("User Task");
//		EndEvent endEvent = createElement(process, "end", EndEvent.class);
//
//		// create the connections between the elements
//		createSequenceFlow(process, startEvent, task1);
//		createSequenceFlow(process, task1, endEvent);
//
//		// validate and write model to file
//		Bpmn.validateModel(modelInstance);
//		File file = File.createTempFile("bpmn-model-api-", ".bpmn");
//		Bpmn.writeModelToFile(file, modelInstance);
//	}
//	
//	public SequenceFlow createSequenceFlow(Process process, FlowNode from, FlowNode to) {
//		  String identifier = from.getId() + "-" + to.getId();
//		  SequenceFlow sequenceFlow = createElement(process, identifier, SequenceFlow.class);
//		  process.addChildElement(sequenceFlow);
//		  sequenceFlow.setSource(from);
//		  from.getOutgoing().add(sequenceFlow);
//		  sequenceFlow.setTarget(to);
//		  to.getIncoming().add(sequenceFlow);
//		  return sequenceFlow;
//		}
//	
//	protected <T extends ModelElementInstance> T createElement(ModelElementInstance parentElement, String id, Class<T> elementClass) {
//		  T element = modelInstance.newInstance(elementClass);
//		  element.setAttributeValue("id", id, true);
//		  parentElement.addChildElement(element);
//		  return element;
//		}
//
//	
//	protected <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String id, Class<T> elementClass) {
//	  T element = modelInstance.newInstance(elementClass);
//	  element.setAttributeValue("id", id, true);
//	  parentElement.addChildElement(element);
//	  return element;
//	}
//}
