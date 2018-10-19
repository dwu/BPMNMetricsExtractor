package camunda;

import java.io.File;
import java.util.Collection;
import org.camunda.bpm.model.bpmn.*;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

public class bpmnModelReader {

	public static void main(String[] args){
		
		File loadedFile = new File("airline.bpmn");
		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(loadedFile);
		ModelElementType taskType = modelInstance.getModel().getType(Task.class);
		Collection<ModelElementInstance> taskInstances = modelInstance.getModelElementsByType(taskType);
		System.out.println("Numero di task: " + taskInstances.size());
	
	}
}
