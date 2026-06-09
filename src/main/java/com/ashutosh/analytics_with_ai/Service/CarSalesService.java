package com.ashutosh.analytics_with_ai.Service;

import com.ashutosh.analytics_with_ai.dto.MonthlyCountDTO;
import com.ashutosh.analytics_with_ai.dto.UploadSalesResponse;
import com.ashutosh.analytics_with_ai.dto.WeeklyCountDTO;
import com.ashutosh.analytics_with_ai.dto.YearlyCountDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarSalesService {

    UploadSalesResponse uploadCsv(MultipartFile file);

    List<YearlyCountDTO> getYearlyCarsCount();

    List<MonthlyCountDTO> getMontlyCarsCount(int year);

    List<WeeklyCountDTO> getWeeklyCarsCount(int year, int month);
}
