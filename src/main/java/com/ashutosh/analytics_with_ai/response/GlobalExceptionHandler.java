package com.ashutosh.analytics_with_ai.response;

import com.ashutosh.analytics_with_ai.dto.UploadSalesResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<UploadSalesResponse>> handleAllExceptions(Exception e) {
        UploadSalesResponse response = new UploadSalesResponse(0, 0, 0);
        ApiResponse<UploadSalesResponse> apiResponse = new ApiResponse<UploadSalesResponse>(
                false,
                e.getMessage(),
                response,
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<ApiResponse<UploadSalesResponse>>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
