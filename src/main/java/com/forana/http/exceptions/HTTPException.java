package com.forana.http.exceptions;

/**
 * Base exception for all of HTTPls.
 * 
 * @author forana
 */
public abstract class HTTPException extends Exception {
    private static final long serialVersionUID = 1L;

    public HTTPException(Throwable cause) {
        super(cause);
    }
}
