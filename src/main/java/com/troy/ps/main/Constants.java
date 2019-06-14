package com.troy.ps.main;

import java.math.*;
import java.text.*;

import com.troyberry.math.*;

public class Constants {
	private static final DecimalFormat df = new DecimalFormat("#,###,###,###.###");
	static {
		df.setRoundingMode(RoundingMode.UP);
	}

	// Distances
	//format:off
	private static final double METER = 10;//Only hardcoded value
	private static final double LIGHT_YEAR = 							 METER * 9.460731e+15;

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
	
	public static final float ONE_HUNDRED_CENTIMETERS = 		(float) (METER / 1.0);
	public static final float TEN_CENTIMETERS = 				(float) (METER / 10.0);
	public static final float ONE_CENTIMETER = 					(float) (METER / 100.0);
	public static final float ONE_MILLIMETER = 					(float) (METER / 1000.0);
	public static final float ONE_HUNDRED_MICROMETERS = 		(float) (METER / 10000.0);
	public static final float TEN_MICROMETERS = 				(float) (METER / 100000.0);
	public static final float ONE_MICROMETER = 					(float) (METER / 1000000.0);
	public static final float ONE_HUNDRED_NANOMETERS= 			(float) (METER / 10000000.0);
	public static final float TEN_NANOMETERS = 					(float) (METER / 100000000.0);
	public static final float ONE_NANOMETER = 					(float) (METER / 1000000000.0);
	
	public static final double PLANK_LENGTH = 					METER * 1.61622938e-35;//Double because the value is so small...
	
	public static final float ONE_LIGHT_YEAR = 					(float) (LIGHT_YEAR * 1.0);
	public static final float TEN_LIGHT_YEARS = 				(float) (LIGHT_YEAR * 10.0);
	public static final float ONE_HUNDRED_LIGHT_YEARS = 		(float) (LIGHT_YEAR * 100.0);
	//format:on

	public static String getSpeed(Vector3d vec, String unit) {
		return getSpeed(vec.length(), unit);
	}

	public static String getSpeed(Vector3f vec, String unit) {
		return getSpeed(StrictMath.sqrt(vec.lengthSquared()), unit);// Don't call length() because the cast to float looses precision
	}

	public static String getSpeed(double speed, String unit) {
		return getDistanceText(speed) + " per " + unit;
	}

	public static String getDistanceText(double distance) {
		String end;
		double multiplier;
		boolean negative = distance < 0.0;
		distance = Math.abs(distance);
		if (distance < 999 * ONE_NANOMETER) {
			end = "Nanometer";
			multiplier = 1000000000.0;
		} else if (distance < 999 * ONE_MICROMETER) {
			end = "Micrometer";
			multiplier = 1000000.0;
		} else if (distance < 10 * ONE_MILLIMETER) {
			end = "Millimeter";
			multiplier = 1000.0;
		} else if (distance < 100 * ONE_CENTIMETER) {
			end = "Centameter";
			multiplier = 100.0;
		} else if (distance < 1000 * ONE_METER) {
			end = "Meter";
			multiplier = 1.0;
		} else if (distance < 1000000000 * ONE_KILOMETER) {
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
