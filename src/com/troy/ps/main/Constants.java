package com.troy.ps.main;

import java.math.*;
import java.text.*;

public class Constants {
	private static final DecimalFormat df = new DecimalFormat("#,###,###,###.###");
	static {
		df.setRoundingMode(RoundingMode.UP);
	}
	
	
	
	//Distances
	private static final double METER = 0.001;
	
	private static final double CENTAMETER = METER / 100.0;
	private static final double MILLIMETER = METER / 1000.0;
	private static final double MICROMETER = METER / 1000000.0;
	private static final double NANOMETER = METER / 1000000000.0;


	private static final float KILOMETER = 						(float) (METER * 1000.0f);
	private static final float ASTRONOMICAL_UNIT = 				(float) (METER * 149597870700.0);
	public static final float LIGHT_YEAR = (float) (METER * 9.460731e+15);

	public static final float ONE_METER = 						(float) (METER * 1.0);
	public static final float TEN_METERS = 						(float) (METER * 10.0);
	public static final float ONE_HUNDRED_METERS = 				(float) (METER * 100.0);
	public static final float ONE_KILOMETER = 					(float) (METER * 1000.0);
	public static final float TEN_KILOMETERS = 					(float) (METER * 10000.0);
	public static final float ONE_HUNDRED_KILOMETERS = 			(float) (METER * 100000.0);
	public static final float ONE_THOUSAND_KILOMETERS = 		(float) (METER * 1000000.0);
	public static final float TEN_THOUSAND_KILOMETERS = 		(float) (METER * 10000000.0);
	public static final float ONE_HUNDRED_THOUSAND_KILOMETERS = (float) (METER * 100000000.0);
	public static final float ONE_MILLION_KILOMETERS = 			(float) (METER * 1000000000.0);
	public static final float TEN_MILLION_KILOMETERS = 			(float) (METER * 10000000000.0);
	public static final float ONE_HUNDRED_MILLION_KILOMETERS = 	(float) (METER * 100000000000.0);

	public static String getDistanceText(double distance) {
		String end;
		double multiplier;
		boolean negative = distance < 0.0;
		distance = Math.abs(distance);
		if (distance < 999 * NANOMETER) {
			end = "Nanometer";
			multiplier = 1000000000.0;
		} else if (distance < 999 * MICROMETER) {
			end = "Micrometer";
			multiplier = 1000000.0;
		} else if (distance < 10 * MILLIMETER) {
			end = "Millimeter";
			multiplier = 1000.0;
		} else if (distance < 100 * CENTAMETER) {
			end = "Centameter";
			multiplier = 100.0;
		} else if (distance < 1000 * METER) {
			end = "Meter";
			multiplier = 1.0;
		} else if (distance < 1000000000 * KILOMETER) {
			end = "Kiloeter";
			multiplier = 0.001;
		} else {
			end = "Light Year";
			multiplier = 1.0570008e-16;
		}
		double value = distance * (1 / METER) * multiplier;
		return (negative ? "-" : "") + df.format(value) + " " + end + (value != 1.0 ? "s" : "");
	}

	private Constants() {
	}

}
