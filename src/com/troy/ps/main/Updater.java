package com.troy.ps.main;

import com.troy.ps.gamestate.*;
import com.troyberry.opengl.util.*;
import com.troyberry.util.*;

public class Updater extends Thread {

	public static final double UPDATES_PER_SECOND = 100.0, TIME_PER_UPDATE = 1000000000.0 / UPDATES_PER_SECOND;

	private static Window window;
	private static volatile boolean running = false;
	private static Thread thread = null;
	
	private static double delta = 0.0;
	private static long updateTimer;

	private static int updates = 0;
	private static long now = -1L, lastTime;

	public static void init(Window window) {
		Updater.window = window;
		if (thread == null) {
			running = true;
			thread = new Updater();
			thread.start();
		}
	}

	private void update(Window window, float delta) {
		GameStateManager.update(window, delta);
		
		if (updateTimer >= 1000000000) {
			if (updates * 1.05 < (double) UPDATES_PER_SECOND) System.out.println("5% behind on update " + updates + "/" + UPDATES_PER_SECOND);
			updates = 0;
			updateTimer = 0;
		}
	}

	public void run() {
		lastTime = System.nanoTime();
		while (running) {
			now = System.nanoTime();
			long nowMinusLastTime = (now - lastTime);

			delta += nowMinusLastTime / TIME_PER_UPDATE;
			updateTimer += nowMinusLastTime;
			lastTime = now;
			
			while (delta >= 1) {
				update(window, (float) (delta / UPDATES_PER_SECOND));
				updates++;
				delta -= 1.0;
			}
		}
	}
	
	public static void cleanUp() {
		running = false;
		ThreadUtils.join(thread);
	}
}
