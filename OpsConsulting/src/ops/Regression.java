package ops;

import java.lang.Math;
import java.util.ArrayList;

import JSci.maths.statistics.TDistribution;

public class Regression {
	private double a;
	private double b;
	private double R2;
	private double x0;
	private double ci; //confidence interval
	public int count;
	private double SSX=0;
	private double x_avg=0;
	private double SYX = 0;
	
	public boolean reg(ArrayList<Double> x_data,
			ArrayList<Double> y_data) {
		if (x_data.size() != y_data.size()) {
			return false;
		} 
		else {	
			int n = x_data.size();
			count = n;
			System.out.println(count);
			double xy=0, x=0, y=0, xx=0;
			for (int i=0;i<n;i++) {
				xy+=x_data.get(i)*y_data.get(i);
				x+=x_data.get(i);
				y+=y_data.get(i);
				xx+=Math.pow(x_data.get(i), 2);
			}
			
			b = (xy-x*y/n)/(xx-x*x/n);
			a = (y-b*x)/n;
			
			double SStot = 0, SSerr = 0;
			double y_avg = y/n;
			x_avg = x/n;
			
			for (int i=0;i<n;i++) {
				SSX+=Math.pow((x_data.get(i)-x_avg), 2);
				SStot+=Math.pow((y_data.get(i)-y_avg), 2);
				SSerr+=Math.pow((y_data.get(i)-a-b*x_data.get(i)), 2);
			}
			R2 = 1-SSerr/SStot;

			{
				double temp1=0;
				double temp2=0;
				double temp3=0;
				for (int i=0;i<n;i++) {
					temp1+=(x_data.get(i)-x_avg)*(y_data.get(i)-y_avg);
					temp2+=Math.pow((x_data.get(i)-x_avg), 2);
					temp3+=Math.pow((y_data.get(i)-y_avg), 2);
				}
				temp1=Math.pow(temp1, 2);
				SYX=temp3-temp1/temp2;
			}
			
			SYX=Math.sqrt(SYX/(n-2));
			return true;
		}
	}
	
	public double getCi(double x) {
		int df = count-2;
		double p = 0.05;
		TDistribution k = new TDistribution(df);
		double t = k.inverse(1-p/2);
		System.out.println("df=" + df + "t=: " + t);
		ci = t*SYX*Math.sqrt(1/count+(x-x_avg)*(x-x_avg)/SSX);
		
		return ci;
	}
	
	public double getA() {
		return a;
	}
	public double getB() {
		return b;
	}
	public double getR2() {
		return R2;
	}

	public double getX0() {
		return x0;
	}

	public void setX0(double x0) {
		this.x0 = x0;
	}
	
	public double getY0() {
		return a+b*x0;
	}
}
