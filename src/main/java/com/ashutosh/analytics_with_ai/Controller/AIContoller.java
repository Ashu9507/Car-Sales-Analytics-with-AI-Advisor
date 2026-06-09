package com.ashutosh.analytics_with_ai.Controller;

import com.ashutosh.analytics_with_ai.Service.AIQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/ai")
public class AIContoller {

    private final AIQueryService aiService;

    public AIContoller(AIQueryService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<String> as(@RequestBody String question) {
        return ResponseEntity.ok(aiService.process(question));
    }

}
