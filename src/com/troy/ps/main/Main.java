package com.troy.ps.main;

import java.io.*;

public class Main {
	
	public static void main(String[] args) {
		
		PhysicsSimulator ps = new PhysicsSimulator(args);
		Thread GLThread = new Thread(ps, "Open GL Thread");
		GLThread.start();
	}

}
