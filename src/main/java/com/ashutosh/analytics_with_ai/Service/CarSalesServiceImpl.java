package com.ashutosh.analytics_with_ai.Service;

import com.ashutosh.analytics_with_ai.Entity.CarSales;
import com.ashutosh.analytics_with_ai.dto.MonthlyCountDTO;
import com.ashutosh.analytics_with_ai.dto.UploadSalesResponse;
import com.ashutosh.analytics_with_ai.dto.WeeklyCountDTO;
import com.ashutosh.analytics_with_ai.dto.YearlyCountDTO;
import com.ashutosh.analytics_with_ai.repository.CarSalesRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CarSalesServiceImpl implements CarSalesService{

    private final CarSalesRepository carSalesRepository;

    public CarSalesServiceImpl(CarSalesRepository carSalesRepository) {
        this.carSalesRepository = carSalesRepository;
    }

    @Override
    public UploadSalesResponse uploadCsv(MultipartFile file) {

        List<CarSales> carSalesList = new ArrayList<>();

        int failCount=0;
        int totalRecords=0;
        //Prevent memory leaks using this way
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            //CSV Format
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true) //Take first line as header = skip first line
                    .setIgnoreHeaderCase(true) //Header file me se case sensitive ko thik kar dega
                    .setTrim(true)
                    .build();


            //CSV Parsing
            CSVParser csvParser = CSVParser.parse(reader, csvFormat);
            List<CSVRecord> records = csvParser.getRecords();
            for (CSVRecord record : records) {

                totalRecords++;

                try {

                    String carNumber = record.get("Car Number");
                    boolean exists = carSalesRepository.existsByCarNumber(carNumber);

                    if (exists) { //For Duplicate data
                        failCount++;
                        continue;
                    }

                    CarSales carSales = new CarSales();
                    carSales.setCarNumber(record.get("Car Number"));
                    carSales.setBrand(record.get("Brand"));
                    carSales.setModel(record.get("Model"));
                    carSales.setColor(record.get("Color"));
                    carSales.setYear(Integer.parseInt(record.get("Year")));
                    carSales.setDateofPurchase(LocalDate.parse(record.get("Date of Purchase"), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    carSales.setTimeofPurchase(LocalTime.parse(record.get("Time of Purchase")));
                    carSales.setPrice(Long.parseLong(record.get("Price (Rs)")));
                    carSales.setMileage(Double.parseDouble(record.get("Mileage (km/l)")));
                    carSales.setEngine(Integer.parseInt(record.get("Engine (cc)")));
                    carSales.setFuelType(record.get("Fuel Type"));
                    carSales.setPaymentMode(record.get("Payment Mode"));
                    carSales.setState(record.get("State"));
                    carSales.setCity(record.get("City"));
                    carSales.setCustomerName(record.get("Customer Name"));
                    carSales.setContactNumber(record.get("Contact Number"));
                    carSales.setEmail(record.get("Email"));
                    carSales.setWarrantyPeriod(Integer.parseInt(record.get("Warranty Period (years)")));

                    carSalesList.add(carSales);
                } catch (Exception e) {
                    //failCount++;
                    throw new RuntimeException(e);
                }
            }

                if(!carSalesList.isEmpty()) {
                    carSalesRepository.saveAll(carSalesList);
                }

        } catch (Exception e) {
            throw new RuntimeException("Unable to Parse CSV" + e.getMessage());
        }


        return new UploadSalesResponse(totalRecords,totalRecords-failCount, failCount);
    }

    @Override
    public List<YearlyCountDTO> getYearlyCarsCount() {

        return carSalesRepository.getYearlyCount() ;
    }

    @Override
    public List<MonthlyCountDTO> getMontlyCarsCount(int year) {
        List<MonthlyCountDTO> data = carSalesRepository.getMonthlyCountByYear(year);

        Map<Integer, Long> map = data.stream()
                .collect(Collectors.toMap(
                   MonthlyCountDTO::month,
                   MonthlyCountDTO::count
                ));

        List<MonthlyCountDTO> result = new ArrayList<>();

        for (int i = 1;i <= 12; i++) {
            result.add(new MonthlyCountDTO(
                    i,
                    map.getOrDefault(i, 0L)
            ));
        }

        return result;
    }

    @Override
    public List<WeeklyCountDTO> getWeeklyCarsCount(int year, int month) {
        List<WeeklyCountDTO> data = carSalesRepository.findWeekOfMonthSalesCount(year, month);

        Map<Integer, Long> map = data.stream()
                .collect(Collectors.toMap(
                        WeeklyCountDTO::week,
                        WeeklyCountDTO::count
                ));

        List<WeeklyCountDTO> result = new ArrayList<>();

        for (int i=1; i<=5; i++) {
            result.add(new WeeklyCountDTO(
                    i,
                    map.getOrDefault(i, 0L)
            ));
        }

        return result;
    }
}
