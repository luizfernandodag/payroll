package com.atdev.payroll;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.atdev.payroll.controller.PayrollController;
import com.atdev.payroll.service.PayrollService;

@WebMvcTest(controllers = PayrollController.class,
            excludeAutoConfiguration = SecurityAutoConfiguration.class) // <- desabilita segurança
class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayrollService payrollService;

    @Test
    void testProcessPayrollEndpoint() throws Exception {
        // Cria arquivo CSV simulado
        MockMultipartFile file = new MockMultipartFile(
                "file", "payroll.csv", "text/csv",
                "name,email\nJohn,john@example.com".getBytes()
        );

        // Simula retorno do service
        List<String> emails = List.of("john@example.com");
        String company = "atdev";
        PayrollService.ProcessResult result = new PayrollService.ProcessResult(emails, company);

        when(payrollService.processPayroll(Mockito.anyList(), Mockito.eq(company)))
                .thenReturn(result);

        // Executa request multipart sem httpBasic, porque a segurança está desabilitada
        mockMvc.perform(multipart("/payroll/process")
                    .file(file)
                    .param("company", company))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emails[0]").value("john@example.com"));
    }
}
