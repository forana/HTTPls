package com.forana.http.exceptions;

/**
 * Base exception for all of HTTPls.
 * 
 * @author forana
 */
public abstract class HTTPException extends Exception {
    private static final long serialVersionUID = 1L;

    protected HTTPException(String message) {
        super(message);
    }

    protected HTTPException(Throwable cause) {
        super(cause);
    }
}
