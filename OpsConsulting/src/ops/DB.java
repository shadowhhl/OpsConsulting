package ops;

import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shadowhhl
 * DB is responsible for database connection, query, management, and other stuffs.
 */
public class DB {

	/**
	 * @param sqlStmt
	 * @return resultSet
	 * connect(String sqlStmt) first connects to the database, executes the SQL statement 
	 * specified, and returns the result as a ResultSet
	 */
	public ResultSet connect(String sqlStmt) {
		ResultSet rs = null;								//Initialize the ResultSet
		
		try {
			Class.forName("com.mysql.jdbc.Driver");			//Initialize MySQL JDBC driver
			//System.out.println("Success loading Mysql Driver");		//Successfully loading
		}
		catch (Exception e) {
			e.printStackTrace();							//Handle exception
		}
		
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://dbw.cs.columbia.edu:3306/hldb", 
					"hailiang", "hailiang123");				//Establish connection to the database
			Statement stmt = connection.createStatement();	//Create SQL statement
			rs = stmt.executeQuery(sqlStmt);				//Execute statement and get the ResultSet
		}
		catch (Exception e) {
			e.printStackTrace();		//Handle exception
		}

		return rs;
	}
	
	/**
	 * @param rs
	 * @return boolean
	 * writeCSV(ResultSet rs) writes the result into a CSV file
	 */
	public boolean writeCSVforMLX(ResultSet rs) {
		SimpleDateFormat dateFileName = new SimpleDateFormat("yy_MM_dd_HHmmss");
			//Initialize file name
			//File is named after the time generated
		Date dNow = new Date();
		String fileName = dateFileName.format(dNow)+".csv";
		
		try {
			PrintWriter csvOut = new PrintWriter(new File(fileName).getAbsoluteFile());
			try {
				csvOut.println("Date,Price");							//Write the first row, the header row, into the CSV file
				while (rs.next()) {										//If the ResultSet has more records
					try {
						Double price = new Double(rs.getString(2));					
							//Get the price field from the ResultSet
							//Will check whether the price is a valid number
							//If not, an exception will be generated.
						csvOut.println(rs.getString(1) + "," + price.toString());	
							//If the price is valid, write the date and the price to the CSV file
					} catch (Exception e) {
						e.printStackTrace();		//Handle exception
						throw(e);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();				//Handle exception
				throw(e);
			} finally {
				csvOut.close();						//Close the file
			}
		} catch (Exception e) {
			e.printStackTrace();					//Handle exception
			return false;							//Return false if any exception is generated
		} 
		return true;								//Return true if no exception is generated and the CSV file is good
	}
	
	
}
