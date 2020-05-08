package com.viettel.backend.exeption;

/**
 * The Class BussinessException.
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = -6127762932224120122L;
	
	private String code;

	public BusinessException() {
	}

	public BusinessException(String code) {
		this(code, null);
	}
	
	public BusinessException(String code, String message) {
	    this(code, message, null);
	}
	
	public BusinessException(String code, String message, Throwable cause) {
		super(message, cause);
        this.code = code;
	}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
