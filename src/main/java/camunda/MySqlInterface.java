package camunda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySqlInterface {
	
	private static final String CONN_STRING = "jdbc:mysql://localhost:3306/bpmn_repository?serverTimezone=Europe/Rome";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
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
		String modelInfosInsert = insertModelInfosString(json);
		String basicMetricsInsert = insertBasicMetricsString(json);
		String advMetricsInsert = insertAdvancedMetricsString(json);
		try {
			System.out.println("Saving model informations...");
			statement.executeUpdate(modelInfosInsert);
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
	
	private String insertModelInfosString(JsonEncoder json) {
		String insertInfos = "INSERT INTO MODELS_INFO VALUES (";
		ArrayList<String> modelInfos = json.getHeaderValues();
		//Id
		insertInfos += "'" + modelInfos.get(3) + "', ";
		//File Name
		insertInfos += "'" + modelInfos.get(1) + "', ";
		//Time
		insertInfos += "'" + modelInfos.get(0) + "', ";
		//Date
		insertInfos += "'" + modelInfos.get(2) + "')";
		return insertInfos;
	}
	
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
	        String infosCreate = createModelInfosTableString(json);
	        String basicCreate = createBasicTableString(json);
	        String advancedCreate = createAdvancedTableString(json);
	        System.out.println("Creazione tabella MODELS_INFO...");
	        statement.executeUpdate(infosCreate);
	        System.out.println("Creazione tabella completata\nCreazione tabella BASIC_METRICS...");
	    	statement.executeUpdate(basicCreate);
	        System.out.println("Creazione tabella completata\nCreazione tabella ADVANCED_METRICS...");
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
        String basicCreate = "CREATE TABLE BASIC_METRICS (id INTEGER NOT NULL, ";
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
        String advCreate = "CREATE TABLE ADVANCED_METRICS (id INTEGER NOT NULL, ";
        for (int i = 0; i < advancedMetricsNames.size(); i++) {
        	advCreate += advancedMetricsNames.get(i) + " REAL, ";
        }
        advCreate += " PRIMARY KEY ( id ))";
        return advCreate;
	}
	
	private String createModelInfosTableString(JsonEncoder json) {
        String infosCreate = "CREATE TABLE MODELS_INFO (id INTEGER NOT NULL, ";
        infosCreate += " FILE_NAME VARCHAR(50), ";
        infosCreate += " CREATION_TIME TIME, ";
        infosCreate += " CREATION_DATE DATE, ";
        infosCreate += " PRIMARY KEY ( id ))";
        return infosCreate;
	}

}
