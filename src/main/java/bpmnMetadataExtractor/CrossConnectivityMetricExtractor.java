package bpmnMetadataExtractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

/**
 * Classe che effettua l'estrazione della metrica Cross-Connectivity.
 * Ad ora, il peso degli eventi e dei gateway diversi dall'exclusive e dall'inclusive sono settati a 1, in quanto nel paper 
 * di riferimento non sono menzionati.
 * 
 * @author PROSLabTeam
 *
 */
public class CrossConnectivityMetricExtractor {
	
	//rimuovere il riferimento all'oggetto basicExtractor e al suo posto aggiungere direttamente le liste di oggetti necessarie per eliminare la dipendenza?
	private BpmnBasicMetricsExtractor basicExtractor;
	private Map<String, Double> nodeValues;
	private Map<String, Double> archValues;
	private Collection<Double> totalConnections;
	private ArrayList<String> visitedNodes;
	
	public CrossConnectivityMetricExtractor(BpmnBasicMetricsExtractor basicExtractor) {
		this.basicExtractor = basicExtractor;
		nodeValues = new HashMap<String, Double>();
		archValues = new HashMap<String, Double>();
		totalConnections = new ArrayList<Double>();
		visitedNodes = new ArrayList<String>();
	}
	
	public double calculateCrossConnectivity() {
		getNodesWeights();
		getArchsWeights();
		getConnections();
		double result = getCrossConnectivity();
		if (!Double.isFinite(result))
			return 0;
		else 
			return result;
	}
	
	/**
	 * Metodo che calcola i pesi di tutti i nodi e li inserisce in nodeValues
	 * "d" � pari alla somma degli archi entranti e uscenti da un nodo
	 * AND / TASK / EVENTI : 1
	 * XOR : 1 / d 
	 * OR : (1 / 2^d - 1) + ((2^d - 2) / (2^d - 1)) * 1 / d  
	 */
	private void getNodesWeights() {
		Collection<ModelElementInstance> nodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
		double value = 0.0;
		double d = 0.0;
		double powOfD = 0.0;
		for (ModelElementInstance modelNode : nodes) {
			FlowNode node = (FlowNode) modelNode;
			if (node instanceof ExclusiveGateway) {
				//XOR
				d = (node.getIncoming().size() + node.getOutgoing().size());
				nodeValues.put(node.getId(), 1.0 / d);
			} else if (node instanceof InclusiveGateway) {
				//OR
				d = (node.getIncoming().size() + node.getOutgoing().size());
				powOfD = Math.pow(2.0, d);
				value = (1.0 / (powOfD - 1.0)) + (((powOfD - 2.0) / (powOfD - 1.0)) * 1.0 / d);
				nodeValues.put(node.getId(), value);
			} else if (node instanceof FlowNode) {
				//ALTRI GATEWAY, TASK E EVENTI
				nodeValues.put(node.getId(), 1.0);
			} 
		}
	}
	
	/**
	 * Metodo che calcola il peso degli archi e li inserisce in archValues
	 * Il peso di un arco � dato dalla prodotto del peso del nodo sorgente e del peso del nodo bersaglio
	 */
	private void getArchsWeights() {
		Collection<ModelElementInstance> nodes = basicExtractor.getCollectionOfElementType(SequenceFlow.class);
		double value = 0;
		double node1;
		double node2;
		for (ModelElementInstance modelArch : nodes) {
			try {
				SequenceFlow arch = (SequenceFlow) modelArch;
				node1 = nodeValues.get(arch.getSource().getId());
				node2 = nodeValues.get(arch.getTarget().getId());
				value = node1 * node2;
				archValues.put(arch.getId(), value);
			}catch(Exception e) {continue;}
		}
	}
	
	/**
	 * Metodo per ottenere tutti i pesi delle possibili connessioni tra i nodi.
	 * Il valore di connessione tra due nodi n1 e n2 � data dal valore massimo di percorso tra n1 e n2.
	 */
	private void getConnections() {
		Collection<ModelElementInstance> nodes = basicExtractor.getCollectionOfElementType(FlowNode.class);
		//Lista in cui vengono posti temporaneamente i vari valori di percorso di ogni coppia di nodi in ogni ciclo
		Collection<Double> toCalc = new ArrayList<Double>();
		Double tempSum = 0.0;
		//Si calcola il valore di percorso tra ogni nodo
		for (ModelElementInstance modelNode1 : nodes) {
			FlowNode node1 = (FlowNode) modelNode1;
			for (ModelElementInstance modelNode2 : nodes) {
				FlowNode node2 = (FlowNode) modelNode2;
				if (!node1.getId().equals(node2.getId())) {
					//I nodi da confrontare non sono lo stesso nodo TODO: considerare i loop
					visitedNodes.clear();
					try {
						toCalc.add(findPathValueBetween(node1, node2, 0.0));
					}catch(Exception e) {continue;}
				}
			}
			tempSum = 0.0;
			//Si sommano tutti valori di percorso calcolati per ottenere la connessione
			for (Double pathVal : toCalc) {
				tempSum += pathVal;
			}
			toCalc.clear();
			totalConnections.add(tempSum);
		}
	}
	
	/**
	 * Metodo che calcola la CC.
	 * La metrica � data dalla seguente formula:
	 * Sommatoria Connessioni / N * (N - 1)
	 * dove N � pari al numero di Nodi nel modello.
	 * @return CC
	 */
	private double getCrossConnectivity() {
		Double connectionSum = 0.0;
		for (Double connection : totalConnections) {
			connectionSum += connection;
		}
		//System.out.println(nodeValues.size());
		return connectionSum / (nodeValues.size() * (nodeValues.size() -1));
	}
	
	/**
	 * Metodo ricorsivo che esplora il modello mentre calcola il valore del path.
	 * Se si arriva al nodo bersaglio, il metodo ritorna il valore del path.
	 * Se si arriva ad aver analizzato tutti gli archi uscenti senza aver trovato il nodo bersaglio, il metodo ritorna 0.
	 * Se si analizza un arco uscente senza trovare il nodo bersaglio, il metodo passa all'arco successivo.
	 * Se si analizza un arco uscente e si trova il nodo bersaglio, il valore del percorso ottenuto � aggiunto al pathReturnValues.
	 * Una volta finito di analizzare i percorsi derivati dagli archi uscenti, si itera su pathReturnValues e si ritorna il valore maggiore della lista.
	 * @param sourceNode nodo sorgente
	 * @param targetNode nodo bersaglio
	 * @param pathValue valore del path
	 * @return valore massimo del path tra i due nodi 
	 */
	private Double findPathValueBetween(FlowNode sourceNode, FlowNode targetNode, double pathValue) {
		ArrayList<SequenceFlow> archs = new ArrayList<SequenceFlow>(sourceNode.getOutgoing());
		double newPathValue = 0.0;
		double archValue = 0.0;
		double tempPathValue = 0.0;
		double maxPathValue = 0.0;
		if (visitedNodes.contains(sourceNode.getId())) {
			return 0.0;
		} else {
			visitedNodes.add(sourceNode.getId());
		}
		for (int i = 0; i < archs.size(); i++) {;
			archValue = archValues.get(archs.get(i).getId());
			newPathValue = pathValue == 0.0 ? archValue : pathValue * archValue;
			if (archs.get(i).getTarget().getId().equals(targetNode.getId()))
				//il targetNode � stato trovato, quindi si returna il valore del path
				return newPathValue;
			else {
				//il target node non � stato ancora trovato, quindi si continua ad esplorare il modello richiamando il metodo ricorsivamente
				tempPathValue = findPathValueBetween(archs.get(i).getTarget(), targetNode, newPathValue);
				if (tempPathValue != 0.0) 
					//la chiamata ricorsiva ha trovato un percorso possibile, che viene confrontato con il maggiore trovato ad ora
					maxPathValue = tempPathValue > maxPathValue ? tempPathValue : maxPathValue;
			} 
		}
		visitedNodes.remove(sourceNode.getId());
		return maxPathValue;
	}
}