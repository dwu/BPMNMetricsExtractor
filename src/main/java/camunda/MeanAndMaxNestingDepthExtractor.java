//package camunda;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//import org.camunda.bpm.model.bpmn.instance.FlowNode;
//import org.camunda.bpm.model.bpmn.instance.StartEvent;
//import org.camunda.bpm.model.xml.instance.ModelElementInstance;
//
//public class MeanAndMaxNestingDepthExtractor {
//	
//	//rimuovere il riferimento all'oggetto basicExtractor e al suo posto aggiungere direttamente le liste di oggetti necessarie per eliminare la dipendenza?
//	private BpmnBasicMetricsExtractor basicExtractor;
//	private Collection<Double> nodeDepths;
//	private ArrayList<String> visitedNodes;
//	
//	public MeanAndMaxNestingDepthExtractor(BpmnBasicMetricsExtractor basicExtractor) {
//		this.basicExtractor = basicExtractor;
//		nodeDepths = new ArrayList<Double>();
//		visitedNodes = new ArrayList<String>();
//	}
//	
//	private void getNestingDepths() {
//		Collection<ModelElementInstance> nodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
//		Collection<ModelElementInstance> startNodes = basicExtractor.getCollectionOfElementType(StartEvent.class);
//		if (startNodes.size() == 1) {
//			for (ModelElementInstance modelNode1 : nodes) {
//				FlowNode node1 = (FlowNode) modelNode1;
//				for (ModelElementInstance modelNode2 : nodes) {
//					FlowNode node2 = (FlowNode) modelNode2;
//					if (!node1.getId().equals(node2.getId())) {
//						//I nodi da confrontare non sono lo stesso nodo TODO: considerare i loop
//						visitedNodes.clear();
//						toCalc.add(findPathValueBetween(node1, node2, 0.0));
//					}
//				}
//				tempSum = 0.0;
//				//Si sommano tutti valori di percorso calcolati per ottenere la connessione
//				for (Double pathVal : toCalc) {
//					tempSum += pathVal;
//				}
//				toCalc.clear();
//				totalConnections.add(tempSum);
//			}
//		} else {
//			return;
//		}
//	}
//	
//
//}
