package com.vuviet.jobhunter.util.error;

import com.vuviet.jobhunter.entity.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleAllException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(ex.getMessage());
        res.setError("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(value = {
            IdInvalidException.class,
            UsernameNotFoundException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex){
        RestResponse<Object> res=new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Exception occurs...");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex){
        BindingResult result=ex.getBindingResult();
        final List<FieldError> fieldErrors=result.getFieldErrors();

        RestResponse<Object> res=new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors= new ArrayList<>();
        for(FieldError f:fieldErrors){
            errors.add(f.getDefaultMessage());
        }

        res.setMessage(errors.size()>1?errors:errors.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            StorageException.class
    })
    public ResponseEntity<RestResponse<Object>> handleFileUploadException(Exception ex){
        RestResponse<Object> res=new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Exception upload file...");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            PermissionException.class
    })
    public ResponseEntity<RestResponse<Object>> handlePermissionException(Exception ex){
        RestResponse<Object> res=new RestResponse<>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError("Forbidden");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

    @ExceptionHandler(value = {
            NoResourceFoundException.class
    })
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex){
        RestResponse<Object> res=new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("404 Not Found. URL may not exist");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }
}
