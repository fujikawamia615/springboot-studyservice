package com.hdd.studysys.exception;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        Throwable cause = e.getMostSpecificCause();
        if (cause instanceof SQLException sqlEx && sqlEx.getErrorCode() == 1062) {
            result.put("message", "唯一键重复");
        } else {
            result.put("message", "数据库操作失败");
        }
        return result;
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "服务器异常: " + e.getMessage());
        return result;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        String msg = e.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        result.put("message", msg);
        return result;
    }
}
