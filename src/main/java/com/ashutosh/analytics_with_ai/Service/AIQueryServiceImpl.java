package com.ashutosh.analytics_with_ai.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AIQueryServiceImpl implements AIQueryService{

    private final ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate;

    public AIQueryServiceImpl(ChatClient.Builder builder, JdbcTemplate jdbcTemplate) {
        this.chatClient = builder.build();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String process(String question) {

        String sql = generateSql(question);
        System.out.println(sql);
        if(sql.equalsIgnoreCase("INVALID")) {
            return "Only table(car) related query questions is allowed";
        }
        System.out.println(isSafe(sql));
        if ((!isSafe(sql))) {
            return "Unsafe";
        }

        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

            if (result.isEmpty()) return "No data found";

            return toNaturalLanguage(question, result);

        } catch (Exception e) {
            e.printStackTrace();
            return "Query Failed: " + e.getMessage();
        }
    }

    private boolean isSafe(String sql) {
        String lower = sql.toLowerCase();
        return lower.startsWith("select")
                && !lower.contains("drop")
                && !lower.contains("delete")
                && !lower.contains("update")
                && !lower.contains("insert");
    }

    private String generateSql(String question) {
        try {
            String prompt = """
                    You are a SQL generator.
                    
                    Table: car_sales
                    Columns: id, brand, car_number, city, color, contact_number, customer_name, date_of_purchase, email, engine, fuel_type, mileage, model, payment_mode, price, state, time_of_purchase, warranty_period, year
                    
                    Rules:
                    Only SELECT queries
                    Use only given columns
                    If not related, return: INVALID
                    Return only SQL
                    Just write the query and nothing else
                    
                    Question:
                    """ + question;


            return chatClient.prompt().user(prompt).call().content().trim();
        } catch (Exception e) {
            return "AI Error: "+ e.getMessage();
        }
    }

    private String toNaturalLanguage(String question, List<Map<String, Object>> result) {
        String prompt = """
            Convert database result into a human readable answer.

            User Question:
            """ + question + """

            DB Result:
            """ + result.toString() + """

            Rules:
            Answer clearly (don't write too much)
            Do not show JSON
            Do not explain SQL
            Limit db rows to 50 
            """;
        return chatClient.prompt().user(prompt).call().content().trim();
    }
}
