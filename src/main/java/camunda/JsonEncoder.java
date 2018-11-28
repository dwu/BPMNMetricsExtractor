package camunda;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.camunda.bpm.engine.impl.util.json.*;
/**
 * 
 * @author PROSLabTeam
 *	Classe per la creazione di un file .json per esportare le metriche estratte 
 */
public class JsonEncoder {
	
	
	private JSONObject json;
	private String fileName;
	
	/**
	 * Costruttore per l'encoder Json
	 */
	public JsonEncoder(String fileName){
		json = new JSONObject();
		this.fileName = fileName;
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
	public void addAdvancedMetric(String metricName, double n){
		this.json.getJSONObject("Advanced Metrics").put(metricName, n);
	}
	
	public ArrayList<String> getBasicMetricsNames() {
		JSONArray namesArray = this.json.getJSONObject("Basic Metrics").names();
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < namesArray.length(); ++i) {
			names.add(namesArray.getString(i));
        }
		return names;
	}
	
	public ArrayList<String>  getAdvancedMetricsNames() {
		JSONArray namesArray = this.json.getJSONObject("Advanced Metrics").names();
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < namesArray.length(); ++i) {
			names.add(namesArray.getString(i));
        }
		return names;
	}
	
	
	
	/**
	 * 
	 * @return file json
	 */
	public String getString(){
		return this.json.toString();
	}
	
	/**
	 * Metodo per esportare il json in un file il cui nome contiene il nome del modello bpmn e un timestamp
	 */
	public void exportJson(){
		LocalDateTime now = LocalDateTime.now();
		String outputFileName = fileName + " - " + now.getDayOfMonth() + "-" + now.getMonthValue() + "-" + now.getYear() + "--" + now.getHour() + "-" + now.getMinute();
		this.populateHeader(now);
		try(FileWriter file = new FileWriter(outputFileName + ".json"))
		{
			file.write(json.toString());
			file.flush();		
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	private void populateHeader(LocalDateTime now) {
		this.json.getJSONObject("Header").put("File Name", fileName);
		String time = now.getHour() + ":" + now.getMinute();
		this.json.getJSONObject("Header").put("Time", time);
		String date = now.getDayOfMonth() + "/" + now.getMonthValue() + "/" + now.getYear();
		this.json.getJSONObject("Header").put("Date", date);
		int id = createFileId(now);
		this.json.getJSONObject("Header").put("ID", id);
	}

	/**
	 * Function that creates an ID based on date, time and file name.
	 * TODO Files created in the same moment, with the same letters in their name have the same ID
	 * @param now LocalDateTime initialized during the json finalization
	 * @return the id for the file
	 */
	private int createFileId(LocalDateTime now) {
		int baseId = 42;
		int temp = 0;
		for (int i = 0; i < fileName.length(); i++) {
			temp += (int) fileName.charAt(i);
		}
		temp += now.getHour() + + now.getMinute() + now.getSecond() + now.getDayOfMonth() + now.getMonthValue() + now.getYear();
		return baseId * 31 + temp;
	}
	
	
}
