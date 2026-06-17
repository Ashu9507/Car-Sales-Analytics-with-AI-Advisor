package com.ashutosh.analytics_with_ai.repository;

import com.ashutosh.analytics_with_ai.Entity.CarSales;
import com.ashutosh.analytics_with_ai.dto.MonthlyCountDTO;
import com.ashutosh.analytics_with_ai.dto.WeeklyCountDTO;
import com.ashutosh.analytics_with_ai.dto.YearlyCountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarSalesRepository extends JpaRepository<CarSales,Long> {

    boolean existsByCarNumber(String carNumber);

    @Query("""
            Select new com.ashutosh.analytics_with_ai.dto.YearlyCountDTO(c.year, count(c))
            from CarSales c
            Group by c.year
            Order by c.year
            """)
    List<YearlyCountDTO> getYearlyCount();

@Query(value = """
    SELECT
        MONTH(date_of_purchase),
        COUNT(*)
    FROM car_sales
    WHERE YEAR(date_of_purchase) = :year
    GROUP BY MONTH(date_of_purchase)
    ORDER BY MONTH(date_of_purchase)
    """, nativeQuery = true)
List<Object[]> getMonthlyCountByYear(@Param("year") int year);

    @Query("""
        SELECT NEW com.ashutosh.analytics_with_ai.dto.WeeklyCountDTO(
            CAST(COALESCE((EXTRACT(DAY FROM c.dateofPurchase) - 1) / 7 + 1, 1) AS int), 
            COUNT(c)
        )
        FROM CarSales c
        WHERE EXTRACT(YEAR FROM c.dateofPurchase) = :year
          AND EXTRACT(MONTH FROM c.dateofPurchase) = :month
        GROUP BY CAST(COALESCE((EXTRACT(DAY FROM c.dateofPurchase) - 1) / 7 + 1, 1) AS int)
        ORDER BY CAST(COALESCE((EXTRACT(DAY FROM c.dateofPurchase) - 1) / 7 + 1, 1) AS int)
       """)
List<WeeklyCountDTO> findWeekOfMonthSalesCount(
        @Param("year") int year,
        @Param("month") int month
);
}
