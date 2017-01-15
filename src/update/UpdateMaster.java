package update;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

import com.troyberry.math.Maths;
import com.troyberry.util.data.UpdateTimeStamp;
import com.troyberry.util.data.UpdateTimeStampManager;
import com.troyberry.util.thread.Executor;

import world.World;


public class UpdateMaster implements Runnable {
	public static final double UPS = 2.5, NS_PER_UPS = 1000000000.0 / UPS;
	
	private static Thread thread;
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static volatile int currentUPS = 0;
	private static World world;
	private static int workerThreads;
	private static Executor executor;
	private static UpdateTimeStampManager manager;
	
	public static void init(World world) {
		int cores = Runtime.getRuntime().availableProcessors();
		workerThreads = Math.max(1, cores - 2);// Leave 1 core for the rendering and 1 core for java / the OS
		UpdateMaster.world = world;
		manager = new UpdateTimeStampManager();
		executor = new Executor(2, "Threads for sphere calculations");
		thread = new Thread(new UpdateMaster(), "Update Manager Thread");
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
	
	private void update() {

		executor.update();
		int spheresPerWorker = Maths.ceil((double)world.getSphereCount() / (double)workerThreads);
		int lastEnd = 0;
		for(int i = 0; i < workerThreads; i++) {
			int startIndex = lastEnd;
			int endIndex = i * (spheresPerWorker - 1) + spheresPerWorker;
			lastEnd = endIndex;
			if(i == workerThreads - 1){
				endIndex = world.getSphereCount();
			}
			executor.submitTask(new Processor(startIndex,endIndex, world.getActualObjectsArray(), world));
			
		}
		executor.update();
		executor.waitForTasksAllTasksToComplete();
		world.update();
	}

	public void run() {
		double delta = 0.0, missedTime = 0.0;
		int updates = 0;
		long now, lastTime = System.nanoTime();
		long timer = 0;
		running.set(true);
		while (running.get()) {
			now = System.nanoTime();
			long nowMinusLastTime = (now - lastTime);

			delta += nowMinusLastTime / NS_PER_UPS;
			timer += nowMinusLastTime;
			lastTime = now;
			
			if (delta >= 1) {
				update();
				new UpdateTimeStamp(manager, now, UPS);
				updates++;
				delta--;
			}
			if(delta > 5.0){
				missedTime += delta - 5.0;
				delta = 5.0;
			}
		}
	}
	
	private UpdateMaster() {
	}


	public static int getCurrentUPS() {
		return currentUPS;
	}

	public static void shutdown() {
		running.set(false);
		executor.shutdown();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
