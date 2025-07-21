package data_generation.data_generation.DataGeneration.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Invoice {
    private int userId;
    private LocalDateTime invoiceDate;
    private String invoiceNumber;
    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingCountry;
    private String billingPostcode;
    private double total;
    private String paymentMethod;
    private String paymentAccountName;
    private String paymentAccountNumber;
    private String status;
    private String statusMessage;
}