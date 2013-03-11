package ops;

/**
 * @author shadowhhl
 * DataPoint is to record each data point, either index data or transaction data
 * It includes the price/value of the data, as well as the date
 */
public class DataPoint {
	public Integer year;		//year value of the value
	public Integer month;		//month value of the value
	public Integer day;			//day value of the value
	public Double price;		//price value of the value
	
	public DataPoint() {		//Initialization
		year = 0;
		month = 0;
		day = 0;
		price = 0.0;
	}
}
