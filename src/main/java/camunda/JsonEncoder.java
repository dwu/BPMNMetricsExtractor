package camunda;

import java.io.FileWriter;
import java.io.IOException;

import org.camunda.bpm.engine.impl.util.json.*;
/**
 * 
 * @author PROSLabTeam
 *	Classe per la creazione di un file .json per esportare le metriche estratte 
 */
public class JsonEncoder {
	
	
	private JSONObject json = new JSONObject();
	
	/**
	 * Costruttore per l'encoder Json
	 */
	public JsonEncoder(){
		this.initializeJSON();
	}
	
	/**
	 * Si inizializza il JSONObject per avere il seguente formato:
	 * {
	 * "Header":{},
	 * "Basic Metrics":{},
	 * "Advanced Metrics":{}
	 * }
	 */
	public void initializeJSON(){
		JSONObject header = new JSONObject();
		
		JSONObject basicMetrics = new JSONObject();
		
		JSONObject advancedMetrics = new JSONObject();
		
		this.json.put("Header", header).put("Basic Metrics", basicMetrics).put("Advanced Metrics", advancedMetrics);
	
	}
	
	/**
	 * Metodo per aggiungere le metriche di base al json
	 * @param metricName nome della metrica da aggiungere
	 * @param n numero delle metriche
	 */
	public void addBasicMetric(String metricName, int n){
		this.json.getJSONObject("Basic Metrics").put(metricName, n);
	}
	
	/**
	 * Metodo per aggiungere le metriche avazate al json
	 * @param metricName nome della metrica da aggiungere
	 * @param n numero delle metriche
	 */
	public void addAdvancedMetric(String metricName, float n){
		this.json.getJSONObject("Advanced Metrics").put(metricName, n);
	}
	
	/**
	 * Stampa il json sulla console
	 * @return file json
	 */
	public String print(){
		return this.json.toString();
	}
	
	/**
	 * Metodo per esportare il json su un file
	 */
	public void exportJson(){
		try(FileWriter file = new FileWriter("metriche.json"))
		{
			file.write(json.toString());
			file.flush();		
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
