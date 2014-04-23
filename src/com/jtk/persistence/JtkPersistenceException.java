package com.jtk.persistence;

import com.jtk.util.JtkException;

public class JtkPersistenceException extends JtkException {

	private static final long serialVersionUID = 1L;

	public JtkPersistenceException() {
		super();
	}

	public JtkPersistenceException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JtkPersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public JtkPersistenceException(String message) {
		super(message);
	}

	public JtkPersistenceException(Throwable cause) {
		super(cause);
	}

}
