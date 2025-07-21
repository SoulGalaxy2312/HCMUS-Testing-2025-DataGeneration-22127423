package data_generation.data_generation.DataGeneration.db;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import data_generation.data_generation.DataGeneration.entity.ProductCsvRecord;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvProductReader {

    public List<ProductCsvRecord> readProducts(String fileName) {
        List<ProductCsvRecord> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(fileName).getInputStream()))) {

            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false; // Skip header
                    continue;
                }

                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                String name = parts[0].trim();
                String description = parts[1].trim();

                products.add(new ProductCsvRecord(name, description));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }
}

