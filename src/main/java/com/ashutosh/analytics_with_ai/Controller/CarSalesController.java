package com.ashutosh.analytics_with_ai.Controller;

import com.ashutosh.analytics_with_ai.Service.CarSalesService;
import com.ashutosh.analytics_with_ai.dto.MonthlyCountDTO;
import com.ashutosh.analytics_with_ai.dto.UploadSalesResponse;
import com.ashutosh.analytics_with_ai.dto.WeeklyCountDTO;
import com.ashutosh.analytics_with_ai.dto.YearlyCountDTO;
import com.ashutosh.analytics_with_ai.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/car_sales")
@CrossOrigin(origins = "{https://car-sales-analytics-dashboard-ai.streamlit.app}")
public class CarSalesController {

    private final CarSalesService salesService;

    public CarSalesController(CarSalesService salesService) {
        this.salesService = salesService;
    }


    @PostMapping("/upload_csv")
    public ResponseEntity<ApiResponse<UploadSalesResponse>> Uploadfile(@RequestParam("file")MultipartFile file) {

        if (file.isEmpty()) {
            UploadSalesResponse emptyResponse = new UploadSalesResponse(0, 0, 0);
            ApiResponse<UploadSalesResponse> apiResponse = new ApiResponse<>(
                    false,
                    "File is Empty",
                    emptyResponse,
                    HttpStatus.BAD_REQUEST.value()
            );
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }

        UploadSalesResponse salesResponse = salesService.uploadCsv(file);
        ApiResponse<UploadSalesResponse> apiResponse1 = getApiResponse(salesResponse);
        return ResponseEntity.ok(apiResponse1);
    }

    private static ApiResponse<UploadSalesResponse> getApiResponse(UploadSalesResponse response) {
        
        String message;
        boolean success;
        
        if(response.getFailedCount()==0) {
            message = "All records uploaded successfully";
            success = true;
        } else if (response.getSuccessCount()==0) {
            message = "All records failed to upload";
            success = false;
        } else {
            message = "Uploaded with some errors "+response.getFailedCount()+" rows failed";
            success = false;
        }

        return new ApiResponse<UploadSalesResponse>(success, message, response, HttpStatus.OK.value());
    }

    @GetMapping("/yearly-count")
    public ResponseEntity<ApiResponse<List<YearlyCountDTO>>> yearlyCount() {

        List<YearlyCountDTO> carsCount = salesService.getYearlyCarsCount();
        ApiResponse<List<YearlyCountDTO>> response = new ApiResponse<>(
                true,
                "Yearly Data Read Properly",
                carsCount,
                HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/monthly-count")
    public ResponseEntity<ApiResponse<List<MonthlyCountDTO>>> MonthlyCount(@RequestParam int year) {

        List<MonthlyCountDTO> carsCount = salesService.getMontlyCarsCount(year);
        ApiResponse<List<MonthlyCountDTO>> response = new ApiResponse<>(
                true,
                "Monthly Data Read Properly",
                carsCount,
                HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weekly-count")
    public ResponseEntity<ApiResponse<List<WeeklyCountDTO>>> WeeklyCount(@RequestParam int year, @RequestParam int month) {

        List<WeeklyCountDTO> carsCount = salesService.getWeeklyCarsCount(year, month);
        ApiResponse<List<WeeklyCountDTO>> response = new ApiResponse<>(
                true,
                "Weekly Data Read Properly",
                carsCount,
                HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }
}
