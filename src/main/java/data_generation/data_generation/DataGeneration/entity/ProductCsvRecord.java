package data_generation.data_generation.DataGeneration.entity;

public class ProductCsvRecord {
    private final String name;
    private final String description;

    public ProductCsvRecord(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}