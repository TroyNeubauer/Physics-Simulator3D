package utils;

import java.util.concurrent.atomic.AtomicBoolean;

import com.troy.planettest.main.Main;

import renderEngine.RenderEngine;

public class Updater implements Runnable {
	
	public static final double UPS = 2.0, NS_PER_UPS = 1000000000.0 / UPS;

	private static Thread thread;
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static volatile int currentUPS = 0;
	
	public static void init() {
		running.set(true);
		thread  = new Thread(new Updater(), "Update Thread");
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
	
	
	public static void stop(){
		running.set(false);
	}

	public void run() {
		double delta = 0;
		int updates = 0, missedUpdates;
		long now, lastTime = System.nanoTime();
		long updateTimer = 0;
		while (running.get()) {
			now = System.nanoTime();
			long difference = (now - lastTime);

			delta += (double)difference / NS_PER_UPS;
			updateTimer += difference;
			lastTime = now;

			if (delta >= 1) {
				updates++;
				delta--;
				Main.update();
			}
			if (updateTimer >= 1000000000) {
				if (updates * 1.15 < (double) UPS) System.out.println(((1.0 - updates / UPS) * 100.0) +"% behind on update " + updates + "/" + UPS);
				currentUPS = updates;
				updates = 0;
				updateTimer = 0;
			}

		}
	}

	
	
	
	private Updater() {
	}


	public static int getCurrentUPS() {
		return currentUPS;
	}
}
