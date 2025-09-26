package com.atdev.payroll.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atdev.payroll.dto.PayrollRequestDto;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVReader;

import jakarta.mail.internet.MimeMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Service
public class PayrollService {

    private final JavaMailSender mailSender;
    private final Validator validator;

    public PayrollService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Parses CSV to DTOs with validation
     */
    public List<PayrollRequestDto> parseCsvToDtos(MultipartFile csvFile) throws Exception {
        List<PayrollRequestDto> payrollDtos = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8))) {
            String[] header = reader.readNext(); // skip header
            if (header == null) throw new Exception("CSV is empty");

            String[] line;
            int rowNum = 1;
            while ((line = reader.readNext()) != null) {
                PayrollRequestDto dto = mapCsvLineToDto(line);

                // Validate DTO
                Set<ConstraintViolation<PayrollRequestDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (ConstraintViolation<PayrollRequestDto> v : violations) {
                        sb.append(v.getPropertyPath()).append(": ").append(v.getMessage()).append("; ");
                    }
                    throw new IllegalArgumentException("Validation failed at row " + rowNum + ": " + sb);
                }

                payrollDtos.add(dto);
                rowNum++;
            }
        }

        return payrollDtos;
    }

    private PayrollRequestDto mapCsvLineToDto(String[] line) {
        PayrollRequestDto dto = new PayrollRequestDto();
        dto.setFullName(line[0].trim());
        dto.setEmail(line[1].trim());
        dto.setPosition(line[2].trim());
        dto.setHealthDiscountAmount(parseDouble(line[3]));
        dto.setSocialDiscountAmount(parseDouble(line[4]));
        dto.setTaxesDiscountAmount(parseDouble(line[5]));
        dto.setOtherDiscountAmount(parseDouble(line[6]));
        dto.setGrossSalary(parseDouble(line[7]));
        dto.setGrossPayment(parseDouble(line[8]));
        dto.setNetPayment(parseDouble(line[9]));
        dto.setPeriod(line[10].trim());
        return dto;
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Processes payroll from DTOs (PDF + email)
     */
    public ProcessResult processPayroll(List<PayrollRequestDto> payrollDtos, String company) throws Exception {
        List<String> sentEmails = new ArrayList<>();

        for (PayrollRequestDto dto : payrollDtos) {
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

            // Add company info
            document.add(new Paragraph(company, new Font(Font.HELVETICA, 16, Font.BOLD)));
            document.add(new Paragraph("Comprobante de pago (" + dto.getPeriod() + ")"));
            document.add(new Paragraph(dto.getFullName()));
            document.add(new Paragraph(dto.getPosition()));
            document.add(new Paragraph("\n"));

            // Payroll table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            table.addCell("Salario bruto"); table.addCell(dto.getGrossSalary().toString());
            table.addCell("Pago bruto"); table.addCell(dto.getGrossPayment().toString());

            table.addCell("SFS"); table.addCell(dto.getSocialDiscountAmount().toString());
            table.addCell("AFP"); table.addCell(dto.getHealthDiscountAmount().toString());
            table.addCell("ISR"); table.addCell(dto.getTaxesDiscountAmount().toString());
            table.addCell("Otros"); table.addCell(dto.getOtherDiscountAmount().toString());

            table.addCell("Pago neto"); table.addCell(dto.getNetPayment().toString());

            document.add(table);
            document.close();

            // 2️⃣ Send email with PDF
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(dto.getEmail());
            helper.setSubject("Your Payroll");
            helper.setText("Hello " + dto.getFullName() + ",\n\nPlease find attached your payroll PDF.");
            helper.addAttachment("payroll.pdf", new ByteArrayResource(baos.toByteArray()));

            mailSender.send(message);
            sentEmails.add(dto.getEmail());
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
