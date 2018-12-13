package bpmnMetadataExtractor;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

/**
 * A class that implements the Connectors Interplay Metrics as defined by Jan Mendling (2008) Metrics for Process Models
 * @author PROSLabTeam
 *
 */
public class ConnectorInterplayMetricsExtractor {

	private GatewayMismatchMetricExtractor gmExtractor;
	private ConnectorsHeterogeneityMetricExtractor chExtractor;
	
	public ConnectorInterplayMetricsExtractor(BpmnBasicMetricsExtractor basicMetricExtractor) {
		this.gmExtractor = new GatewayMismatchMetricExtractor(basicMetricExtractor);
		this.chExtractor = new ConnectorsHeterogeneityMetricExtractor(basicMetricExtractor);
	}
	
	public int getGatewaysMismatchMetric() {
		return this.gmExtractor.getGatewaysMismatch();
	}
	
	public double getConnectorsHeterogeneityMetric() {
		return this.chExtractor.getConnectorsHomogeneity();
	}
	
	/**
	 * A class that implements the Connectors Heterogeneity Metric as defined by Jan Mendling in Metrics for Process Models (2008) 
	 * @author PROSLabTeam
	 *
	 */
	private class ConnectorsHeterogeneityMetricExtractor {
		
		private BpmnBasicMetricsExtractor basicMetricExtractor;
		
		public ConnectorsHeterogeneityMetricExtractor(BpmnBasicMetricsExtractor basicMetricExtractor) {
			this.basicMetricExtractor = basicMetricExtractor;
		}
		/**
		 * Metric: CH - Connectors heterogeneity. It gives the entropy over the different connectors types.
		 * @return the connectors homogeneity factor
		 */
		public double getConnectorsHomogeneity() {
			double toReturn = -(this.andRelativeFrequenceTimesLogarithm() + this.xorRelativeFrequenceTimesLogarithm() + this.orRelativeFrequenceTimesLogarithm());
			if (Double.isFinite(toReturn))
				return toReturn;
			else 
				return 0.0;
		}
		/**
		 * Internal method used to compute the relative frequency (p(l)) multiplied by log3(p(l)). In this case l = Inclusive Gateway (or).
		 * @return p(inclusiveGateway)*log3(p(inclusiveGateway)
		 */
		private double orRelativeFrequenceTimesLogarithm() {
			double relativeFrequence = this.getRelativeFrequencyOfGateway(InclusiveGateway.class);
			return relativeFrequence*this.logBase3(relativeFrequence);
		}
		/**
		 * Internal method used to compute the relative frequency (p(l)) multiplied by log3(p(l)). In this case l = Exclusive Gateway (xor).
		 * @return p(exclusiveGateway)*log3(p(exclusiveGateway)
		 */
		private double xorRelativeFrequenceTimesLogarithm() {
			double relativeFrequence = this.getRelativeFrequencyOfGateway(ExclusiveGateway.class);
			return relativeFrequence*this.logBase3(relativeFrequence);
		}
		/**
		 * Internal method used to compute the relative frequency (p(l)) multiplied by log3(p(l)). In this case l = Parallel Gateway (and).
		 * @return p(parallelGateway)*log3(p(parallelGateway)
		 */
		private double andRelativeFrequenceTimesLogarithm() {
			double relativeFrequence = this.getRelativeFrequencyOfGateway(ParallelGateway.class);
			return relativeFrequence*this.logBase3(relativeFrequence);
		}
		/**
		 * Internal method used to compute the relative frequency of a specified type of gateway.
		 * @param type - the calss of gateways of which relative frequency has to be calculated
		 * @return the relative frequency of the specified gateway
		 */
		private double getRelativeFrequencyOfGateway(Class type) {
			Collection<ModelElementInstance> gateways = this.basicMetricExtractor.getCollectionOfElementType(Gateway.class);
			Collection<ModelElementInstance> specificGateways = this.basicMetricExtractor.getCollectionOfElementType(type);
			try {
				return (double)specificGateways.size()/gateways.size();
			} catch (ArithmeticException e) {
				return 0;
			}
		}
		/**
		 * Internal method used to compute the log3 of a number
		 * @param number - the number of which log3 has to be computed
		 * @return the log3 of the given number
		 */
		private double logBase3(double number) {
			double toReturn = 0;
			try {
				toReturn = Math.log(number) / Math.log(3);
			} catch (ArithmeticException e) {
				System.out.println(e);
			}
			return toReturn;
		}
	}
	
	
	/**
	 * A class that implements the Connector Mismatch Metric as defined by Jan Mendling in Metrics for Process Models (2008) 
	 * @author PROSLabTeam
	 *
	 */
	private class GatewayMismatchMetricExtractor {
		
		private BpmnBasicMetricsExtractor basicMetricExtractor;
		
		public GatewayMismatchMetricExtractor(BpmnBasicMetricsExtractor basicMetricExtractor) {
			this.basicMetricExtractor = basicMetricExtractor;
		}
			
		/**
		 * Metric: GM - Gateway Mismatch. It is the sum of gateway pairs that do not match with each other
		 * @return the sum of gateway pairs that do not match with each other
		 */
		public int getGatewaysMismatch() {
			return this.getInclusiveGatewaysMismatch() + this.getExclusiveGatewaysMismatch() + this.getParallelGatewayMismatch();
		}
		
		
		/**
		 * Internal method that measures the mismatch between Exclusive (OR) gateways
		 * @return the XOr-Connector mismatch
		 */
		private int getExclusiveGatewaysMismatch() {
			return Math.abs(this.getTotalDegreeOfSplitGateways("org.camunda.bpm.model.bpmn.impl.instance.ExclusiveGatewayImpl") - this.getTotalDegreeOfJoinGateways("org.camunda.bpm.model.bpmn.impl.instance.ExclusiveGatewayImpl"));
		}
		/**
		 * Internal method that measures the mismatch between Complex (XOR) gateways
		 * @return the Or-Connector mismatch
		 */
		private int getInclusiveGatewaysMismatch() {
			return Math.abs(this.getTotalDegreeOfSplitGateways("org.camunda.bpm.model.bpmn.impl.instance.InclusiveGatewayImpl") - this.getTotalDegreeOfJoinGateways("org.camunda.bpm.model.bpmn.impl.instance.InclusiveGatewayImpl"));
		}
		/**
		 * Internal method that measures the mismatch between Parallel (AND) gateways
		 * @return the And-Connector mismatch
		 */
		private int getParallelGatewayMismatch() {
			return Math.abs(this.getTotalDegreeOfSplitGateways("org.camunda.bpm.model.bpmn.impl.instance.ParallelGatewayImpl") - this.getTotalDegreeOfJoinGateways("org.camunda.bpm.model.bpmn.impl.instance.ParallelGatewayImpl"));
		}
		
		
		
		/**
		 * Internal method that computes the degree of all the Join-Gateways specified by the classPathName parameter. 
		 * The degree of a join-gateway is defined as its in-degree (The number of incoming edges).
		 * @param classPathName - the path name of the specific gateway class which degree has to be computed
		 * @return the total degree of the specified gateway
		 */
		private int getTotalDegreeOfJoinGateways(String classPathName) {
			Class<?> gatewayClass;
			int totalDegree = 0;
			try {
				gatewayClass = Class.forName(classPathName);
				Collection<ModelElementInstance> gateways = this.basicMetricExtractor.getCollectionOfElementType(Gateway.class);
				for (ModelElementInstance modelGateway: gateways) {
					Gateway gateway = (Gateway) modelGateway;
					if (gateway.getClass().equals(gatewayClass)) {
						if (gateway.getIncoming().size() > 1) {
							totalDegree += gateway.getIncoming().size();
						}
					}
					}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return totalDegree;
		}
		
		/**
		 * Internal method that computes the degree of all the Split-Gateways specified by the classPathName parameter. 
		 * The degree of a split-gateway is defined as its out-degree (The number of outgoing edges).
		 * @param classPathName - the path name of the specific gateway class which degree has to be computed
		 * @return the total degree of the specified gateway
		 */
		private int getTotalDegreeOfSplitGateways(String classPathName) {
			Class<?> gatewayClass;
			int totalDegree = 0;
			try {
				gatewayClass = Class.forName(classPathName);
				Collection<ModelElementInstance> gateways = this.basicMetricExtractor.getCollectionOfElementType(Gateway.class);
				for (ModelElementInstance modelGateway: gateways) {
					Gateway gateway = (Gateway) modelGateway;
					if (gateway.getClass().equals(gatewayClass)) {
						if (gateway.getOutgoing().size() > 1) {
							totalDegree += gateway.getOutgoing().size();
						}
					}
					}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return totalDegree;
		}
	}
}
