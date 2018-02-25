package YuriReznik.Server.persistancy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import YuriReznik.Server.exceptions.NoSuchDataBaseFound;

public enum DataBaseAccess {
	INSTANCE;
	
	private Connection connection;
	private String CREATE_DB_SCRIPT = "createTables.sql";
	private String DRIVER_CLASS = "com.mysql.jdbc.Driver";
	private String DB_BASE_URL = "jdbc:mysql://localhost/";
	private String DB_NAME = "yurivitalimvc";
	private String DB_USER = "scott";
	private String DB_PASSWORD = "tiger";
	
	private DataBaseAccess() {
		try {
			initDBConnection();
		} catch (NoSuchDataBaseFound noSuchDataBaseFound) {
			System.out.println("No database found. Creating...");
			importInitialData();
			initDBConnection();
		}
	}

	private void initDBConnection() throws NoSuchDataBaseFound{
		try {
			Class.forName(DRIVER_CLASS);
			System.out.println("Driver loaded");
			connection = createSQLConnection(DB_BASE_URL + DB_NAME, DB_USER, DB_PASSWORD);
			System.out.println("Database connected");
		} catch (SQLException | ClassNotFoundException e) {
			if (e instanceof MySQLSyntaxErrorException) {
				if (e.getMessage().matches("Unknown database '" + DB_NAME + "'")) {
					throw new NoSuchDataBaseFound(e);
				}
			}
			System.out.println("Failed to init DB Connection: " + e.getMessage());				
		}
	}
	
	private Connection createSQLConnection(String url, String user, String password) throws ClassNotFoundException, SQLException {
		// Establish a connection
		Connection connection = DriverManager.getConnection(url, user, password);
		return connection;
	}
	
	public PreparedStatement getPreparedStatement(String query) throws SQLException {
		return connection.prepareStatement(query);
	}

	public Connection createCustomConnection() throws SQLException {
		try {
			Connection connection = createSQLConnection(DB_BASE_URL + DB_NAME, DB_USER, DB_PASSWORD);
			return connection;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public ResultSet executeQuery(String query) throws SQLException{
		return getPreparedStatement(query).executeQuery();
	}
	
	public void importInitialData() {
		// Get the object of DataInputStream
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(CREATE_DB_SCRIPT);
		} catch (FileNotFoundException e) {
			System.out.println("File " + CREATE_DB_SCRIPT + " does not exists. Loading from inside jar");
			inputStream = this.getClass().getResourceAsStream("/" + CREATE_DB_SCRIPT);
		}

		try (
				DataInputStream in = new DataInputStream(inputStream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				Connection connection = createSQLConnection(DB_BASE_URL, DB_USER, DB_PASSWORD);
				) {
			Statement stmt = connection.createStatement();
			String strLine = "", strLine1 = "";
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (strLine != null && !strLine.trim().equals("")) {
					if (!strLine.trim().contains("/*") && !strLine.trim().contains("*/")) {
						if (strLine.indexOf(';') >= 0) {
							strLine1 += strLine;
							//System.out.println(strLine1);
							stmt.execute(strLine1);
							strLine1 = "";
						} else
							strLine1 += strLine;
					}
				}
			}
			System.out.println("Database created");
		} catch (Exception e) {
			System.out.println("Failed to create initial tables: " + e.getMessage());
		}
	}
}
