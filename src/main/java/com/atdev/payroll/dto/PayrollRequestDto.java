package com.atdev.payroll.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class PayrollRequestDto {
    
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Gross salary is required")
    @Positive(message = "Gross salary must be positive")
    private Double grossSalary;

    // Add other fields as needed
    private Double healthDiscountAmount;
    private Double socialDiscountAmount;
    private Double taxesDiscountAmount;
    private Double otherDiscountAmount;
    private Double grossPayment;
    @PositiveOrZero(message = "Net payment must be positive or zero")
    private Double netPayment;
    private String period;
    public PayrollRequestDto(@NotBlank(message = "Full name is required") String fullName,
            @Email(message = "Email should be valid") @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "Position is required") String position,
            @NotNull(message = "Gross salary is required") @Positive(message = "Gross salary must be positive") Double grossSalary,
            Double healthDiscountAmount, Double socialDiscountAmount, Double taxesDiscountAmount,
            Double otherDiscountAmount, Double grossPayment, Double netPayment, String period) {
        this.fullName = fullName;
        this.email = email;
        this.position = position;
        this.grossSalary = grossSalary;
        this.healthDiscountAmount = healthDiscountAmount;
        this.socialDiscountAmount = socialDiscountAmount;
        this.taxesDiscountAmount = taxesDiscountAmount;
        this.otherDiscountAmount = otherDiscountAmount;
        this.grossPayment = grossPayment;
        this.netPayment = netPayment;
        this.period = period;
    }
    public PayrollRequestDto() {
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public Double getGrossSalary() {
        return grossSalary;
    }
    public void setGrossSalary(Double grossSalary) {
        this.grossSalary = grossSalary;
    }
    public Double getHealthDiscountAmount() {
        return healthDiscountAmount;
    }
    public void setHealthDiscountAmount(Double healthDiscountAmount) {
        this.healthDiscountAmount = healthDiscountAmount;
    }
    public Double getSocialDiscountAmount() {
        return socialDiscountAmount;
    }
    public void setSocialDiscountAmount(Double socialDiscountAmount) {
        this.socialDiscountAmount = socialDiscountAmount;
    }
    public Double getTaxesDiscountAmount() {
        return taxesDiscountAmount;
    }
    public void setTaxesDiscountAmount(Double taxesDiscountAmount) {
        this.taxesDiscountAmount = taxesDiscountAmount;
    }
    public Double getOtherDiscountAmount() {
        return otherDiscountAmount;
    }
    public void setOtherDiscountAmount(Double otherDiscountAmount) {
        this.otherDiscountAmount = otherDiscountAmount;
    }
    public Double getGrossPayment() {
        return grossPayment;
    }
    public void setGrossPayment(Double grossPayment) {
        this.grossPayment = grossPayment;
    }
    public Double getNetPayment() {
        return netPayment;
    }
    public void setNetPayment(Double netPayment) {
        this.netPayment = netPayment;
    }
    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }

    // Getters and setters
}
