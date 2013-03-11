package ops;

import ops.DB;
import java.sql.*;
import java.lang.Math;

public class NpvAnalyzer {
	private ResultSet rs;
	private String sqlStmt;
	
	/*Julia's version*/
	private double irr = 0.18;
	private double rentalIncomePercentage = 0.1; //rental income (as % of value)
	private double rentalIncomeIncrementInPercentage = 0.05;
	private double inflationRate = 0.02;
	private double changeInValue = 0.15;
	private double horizon = 10;
	private double maintenaceCostInPercentage = 0.07;
	private double transactionCostInPercentage = 0.08; 
	private double appraiserFMV;
	
	private double baseRentalIncomePerYear = 0;
	private double rentalIncomeOverThePeriod = 0;
	private double maintenanceCostOverThePeriod = 0;
	private double fmvAtTheEndOfPeriod = 0;
	private double transactionCost = 0;
	private double npv = 0;
	private double netIncome = 0;
	
	public NpvAnalyzer() {
		
	}
	
	public NpvAnalyzer(String sqlStmt) {
		DB dbConn = new DB();
		rs = dbConn.connect(sqlStmt);
	}

	
	public void doCalculation() {
		//Calculate the base rental income per year
		baseRentalIncomePerYear = rentalIncomePercentage*appraiserFMV;
		
		//Calculate the rental income over the period
		rentalIncomeOverThePeriod = baseRentalIncomePerYear*(1-Math.pow((1+rentalIncomeIncrementInPercentage)/(1+irr), horizon))/(irr - rentalIncomeIncrementInPercentage);
		
		//Calculate the maintenance cost over the period
		maintenanceCostOverThePeriod = appraiserFMV*maintenaceCostInPercentage*(1-Math.pow((1+inflationRate)/(1+irr), horizon))/(irr-inflationRate);
		
		//Calculate the FMV at the end of the period
		fmvAtTheEndOfPeriod = appraiserFMV*(1+changeInValue);
		
		//Calculate the transaction cost
		transactionCost = transactionCostInPercentage * fmvAtTheEndOfPeriod;
		
		//Calculate the NPV
		npv = fmvAtTheEndOfPeriod/Math.pow(1+irr, horizon)+rentalIncomeOverThePeriod-maintenanceCostOverThePeriod-transactionCost/Math.pow(1+irr, horizon);
		
		//Calculate the net income
		netIncome = baseRentalIncomePerYear*Math.pow(1+rentalIncomeIncrementInPercentage, horizon+1)/(irr-rentalIncomeIncrementInPercentage) - appraiserFMV*maintenaceCostInPercentage*Math.pow(1+inflationRate, 1+horizon)/(irr - inflationRate);	
	}
	
	public void doQuery(String accountNo)  {
		DB dbConnARC = new DB();
		
		sqlStmt = "select arc_AppraiserFMV from ARC_CU_Data where arc_Account = '" + accountNo + "'";
		System.out.println(sqlStmt);
		rs = dbConnARC.connect(sqlStmt);
		
		String apprasierFMVStr;
		try {
			if (rs.next()) {
				apprasierFMVStr = rs.getString(1);
				
				try {
					appraiserFMV = Double.valueOf(apprasierFMVStr).doubleValue();
				} catch (Exception e) {
					appraiserFMV = 0;
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getIrr() {
		return irr;
	}

	public void setIrr(double irr) {
		this.irr = irr;
	}

	public double getRentalIncomePercentage() {
		return rentalIncomePercentage;
	}

	public void setRentalIncomePercentage(double rentalIncomePercentage) {
		this.rentalIncomePercentage = rentalIncomePercentage;
	}

	public double getRentalIncomeIncrementInPercentage() {
		return rentalIncomeIncrementInPercentage;
	}

	public void setRentalIncomeIncrementInPercentage(
			double rentalIncomeIncrementInPercentage) {
		this.rentalIncomeIncrementInPercentage = rentalIncomeIncrementInPercentage;
	}

	public double getInflationRate() {
		return inflationRate;
	}

	public void setInflationRate(double inflationRate) {
		this.inflationRate = inflationRate;
	}

	public double getChangeInValue() {
		return changeInValue;
	}

	public void setChangeInValue(double changeInValue) {
		this.changeInValue = changeInValue;
	}

	public double getHorizon() {
		return horizon;
	}

	public void setHorizon(double horizon) {
		this.horizon = horizon;
	}

	public double getMaintenaceCostInPercentage() {
		return maintenaceCostInPercentage;
	}

	public void setMaintenaceCostInPercentage(double maintenaceCostInPercentage) {
		this.maintenaceCostInPercentage = maintenaceCostInPercentage;
	}

	public double getTransactionCostInPercentage() {
		return transactionCostInPercentage;
	}

	public void setTransactionCostInPercentage(double transactionCostInPercentage) {
		this.transactionCostInPercentage = transactionCostInPercentage;
	}

	public double getAppraiserFMV() {
		return appraiserFMV;
	}

	public void setAppraiserFMV(double appraiserFMV) {
		this.appraiserFMV = appraiserFMV;
	}

	public double getBaseRentalIncomePerYear() {
		return baseRentalIncomePerYear;
	}

	public void setBaseRentalIncomePerYear(double baseRentalIncomePerYear) {
		this.baseRentalIncomePerYear = baseRentalIncomePerYear;
	}

	public double getRentalIncomeOverThePeriod() {
		return rentalIncomeOverThePeriod;
	}

	public void setRentalIncomeOverThePeriod(double rentalIncomeOverThePeriod) {
		this.rentalIncomeOverThePeriod = rentalIncomeOverThePeriod;
	}

	public double getMaintenanceCostOverThePeriod() {
		return maintenanceCostOverThePeriod;
	}

	public void setMaintenanceCostOverThePeriod(double maintenanceCostOverThePeriod) {
		this.maintenanceCostOverThePeriod = maintenanceCostOverThePeriod;
	}

	public double getFmvAtTheEndOfPeriod() {
		return fmvAtTheEndOfPeriod;
	}

	public void setFmvAtTheEndOfPeriod(double fmvAtTheEndOfPeriod) {
		this.fmvAtTheEndOfPeriod = fmvAtTheEndOfPeriod;
	}

	public double getTransactionCost() {
		return transactionCost;
	}

	public void setTransactionCost(double transactionCost) {
		this.transactionCost = transactionCost;
	}

	public double getNpv() {
		return npv;
	}

	public void setNpv(double npv) {
		this.npv = npv;
	}

	public double getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(double netIncome) {
		this.netIncome = netIncome;
	}

	
}
