package com.atdev.payroll.service;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

@Service
public class PayrollService {

    public ProcessResult processPayroll(MultipartFile csvFile) throws Exception {
        List<String> sentEmails = new ArrayList<>();

        // 1️⃣ Parse CSV
       try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream(), "UTF-8"))) {
    String[] line;
    boolean firstLine = true;

    while ((line = reader.readNext()) != null) {
        if (firstLine) { 
            firstLine = false; // skip header
            continue;
        }

        // Remove BOM from the first cell
        String email = line[0].replace("\uFEFF", "").trim();

        sentEmails.add(email);

        // Simulate PDF generation and email sending
        System.out.println("Generating PDF for " + line[1]);
        System.out.println("Sending payroll email to " + email);
    }
} catch (Exception e) {
    throw new Exception("Failed to process CSV: " + e.getMessage(), e);
}

        // 4️⃣ Return result
        return new ProcessResult(sentEmails, LocalDateTime.now().toString());
    }

    // Inner DTO
    public static class ProcessResult {
        public List<String> emails;
        public String datetime;

        public ProcessResult(List<String> emails, String datetime) {
            this.emails = emails;
            this.datetime = datetime;
        }
    }
}
