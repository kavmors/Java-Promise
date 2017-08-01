package com.kavmors.javapromise;

/**
 * An functional interface calls when the promise is fulfilled with a result.
 * @author KavMors
 */
@FunctionalInterface
public interface OnFulfilled {
	/**
	 * Call when the promise is fulfilled.
	 * @param value the resolved value
	 * @return A Promise or any-type result to the next caller.
	 */
	Object call(Object value);
}
