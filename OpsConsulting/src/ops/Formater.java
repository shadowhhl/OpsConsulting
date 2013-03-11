package ops;

import java.text.NumberFormat;
import java.util.Locale;

public class Formater {
	static public String toShortDouble(double d) {
		if (Double.isNaN(d)) {
			return "NaN";
		}
		else {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(1);
			return nf.format(d);
		}
	}
	
	static public String toShortDouble(double d, int franctionDigits) {
		if (Double.isNaN(d)) {
			return "NaN";
		}
		else {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(franctionDigits);
			nf.setMinimumFractionDigits(franctionDigits);
			return nf.format(d);
		}
	}
	
	static public String toCurrency(double d) {
		if (Double.isNaN(d)) {
			return "NaN";
		}
		else {
			NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
			return nf.format(d);
		}
		
	}
	
	static public String toPercentage(double d) {
		if (Double.isNaN(d)) {
			return "NaN";
		}
		else {
			NumberFormat nf = NumberFormat.getPercentInstance(Locale.US);
			return nf.format(d);
		}
	}
}
