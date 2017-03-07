package com.troy.ps.main;

public class Constants {
	//Distances
	private static final float METER = 0.000001f;
	private static final float CENTAMETER = METER / 1000.0f;
	private static final float KILOMETER = METER * 1000.0f;
	private static final float ASTRONOMICAL_UNIT = METER * 149597870700.0f;
	private static final float LIGHT_YEAR = ASTRONOMICAL_UNIT * 63241f;

	public static final float ONE_METER = METER;
	public static final float TEN_METERS = METER * 10.0f;
	public static final float ONE_HUNDRED_METERS = METER * 100.0f;

	public static final float ONE_KILOMETER = KILOMETER;
	public static final float TEN_KILOMETERS = KILOMETER * 10.0f;
	public static final float ONE_HUNDRED_KILOMETERS = KILOMETER * 100.0f;

	public static final float ONE_THOUSAND_KILOMETERS = KILOMETER * 1000.0f;
	public static final float TEN_THOUSAND_KILOMETERS = KILOMETER * 10000.0f;
	public static final float ONE_HUNDRED_THOUSAND_KILOMETERS = KILOMETER * 100000.0f;

	private Constants() {
	}

}
