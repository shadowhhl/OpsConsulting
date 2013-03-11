package ops;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MarketAnalyzer {
	
	private ResultSet rs;
	
	private Regression regSPSqft1M, regSPSqft6M, regSPSqft1Y;
	private AvgResult avgResult1M, avgResult6M, avgResult1Y;
	private ExtpoResult extpoResult1M, extpoResult6M, extpoResult1Y;
	
	class ExtpoResult {
		double extpoPrice = 0;
		int counter = 0;
		double std = 0;
		String remarks;
	}
	
	class AvgResult {
		double avgSPSqft = 0;
		double stdSPSqft = 0;
		int counter = 0;
		String remarks;
	}
	
	class PriceSeries {
		int timeFrame;
		ArrayList< DataPoint > prices = new ArrayList< DataPoint >();
	}
	
	public MarketAnalyzer() {
		System.out.println("default initializer of ma");
	}
	
	public MarketAnalyzer(String sqlStmt) {
		System.out.println("String initializer of ma");
		DB dbConn = new DB();
		rs = dbConn.connect(sqlStmt);
		dbConn.writeCSVforMLX(rs);
	}
		
	private ArrayList< DataPoint > parseResultSet() throws Exception {
		ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
		rs.absolute(1);
		try {
			while (rs.next()) {
				DataPoint dataPoint = new DataPoint();
				
				try {
					Double price = new Double(rs.getString(2));
					dataPoint.price = price;
				} catch (Exception e) {
					continue;
				}
				
				String dateStr = rs.getString(1);
				int firstInd = dateStr.indexOf("/");
				int lastInd = dateStr.lastIndexOf("/");
				
				String monthStr = dateStr.substring(0, firstInd);
				String dayStr = dateStr.substring(firstInd+1, lastInd);
				String yearStr = dateStr.substring(lastInd+1, lastInd+5);
				dataPoint.year = new Integer(yearStr);
				dataPoint.month = new Integer(monthStr);
				dataPoint.day = new Integer(dayStr);
				
				dataPoints.add(dataPoint);
			}
		} catch (Exception e) {
			throw e;
		}
		
		return dataPoints;
	}
	
	private AvgResult doAverage(int timeFrame, PriceSeries priceSeries) throws Exception {
		AvgResult avgResult = new AvgResult();
		double SPSqft = 0.0;
		int counter = 0;
		
		ArrayList< DataPoint > prices = priceSeries.prices;
		
		for (DataPoint dp : prices) {
			SPSqft += dp.price;
			counter += 1;
		}
		
		avgResult.counter = counter;
		avgResult.avgSPSqft = SPSqft/counter;
		
		double sum = 0;
		for (DataPoint dp : prices) {
			sum += Math.pow((dp.price-avgResult.avgSPSqft), 2);
		}
		
		avgResult.stdSPSqft = Math.sqrt(sum/counter);
		
		return avgResult;
	}
	
	private PriceSeries getPriceSeries(int timeFrame, ArrayList< DataPoint > dataPoints) throws Exception {
		PriceSeries priceSeries = new PriceSeries();
		
		priceSeries.timeFrame = timeFrame;
		
		/* initial date strings*/
		Date nowDate = new Date();
		
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		String nowYearStr = yearFormat.format(nowDate);
		int nowYear = new Integer(nowYearStr);
		
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
		String nowMonthStr = monthFormat.format(nowDate);
		int nowMonth = new Integer(nowMonthStr);
		/*date strings initialization finish*/
		
		for (DataPoint dp:dataPoints) {
			double monthDiff = (nowYear*12+nowMonth-dp.year*12-dp.month);

			if(monthDiff <= timeFrame) {
				priceSeries.prices.add(dp);
			}
		}
		
		return priceSeries;
	}
	
	public void doAnalysis(Addition addition) throws Exception{
		PriceSeries priceSeries1M, priceSeries6M, priceSeries1Y;
		
		ArrayList< DataPoint > dataPoints = parseResultSet();
		ArrayList< DataPoint > indexDataPoints = getIndexData(addition.indexName);
		
		priceSeries1M = getPriceSeries(1, dataPoints);
		priceSeries6M = getPriceSeries(6, dataPoints);
		priceSeries1Y = getPriceSeries(12, dataPoints);
		
		avgResult1M = doAverage(1, priceSeries1M);
		avgResult6M = doAverage(6, priceSeries6M);
		avgResult1Y = doAverage(12, priceSeries1Y);
		
		//calculate regression
		regSPSqft1M = setRegSPSqft(priceSeries1M.prices, indexDataPoints);
		regSPSqft6M = setRegSPSqft(priceSeries6M.prices, indexDataPoints);
		regSPSqft1Y = setRegSPSqft(priceSeries1Y.prices, indexDataPoints);
		double x0 = getCurrentIndex(indexDataPoints);
		regSPSqft1M.setX0(x0);
		regSPSqft6M.setX0(x0);
		regSPSqft1Y.setX0(x0);
		
		//do extrapolation
		extpoResult1M = doExtpo(priceSeries1M.prices, indexDataPoints);
		extpoResult6M = doExtpo(priceSeries6M.prices, indexDataPoints);
		extpoResult1Y = doExtpo(priceSeries1Y.prices, indexDataPoints);
	}
	private double getCurrentIndex(ArrayList< DataPoint > indexData) throws Exception{
		DataPoint dp = indexData.get(indexData.size()-1);
		return dp.price;
	}
	
	private ExtpoResult doExtpo(ArrayList< DataPoint > priceData, ArrayList< DataPoint > indexData) throws Exception {
		ExtpoResult extpoResult = new ExtpoResult();
		
		ArrayList< Double > extpoPrices = new ArrayList<Double>();
		double currentIndex = getCurrentIndex(indexData);
		
		for (DataPoint dp: priceData) {
			for (DataPoint dpInd: indexData) {
				if (dpInd.year.equals(dp.year) && dpInd.month.equals(dp.month)) {
					double indexChange = currentIndex/dpInd.price;
					double newPrice = dp.price*indexChange;
					extpoPrices.add(newPrice);
					break;
				}
			}
		}
		double sum = 0;
		double std = 0;
		for (Double p:extpoPrices) {
			sum+=p;
		}

		extpoResult.extpoPrice = sum/extpoPrices.size();
		extpoResult.counter = extpoPrices.size();
		
		for (Double p:extpoPrices) {
			std+=Math.pow((p-sum/extpoPrices.size()), 2);
		}
		extpoResult.std=Math.sqrt(std/extpoPrices.size());
		
		return extpoResult;
	}
	
	private ArrayList< DataPoint > getIndexData(String indexName) throws Exception {
		//read index data
		ArrayList< DataPoint > indexData = new ArrayList<DataPoint>();
		
		try {
			BufferedReader br;
			if (indexName.equals("caseshiller")) {
				br = new BufferedReader(new InputStreamReader(new FileInputStream("index_caseshiller.txt")));
			}
			else if (indexName.equals("zillow")) {
				br = new BufferedReader(new InputStreamReader(new FileInputStream("index_zillow.txt")));
			} else {
				br = new BufferedReader(new InputStreamReader(new FileInputStream("index.txt")));
			}
			
			String newLine;
			while((newLine = br.readLine())!=null) {
				DataPoint indexDataPoint = new DataPoint();
				int commaInd = newLine.indexOf(",");
				
				String monthStr = newLine.substring(0, 2);
				String yearStr = newLine.substring(3, 7);
				indexDataPoint.year = new Integer(yearStr);
				indexDataPoint.month = new Integer(monthStr);
				
				String priceStr = newLine.substring(commaInd+1);
				indexDataPoint.price = new Double(priceStr);
				
				indexData.add(indexDataPoint);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return indexData;
	}
	
	private Regression setRegSPSqft(ArrayList< DataPoint > priceDataPoints, ArrayList<DataPoint> indexData) throws Exception {
		
		//Extract data
		ArrayList< Double > index = new ArrayList< Double >();
		ArrayList< Double > prices = new ArrayList< Double >();
		
		for (DataPoint dp:priceDataPoints) {
			int dpYear = dp.year;
			int dpMonth = dp.month;
			for (DataPoint indDp:indexData) {
				if (indDp.year == dpYear && indDp.month ==dpMonth) {
					index.add(indDp.price);
					prices.add(dp.price);
					break;
				}
			}
		}
		
		//do regression
		Regression reg = new Regression();
		reg.reg(index, prices);
		
		return reg;
	}
	
//	private ArrayList<String> getStreetName(String rawText) {
//		ArrayList<String> nameList = new ArrayList<String>();
//		
//		String[] rawList;
//		rawList = rawText.split(" ");
//		try {
//			BufferedReader br;
//			br = new BufferedReader(new InputStreamReader(new FileInputStream("streetname.txt")));
//			
//			String newLine;
//			String properName;
//			while((newLine = br.readLine())!=null) {
//				if (newLine.equals("#")) {
//					properName = br.readLine();
//				}
//				else {
//					
//				}
//				DataPoint indexDataPoint = new DataPoint();
//				int commaInd = newLine.indexOf(",");
//				
//				String monthStr = newLine.substring(0, 2);
//				String yearStr = newLine.substring(3, 7);
//				indexDataPoint.year = new Integer(yearStr);
//				indexDataPoint.month = new Integer(monthStr);
//				
//				String priceStr = newLine.substring(commaInd+1);
//				indexDataPoint.price = new Double(priceStr);
//				
//				indexData.add(indexDataPoint);
//			}
//			br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		
//		return null;
//	}
	
	public double getAvg1MSPSqft() {
		return avgResult1M.avgSPSqft;
	}

	public double getAvg6MSPSqft() {
		return avgResult6M.avgSPSqft;
	}

	public double getAvg1YSPSqft() {
		return avgResult1Y.avgSPSqft;
	}

	public double getStd1MSPSqft() {
		return avgResult1M.stdSPSqft;
	}

	public double getStd6MSPSqft() {
		return avgResult6M.stdSPSqft;
	}

	public double getStd1YSPSqft() {
		return avgResult1Y.stdSPSqft;
	}

	public ResultSet getRs() {
		return rs;
	}

	public Regression getReg1MSPSqft() {
		return regSPSqft1M;
	}

	public Regression getReg6MSPSqft() {
		return regSPSqft6M;
	}

	public Regression getReg1YSPSqft() {
		return regSPSqft1Y;
	}

	public int getCounter1M() {
		return avgResult1M.counter;
	}

	public int getCounter6M() {
		return avgResult6M.counter;
	}

	public int getCounter1Y() {
		return avgResult1Y.counter;
	}

	public double getExtpoResultPrice1M() {
		return extpoResult1M.extpoPrice;
	}

	public double getExtpoResultPrice6M() {
		return extpoResult6M.extpoPrice;
	}

	public double getExtpoResultPrice1Y() {
		return extpoResult1Y.extpoPrice;
	}
	
	public double getExtpoResultStd1M() {
		return extpoResult1M.std;
	}
	
	public double getExtpoResultStd6M() {
		return extpoResult6M.std;
	}
	
	public double getExtpoResultStd1Y() {
		return extpoResult1Y.std;
	}
}
