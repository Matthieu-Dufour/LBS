package org.lpro.lbs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerError extends RuntimeException {
    private static final long serialVersionUID = 12344567890L;
    
    public InternalServerError(String message) {
        super(message,null,false,false);
    }
    
    public InternalServerError(String message, Throwable cause) {
        super(message, cause,false,false);
    }
    
    
}
