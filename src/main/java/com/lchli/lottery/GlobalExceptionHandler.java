package com.lchli.lottery;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public BaseResponse defaultExceptionHandler(HttpServletRequest request, Throwable e) {
        e.printStackTrace();

        BaseResponse response = new BaseResponse();
        response.status = BaseResponse.RESPCODE_FAIL;
        response.message = e.getMessage();

        return response;
    }

}
