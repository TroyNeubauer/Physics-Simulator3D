package com.troy.ps.glRequestProcessing;

import java.util.*;

public class GlRequestQueue {
	
	private volatile List<GlRequest> requestQueue = new ArrayList<GlRequest>(100);
	
	public synchronized void addRequest(GlRequest request){
		requestQueue.add(request);
	}
	
	public synchronized GlRequest acceptNextRequest(){
		return requestQueue.remove(0);
	}
	
	public synchronized boolean hasRequests(){
		return !requestQueue.isEmpty();
	}

}
