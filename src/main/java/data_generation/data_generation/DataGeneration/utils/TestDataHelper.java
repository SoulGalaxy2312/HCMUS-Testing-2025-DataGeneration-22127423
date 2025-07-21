package data_generation.data_generation.DataGeneration.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import data_generation.data_generation.DataGeneration.config.DatabaseConnection;
import data_generation.data_generation.DataGeneration.db.DatabaseWriteService;
import lombok.Data;

@Component
@Data
public class TestDataHelper {

    private final DatabaseConnection databaseConnection;
    private final DatabaseWriteService databaseWriteService;

    private final String[] newCategories = {
        "Cutting Tools",
        "Measuring Instruments",
        "Ladders & Scaffolding",
        "Power Tool Accessories",
        "Workshop Lighting",
        "Lubricants & Cleaners",
        "Tool Sharpening Tools",
        "Workshop Furniture",
        "Fastening Tools",
        "Protective Storage Cases"
    };

    private final String[] newBrands = {
        "Iron Pro",
        "Tough Max"
    };

    private final Random random = new Random();

    private final Map<String, Integer> newCategoryMap = new HashMap<>();
    private final Map<String, Integer> newBrandMap = new HashMap<>();
    private final List<Integer> newProductIds = new ArrayList<>();
    private final List<Integer> newImagesIds = new ArrayList<>();
    private final List<Integer> newUserIds = new ArrayList<>();
    private final List<Integer> newInvoiceIds = new ArrayList<>();
    private final List<Integer> newInvoiceItemIds = new ArrayList<>();

    public TestDataHelper(DatabaseConnection databaseConnection,
                          DatabaseWriteService databaseWriteService) {
        this.databaseConnection = databaseConnection;
        this.databaseWriteService = databaseWriteService;
    }
}