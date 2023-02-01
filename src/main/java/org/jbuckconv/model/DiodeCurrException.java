package org.jbuckconv.model;

public class DiodeCurrException extends RuntimeException {
	
	final String errmsg = "Id/Is <= 1";

	public DiodeCurrException() {
		super();		
	}

	public DiodeCurrException(String message) {
		super(message);		
	}

	public DiodeCurrException(Throwable cause) {
		super(cause);
	}

	public DiodeCurrException(String message, Throwable cause) {
		super(message, cause);
	}

	public DiodeCurrException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	@Override
	public String getMessage() {
		return errmsg;
	}
	
	private static final long serialVersionUID = 2457120149456300301L;

}
