package camunda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlInterface {
	
	private static final String CONN_STRING = "jdbc:mysql://localhost:3306/bpmn_repository?serverTimezone=Europe/Rome";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
	
	public void connect(JsonEncoder json) {
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
	    try {
	        Class.forName(DRIVER_CLASS);
	        System.out.println("Connecting to database...");
	        connection = DriverManager.getConnection(CONN_STRING,USERNAME,PASSWORD);
	        System.out.println("Connected");
	        statement = connection.createStatement();   
	        //CODICE PER LA CREAZIONE DEL DATABASE 
	        //String basicCreate = createBasicTableString(json);
	        //String advancedCreate = createAdvancedTableString(json);
        	//statement.executeUpdate(basicCreate);
	        //statement.executeUpdate(advancedCreate);
	    } catch (SQLException | ClassNotFoundException e){
	    	System.err.println(e);
	    } finally {
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

}
