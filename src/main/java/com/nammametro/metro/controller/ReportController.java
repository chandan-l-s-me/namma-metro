package com.nammametro.metro.controller;

import com.nammametro.metro.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/report")
@CrossOrigin
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public Map<String, Object> getReport() {
        return reportService.generateReport();
    }
}