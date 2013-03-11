package ops;

import java.util.ArrayList;

import ops.MarketAnalyzer;
import ops.Addition;
import ops.Regression;
import ops.NpvAnalyzer;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * @author shadowhhl
 * The GUI initializer
 */
public class MainWindow {
	/**
	 * Put all text input field declaration here
	 */
	private static Text textZip;
	private static Text textSqftLow;
	private static Text textSqftHigh;
	private static Text textPriceLow;
	private static Text textPriceHigh;
	
	/**
	 * Put all combo box declaration here 
	 */
	private static Combo comboType;
	private static Combo comboBr;
	private static Combo comboFace;
	private static Combo comboYbLow;
	private static Combo comboYbHigh;
	private static Combo comboBchmkIndex;
	
	protected Shell shell;
	protected Shell newShell;
	
	/**
	 * Put all text output field declaration here
	 */
	private Text text1MAvg;
	private Text text6MAvg;
	private Text text1YAvg;
	private Text text1MStd;
	private Text text6MStd;
	private Text text1YStd;
	private Text text1MRegA;
	private Text text6MRegA;
	private Text text1YRegA;
	private Text text1MRegB;
	private Text text6MRegB;
	private Text text1YRegB;
	private Text text1MRegR;
	private Text text6MRegR;
	private Text text1YRegR;
	private Text text1MCounter;
	private Text text6MCounter;
	private Text text1YCounter;
	private Text text1MExtpoPrice;
	private Text text6MExtpoPrice;
	private Text text1YExtpoPrice;
	private Text textRegPrice1M;
	private Text textRegPrice6M;
	private Text textRegPrice1Y;
	private MarketAnalyzer marketAnalyzer; 			//An instance of MarketAnalyzer
	private NpvAnalyzer npvAnalyzer;				//An instance of NpvAnalyzer
	
	Regression reg1M, reg6M, reg1Y;
	private Text textIRR;
	private Text textRentalIncomeIncrease;
	private Text textInflationRate;
	private Text textChangeInValue;
	private Text textHorizon;
	private Text textMaintenanceCosts;
	private Text textAccountNo;
	private Text textNPV;
	private Text textNetIncome;
	private Text textTransactionCosts;
	private Text textRentalIncomeInPercentage;
	private Button btnCalculate;
	private Button btnSensitivityAnalysis;
	
	private Label[] lblFinalEstimation;
	private Text[] textFinalEstimation;
	private Text[] textFinalEstLowInterval;
	private Text[] textFinalEstHighInterval;
	/**
	 * Launch the application.
	 * @param args
	 */
	
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
			//Window closed after this line
		} catch (Exception e) {
			e.printStackTrace();					//Handle exception
		}
	}
	
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			
		}
	}
	
	/**
	 * @param status
	 * status 0 means search button is clicked
	 * status 1 menas redo button is clicked
	 * @throws Exception
	 * The event when the search button or redo button is clicked
	 */
	private void searchClickEvent(int status) throws Exception{
		if (status == 0) {										//If search button is clicked
			String str = new String();
			str = getSqlStmtForMLX();									//New SQL statement according to search criteria defined
			System.out.println(str);							
			
			//Search from database and generate CSV result
			marketAnalyzer = new MarketAnalyzer(str);			
				//Initialize the MarketAnalyzer with the SQL statement defined
				//A new ResultSet will be generated
		} else if (status == 1) {								//If redo button is clicked
			//do nothing, use the old marketAnalyzer
		}
		
		Addition addition = new Addition();
		int bchmkIndex = comboBchmkIndex.getSelectionIndex();
		if (bchmkIndex == 0 || bchmkIndex == -1) {
			addition.indexName = "caseshiller";
		} else if (bchmkIndex == 1) {
			addition.indexName = "zillow";
		}
		
		//Calculate average prices
		marketAnalyzer.doAnalysis(addition);
		text1MAvg.setText(Formater.toCurrency(marketAnalyzer.getAvg1MSPSqft()));
		text6MAvg.setText(Formater.toCurrency(marketAnalyzer.getAvg6MSPSqft()));
		text1YAvg.setText(Formater.toCurrency(marketAnalyzer.getAvg1YSPSqft()));
		
		text1MStd.setText(Formater.toCurrency(marketAnalyzer.getStd1MSPSqft()));
		text6MStd.setText(Formater.toCurrency(marketAnalyzer.getStd6MSPSqft()));
		text1YStd.setText(Formater.toCurrency(marketAnalyzer.getStd1YSPSqft()));
		
		text1MCounter.setText(new Integer(marketAnalyzer.getCounter1M()).toString());
		text6MCounter.setText(new Integer(marketAnalyzer.getCounter6M()).toString());
		text1YCounter.setText(new Integer(marketAnalyzer.getCounter1Y()).toString());
		
		//Calculate Price-Index regression
		reg1M = marketAnalyzer.getReg1MSPSqft();
		reg6M = marketAnalyzer.getReg6MSPSqft();
		reg1Y = marketAnalyzer.getReg1YSPSqft();
		
		text1MRegA.setText(Formater.toShortDouble(reg1M.getA(), 3));
		text1MRegB.setText(Formater.toShortDouble(reg1M.getB(), 3));
		text1MRegR.setText(Formater.toShortDouble(reg1M.getR2(), 3));
		
		textRegPrice1M.setText(Formater.toCurrency(reg1M.getY0()));
		
		text6MRegA.setText(Formater.toShortDouble(reg6M.getA(), 3));
		text6MRegB.setText(Formater.toShortDouble(reg6M.getB(), 3));
		text6MRegR.setText(Formater.toShortDouble(reg6M.getR2(), 3));
		
		textRegPrice6M.setText(Formater.toCurrency(reg6M.getY0()));
		System.out.println(reg6M.getR2());
		text1YRegA.setText(Formater.toShortDouble(reg1Y.getA(), 3));
		text1YRegB.setText(Formater.toShortDouble(reg1Y.getB(), 3));
		text1YRegR.setText(Formater.toShortDouble(reg1Y.getR2(), 3));
		System.out.println(reg1Y.getR2());
		textRegPrice1Y.setText(Formater.toCurrency(reg1Y.getY0()));
		
		text1MExtpoPrice.setText(Formater.toCurrency(marketAnalyzer.getExtpoResultPrice1M()));
		text6MExtpoPrice.setText(Formater.toCurrency(marketAnalyzer.getExtpoResultPrice6M()));
		text1YExtpoPrice.setText(Formater.toCurrency(marketAnalyzer.getExtpoResultPrice1Y()));
		
		ArrayList<String> finalEstText = new ArrayList<String>();
		finalEstText = getFinalEstimation();
		updateMiddlePanel(finalEstText);
	}
	
	private ArrayList<String> getFinalEstimation() {
		class ResultSet {
			String method;
			double est;
			double low;
			double high;
			double gap;
		}
		
		ArrayList<String> texts = new ArrayList<String>();
		ResultSet[] rs = new ResultSet[7];
		for (int i=0;i<7;i++) {
			rs[i] = new ResultSet();
		}
		//result of distribution
		rs[0].method = "1 Month Simple";
		rs[0].est = marketAnalyzer.getAvg1MSPSqft();
		rs[0].low = rs[0].est-marketAnalyzer.getStd1MSPSqft()*1.96/Math.sqrt(marketAnalyzer.getCounter1M());
		rs[0].high = rs[0].est+marketAnalyzer.getStd1MSPSqft()*1.96/Math.sqrt(marketAnalyzer.getCounter1M());
		rs[0].gap = rs[0].high-rs[0].low;
		
		rs[1].method = "6 Months Simple";
		rs[1].est = marketAnalyzer.getAvg6MSPSqft();
		rs[1].low = rs[1].est-marketAnalyzer.getStd6MSPSqft()*1.96/Math.sqrt(marketAnalyzer.getCounter6M());
		rs[1].high = rs[1].est+marketAnalyzer.getStd6MSPSqft()*1.96/Math.sqrt(marketAnalyzer.getCounter6M());
		rs[1].gap = rs[1].high-rs[1].low;
		
		rs[2].method = "1 Year Simple";
		rs[2].est = marketAnalyzer.getAvg1YSPSqft();
		rs[2].low = rs[2].est-marketAnalyzer.getStd1YSPSqft()*1.96/Math.sqrt(marketAnalyzer.getCounter1Y());
		rs[2].high = rs[2].est+marketAnalyzer.getStd1YSPSqft()*1.96/Math.sqrt(marketAnalyzer.getCounter1Y());
		rs[2].gap = rs[2].high-rs[2].low;
		
		//result of regression
		if (reg6M.count>2) {
			rs[3].method = "6 Months Regression";
			rs[3].est = reg6M.getY0();
			rs[3].low = rs[3].est-reg6M.getCi(reg6M.getX0());
			rs[3].high=rs[3].est+reg6M.getCi(reg6M.getX0());
			rs[3].gap=rs[3].high-rs[3].low;
		}
		else {
			rs[3].method = "";
			rs[3].est=0; rs[3].low=0;rs[3].high=0;
			rs[3].gap=999999999;
		}
		
		if (reg1Y.count>2) {
			rs[4].method = "1 Year Regression";
			rs[4].est = reg1Y.getY0();
			rs[4].low = rs[4].est-reg1Y.getCi(reg1Y.getX0());
			rs[4].high=rs[4].est+reg1Y.getCi(reg1Y.getX0());
			rs[4].gap=rs[4].high-rs[4].low;
		}
		else {
			rs[4].method = "";
			rs[4].est=0;rs[4].low=0;rs[4].high=0;
			rs[4].gap=999999999;
		}
		
		rs[5].method = "6 Months Extrapolation";
		rs[5].est = marketAnalyzer.getExtpoResultPrice6M();
		rs[5].low = rs[5].est-marketAnalyzer.getExtpoResultStd6M()*1.96/Math.sqrt(marketAnalyzer.getCounter6M());
		rs[5].high = rs[5].est+marketAnalyzer.getExtpoResultStd6M()*1.96/Math.sqrt(marketAnalyzer.getCounter6M());
		rs[5].gap = rs[5].high-rs[5].low;
		
		rs[6].method = "1 Year Extrapolation";
		rs[6].est = marketAnalyzer.getExtpoResultPrice1Y();
		rs[6].low = rs[6].est-marketAnalyzer.getExtpoResultStd1Y()*1.96/Math.sqrt(marketAnalyzer.getCounter1Y());
		rs[6].high = rs[6].est+marketAnalyzer.getExtpoResultStd1Y()*1.96/Math.sqrt(marketAnalyzer.getCounter1Y());
		rs[6].gap = rs[6].high-rs[6].low;
		
		for (int i=0;i<7;i++) {
			for (int j=0;j<7;j++) {
				if (rs[i].gap<rs[j].gap) {
					ResultSet rsTemp = rs[i];
					rs[i]=rs[j];
					rs[j]=rsTemp;
				}
			}
		}
		
		for (int i=0;i<7;i++) {			
			if (Double.isNaN(rs[i].est)) {
			}
			else {
				texts.add(rs[i].method);
				texts.add(Formater.toCurrency(rs[i].est));
				texts.add(Formater.toCurrency(rs[i].low));
				texts.add(Formater.toCurrency(rs[i].high));
			}
		}
		//If more than 10 data points in the recent time interval
//		if (marketAnalyzer.getCounter1M() >= 10) {
//			textFinalEst1M.setText(Formater.toCurrency(marketAnalyzer.getAvg1MSPSqft()));
//			lblEstReason.setText("Enough data recently\nand average the result");
//		}
//		else if (marketAnalyzer.getCounter6M() >= 30) {
//			textFinalEst1M.setText(Formater.toCurrency(marketAnalyzer.getReg6MSPSqft().getY0()));
//			lblEstReason.setText("Use 6-month regression");
//		}
//		else if (marketAnalyzer.getCounter1Y() >= 50) {
//			textFinalEst1M.setText(Formater.toCurrency(marketAnalyzer.getReg1YSPSqft().getY0()));
//			lblEstReason.setText("Use 1-year regression");
//		}
//		else {
//			textFinalEst1M.setText(Formater.toCurrency(marketAnalyzer.getExtpoResultPrice6M()));
//			lblEstReason.setText("Not enough data.\nUse extrapolation");
//		}
		
		
		return texts;
	}
	
	private void updateMiddlePanel(ArrayList<String> texts) {
		int actualNumLines = texts.size()/4;
		int numLines=7;
		
		for (int i=0;i<actualNumLines;i++) {
			lblFinalEstimation[i].setText(texts.get(i*4+0));
			textFinalEstimation[i].setText(texts.get(i*4+1));
			textFinalEstLowInterval[i].setText(texts.get(i*4+2));
			textFinalEstHighInterval[i].setText(texts.get(i*4+3));
		}
		
		for (int i=actualNumLines;i<numLines;i++) {
			lblFinalEstimation[i].setText("");
			textFinalEstimation[i].setText("");
			textFinalEstHighInterval[i].setText("");
			textFinalEstLowInterval[i].setText("");
		}
	}
	
	private void createLeftPanel() {
		Label lblType = new Label(shell, SWT.NONE);
		lblType.setBounds(155, 10, 33, 16);
		lblType.setText("Type");
		
		Label lblZipcode = new Label(shell, SWT.NONE);
		lblZipcode.setBounds(155, 40, 59, 16);
		lblZipcode.setText("Zipcode");

		Label lblSqft = new Label(shell, SWT.NONE);
		lblSqft.setBounds(155, 70, 31, 16);
		lblSqft.setText("SqFt");
		
		Label lblBr = new Label(shell, SWT.NONE);
		lblBr.setText("Number of \nBedrooms");
		lblBr.setBounds(155, 100, 65, 30);
		
		Label lblPrice = new Label(shell, SWT.NONE);
		lblPrice.setBounds(155, 145, 59, 16);
		lblPrice.setText("List Price");
		
		Label lblFace = new Label(shell, SWT.NONE);
		lblFace.setBounds(155, 175, 59, 16);
		lblFace.setText("Face");
		
		Label lblYearBuilt = new Label(shell, SWT.NONE);
		lblYearBuilt.setBounds(155, 205, 59, 16);
		lblYearBuilt.setText("Year Built");
		
		textZip = new Text(shell, SWT.BORDER);
		textZip.setBounds(245, 40, 64, 19);
		
		textSqftLow = new Text(shell, SWT.BORDER);
		textSqftLow.setBounds(245, 70, 64, 19);
		
		Label lblTo = new Label(shell, SWT.NONE);
		lblTo.setBounds(318, 70, 15, 14);
		lblTo.setText("to");
		
		textSqftHigh = new Text(shell, SWT.BORDER);
		textSqftHigh.setBounds(339, 70, 64, 19);
		
		comboBr = new Combo(shell, SWT.NONE);
		comboBr.setItems(new String[] {"Any", "1", "2", "3", "4", "5", "6", "7"});
		comboBr.setBounds(245, 100, 65, 25);
		comboBr.select(0);
		
		textPriceLow = new Text(shell, SWT.BORDER);
		textPriceLow.setBounds(245, 145, 64, 19);
		
		Label lblTo_1 = new Label(shell, SWT.NONE);
		lblTo_1.setBounds(318, 145, 15, 14);
		lblTo_1.setText("to");
		
		textPriceHigh = new Text(shell, SWT.BORDER);
		textPriceHigh.setBounds(339, 145, 64, 19);
		
		comboFace = new Combo(shell, SWT.NONE);
		comboFace.setItems(new String[] {"Any", "N", "NE", "E", "SE", "S", "SW", "W", "NW"});
		comboFace.setBounds(245, 175, 65, 25);
		comboFace.select(0);
		
		comboYbLow = new Combo(shell, SWT.NONE);
		comboYbLow.setItems(new String[] {"Any", "1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959", "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969", "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010"});
		comboYbLow.setBounds(245, 205, 65, 25);
		comboYbLow.select(0);
		
		Label lblTo_2 = new Label(shell, SWT.NONE);
		lblTo_2.setBounds(318, 205, 15, 14);
		lblTo_2.setText("to");
		
		comboYbHigh = new Combo(shell, SWT.NONE);
		comboYbHigh.setItems(new String[] {"Any", "1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959", "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969", "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010"});
		comboYbHigh.setBounds(339, 205, 65, 25);
		comboYbHigh.select(0);
	
		comboType = new Combo(shell, SWT.NONE);
		comboType.setItems(new String[] {"SFR", "CONDO"});
		comboType.setBounds(245, 10, 65, 25);
		comboType.select(0);
		
		Button btnSearch = new Button(shell, SWT.NONE);
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					searchClickEvent(0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSearch.setBounds(96, 238, 94, 28);
		btnSearch.setText("Search");
		
		text1MAvg = new Text(shell, SWT.BORDER);
		text1MAvg.setEditable(false);
		text1MAvg.setBounds(173, 303, 64, 19);
		
		Label lblPriceEstimation = new Label(shell, SWT.NONE);
		lblPriceEstimation.setBounds(10, 283, 102, 14);
		lblPriceEstimation.setText("Price Estimates");
		
		Label lblEstimationMethod = new Label(shell, SWT.NONE);
		lblEstimationMethod.setBounds(435, 10, 120, 14);
		lblEstimationMethod.setText("Final Estimates\n");
		
		Label lblEstMethod = new Label(shell, SWT.NONE);
		lblEstMethod.setBounds(173, 318, 59, 14);
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Verdana", 15, SWT.ITALIC));
		lblNewLabel.setBounds(10, 92, 120, 65);
		lblNewLabel.setText("Altura\nConsulting");
		
		Label lblm1MAvg = new Label(shell, SWT.CENTER);
		lblm1MAvg.setBounds(173, 283, 59, 14);
		lblm1MAvg.setText("1 Month");
		
		text6MAvg = new Text(shell, SWT.BORDER);
		text6MAvg.setEditable(false);
		text6MAvg.setBounds(253, 303, 64, 19);
		
		Label lblm6MAvg = new Label(shell, SWT.CENTER);
		lblm6MAvg.setText("6 Months");
		lblm6MAvg.setBounds(253, 283, 59, 14);
		
		text1YAvg = new Text(shell, SWT.BORDER);
		text1YAvg.setEditable(false);
		text1YAvg.setBounds(335, 303, 64, 19);
		
		Label lbly1YAvg = new Label(shell, SWT.CENTER);
		lbly1YAvg.setText("1 Year");
		lbly1YAvg.setBounds(335, 283, 59, 14);
		
		Label lblAverage = new Label(shell, SWT.NONE);
		lblAverage.setAlignment(SWT.RIGHT);
		lblAverage.setBounds(58, 306, 71, 19);
		lblAverage.setText("Average");
		
		Label lblStd = new Label(shell, SWT.NONE);
		lblStd.setAlignment(SWT.RIGHT);
		lblStd.setBounds(58, 331, 71, 14);
		lblStd.setText("Std");
		
		text1MStd = new Text(shell, SWT.BORDER);
		text1MStd.setEditable(false);
		text1MStd.setBounds(173, 328, 64, 19);
		
		text6MStd = new Text(shell, SWT.BORDER);
		text6MStd.setEditable(false);
		text6MStd.setBounds(253, 328, 64, 19);
		
		text1YStd = new Text(shell, SWT.BORDER);
		text1YStd.setEditable(false);
		text1YStd.setBounds(335, 328, 64, 19);
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setBounds(10, 393, 72, 19);
		lblNewLabel_1.setText("Regression");
		
		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 385, 415, 2);
		
		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBounds(10, 272, 415, 2);
		
		Label label_2 = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
		label_2.setBounds(151, 283, 2, 285);
		
		text1MRegA = new Text(shell, SWT.BORDER);
		text1MRegA.setEditable(false);
		text1MRegA.setBounds(173, 421, 64, 19);
		
		text6MRegA = new Text(shell, SWT.BORDER);
		text6MRegA.setEditable(false);
		text6MRegA.setBounds(253, 421, 64, 19);
		
		text1YRegA = new Text(shell, SWT.BORDER);
		text1YRegA.setEditable(false);
		text1YRegA.setBounds(335, 421, 64, 19);
		
		Label lblyabx = new Label(shell, SWT.NONE);
		lblyabx.setBounds(10, 421, 59, 14);
		lblyabx.setText("(Y=a+bX)");
		
		Label lblA = new Label(shell, SWT.NONE);
		lblA.setAlignment(SWT.RIGHT);
		lblA.setBounds(105, 424, 24, 14);
		lblA.setText("a");
		
		Label lblB = new Label(shell, SWT.NONE);
		lblB.setAlignment(SWT.RIGHT);
		lblB.setBounds(96, 449, 33, 14);
		lblB.setText("b");
		
		Label lblR = new Label(shell, SWT.NONE);
		lblR.setAlignment(SWT.RIGHT);
		lblR.setBounds(96, 488, 33, 14);
		lblR.setText("R2");
		lblR.setVisible(false);
		
		text1MRegB = new Text(shell, SWT.BORDER);
		text1MRegB.setEditable(false);
		text1MRegB.setBounds(173, 446, 64, 19);
		
		text6MRegB = new Text(shell, SWT.BORDER);
		text6MRegB.setEditable(false);
		text6MRegB.setBounds(253, 446, 64, 19);
		
		text1YRegB = new Text(shell, SWT.BORDER);
		text1YRegB.setEditable(false);
		text1YRegB.setBounds(335, 446, 64, 19);
		
		text1MRegR = new Text(shell, SWT.BORDER);
		text1MRegR.setEditable(false);
		text1MRegR.setBounds(173, 485, 64, 19);
		text1MRegR.setVisible(false);
		
		text6MRegR = new Text(shell, SWT.BORDER);
		text6MRegR.setEditable(false);
		text6MRegR.setBounds(253, 485, 64, 19);
		text6MRegR.setVisible(false);
		
		text1YRegR = new Text(shell, SWT.BORDER);
		text1YRegR.setEditable(false);
		text1YRegR.setBounds(335, 485, 64, 19);
		text1YRegR.setVisible(false);
		
		Label lblNumberOfDeals = new Label(shell, SWT.NONE);
		lblNumberOfDeals.setAlignment(SWT.RIGHT);
		lblNumberOfDeals.setBounds(30, 355, 99, 17);
		lblNumberOfDeals.setText("Number of deals");
		
		text1MCounter = new Text(shell, SWT.BORDER);
		text1MCounter.setEditable(false);
		text1MCounter.setBounds(173, 355, 64, 19);
		
		text6MCounter = new Text(shell, SWT.BORDER);
		text6MCounter.setEditable(false);
		text6MCounter.setBounds(253, 355, 64, 19);
		
		text1YCounter = new Text(shell, SWT.BORDER);
		text1YCounter.setEditable(false);
		text1YCounter.setBounds(335, 355, 64, 19);
		
		Label label_3 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_3.setBounds(10, 512, 415, 2);
		
		Label lblExtropolation = new Label(shell, SWT.NONE);
		lblExtropolation.setBounds(10, 518, 85, 17);
		lblExtropolation.setText("Extrapolation");
		
		Label lblNewPrice = new Label(shell, SWT.NONE);
		lblNewPrice.setAlignment(SWT.RIGHT);
		lblNewPrice.setBounds(68, 541, 61, 17);
		lblNewPrice.setText("New Price");
		
		text1MExtpoPrice = new Text(shell, SWT.BORDER);
		text1MExtpoPrice.setEditable(false);
		text1MExtpoPrice.setBounds(173, 541, 64, 19);
		
		text6MExtpoPrice = new Text(shell, SWT.BORDER);
		text6MExtpoPrice.setEditable(false);
		text6MExtpoPrice.setBounds(253, 541, 64, 19);
		
		text1YExtpoPrice = new Text(shell, SWT.BORDER);
		text1YExtpoPrice.setEditable(false);
		text1YExtpoPrice.setBounds(335, 541, 64, 19);
		
		textRegPrice1M = new Text(shell, SWT.BORDER);
		textRegPrice1M.setEditable(false);
		textRegPrice1M.setBounds(173, 393, 64, 19);
		
		textRegPrice6M = new Text(shell, SWT.BORDER);
		textRegPrice6M.setEditable(false);
		textRegPrice6M.setBounds(253, 393, 64, 19);
		
		textRegPrice1Y = new Text(shell, SWT.BORDER);
		textRegPrice1Y.setEditable(false);
		textRegPrice1Y.setBounds(335, 393, 64, 19);
		
		Label lblPrice_1 = new Label(shell, SWT.NONE);
		lblPrice_1.setAlignment(SWT.RIGHT);
		lblPrice_1.setBounds(68, 393, 61, 17);
		lblPrice_1.setText("Price");
		
		comboBchmkIndex = new Combo(shell, SWT.NONE);
		comboBchmkIndex.setItems(new String[] {"Case Shiller", "Zillow"});
		comboBchmkIndex.setBounds(10, 460, 90, 22);
		comboBchmkIndex.select(0);
		
		Label lblBchmkIndex = new Label(shell, SWT.NONE);
		lblBchmkIndex.setBounds(10, 440, 96, 28);
		lblBchmkIndex.setText("Benchmark Index\n");
		
		Button btnRedo = new Button(shell, SWT.NONE);
		btnRedo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					searchClickEvent(1);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		btnRedo.setBounds(23, 481, 59, 28);
		btnRedo.setText("Redo");
		
		Label label_4 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_4.setBounds(10, 566, 415, 2);
	}
	
	private void createRightPanel() {
		npvAnalyzer = new NpvAnalyzer();
		Double d;
		
		Label lblHistoFinInfo = new Label(shell, SWT.NONE);
		lblHistoFinInfo.setBounds(848, 10, 198, 14);
		lblHistoFinInfo.setText("Historical Financial Information");
		
		Label lblIRR = new Label(shell, SWT.NONE);
		lblIRR.setBounds(861, 70, 90, 14);
		lblIRR.setText("IRR");
		
		Label lblRentalIncomeIncrease = new Label(shell, SWT.NONE);
		lblRentalIncomeIncrease.setBounds(861, 134, 198, 14);
		lblRentalIncomeIncrease.setText("Rental Income Increase / Year");
		
		Label lblChangeInValue = new Label(shell, SWT.NONE);
		lblChangeInValue.setBounds(861, 194, 198, 14);
		lblChangeInValue.setText("Change In Value over the period");
		
		Label lblHorizonPeriod = new Label(shell, SWT.NONE);
		lblHorizonPeriod.setBounds(861, 224, 183, 14);
		lblHorizonPeriod.setText("Horizion Period");
		
		Label lblMaintenanceCosts = new Label(shell, SWT.NONE);
		lblMaintenanceCosts.setBounds(861, 254, 183, 14);
		lblMaintenanceCosts.setText("Maintenance Costs");
		
		Label lblInflationRate = new Label(shell, SWT.NONE);
		lblInflationRate.setBounds(861, 164, 183, 14);
		lblInflationRate.setText("Inflation rate");
		
		textIRR = new Text(shell, SWT.BORDER);
		textIRR.setBounds(1065, 70, 64, 19);
		d = npvAnalyzer.getIrr()*100;
		textIRR.setText(Formater.toShortDouble(d,2));
		
		textRentalIncomeIncrease = new Text(shell, SWT.BORDER);
		textRentalIncomeIncrease.setBounds(1065, 134, 64, 19);
		d = npvAnalyzer.getRentalIncomeIncrementInPercentage()*100;
		textRentalIncomeIncrease.setText(Formater.toShortDouble(d,2));
		
		textInflationRate = new Text(shell, SWT.BORDER);
		textInflationRate.setBounds(1065, 164, 64, 19);
		d = npvAnalyzer.getInflationRate()*100;
		textInflationRate.setText(Formater.toShortDouble(d,2));
		
		textChangeInValue = new Text(shell, SWT.BORDER);
		textChangeInValue.setBounds(1065, 194, 64, 19);
		d = npvAnalyzer.getChangeInValue()*100;
		textChangeInValue.setText(Formater.toShortDouble(d,2));
		
		textHorizon = new Text(shell, SWT.BORDER);
		textHorizon.setBounds(1065, 224, 64, 19);
		d = npvAnalyzer.getHorizon();
		textHorizon.setText(Formater.toShortDouble(d));
		
		textMaintenanceCosts = new Text(shell, SWT.BORDER);
		textMaintenanceCosts.setBounds(1065, 254, 64, 19);
		d = npvAnalyzer.getMaintenaceCostInPercentage()*100;
		textMaintenanceCosts.setText(Formater.toShortDouble(d,2));
		
		Label lblAccountNo = new Label(shell, SWT.NONE);
		lblAccountNo.setBounds(861, 40, 138, 14);
		lblAccountNo.setText("Loan Account");
		
		textAccountNo = new Text(shell, SWT.BORDER);
		textAccountNo.setBounds(1065, 40, 64, 19);
		
		Label lblNpv = new Label(shell, SWT.NONE);
		lblNpv.setBounds(861, 365, 59, 14);
		lblNpv.setText("NPV");
		
		textNPV = new Text(shell, SWT.BORDER);
		textNPV.setEditable(false);
		textNPV.setBounds(1065, 365, 90, 19);
		
		Label lblNetIncome = new Label(shell, SWT.NONE);
		lblNetIncome.setBounds(861, 394, 138, 14);
		lblNetIncome.setText("Net Income");
		
		textNetIncome = new Text(shell, SWT.BORDER);
		textNetIncome.setBounds(1065, 394, 90, 19);
		
		Label lblTransactionCosts = new Label(shell, SWT.NONE);
		lblTransactionCosts.setBounds(861, 284, 120, 14);
		lblTransactionCosts.setText("Transaction Costs");
		
		textTransactionCosts = new Text(shell, SWT.BORDER);
		textTransactionCosts.setBounds(1065, 284, 64, 19);
		d = npvAnalyzer.getTransactionCostInPercentage()*100;
		textTransactionCosts.setText(Formater.toShortDouble(d, 2));
		
		Label lblRentalIncome = new Label(shell, SWT.NONE);
		lblRentalIncome.setBounds(861, 100, 183, 14);
		lblRentalIncome.setText("Rental Income");
		
		textRentalIncomeInPercentage = new Text(shell, SWT.BORDER);
		textRentalIncomeInPercentage.setBounds(1065, 103, 64, 19);
		d = npvAnalyzer.getRentalIncomePercentage()*100;
		textRentalIncomeInPercentage.setText(Formater.toShortDouble(d, 2));
		
		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(846, 345, 339, 2);
		
		Label[] labelPercentSym = new Label[7];
		for (int i=0;i<7;i++) {
			int xCord=1130;
			labelPercentSym[i] = new Label(shell, SWT.NONE);
			if (i!=1) {
				if (i>=5) {
					labelPercentSym[i].setBounds(xCord, 72+(i+1)*30, 15, 14);
					labelPercentSym[i].setText("%");
				}
				else {
					labelPercentSym[i].setBounds(xCord, 72+i*30, 15, 14);
					labelPercentSym[i].setText("%");
				}
			}
			else {
				labelPercentSym[i].setBounds(xCord, 72+i*30, 100, 14);
				labelPercentSym[i].setText("% of Value");
			}
			
		}
		
		btnCalculate = new Button(shell, SWT.NONE);
		btnCalculate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					//get the account number
					String accountNo = textAccountNo.getText();
					npvAnalyzer.doQuery(accountNo);
					
					//get IRR
					String irrStr = textIRR.getText();
					double irr = Double.valueOf(irrStr).doubleValue()/100;
					npvAnalyzer.setIrr(irr);
					
					//get rental income
					String rentalIncomeStr = textRentalIncomeInPercentage.getText();
					double rentalIncome = Double.valueOf(rentalIncomeStr).doubleValue()/100;
					npvAnalyzer.setRentalIncomePercentage(rentalIncome);
					
					//get rental income increase
					String rentalIncomeIncreaseStr = textRentalIncomeIncrease.getText();
					double rentalIncomeIncrease = Double.valueOf(rentalIncomeIncreaseStr).doubleValue()/100;
					npvAnalyzer.setRentalIncomeIncrementInPercentage(rentalIncomeIncrease);
					
					//get inflation rate
					String inflationRateStr = textInflationRate.getText();
					double inflationRate = Double.valueOf(inflationRateStr).doubleValue()/100;
					npvAnalyzer.setInflationRate(inflationRate);
					
					//get the change in value
					String changeInValueStr = textChangeInValue.getText();
					double changeInValue = Double.valueOf(changeInValueStr).doubleValue()/100;
					npvAnalyzer.setChangeInValue(changeInValue);
					
					//get the horizon
					String horizonStr = textHorizon.getText();
					double horizon = Double.valueOf(horizonStr).doubleValue();
					npvAnalyzer.setHorizon(horizon);
					
					//get the maintenance costs
					String maintenanceCostsStr = textMaintenanceCosts.getText();
					double maintenanceCosts = Double.valueOf(maintenanceCostsStr).doubleValue()/100;
					npvAnalyzer.setMaintenaceCostInPercentage(maintenanceCosts);
					
					//get the transaction costs
					String transactionCostsStr = textTransactionCosts.getText();
					double transactionCosts = Double.valueOf(transactionCostsStr).doubleValue()/100;
					npvAnalyzer.setTransactionCostInPercentage(transactionCosts);
					
					npvAnalyzer.doCalculation();
					
					//Set result
					textNPV.setText(Formater.toCurrency(npvAnalyzer.getNpv()));
					textNetIncome.setText(Formater.toCurrency(npvAnalyzer.getNetIncome()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		
			}
		});
		btnCalculate.setBounds(960, 311, 94, 28);
		btnCalculate.setText("Calculate");
		
		btnSensitivityAnalysis = new Button(shell, SWT.NONE);
		btnSensitivityAnalysis.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				SensitivityAnalyzer sa = new SensitivityAnalyzer();
				sa.setNpvAnalyzer(npvAnalyzer);
				sa.open();
			}
		});
		btnSensitivityAnalysis.setBounds(952, 454, 177, 28);
		btnSensitivityAnalysis.setText("Sensitivity Analysis");
		
		
	}
	
	private void createMiddlePanel() {
		int numLines = 7;
		lblFinalEstimation = new Label[numLines];
		textFinalEstimation = new Text[numLines];
		textFinalEstHighInterval = new Text[numLines];
		textFinalEstLowInterval = new Text[numLines];
		
		int lblWidth = 140;
		int textWidth = 70;
		int gap=8;
		int xCord=435;
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBounds(xCord+gap+lblWidth, 10, textWidth, 18);
		label_1.setText("Estimates");
		
		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setBounds(xCord+2*gap+lblWidth+textWidth, 10, textWidth, 18);
		label_2.setText("L Bound");
		
		Label label_3 = new Label(shell, SWT.NONE);
		label_3.setBounds(xCord+3*gap+lblWidth+2*textWidth, 10, textWidth, 18);
		label_3.setText("H Bound");
		
		for (int i=0;i<numLines;i++) {
			lblFinalEstimation[i] = new Label(shell, SWT.NONE);
			lblFinalEstimation[i].setBounds(xCord, 40+i*30, lblWidth, 18);
			lblFinalEstimation[i].setText("");
					
			textFinalEstimation[i] = new Text(shell, SWT.BORDER);
			textFinalEstimation[i].setBounds(gap+xCord+lblWidth, 40+i*30, textWidth, 18);
			textFinalEstimation[i].setEditable(false);
			
			textFinalEstLowInterval[i] = new Text(shell, SWT.BORDER);
			textFinalEstLowInterval[i].setBounds(2*gap+xCord+lblWidth+textWidth, 40+i*30, textWidth, 18);
			textFinalEstLowInterval[i].setEditable(false);
			
			textFinalEstHighInterval[i] = new Text(shell, SWT.BORDER);
			textFinalEstHighInterval[i].setBounds(3*gap+xCord+lblWidth+2*textWidth, 40+i*30, textWidth, 18);
			textFinalEstHighInterval[i].setEditable(false);
		}
	}
	
	private void createSeparator() {
		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
		label_1.setBounds(425, 10, 2, 560);
		Label label_2 = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
		label_2.setBounds(830, 10, 2, 560);
	}
	
	protected void createContents() {
		shell = new Shell(SWT.CLOSE);
		shell.setSize(420, 610);
		shell.setText("Altura Consulting");
		
		createLeftPanel();
		//createMiddlePanel();
		createSeparator();
		//createRightPanel();	
	}
	
	private String getSqlStmtForMLX() {
		String sqlStmt = new String();
		boolean isStmtEmpty = true;
		
		String strZip = textZip.getText();
		String strSqftLow = textSqftLow.getText();
		String strSqftHigh = textSqftHigh.getText();
		String strPriceLow = textPriceLow.getText();
		String strPriceHigh = textPriceHigh.getText();
		
		Integer indexType = comboType.getSelectionIndex();
		Integer indexBr = comboBr.getSelectionIndex();
		Integer indexFace = comboFace.getSelectionIndex();
		Integer indexYbLow = comboYbLow.getSelectionIndex();
		Integer indexYbHigh = comboYbHigh.getSelectionIndex();
		
		//Add zipcode into search
		if (strZip.length() != 0 && strZip != null) {
			if (isStmtEmpty) {
				sqlStmt = sqlStmt + " mlx_ZIP=" + strZip;
				isStmtEmpty = false;
			}
			else {
				sqlStmt = sqlStmt + " and mlx_ZIP=" + strZip;
			}
		}
		
		//Add SqFt into search
		if (strSqftLow.length() != 0 && strSqftLow != null) {
			if (isStmtEmpty) {
				sqlStmt = sqlStmt + " mlx_LA>" + strSqftLow;
				isStmtEmpty = false;
			}
			else {
				sqlStmt = sqlStmt + " and mlx_LA>" + strSqftLow;
			}
		}
		if (strSqftHigh.length() != 0 && strSqftHigh != null) {
			if (isStmtEmpty) {
				sqlStmt = sqlStmt + " mlx_LA<" + strSqftHigh;
				isStmtEmpty = false;
			}
			else {
				sqlStmt = sqlStmt + " and mlx_LA<" + strSqftHigh;
			}
		}
		
		//Add list price into search
		if (strPriceLow.length() != 0 && strPriceLow != null) {
			if (isStmtEmpty) {
				sqlStmt = sqlStmt + " mlx_LPdollar>" + strPriceLow;
				isStmtEmpty = false;
			}
			else {
				sqlStmt = sqlStmt + " and mlx_LPdollar>" + strPriceLow;
			}
		}
		if (strPriceHigh.length() != 0 && strPriceHigh != null) {
			if (isStmtEmpty) {
				sqlStmt = sqlStmt + " mlx_LPdollar<" + strPriceHigh;
				isStmtEmpty = false;
			}
			else {
				sqlStmt = sqlStmt + " and mlx_LPdollar<" + strPriceHigh;
			}
		}
		
		//Add number of bedrooms into search
		if (indexBr == 0 || indexBr == -1) {
			//do nothing
		}
		else {
			if (isStmtEmpty) {
				sqlStmt = sqlStmt + " mlx_BEDSsharp=" + indexBr.toString();
				isStmtEmpty = false;
			}
			else {
				sqlStmt = sqlStmt + " and mlx_BEDSsharp=" + indexBr.toString();
			}
		}
		
		//Add face into search
		if (indexFace == 0 || indexFace == -1) {
			//do nothing
		}
		else {
			if (isStmtEmpty) {
				sqlStmt = sqlStmt + " mlx_FACE='" + comboFace.getItem(indexFace) + "'";
				isStmtEmpty = false;
			}
			else {
				sqlStmt = sqlStmt + " and mlx_FACE='" + comboFace.getItem(indexFace) + "'";
			}
		}
		
		//Add year built into search
		if (indexYbLow == 0 || indexYbLow == -1) {
			//do nothing
		}
		else {
			if (isStmtEmpty) {
				sqlStmt = sqlStmt + " mlx_YR>" + comboYbLow.getItem(indexYbLow);
				isStmtEmpty = false;
			}
			else {
				sqlStmt = sqlStmt + " and mlx_YR>" + comboYbLow.getItem(indexYbLow);
			}
		}

		if (indexYbHigh == 0 || indexYbHigh == -1) {
			//do nothing
		}
		else {
			if (isStmtEmpty) {
				sqlStmt = sqlStmt + " mlx_YR<" + comboYbHigh.getItem(indexYbHigh);
				isStmtEmpty = false;
			}
			else {
				sqlStmt = sqlStmt + " and mlx_YR<" + comboYbHigh.getItem(indexYbHigh);
			}
		}

		//generate final sql statement
		if (isStmtEmpty) {
			sqlStmt = "select mlx_CD, mlx_SPSqFtdollar from MLX_" + comboType.getItem(indexType) + 
					"_data where mlx_SPSqFtdollar != ''";
		}
		else {
			sqlStmt = "select mlx_CD, mlx_SPSqFtdollar from MLX_" + comboType.getItem(indexType) + 
					"_data where mlx_SPSqFtdollar != '' and " + sqlStmt;;
		}
					
		return sqlStmt;
	}
}
