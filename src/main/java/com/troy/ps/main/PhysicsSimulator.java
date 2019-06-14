package com.troy.ps.main;

import static com.troy.ps.main.CommandArgsOptions.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.*;

import com.troy.ps.gamestate.GameStateManager;
import com.troy.ps.glRequestProcessing.GlRequestProcessor;
import com.troyberry.logging.Log;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;
import com.troyberry.resources.ResourceProcessor;
import com.troyberry.util.*;

public class PhysicsSimulator implements Runnable {

	private static Logger logger = LogManager.getLogger(PhysicsSimulator.class);
	public static final boolean DEBUG = true;
	public static final boolean DISABLE_CRASH_REPORTS = DEBUG;

	/** A 100MB preallocation to ensure the heap is reasonably sized. */
	public static byte[] memoryReserve = new byte[BinarySize.MEGABYTE.getIntValue() * 100];

	private Window window;
	private boolean running;
	private boolean hasCrashed;
	private CrashReport crashReport;
	private CommandLine options;

	public PhysicsSimulator(String[] args) throws ParseException {
		this.options = new DefaultParser().parse(new CommandArgsOptions(), args);
	}

	/**
	 * Starts the game: initializes the canvas, the title, the settings, etc.
	 */
	public void startGame() throws Exception {
		logger.info("Starting Game");
		VersionManager.setVersion(new Version());
		GLUtil.init();
		createWindow();
		window.setClearColor(1, 1, 1);
		window.enableFPSInTitle();
		window.center();
		window.show();
		Mouse.init(window);
		Keyboard.init(window);

		GameStateManager.checkForChanges(window);

		Updater.init(window);
	}

	private void createWindow() {
		int width = 900, height = 540;
		boolean fullscreen = options.hasOption(FULLSCREEN);
		if (!fullscreen) {// If we don't want fullscreen
			if (options.hasOption(WIDTH) && options.hasOption(HEIGHT)) {
				width = MiscUtil.getIntOrDefaultValue(options.getOptionValue(WIDTH), "Invalid width!", width);
				height = MiscUtil.getIntOrDefaultValue(options.getOptionValue(HEIGHT), "Invalid height!", height);
			} else if (options.hasOption(WIDTH) || options.hasOption(HEIGHT))
				System.out.println("Only one window dimension provided! Ignoring "
						+ (options.hasOption(WIDTH) ? "width " + options.getOptionValue(WIDTH)
								: "height " + options.getOptionValue(HEIGHT)));
			window = new Window(width, height);

		} else window = new Window();// Create the fullscreen window

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
						GLUtil.checkForErrors("Game Loop");
					} catch (OutOfMemoryError e) {
						freeMemory();
						Log.error("Out of memory!!!\n Try running with the VM arg \"-Xss1G\" \n");
						e.printStackTrace();
						// TODO: show out of memory screen
						System.exit(1);
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

	private int loop = 0;

	private void runGameLoop() {

		window.clear();
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
				File reportFile = new File(crashReportFolder,
						"crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
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
		// TODO: other memory stuff later
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
