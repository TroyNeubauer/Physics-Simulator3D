package com.troy.ps.main;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.plaf.*;

import com.troy.ps.gamestate.*;
import com.troy.ps.glRequestProcessing.GlRequestProcessor;
import com.troy.ps.resourceProcessing.*;
import com.troyberry.logging.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.resources.*;
import com.troyberry.opengl.util.*;
import com.troyberry.util.*;

public class PhysicsSimulator implements Runnable {

	public static final boolean DEBUG = true;
	public static final boolean DISABLE_CRASH_REPORTS = DEBUG;

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
		window = new Window();
		window.setClearColor(0, 0, 0);
		
		Mouse.init(window);
		Keyboard.init(window);

		
		GameStateManager.checkForChanges(window);
		
		Updater.init(window);
	}

	@Override
	public void run() {

		this.running = true;

		try {
			this.startGame();
		} catch (Throwable throwable) {
			this.crashReport = new CrashReport("Initializing game", throwable);
			this.displayCrashReport(crashReport);

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
			this.freeMemory();
			Log.fatal("Unreported exception thrown!\n");
			this.displayCrashReport(new CrashReport("Unexpected error", t));
		} finally {
			close();
		}

	}

	private void runGameLoop() {
		GameStateManager.checkForChanges(window);
		GameStateManager.render(window);
		window.update();
		GlRequestProcessor.dealWithTopRequests();
	}

	public void displayCrashReport(CrashReport crashReport) {
		String crash = crashReport.getCompleteReport();
		if (!DISABLE_CRASH_REPORTS) {
			try {
				File crashReportFolder = new File(System.getProperty("user.dir"), "Crash Reports");
				File reportFile = new File(crashReportFolder, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
				crashReportFolder.mkdirs();
				reportFile.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile));
				writer.write(crash);
				writer.close();
			} catch (Exception ignore) {
			}
		}
		System.err.println(crash);
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
		Updater.cleanUp();
		window.destroy();
	}

	public Window getWindow() {
		return window;
	}

}
