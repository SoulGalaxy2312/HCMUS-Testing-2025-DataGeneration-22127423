package data_generation.data_generation.DataGeneration.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import data_generation.data_generation.DataGeneration.config.DatabaseConnection;
import data_generation.data_generation.DataGeneration.db.DatabaseWriteService;

@Component
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

    public TestDataHelper(DatabaseConnection databaseConnection,
                          DatabaseWriteService databaseWriteService) {
        this.databaseConnection = databaseConnection;
        this.databaseWriteService = databaseWriteService;
    }

    public String[] getNewCategories() {
        return newCategories;
    }

    public String[] getNewBrands() {
        return newBrands;
    }

    public List<Integer> getNewImagesIds() {
        return newImagesIds;
    }

    public Map<String, Integer> getNewCategoryMap() {
        return newCategoryMap;
    }

    public Map<String, Integer> getNewBrandMap() {
        return newBrandMap;
    }
    
    public List<Integer> getNewProductIds() {
        return this.newProductIds;
    }

    public Random getRandom() {
        return random;
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public DatabaseWriteService getDatabaseWriteService() {
        return databaseWriteService;
    }
}