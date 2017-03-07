package com.troy.ps.main;


import java.io.*;
import java.text.*;
import java.util.*;

import com.troy.ps.gamestate.*;
import com.troy.ps.glRequestProcessing.GlRequestProcessor;
import com.troy.ps.resourceProcessing.*;
import com.troyberry.logging.*;
import com.troyberry.math.*;
import com.troyberry.opengl.resources.*;
import com.troyberry.opengl.util.*;
import com.troyberry.util.*;

public class PhysicsSimulator implements Runnable {
	/** A 10MiB preallocation to ensure the heap is reasonably sized. */
	public static byte[] memoryReserve = new byte[Maths.pow(2, 10) * Maths.pow(2, 10) * 10];

	private Window window;
	private boolean running;
	private boolean hasCrashed;
	private CrashReport crashReport;

	public PhysicsSimulator(String[] args) {

	}

	/**
	 * Starts the game: initializes the canvas, the title, the settings, etc.
	 */
	public void startGame() throws Exception {
		GLUtil.init();
		VersionManager.setVersion(new Version());
		window = new Window(1440, 810);
		
		window.setClearColor(0, 0, 0);
		OpenCLManager.create();
		GameStateManager.update();
		OpenCLManager.forceUpdate();
	}

	@Override
	public void run() {

		this.running = true;
		CrashReport crashreport;

		try {
			this.startGame();
		} catch (Throwable throwable) {
			crashreport = new CrashReport("Initializing game", throwable);
			this.displayCrashReport(crashreport);
			return;
		}

		try {
			while (this.running) {
				if (!this.hasCrashed || this.crashReport == null) {
					try {
						this.runGameLoop();
					} catch (OutOfMemoryError e) {
						this.freeMemory();
						Log.error("Out of memory!!!\n" + e);
						//TODO: show out of memory screen 
						System.gc();
					}
					if (window.isCloseRequested()) break;
					continue;
				}

				this.displayCrashReport(this.crashReport);
				return;
			}
		} catch (Throwable t) {
			crashreport = new CrashReport("Unexpected error", t);
			this.freeMemory();
			Log.fatal("Unreported exception thrown!\n" + t);
			this.displayCrashReport(crashreport);
		} finally {
			close();
		}

	}

	private void runGameLoop() {
		OpenCLManager.update();
		GameStateManager.update();
		GameStateManager.render();
		window.update();
		GlRequestProcessor.dealWithTopRequests();
	}

	public void displayCrashReport(CrashReport report) {
		File crashReportFolder = new File("./crash-reports");
		File reportFile = new File(crashReportFolder, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()));
		report.print();
	}

	public void freeMemory() {
		memoryReserve = new byte[0];
		//TODO: other memory stuff later
		System.gc();
	}

	public void crashed(CrashReport crash) {
		this.hasCrashed = true;
		this.crashReport = crash;
	}

	public void close() {
		window.hide();
		GameStateManager.cleanUp();
		GlRequestProcessor.completeAllRequests();
		ResourceProcessor.cleanUp();
		window.destroy();
	}

	public Window getWindow() {
		return window;
	}

}

