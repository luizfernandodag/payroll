package com.atdev.payroll.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVReader;

import jakarta.mail.internet.MimeMessage;

@Service
public class PayrollService {

    private final JavaMailSender mailSender;

    public PayrollService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public ProcessResult processPayroll(MultipartFile csvFile, String company) throws Exception {
        List<String> sentEmails = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8))) {
            String[] line;
            boolean firstLine = true;

            while ((line = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header
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

                // 1️⃣ Generate PDF in memory
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Document document = new Document();
                PdfWriter.getInstance(document, baos);
                document.open();

                // Add logo
                String logoPath = "logos/" + company + ".png";
                InputStream logoStream = getClass().getClassLoader().getResourceAsStream(logoPath);
                if (logoStream == null) {
                    logoStream = getClass().getClassLoader().getResourceAsStream("logos/default.png");
                }
                
                if (logoStream != null) {
                    Image logo = Image.getInstance(IOUtils.toByteArray(logoStream));
                    logo.scaleToFit(120, 50);
                    document.add(logo);
                }

                // Add company name from ENV
                // String companyInfo = System.getenv().getOrDefault("COMPANY_INFO", "FakeClients");
                
                String companyInfo = company;
                document.add(new Paragraph(companyInfo, new Font(Font.HELVETICA, 16, Font.BOLD)));
                document.add(new Paragraph("Comprobante de pago (" + period + ")"));
                document.add(new Paragraph(fullName));
                document.add(new Paragraph(position));
                document.add(new Paragraph("\n"));

                // Payroll table
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);

                table.addCell("Salario bruto"); table.addCell(grossSalary);
                table.addCell("Pago bruto"); table.addCell(grossPayment);

                table.addCell("SFS"); table.addCell(socialDiscount);
                table.addCell("AFP"); table.addCell(healthDiscount);
                table.addCell("ISR"); table.addCell(taxesDiscount);
                table.addCell("Otros"); table.addCell(otherDiscount);

                table.addCell("Pago neto"); table.addCell(netPayment);

                document.add(table);

                document.close();

                // 2️⃣ Send email with PDF
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(email);
                helper.setSubject("Your Payroll");
                helper.setText("Hello " + fullName + ",\n\nPlease find attached your payroll PDF.");
                helper.addAttachment("payroll.pdf", new ByteArrayResource(baos.toByteArray()));

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
