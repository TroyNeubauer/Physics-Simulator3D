package com.troy.ps.resourceProcessing;

import java.util.ArrayList;
import java.util.List;

public class ResourceQueue {

	private volatile List<ResourceRequest> requestQueue = new ArrayList<ResourceRequest>();
	
	public synchronized void addRequest(ResourceRequest request){
		requestQueue.add(request);
	}
	
	public synchronized ResourceRequest acceptNextRequest(){
		return requestQueue.remove(0);
	}
	
	public synchronized boolean hasRequests(){
		return !requestQueue.isEmpty();
	}
	
}
