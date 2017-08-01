# Java Promise #

A Promise for [Promises/A+](https://promisesaplus.com/) in Java.

----------

## Usage ##

    new Promise(new Callable() {
	  @Override
	  public void call(OnFulfilled ful, OnRejected rej) {
		//...dosomething
		ful.call(value);	//when fulfilled
		rej.call(reason);	//when rejected
	  }
	})
	.then(new OnFulfulled() {
	  @Override
	  public Object call(Object value) {
		return value;
	  }
	}, new OnRejected() {
	  @Override
	  public Object call(Object reason) {
		return reason;
	  }
	});

Or coding in Lambda

	new Promise((ful, rej) -> ful.call(value))
	  .then(value -> value, reason -> reason);
		