package com.zikan.salary.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple test controller to verify the application is running
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Zikan Salary Disbursement System");
        response.put("version", "1.0.0");
        return response;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Zikan Salary Disbursement System");
        response.put("description", "Host-to-host salary disbursement system");
        response.put("features", new String[]{
            "SFTP file upload",
            "File validation",
            "Maker-checker workflow",
            "Infosys integration",
            "Acknowledgement system"
        });
        return response;
    }
}
