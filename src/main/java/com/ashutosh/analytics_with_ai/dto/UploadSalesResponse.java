package com.ashutosh.analytics_with_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadSalesResponse {

    private int totalRecords;
    private int successCount;
    private int failedCount;
}
