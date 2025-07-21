package data_generation.data_generation.DataGeneration.entity;

import lombok.Data;

@Data
public class InvoiceItem {
    private int invoiceId;
    private int productId;
    private double unitPrice;
    private int quantity;
}
