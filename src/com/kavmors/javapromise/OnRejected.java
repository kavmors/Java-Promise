package com.kavmors.javapromise;

/**
 * An functional interface calls when the promise is rejected with a reason.
 * @author KavMors
 */
@FunctionalInterface
public interface OnRejected {
	/**
	 * Call when the promise is rejected.
	 * @param reason the rejected reason
	 * @return A Promise or any-type reason to the next caller.
	 */
	Object call(Object reason);
}
