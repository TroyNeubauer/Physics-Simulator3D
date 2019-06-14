package com.troy.ps.main;

public class Options {
	
	public static boolean showOpenCLInfo = false;
		
	private static float mouseScaleX = 0.2f, mouseScaleY = 0.2f, globalMouseScale = 0.2f;
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
