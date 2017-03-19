package com.troy.ps.main;

public class Options {
	
	public static boolean showOpenCLInfo = false;
	
	public static float pointSize = 3.0f;
	
	private static float mouseScaleX = 0.0f, mouseScaleY = 0.0f, globalMouseScale = 0.1f;
	private  static boolean enableIndividualAxisScrolling = false;
	
	public static float getMouseXScale() {
		if(enableIndividualAxisScrolling)return mouseScaleX;
		else return globalMouseScale;
	}
	
	public static float getMouseYScale() {
		if(enableIndividualAxisScrolling)return mouseScaleY;
		else return globalMouseScale;
	}


	public Options() {
	}

}
