package bpmnMetadataExtractor;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.util.json.*;
/**
 * 
 * @author PROSLabTeam
 *	Classe per la creazione di un file .json per esportare le metriche estratte 
 */
public class JsonEncoder {
	
	
	private JSONObject json;
	private String fileName;
	private Map<String, String[]> metricsInfos;  
	
	/**
	 * Costruttore per l'encoder Json
	 */
	public JsonEncoder(String fileName){
		json = new JSONObject();
		this.fileName = fileName;
		this.initializeJSON();
		this.initializeMetricsInfos();
	}

	public JSONObject getJson() {
		return json;
	}
	
	/**
	 * Si inizializza il JSONObject per avere il seguente formato:
	 * {
	 * "Header":{},
	 * "Basic Metrics":{},
	 * "Advanced Metrics":{}
	 * }
	 */
	private void initializeJSON(){
		JSONObject header = new JSONObject();
		JSONObject basicMetrics = new JSONObject();
		JSONObject advancedMetrics = new JSONObject();
		this.json.put("Header", header).put("Basic Metrics", basicMetrics).put("Advanced Metrics", advancedMetrics);
		this.json.getJSONObject("Basic Metrics").put("NT", 0);
	}
	
	private void initializeMetricsInfos() {
		String path = "";
		try {
			path = Paths.get(this.getClass().getClassLoader().getResource("metriche_complete.txt").toURI()).toString();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(path);
		metricsInfos = new HashMap<String, String[]>();
		List<String> metrics = new ArrayList<String>();
		try {
//			metrics = Files.readAllLines(Paths.get("metriche_complete.txt"), StandardCharsets.ISO_8859_1);
			metrics = Files.readAllLines(Paths.get(path), StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			System.out.println(e);
			System.out.println("Error reading metrics file");
		}
		String name, desc, source;
		String infos[];
		for (String m : metrics) {
			infos = new String[2];
			name = m.substring(0, m.indexOf('%'));
			name = name.trim();
			desc = m.substring(m.indexOf('%') + 1, m.indexOf('{'));
			desc = desc.trim();
			source = m.substring(m.indexOf('{') + 1, m.length() - 1);
			source = source.trim();
			infos[0] = desc;
			infos[1] = source;
			metricsInfos.put(name, infos);
		}
	}
	
	/**
	 * Metodo per aggiungere le metriche di base al json
	 * @param metricName nome della metrica da aggiungere
	 * @param n numero delle metriche
	 */
	public void addBasicMetric(String metricName, int n){
		JSONObject basicMetric = new JSONObject();
		String metricInfos[] = metricsInfos.get(metricName);
		basicMetric.put("Value", n);
		basicMetric.put("Description", metricInfos[0]);
		basicMetric.put("Source", metricInfos[1]);
		this.json.getJSONObject("Basic Metrics").put(metricName, basicMetric);
	}
	
	/**
	 * Metodo per aggiungere le metriche avazate al json
	 * @param metricName nome della metrica da aggiungere
	 * @param n numero delle metriche
	 */
	public void addAdvancedMetric(String metricName, double n){
		JSONObject advMetric = new JSONObject();
		String metricInfos[] = metricsInfos.get(metricName);
		advMetric.put("Value", n);
		advMetric.put("Description", metricInfos[0]);
		advMetric.put("Source", metricInfos[1]);
		this.json.getJSONObject("Advanced Metrics").put(metricName, advMetric);
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
	
	public ArrayList<Integer> getBasicMetricsValues() {
		JSONObject basicMetrics = this.json.getJSONObject("Basic Metrics");
		JSONArray namesArray = basicMetrics.names();
		ArrayList<Integer> values = new ArrayList<Integer>();
		for (int i = 0; i < namesArray.length(); ++i) {
			values.add(basicMetrics.getJSONObject(namesArray.getString(i)).getInt("Value"));
        }
		return values;
	}
	
	public ArrayList<Double> getAdvancedMetricsValues() {
		JSONObject advancedMetrics = this.json.getJSONObject("Advanced Metrics");
		JSONArray namesArray = advancedMetrics.names();
		ArrayList<Double> values = new ArrayList<Double>();
		for (int i = 0; i < namesArray.length(); ++i) {
			values.add(advancedMetrics.getJSONObject(namesArray.getString(i)).getDouble("Value"));
        }
		return values;
	}
	
	public ArrayList<String> getHeaderValues() {
		JSONObject header = this.json.getJSONObject("Header");
		JSONArray namesArray = header.names();
		ArrayList<String> values = new ArrayList<String>();
		for (int i = 0; i < namesArray.length(); ++i) {
			values.add(header.getString(namesArray.getString(i)));
        }
		return values;
	}
	
	public String getModelId() {
		JSONObject header = this.json.getJSONObject("Header");
		return header.getString("id");
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

	public void populateHeader(LocalDateTime now) {
		this.json.getJSONObject("Header").put("File_Name", fileName);
		String hour = appendLeadingZero(now.getHour());
		String minute = appendLeadingZero(now.getMinute());
		String second = appendLeadingZero(now.getSecond());
		String time = hour + ":" + minute + ":" + second;
		this.json.getJSONObject("Header").put("Creation_Time", time);
		String month = appendLeadingZero(now.getMonthValue());
		String day = appendLeadingZero(now.getDayOfMonth());
		String date = now.getYear() + "-" + month + "-" + day;
		this.json.getJSONObject("Header").put("Creation_Date", date);
		int id = createFileId(now);
		this.json.getJSONObject("Header").put("id", id);
	}

	private String appendLeadingZero(int number) {
		return number < 10 ? "0" + Integer.toString(number) : Integer.toString(number);
	}

	/**
	 * Function that creates an ID based on date, time and file name.
	 * TODO Files created in the same moment, with the same letters in their name have the same ID
	 * @param now: LocalDateTime initialized during the json finalization
	 * @return the id for the file
	 */
	private int createFileId(LocalDateTime now) {
		int baseId = 42;
		int temp = 0;
		for (int i = 0; i < fileName.length(); i++) {
			temp += (int) fileName.charAt(i);
		}
		temp += now.getHour() + now.getMinute() + now.getSecond() + now.getDayOfMonth() + now.getMonthValue() + now.getYear();
		return baseId * 31 + temp;
	}
	
	
}
