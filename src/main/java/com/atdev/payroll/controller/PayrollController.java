package com.atdev.payroll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atdev.payroll.service.PayrollService;

@RestController
@RequestMapping("/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayroll(
            @RequestParam(defaultValue = "do") String country,
            @RequestParam String company,
            @RequestPart("file") MultipartFile csvFile) {

        try {
            PayrollService.ProcessResult result = payrollService.processPayroll(csvFile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
