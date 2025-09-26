package com.atdev.payroll.service;

import com.opencsv.CSVReader;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayrollService {

    private final JavaMailSender mailSender;

    public PayrollService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public ProcessResult processPayroll(MultipartFile csvFile) throws Exception {
        List<String> sentEmails = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8))) {
            String[] line;
            boolean firstLine = true;

            while ((line = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header row
                }

                // CSV order:
                // full_name, email, position, health_discount_amount,
                // social_discount_amount, taxes_discount_amount,
                // other_discount_amount, gross_salary, gross_payment,
                // net_payment, period
                String fullName = line[0].trim();
                String email = line[1].trim();
                String position = line[2].trim();
                String healthDiscount = line[3].trim();
                String socialDiscount = line[4].trim();
                String taxesDiscount = line[5].trim();
                String otherDiscount = line[6].trim();
                String grossSalary = line[7].trim();
                String grossPayment = line[8].trim();
                String netPayment = line[9].trim();
                String period = line[10].trim();

                // 1️⃣ Generate PDF in memory with full payroll info
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Document document = new Document();
                PdfWriter.getInstance(document, baos);
                document.open();

                document.add(new Paragraph("Payroll Details"));
                document.add(new Paragraph("Generated on: " + LocalDateTime.now()));
                document.add(new Paragraph("\n"));

                PdfPTable table = new PdfPTable(2);
                table.addCell("Full Name"); table.addCell(fullName);
                table.addCell("Email"); table.addCell(email);
                table.addCell("Position"); table.addCell(position);
                table.addCell("Health Discount"); table.addCell(healthDiscount);
                table.addCell("Social Discount"); table.addCell(socialDiscount);
                table.addCell("Taxes Discount"); table.addCell(taxesDiscount);
                table.addCell("Other Discount"); table.addCell(otherDiscount);
                table.addCell("Gross Salary"); table.addCell(grossSalary);
                table.addCell("Gross Payment"); table.addCell(grossPayment);
                table.addCell("Net Payment"); table.addCell(netPayment);
                table.addCell("Period"); table.addCell(period);

                document.add(table);
                document.close();

                // 2️⃣ Send email with PDF
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(email);
                helper.setSubject("Your Payroll");
                helper.setText("Hello " + fullName + ",\n\nPlease find attached your payroll PDF.");
                helper.addAttachment("payroll.pdf", new org.springframework.core.io.ByteArrayResource(baos.toByteArray()));

                mailSender.send(message);

                sentEmails.add(email);
            }
        } catch (Exception e) {
            throw new Exception("Failed to process CSV: " + e.getMessage(), e);
        }

        return new ProcessResult(sentEmails, LocalDateTime.now().toString());
    }

    public static class ProcessResult {
        public List<String> emails;
        public String datetime;

        public ProcessResult(List<String> emails, String datetime) {
            this.emails = emails;
            this.datetime = datetime;
        }
    }
}
