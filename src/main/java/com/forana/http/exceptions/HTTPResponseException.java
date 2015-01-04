package com.forana.http.exceptions;

/**
 * Exception that wraps exceptions occurring in the HTTP response flow.
 * 
 * @author forana
 */
public class HTTPResponseException extends HTTPException {
    private static final long serialVersionUID = 1L;

    public HTTPResponseException(String message) {
        super(message);
    }

    public HTTPResponseException(Throwable cause) {
        super(cause);
    }
}
