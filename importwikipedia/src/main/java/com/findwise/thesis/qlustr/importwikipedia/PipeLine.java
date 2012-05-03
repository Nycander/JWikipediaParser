package com.findwise.thesis.qlustr.importwikipedia;

import java.util.concurrent.*;

public class PipeLine<T> {
	private final BlockingQueue<T> queue;
	private boolean isDone;
	
	public PipeLine() {
		queue = new LinkedBlockingQueue<T>(500);
		isDone = false;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public void setDone() {
		isDone = true;
	}
	
	public T take() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void put(T element) {
		try {
			queue.put(element);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int count() {
		return queue.size();
	}
	
}
