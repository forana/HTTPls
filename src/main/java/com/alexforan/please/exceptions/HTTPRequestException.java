package com.alexforan.please.exceptions;

/**
 * Exception that wraps exceptions occurring in the HTTP request flow.
 * 
 * @author forana
 */
public class HTTPRequestException extends HTTPException {
    private static final long serialVersionUID = 1L;

    public HTTPRequestException(Throwable cause) {
        super(cause);
    }
}
