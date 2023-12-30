package com.example.HardBoard.api;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiControllerAdvice {
    
    // TODO 없는 URL Exception 처리하기
    // TODO data null 입력되는 것 해결
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> bindException(BindException e) {
        return ApiResponse.of(
                HttpStatus.BAD_REQUEST,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, EmptyResultDataAccessException.class})
    public ApiResponse<Object> illegalArgumentException(IllegalArgumentException e){
        return ApiResponse.of(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ApiResponse<Object> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        return ApiResponse.of(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ApiResponse<Object> missingServletRequestParameterException(
            MissingServletRequestParameterException e
    ){
        return ApiResponse.of(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                null
        );
    }
}
