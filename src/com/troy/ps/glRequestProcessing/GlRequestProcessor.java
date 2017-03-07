package com.troy.ps.glRequestProcessing;

/**
 * A class for managing GL requests that need to happen on the main thread
 * @author Troy Neubauer
 *
 */
public class GlRequestProcessor {

	private static final float MAX_TIME_MILLIS = 5f;

	private static GlRequestQueue requestQueue = new GlRequestQueue();
	
	/**
	 * Sends a new requests to the GlRequestProcessor. The requests will be completed when {@link GlRequestProcessor#dealWithTopRequests()}
	 * assuming it has enough time, or when {@link GlRequestProcessor#completeAllRequests()} is called
	 * @param request
	 */
	public static void sendRequest(GlRequest request) {
		requestQueue.addRequest(request);
	}

	/**
	 * Executes the top level requests in the request queue within a set time limit of 5 milliseconds
	 */
	public static void dealWithTopRequests() {
		float remainingTime = MAX_TIME_MILLIS * 1000000;
		long start = System.nanoTime();
		while (requestQueue.hasRequests()) {
			requestQueue.acceptNextRequest().executeGlRequest();
			long end = System.nanoTime();
			long timeTaken = end - start;
			remainingTime -= timeTaken;
			start = end;
			if (remainingTime < 0) {
				break;
			}
		}
	}

	/**
	 * Executes all requests inside the queue
	 */
	public static void completeAllRequests() {
		while (requestQueue.hasRequests()) {
			requestQueue.acceptNextRequest().executeGlRequest();
		}
	}

}
