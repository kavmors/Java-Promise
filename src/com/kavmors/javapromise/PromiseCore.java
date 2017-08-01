package com.kavmors.javapromise;

import java.util.ArrayList;
import java.util.List;

public abstract class PromiseCore {
	@FunctionalInterface
	public interface Callable {
		void call(OnFulfilled ful, OnRejected rej);
	}

	public enum State { PENDING, FULFILLED, REJECTED };

	protected State mState = State.PENDING;
	protected Object mValue = null;
	private List<OnFulfilled> mFulHandle = new ArrayList<>();
	private List<OnRejected> mRejHandle = new ArrayList<>();

	public PromiseCore(Callable callable) {
		try {
			callable.call(value->resolve(value), reason->reject(reason));
		} catch (Exception e) {
			reject(e);
		}
	}

	private PromiseCore resolve(Object value) {
		if (value instanceof Promise) {
			return ((Promise) value).then(ful->resolve(value), rej->reject(value));
		}
		//TODO:async
		if (mState == State.PENDING) {
			mState = State.FULFILLED;
			mValue = value;
			mFulHandle.forEach(i->i.call(value));
		}
		return null;
	}

	private PromiseCore reject(Object reason) {
		//TODO:asyn
		if (mState == State.PENDING) {
			mState = State.REJECTED;
			mValue = reason;
			mRejHandle.forEach(i->i.call(reason));
		}
		return null;
	}

	private Object resolvePromise(Object x, OnFulfilled ful, OnRejected rej) {
		if (x instanceof PromiseCore) {
			if (((PromiseCore) x).mState == State.PENDING) {
				((PromiseCore) x).then((value)->resolvePromise(value, ful, rej), rej);
			} else {
				((PromiseCore) x).then(ful, rej);
			}
			return null;
		}

		return ful.call(x);
	}

	/**
	 * Equivalent to calling {@link #then(OnFulfilled, OnRejected) then}(ful, null)
	 * @param ful
	 * @return a new promise resolving to the return value of the caller
	 */
	public PromiseCore then(OnFulfilled ful) {
		return then(ful, null);
	}

	/**
	 * Calls {@link OnFulfilled} with a value, or {@link OnRejected} with a reason
	 * @param ful
	 * @param rej
	 * @return a new promise resolving to the return value of the caller
	 */
	public PromiseCore then(OnFulfilled ful, OnRejected rej) {
		ful = ful != null ? ful : value->value;
		rej = rej != null ? rej : reason->{ throw new RuntimeException(reason.toString()); };

		final OnFulfilled fulF = ful;
		final OnRejected rejF = rej;

		if (mState == State.PENDING) {
			return new Promise((f, j) -> {
				mFulHandle.add(new OnFulfilled() {
					@Override
					public Object call(Object value) {
						//TODO:async
						try {
							Object x = fulF.call(mValue);
							return resolvePromise(x, f, j);
						} catch (Exception e) {
							return j.call(e);
						}
					}
				});
				mRejHandle.add(new OnRejected() {
					@Override
					public Object call(Object reason) {
						//TODO:async
						try {
							Object x = rejF.call(mValue);
							return resolvePromise(x, f, j);
						} catch (Exception e) {
							return j.call(e);
						}
					}
				});
			});
		} else if (mState == State.FULFILLED) {
			return new Promise((f, j) -> {
				//TODO:async
				try {
					Object x = fulF.call(mValue);
					resolvePromise(x, f, j);
				} catch (Exception e) {
					j.call(e);
				}
			});
		} else if (mState == State.REJECTED) {
			return new Promise((f, j) -> {
				//TODO:async
				try {
					Object x = rejF.call(mValue);
					resolvePromise(x, f, j);
				} catch (Exception e) {
					j.call(e);
				}
			});
		}
		throw new IllegalStateException("mState is " + mState);
	}

	/**
	 * Equivalent to calling {@link PromiseCore#then(OnFulfilled, OnRejected) then}(null, rej)
	 * @param rej
	 * @return a new promise resolving to the return value of the caller
	 */
	public PromiseCore catchReject(OnRejected rej) {
		return then(null, rej);
	}

	@Override
	public String toString() {
		return String.format("%s[state=%s,value=%s]", super.toString(), mState, mValue.toString());
	}
}