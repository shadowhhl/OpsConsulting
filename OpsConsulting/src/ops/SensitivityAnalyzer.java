package ops;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;

public class SensitivityAnalyzer{

	protected Shell shell;
    private NpvAnalyzer npvAnalyzer;
    
	public SensitivityAnalyzer() {
		
	}

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
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
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.CLOSE);
		shell.setSize(700, 250);
		shell.setText("Sensitivity Analysis");
		
		final Combo combo = new Combo(shell, SWT.NONE);
		combo.setItems(new String[] {"IRR vs. Horizon Period", "IRR vs. Rental Income Increase", "Horizon Period vs. Rental Income Increase", "Change in Value vs. Rental Income Increase", "Change in Value vs. IRR"});
		combo.setBounds(160, 10, 270, 28);
		
		//Draw table
		int leftPanelWidth = 100;
		int width = 85;
		int height = 20;
		int gap = 15;
		int halfWidth = 40;
		int ox = 10;
		int oy = 40;
		int tablex = 0;
		int tabley = 0;
		
		tablex = ox+leftPanelWidth+halfWidth+gap;
		tabley = oy+2*height+gap;
		
		final Label lblColumnTitle = new Label(shell, SWT.NONE);
		lblColumnTitle.setAlignment(SWT.CENTER);
		lblColumnTitle.setBounds(tablex, oy, 6*width, height);
		//lblColumnTitle.setText("Horizon");
		
		final Label[] lblColumns = new Label[6];
		for (int i=0;i<6;i++) {
			lblColumns[i] = new Label(shell, SWT.NONE);
			lblColumns[i].setAlignment(SWT.CENTER);
			lblColumns[i].setBounds(tablex+i*width, oy+height, width, height);
			//lblColumns[i].setText("Column "+i);
		}
		
		final Label lblRowTitle = new Label(shell, SWT.NONE);
		lblRowTitle.setAlignment(SWT.CENTER);
		lblRowTitle.setBounds(ox, tabley+2*height, leftPanelWidth, 6*height);
		//lblRowTitle.setText("Rental Income");
		
		final Label[] lblRows = new Label[6];
		for (int i=0;i<6;i++) {
			lblRows[i] = new Label(shell, SWT.NONE);
			lblRows[i].setAlignment(SWT.CENTER);
			lblRows[i].setBounds(ox+leftPanelWidth, tabley+i*height, halfWidth, height);
			//lblRows[i].setText("Row " + i);
		}
		
		final Text[][] textResult = new Text[6][6];
		for (int i=0;i<6;i++) {
			for (int j=0;j<6;j++) {
				textResult[i][j] = new Text(shell, SWT.NONE);
				textResult[i][j].setEditable(false);
				textResult[i][j].setBounds(tablex+j*width, tabley+i*height, width, height);
			}
		}
		
		Button btnGo = new Button(shell, SWT.NONE);
		btnGo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				int index = combo.getSelectionIndex();
				switch (index) {
				case 0: {
					lblColumnTitle.setText("IRR");
					lblRowTitle.setText("Horizon Period");
					double cIrr = npvAnalyzer.getIrr();
					double sIrr = 0;
					double irrLeap = 0.02;
					//Deal with negative values
					if (cIrr > 3*irrLeap) {
						sIrr = cIrr - 3*irrLeap;
					}
					else if (cIrr > 2*irrLeap) {
						sIrr = cIrr - 2*irrLeap;
					}
					else if (cIrr > irrLeap) {
						sIrr = cIrr - irrLeap;
					}
					else 
						sIrr = cIrr;
					
					double cHorizon = npvAnalyzer.getHorizon();
					double sHorizon = 0;
					double horizonLeap = 2;
					if (cHorizon > 3*horizonLeap) {
						sHorizon = cHorizon - 3*horizonLeap;
					}
					else if (cHorizon > 2*horizonLeap) {
						sHorizon = cHorizon - 2*horizonLeap;
					}
					else if (cHorizon > horizonLeap) {
						sHorizon = cHorizon - horizonLeap;
					}
					else 
						sHorizon = cHorizon;
					
					for (int i=0;i<6;i++) {
						for (int j=0;j<6;j++) {
							npvAnalyzer.setIrr(sIrr+j*irrLeap);
							npvAnalyzer.setHorizon(sHorizon+i*horizonLeap);
							npvAnalyzer.doCalculation();
							textResult[i][j].setText(Formater.toCurrency(npvAnalyzer.getNpv()));
						}
					}
					
					for (int i=0;i<6;i++) {
						lblColumns[i].setText(Formater.toPercentage(sIrr+i*irrLeap));
						lblRows[i].setText(Formater.toShortDouble(sHorizon+i*horizonLeap));
					}
					
					npvAnalyzer.setIrr(cIrr);
					npvAnalyzer.setHorizon(cHorizon);
					break;
				}
				case 1: {
					lblColumnTitle.setText("IRR");
					lblRowTitle.setText("Rental Income\nIncrease per\nYear");
					double cIrr = npvAnalyzer.getIrr();
					double sIrr = 0;
					double irrLeap = 0.02;
					//Deal with negative values
					if (cIrr > 3*irrLeap) {
						sIrr = cIrr - 3*irrLeap;
					}
					else if (cIrr > 2*irrLeap) {
						sIrr = cIrr - 2*irrLeap;
					}
					else if (cIrr > irrLeap) {
						sIrr = cIrr - irrLeap;
					}
					else 
						sIrr = cIrr;
					
					double cRentaIncrease = npvAnalyzer.getRentalIncomeIncrementInPercentage();
					double sRentaIncrease = 0;
					double rentalIncreaseLeap = 0.01;
					if (cRentaIncrease > 3*rentalIncreaseLeap) {
						sRentaIncrease = cRentaIncrease - 3*rentalIncreaseLeap;
					}
					else if (cRentaIncrease > 2*rentalIncreaseLeap) {
						sRentaIncrease = cRentaIncrease - 2*rentalIncreaseLeap;
					}
					else if (cRentaIncrease > rentalIncreaseLeap) {
						sRentaIncrease = cRentaIncrease - rentalIncreaseLeap;
					}
					else 
						sRentaIncrease = cRentaIncrease;
					
					for (int i=0;i<6;i++) {
						for (int j=0;j<6;j++) {
							npvAnalyzer.setIrr(sIrr+j*irrLeap);
							npvAnalyzer.setRentalIncomeIncrementInPercentage(sRentaIncrease+i*rentalIncreaseLeap);
							npvAnalyzer.doCalculation();
							textResult[i][j].setText(Formater.toCurrency(npvAnalyzer.getNpv()));
						}
					}
					
					for (int i=0;i<6;i++) {
						lblColumns[i].setText(Formater.toPercentage(sIrr+i*irrLeap));
						lblRows[i].setText(Formater.toPercentage(sRentaIncrease+i*rentalIncreaseLeap));
					}
					
					npvAnalyzer.setIrr(cIrr);
					npvAnalyzer.setRentalIncomeIncrementInPercentage(cRentaIncrease);
					break;
				}
				case 2: {
					lblColumnTitle.setText("Horizon Period");
					lblRowTitle.setText("Rental Income\nIncrease per\nYear");
					
					double cHorizon = npvAnalyzer.getHorizon();
					double sHorizon = 0;
					double horizonLeap = 2;
					if (cHorizon > 3*horizonLeap) {
						sHorizon = cHorizon - 3*horizonLeap;
					}
					else if (cHorizon > 2*horizonLeap) {
						sHorizon = cHorizon - 2*horizonLeap;
					}
					else if (cHorizon > horizonLeap) {
						sHorizon = cHorizon - horizonLeap;
					}
					else 
						sHorizon = cHorizon;
					
					double cRentaIncrease = npvAnalyzer.getRentalIncomeIncrementInPercentage();
					double sRentaIncrease = 0;
					double rentalIncreaseLeap = 0.01;
					if (cRentaIncrease > 3*rentalIncreaseLeap) {
						sRentaIncrease = cRentaIncrease - 3*rentalIncreaseLeap;
					}
					else if (cRentaIncrease > 2*rentalIncreaseLeap) {
						sRentaIncrease = cRentaIncrease - 2*rentalIncreaseLeap;
					}
					else if (cRentaIncrease > rentalIncreaseLeap) {
						sRentaIncrease = cRentaIncrease - rentalIncreaseLeap;
					}
					else 
						sRentaIncrease = cRentaIncrease;
					
					for (int i=0;i<6;i++) {
						for (int j=0;j<6;j++) {
							npvAnalyzer.setHorizon(sHorizon+j*horizonLeap);
							npvAnalyzer.setRentalIncomeIncrementInPercentage(sRentaIncrease+i*rentalIncreaseLeap);
							npvAnalyzer.doCalculation();
							textResult[i][j].setText(Formater.toCurrency(npvAnalyzer.getNpv()));
						}
					}
					
					for (int i=0;i<6;i++) {
						lblRows[i].setText(Formater.toPercentage(sRentaIncrease+i*rentalIncreaseLeap));
						lblColumns[i].setText(Formater.toShortDouble(sHorizon+i*horizonLeap));
					}
					
					npvAnalyzer.setHorizon(cHorizon);
					npvAnalyzer.setRentalIncomeIncrementInPercentage(cRentaIncrease);
					
					break;
				}
				case 3: {
					lblColumnTitle.setText("Change in Value over the Period");
					lblRowTitle.setText("Rental Income\nIncrease per\nYear");
					
					double cChange = npvAnalyzer.getChangeInValue();
					double sChange = 0;
					double changeLeap = 0.05;
					sChange = cChange - 3*changeLeap;
					
					double cRentaIncrease = npvAnalyzer.getRentalIncomeIncrementInPercentage();
					double sRentaIncrease = 0;
					double rentalIncreaseLeap = 0.01;
					if (cRentaIncrease > 3*rentalIncreaseLeap) {
						sRentaIncrease = cRentaIncrease - 3*rentalIncreaseLeap;
					}
					else if (cRentaIncrease > 2*rentalIncreaseLeap) {
						sRentaIncrease = cRentaIncrease - 2*rentalIncreaseLeap;
					}
					else if (cRentaIncrease > rentalIncreaseLeap) {
						sRentaIncrease = cRentaIncrease - rentalIncreaseLeap;
					}
					else 
						sRentaIncrease = cRentaIncrease;
					
					for (int i=0;i<6;i++) {
						for (int j=0;j<6;j++) {
							npvAnalyzer.setChangeInValue(sChange+j*changeLeap);
							npvAnalyzer.setRentalIncomeIncrementInPercentage(sRentaIncrease+i*rentalIncreaseLeap);
							npvAnalyzer.doCalculation();
							textResult[i][j].setText(Formater.toCurrency(npvAnalyzer.getNpv()));
						}
					}
					
					for (int i=0;i<6;i++) {
						lblColumns[i].setText(Formater.toPercentage(sChange+i*changeLeap));
						lblRows[i].setText(Formater.toPercentage(sRentaIncrease+i*rentalIncreaseLeap));
					}
					
					npvAnalyzer.setChangeInValue(cChange);
					npvAnalyzer.setRentalIncomeIncrementInPercentage(cRentaIncrease);
					
					break;
				}
				case 4: {
					lblColumnTitle.setText("Change in Value over the Period");
					lblRowTitle.setText("IRR");
					
					double cChange = npvAnalyzer.getChangeInValue();
					double sChange = 0;
					double changeLeap = 0.05;
					sChange = cChange - 3*changeLeap;
					
					double cIrr = npvAnalyzer.getIrr();
					double sIrr = 0;
					double irrLeap = 0.02;
					//Deal with negative values
					if (cIrr > 3*irrLeap) {
						sIrr = cIrr - 3*irrLeap;
					}
					else if (cIrr > 2*irrLeap) {
						sIrr = cIrr - 2*irrLeap;
					}
					else if (cIrr > irrLeap) {
						sIrr = cIrr - irrLeap;
					}
					else 
						sIrr = cIrr;
					
					for (int i=0;i<6;i++) {
						for (int j=0;j<6;j++) {
							npvAnalyzer.setChangeInValue(sChange+j*changeLeap);
							npvAnalyzer.setIrr(sIrr+i*irrLeap);
							npvAnalyzer.doCalculation();
							textResult[i][j].setText(Formater.toCurrency(npvAnalyzer.getNpv()));
						}
					}
					
					for (int i=0;i<6;i++) {
						lblColumns[i].setText(Formater.toPercentage(sChange+i*changeLeap));
						lblRows[i].setText(Formater.toPercentage(sIrr+i*irrLeap));
					}
					
					npvAnalyzer.setChangeInValue(cChange);
					npvAnalyzer.setIrr(cIrr);
					break;
				}
				}
			}
		});
		btnGo.setBounds(440, 10, 62, 28);
		btnGo.setText("Go");
	}

	public NpvAnalyzer getNpvAnalyzer() {
		return npvAnalyzer;
	}

	public void setNpvAnalyzer(NpvAnalyzer npvAnalyzer) {
		this.npvAnalyzer = npvAnalyzer;
	}
}
