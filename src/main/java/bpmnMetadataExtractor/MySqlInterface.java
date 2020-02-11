package bpmnMetadataExtractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySqlInterface {
	
	private static final String CONN_STRING = "jdbc:mysql://localhost:3306/bpmn_repository?serverTimezone=Europe/Rome";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "MAcri";
	private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	public void connect() {
	    try {
	        Class.forName(DRIVER_CLASS);
	        System.out.println("Connecting to database...");
	        connection = DriverManager.getConnection(CONN_STRING,USERNAME,PASSWORD);
	        System.out.println("Connected");
	        statement = connection.createStatement();   
	    } catch (SQLException | ClassNotFoundException e){
	    	System.err.println(e);
	    	closeConnection();
	    } 
	}
	
	public void closeConnection() {
        if (resultSet != null) {
            try {
            	resultSet.close();
            } catch (SQLException e) { /*ignored*/ }
        }
        if (statement != null) {
            try {
            	statement.close();
            } catch (SQLException e) { /*ignored*/ }
        }
        if (connection != null) {
            try {
            	connection.close();
            } catch (SQLException e) { /*ignored*/ }
        }
        System.out.println("Closing connection to database");
	}
	
	public void saveMetrics(JsonEncoder json) {
//		String modelInfosInsert = insertModelInfosString(json);
		String basicMetricsInsert = insertBasicMetricsString(json);
		String advMetricsInsert = insertAdvancedMetricsString(json);
		try {
//			System.out.println("Saving model informations...");
//			statement.executeUpdate(modelInfosInsert);
			System.out.println("Model informations saved\nSaving basic metrics...");
			statement.executeUpdate(basicMetricsInsert);
			System.out.println("Basic metrics saved\nSaving advanced metrics...");
			statement.executeUpdate(advMetricsInsert);
			System.out.println("Advanced metrics saved");
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Connection error, closing connection");
			closeConnection();
		}
	}
	
//	private String insertModelInfosString(JsonEncoder json) {
//		String insertInfos = "INSERT INTO MODELS_INFO VALUES (";
//		ArrayList<String> modelInfos = json.getHeaderValues();
//		//Id
//		insertInfos += "'" + modelInfos.get(3) + "', ";
//		//File Name
//		insertInfos += "'" + modelInfos.get(1) + "', ";
//		//Time
//		insertInfos += "'" + modelInfos.get(0) + "', ";
//		//Date
//		insertInfos += "'" + modelInfos.get(2) + "')";
//		return insertInfos;
//	}
	
	private String insertBasicMetricsString(JsonEncoder json) {
		String insertInto = "INSERT INTO BASIC_METRICS VALUES (";
		ArrayList<Integer> basicMetricsValues = json.getBasicMetricsValues();
		insertInto += "'" + json.getModelId() + "', ";
		for (int value : basicMetricsValues) {
			insertInto += "'" + Integer.toString(value) + "', ";
		}
		String toReturn = insertInto.substring(0, insertInto.length()-2);
		toReturn += ")";
		return toReturn;
	}
	
	private String insertAdvancedMetricsString(JsonEncoder json) {
		String insertInto = "INSERT INTO ADVANCED_METRICS VALUES (";
		ArrayList<Double> advancedMetricsValues = json.getAdvancedMetricsValues();
		insertInto += "'" + json.getModelId() + "', ";
		for (double value : advancedMetricsValues) {
			insertInto += "'" + Double.toString(value) + "', ";
		}
		String toReturn = insertInto.substring(0, insertInto.length()-2);
		toReturn += ")";
		return toReturn;
	}

	public void createTables(JsonEncoder json) {
		try {
	        //String infosCreate = createModelInfosTableString();
	        String basicCreate = createBasicTableString(json);
	        String advancedCreate = createAdvancedTableString(json);
	        //System.out.println("Creazione tabella MODELS_INFO...");
	        //statement.executeUpdate(infosCreate);
	        System.out.println("Creazione tabella completata\nCreazione tabella basic_metrics...");
	    	statement.executeUpdate(basicCreate);
	        System.out.println("Creazione tabella completata\nCreazione tabella advanced_metrics...");
	        statement.executeUpdate(advancedCreate);
	        System.out.println("Creazione tabella completata");
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Connection error, closing connection");
			closeConnection();
		}
	}

	/**
	 * Temp function
	 * @param json
	 * @return
	 */
	private String createBasicTableString(JsonEncoder json) {
		ArrayList<String>  basicMetricsNames = json.getBasicMetricsNames();
        String basicCreate = "CREATE TABLE basic_metrics (id VARCHAR(50) NOT NULL, ";
        for (int i = 0; i < basicMetricsNames.size(); i++) {
        	basicCreate += basicMetricsNames.get(i) + " INTEGER, ";
        }
        basicCreate += " PRIMARY KEY ( id ))";
        return basicCreate;
	}
	
	/**
	 * Temp function
	 * @param json
	 * @return
	 */
	private String createAdvancedTableString(JsonEncoder json) {
		ArrayList<String>  advancedMetricsNames = json.getAdvancedMetricsNames();
        String advCreate = "CREATE TABLE advanced_metrics (id VARCHAR(50) NOT NULL, ";
        for (int i = 0; i < advancedMetricsNames.size(); i++) {
        	advCreate += advancedMetricsNames.get(i) + " REAL, ";
        }
        advCreate += " PRIMARY KEY ( id ))";
        return advCreate;
	}
	
	// deprecated
//	private String createModelInfosTableString() {
//        String infosCreate = "CREATE TABLE MODELS_INFO (id INTEGER NOT NULL, ";
//        infosCreate += " FILE_NAME VARCHAR(50), ";
//        infosCreate += " CREATION_TIME TIME, ";
//        infosCreate += " CREATION_DATE DATE, ";
//        infosCreate += " PRIMARY KEY ( id ))";
//        return infosCreate;
//	}
	
	public void createAndInsertMetricsInfosTable() {
		try {
			System.out.println("Creating table metrics_infos...");
			statement.executeUpdate(createMetricsInfosTableString());
			System.out.println("Table Created \nInserting metrics values...");;
			statement.executeUpdate(generateMetricsInfosInsertString());
			System.out.println("Values inserted correctly");
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Connection error, closing connection");
			closeConnection();
		}
		
	}
	
	private String createMetricsInfosTableString() {
		String metricInfosCreate = "CREATE TABLE metrics_infos ( ";
		metricInfosCreate += "name VARCHAR(30), ";
		metricInfosCreate += "description VARCHAR(150), ";
		metricInfosCreate += "source VARCHAR(150), ";
		metricInfosCreate += "PRIMARY KEY ( name ))";
		return metricInfosCreate;
	}
	
	private String generateMetricsInfosInsertString() throws IOException {
		List<String> advMetrics = Files.readAllLines(Paths.get("metriche_complete.txt"), StandardCharsets.ISO_8859_1);
		String base = "INSERT INTO metrics_infos VALUES ";
		String inserts = "";
		String name, desc, source;
		for (String m : advMetrics) {
			name = m.substring(0, m.indexOf('%'));
			name = name.trim();
			desc = m.substring(m.indexOf('%') + 1, m.indexOf('{'));
			desc = desc.trim();
			source = m.substring(m.indexOf('{') + 1, m.length() - 1);
			source = source.trim();
			inserts += "('" + name + "', '" + desc + "', '" + source + "'), ";
		}
		inserts = inserts.substring(0, inserts.length() - 2);
		return base + inserts;
	}

}
