package data_generation.data_generation.DataGeneration.db;

import data_generation.data_generation.DataGeneration.entity.Invoice;
import data_generation.data_generation.DataGeneration.entity.InvoiceItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class CsvInvoiceReader {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Invoice> readInvoices(String fileName) throws IOException {
        List<Invoice> invoices = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(fileName).getInputStream()))) {

            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { // skip header
                    isFirst = false;
                    continue;
                }
                String[] fields = line.split(",", -1); // -1 to keep empty fields

                Invoice invoice = new Invoice();
                invoice.setInvoiceDate(fields[0].isEmpty() ? null : LocalDateTime.parse(fields[0], DATE_FORMAT));
                invoice.setInvoiceNumber(fields[1]);
                invoice.setBillingAddress(fields[2]);
                invoice.setBillingCity(fields[3]);
                invoice.setBillingState(fields[4]);
                invoice.setBillingCountry(fields[5]);
                invoice.setBillingPostcode(fields[6]);
                invoice.setTotal(fields[7].isEmpty() ? 0.0 : Double.parseDouble(fields[7]));
                invoice.setPaymentMethod(fields[8]);
                invoice.setPaymentAccountName(fields[9]);
                invoice.setPaymentAccountNumber(fields[10]);
                invoice.setStatus(fields[11]);
                invoice.setStatusMessage(fields.length > 12 ? fields[12] : null);

                invoices.add(invoice);
            }
        }
        return invoices;
    }

    public List<InvoiceItem> readInvoiceItems(String fileName) throws IOException {
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(fileName).getInputStream()))) {

            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { // skip header
                    isFirst = false;
                    continue;
                }
                String[] fields = line.split(",", -1); // -1 to keep empty fields

                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setUnitPrice(fields[0].isEmpty() ? 0.0 : Double.parseDouble(fields[0]));
                invoiceItem.setQuantity(fields[1].isEmpty() ? 0 : Integer.parseInt(fields[1]));

                invoiceItems.add(invoiceItem);
            }
        }
        return invoiceItems;
    }
}