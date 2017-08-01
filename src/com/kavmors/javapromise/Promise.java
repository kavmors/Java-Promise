package com.kavmors.javapromise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Promise in Java for Promises/A+, which represents the eventual result of an asynchronous operation
 * @author KavMors
 */
public class Promise extends PromiseCore {
	public Promise(Callable callable) {
		super(callable);
	}

	/**
	 * return a promise that waits for all promises to be fulfilled or any of the promises to be rejected.
	 * @param promises the list of promises
	 * @return a new promise that is fulfilled with an list of resolved values, or is rejected with a reason of the first rejected promise.
	 */
	public static Promise all(List<Promise> promises) {
		return new Promise( (ful, rej) -> {
			Map<Integer, Object> result = new HashMap<>();
			for (int i = 0; i < promises.size(); i++) {
				final int index = i;
				promises.get(i)
				.then(new OnFulfilled() {
					@Override
					public Object call(Object value) {
						result.put(index, value);
						return result.size() == promises.size();	//fulfill: keep the result & check all promises state
					}
				})
				.then(new OnFulfilled() {
					@Override
					public Object call(Object value) {
						System.out.println(value);
						if ((Boolean)value) {		//all promises fulfulled
							List<Object> ret = new ArrayList<>(Collections.nCopies(result.size(), null));
							result.forEach((k, v) -> ret.set(k, v));
							ful.call(result);
						}
						return null;
					}
				}, new OnRejected() {
					@Override
					public Object call(Object reason) {
						rej.call(reason);
						return null;
					}
				});
			}
		});
	}

	/**
	 * return a promise that resolves or rejects any of the promises in promises have been resolved or rejected
	 * @param promises the list of promises
	 * @return a new promise that is fulfilled with a value, or rejected with a reason
	 */
	public static Promise race(List<Promise> items) {
		return new Promise( (ful, rej) -> {
			items.forEach(item -> {
				item.then(value -> ful.call(value), reason -> rej.call(reason));
			});
		});
	}

	/**
	 * Returns a promise that is resolved with the given value
	 * @param value the value to resolve
	 * @return a new promise
	 */
	public static Promise resolve(Object value) {
		return new Promise( (ful, rej) -> ful.call(value) );
	}

	/**
	 * Returns a promise that is rejected with the given reason
	 * @param reason the reason to reject
	 * @return a new promise
	 */
	public static Promise reject(Object reason) {
		return new Promise( (ful, rej) -> rej.call(reason) );
	}

	/**
	 * Return the current value of this promise
	 * @return the resolved value or rejected reason of this promise, null if the promise is pending.
	 */
	public Object value() {
		return mValue;
	}

	/**
	 * Return the current state of this promise
	 * @return the state value
	 */
	public State state() {
		return mState;
	}
}