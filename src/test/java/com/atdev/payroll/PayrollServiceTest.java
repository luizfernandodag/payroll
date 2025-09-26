package com.atdev.payroll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;

import com.atdev.payroll.dto.PayrollRequestDto;
import com.atdev.payroll.service.PayrollService;
import com.atdev.payroll.service.PayrollService.ProcessResult;

import jakarta.mail.internet.MimeMessage;

class PayrollServiceTest {

    private PayrollService payrollService;

    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        // Mock do JavaMailSender
        mailSender = Mockito.mock(JavaMailSender.class);

        // Cria MimeMessage "fake" para testes
        MimeMessage mimeMessage = new MimeMessage((jakarta.mail.Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        payrollService = new PayrollService(mailSender);
    }

    @Test
    void testProcessPayrollFromCsvFile() throws Exception {
        String csvContent = """
            fullName,email,position,healthDiscountAmount,socialDiscountAmount,taxesDiscountAmount,otherDiscountAmount,grossSalary,grossPayment,netPayment,period
            John Doe,john@example.com,Developer,50,30,20,10,1000,1000,890,2025-09
            Jane Smith,jane@example.com,Analyst,60,40,25,15,1200,1200,1060,2025-09
            """;

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "payroll.csv",
            "text/csv",
            csvContent.getBytes()
        );

        // Converte CSV em DTOs
        List<PayrollRequestDto> dtos = payrollService.parseCsvToDtos(file);

        // Chama processPayroll com DTOs
        ProcessResult result = payrollService.processPayroll(dtos, "atdev");

        assertThat(result).isNotNull();
        assertThat(result.getEmails()).containsExactlyInAnyOrder("john@example.com", "jane@example.com");
    }

    @Test
    void testProcessPayrollReturnsEmails() throws Exception {
        String company = "atdev";

        // Cria DTOs de teste
        PayrollRequestDto dto1 = new PayrollRequestDto(
            "John Doe",
            "john@example.com",
            "Developer",
            1000.0,
            50.0,
            30.0,
            20.0,
            10.0,
            1000.0,
            890.0,
            "2025-09"
        );

        PayrollRequestDto dto2 = new PayrollRequestDto(
            "Jane Smith",
            "jane@example.com",
            "Analyst",
            1200.0,
            60.0,
            40.0,
            25.0,
            15.0,
            1200.0,
            1060.0,
            "2025-09"
        );

        List<PayrollRequestDto> dtos = List.of(dto1, dto2);

        // Chama o método com a lista de DTOs
        ProcessResult result = payrollService.processPayroll(dtos, company);

        assertThat(result).isNotNull();
        assertThat(result.getEmails()).containsExactlyInAnyOrder("john@example.com", "jane@example.com");
    }
}
