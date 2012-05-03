package com.findwise.thesis.qlustr.processing;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public interface Condition {
	boolean test(TextData data);
}
