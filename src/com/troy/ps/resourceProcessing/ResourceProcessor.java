package com.troy.ps.resourceProcessing;

import java.util.concurrent.atomic.*;

public class ResourceProcessor extends Thread {

	private static ResourceProcessor processor = new ResourceProcessor();

	private volatile ResourceQueue requestQueue = new ResourceQueue();
	private volatile AtomicBoolean running = new AtomicBoolean(true);
	
	/**
	 * Sends a new Request for the resource processor to execute immediately 
	 * @param request The request to send
	 */
	public static void sendRequest(ResourceRequest request) {
		processor.addRequestToQueue(request);
	}

	/**
	 * Indicates to the ResourceProcessor that it needs to stop. It will complete some of the last requests before terminating.<br>
	 * This method will wait for the ResourceProcessor to completely finish.
	 */
	public static void cleanUp() {
		processor.kill();
		try {
			processor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void run() {
		while (running.get() || requestQueue.hasRequests()) {
			if (requestQueue.hasRequests()) {
				requestQueue.acceptNextRequest().doResourceRequest();
			} else {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void kill() {
		running.set(false);
		indicateNewRequests();
	}

	private synchronized void indicateNewRequests() {
		notify();
	}

	private ResourceProcessor() {
		this.start();
	}

	private void addRequestToQueue(ResourceRequest request) {
		boolean isPaused = !requestQueue.hasRequests();
		requestQueue.addRequest(request);
		if (isPaused) {
			indicateNewRequests();
		}
	}

}
