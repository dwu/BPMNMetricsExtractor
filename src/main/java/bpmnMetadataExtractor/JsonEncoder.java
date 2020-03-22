package bpmnMetadataExtractor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author PROSLabTeam
 *	Classe per la creazione di un file .json per esportare le metriche estratte 
 */
public class JsonEncoder {
	
	
	private JsonObject json;
	private String fileName;
	private Map<String, String[]> metricsInfos;  
	
	/**
	 * Costruttore per l'encoder Json
	 */
	public JsonEncoder(String fileName){
		json = new JsonObject();
		this.fileName = fileName;
		this.initializeJSON();
		this.initializeMetricsInfos();
	}

	public JsonObject getJson() {
		return json;
	}
	
	/**
	 * Si inizializza il JSONObject per avere il seguente formato:
	 * {
	 * "header":{},
	 * "Basic Metrics":{},
	 * "Advanced Metrics":{}
	 * }
	 */
	private void initializeJSON(){
		JsonObject header = new JsonObject();
		JsonObject basicMetrics = new JsonObject();
		JsonObject advancedMetrics = new JsonObject();
		this.json.add("header", header);
		this.json.add("basic_metrics", basicMetrics);
		this.json.add("advanced_metrics", advancedMetrics);
		this.json.getAsJsonObject("basic_metrics").addProperty("NT", 0);
	}
	
	private void initializeMetricsInfos() {
		metricsInfos = new HashMap<String, String[]>();

		BufferedInputStream metricsInputStream = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("metriche_complete.txt"));
		BufferedReader metricsReader  = new BufferedReader(new InputStreamReader(metricsInputStream, StandardCharsets.ISO_8859_1));
		List<String> metrics = new ArrayList<String>();

		try {
			String line;
			while ((line = metricsReader.readLine()) != null) {
				metrics.add(line);
			}
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
		JsonObject basicMetric = new JsonObject();
		String metricInfos[] = metricsInfos.get(metricName);
		basicMetric.addProperty("value", n);
		basicMetric.addProperty("description", metricInfos[0]);
		basicMetric.addProperty("source", metricInfos[1]);
		this.json.getAsJsonObject("basic_metrics").add(metricName, basicMetric);
	}
	
	/**
	 * Metodo per aggiungere le metriche avazate al json
	 * @param metricName nome della metrica da aggiungere
	 * @param n numero delle metriche
	 */
	public void addAdvancedMetric(String metricName, double n){
		JsonObject advMetric = new JsonObject();
		String metricInfos[] = metricsInfos.get(metricName);
		if (!Double.isFinite(n))
			n = 0;
		n = Math.round(n * 1000.0) / 1000.0;
		advMetric.addProperty("value", n);
		advMetric.addProperty("description", metricInfos[0]);
		advMetric.addProperty("source", metricInfos[1]);
		this.json.getAsJsonObject("advanced_metrics").add(metricName, advMetric);
	}
	
	public ArrayList<String> getBasicMetricsNames() {
		JsonArray namesArray = this.json.getAsJsonObject("basic_metrics").getAsJsonArray();
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < namesArray.size(); ++i) {
			names.add(namesArray.get(i).getAsString());
        }
		return names;
	}
	
	public ArrayList<String>  getAdvancedMetricsNames() {
		JsonArray namesArray = this.json.getAsJsonObject("advanced_metrics").getAsJsonArray();
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < namesArray.size(); ++i) {
			names.add(namesArray.get(i).getAsString());
        }
		return names;
	}
	
	public ArrayList<Integer> getBasicMetricsValues() {
		JsonObject basicMetrics = this.json.getAsJsonObject("basic_metrics");
		JsonArray namesArray = basicMetrics.getAsJsonArray();
		ArrayList<Integer> values = new ArrayList<Integer>();
		for (int i = 0; i < namesArray.size(); ++i) {
			values.add(basicMetrics.getAsJsonObject(namesArray.get(i).getAsString()).get("value").getAsInt());
        }
		return values;
	}
	
	public ArrayList<Double> getAdvancedMetricsValues() {
		JsonObject advancedMetrics = this.json.getAsJsonObject("advanced_metrics");
		JsonArray namesArray = advancedMetrics.getAsJsonArray();
		ArrayList<Double> values = new ArrayList<Double>();
		for (int i = 0; i < namesArray.size(); ++i) {
			values.add(advancedMetrics.getAsJsonObject(namesArray.get(i).getAsString()).get("value").getAsDouble());
        }
		return values;
	}
	
	public ArrayList<String> getHeaderValues() {
		JsonObject header = this.json.getAsJsonObject("header");
		JsonArray namesArray = header.getAsJsonArray();
		ArrayList<String> values = new ArrayList<String>();
		for (int i = 0; i < namesArray.size(); ++i) {
			values.add(header.getAsJsonObject(namesArray.get(i).getAsString()).getAsString());
        }
		return values;
	}
	
	public String getModelId() {
		JsonObject header = this.json.getAsJsonObject("header");
		return header.get("id").getAsString();
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
		this.json.getAsJsonObject("header").addProperty("file_name", fileName);
		String hour = appendLeadingZero(now.getHour());
		String minute = appendLeadingZero(now.getMinute());
		String second = appendLeadingZero(now.getSecond());
		String time = hour + ":" + minute + ":" + second;
		this.json.getAsJsonObject("header").addProperty("creation_time", time);
		String month = appendLeadingZero(now.getMonthValue());
		String day = appendLeadingZero(now.getDayOfMonth());
		String date = now.getYear() + "-" + month + "-" + day;
		this.json.getAsJsonObject("header").addProperty("creation_date", date);
		String id = createFileId(now);
		this.json.getAsJsonObject("header").addProperty("id", id);
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
	private String createFileId(LocalDateTime now) {
		int baseIdLen = 32;
		String idStr = Integer.toString(((int)(Math.floor(Math.random() * 50)))) + "_";
		idStr += Long.toString(System.currentTimeMillis()) + "_";
		do {
			idStr += Integer.toString(((int)(Math.floor(Math.random() * 35))));
		} while (idStr.length() < baseIdLen);
		return idStr;
	}
	
	
}
