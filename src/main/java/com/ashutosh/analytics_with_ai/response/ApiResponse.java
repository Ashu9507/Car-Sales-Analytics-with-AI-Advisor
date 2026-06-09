package com.ashutosh.analytics_with_ai.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int statusCode;
    private LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, T data, int statusCode) {
        this.data=data;
        this.message=message;
        this.statusCode=statusCode;
        this.success=success;
        this.timestamp=LocalDateTime.now();
    }
}
