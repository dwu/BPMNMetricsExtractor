package camunda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlInterface {
	
	private static final String CONN_STRING = "jdbc:mysql://localhost:3306/bpmn_repository?serverTimezone=Europe/Rome";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	private Statement statement = null;
	private Connection connection = null;
	private ResultSet resultSet = null;
	
	public void connect() {
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        connection = DriverManager.getConnection(CONN_STRING,USERNAME,PASSWORD);
	        System.out.println("Connected");
	        // Statements allow to issue SQL queries to the database
	        statement = connection.createStatement();
	        // Result set get the result of the SQL query
	        resultSet = statement.executeQuery("select * from repository");
	        writeResultSet(resultSet);
	    } catch (SQLException | ClassNotFoundException e){
	    	System.err.println(e);
	    }
	}
	
	private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            String dati = resultSet.getString("STRING");
            System.out.println("STRING: " + dati);
        }
    }

}
