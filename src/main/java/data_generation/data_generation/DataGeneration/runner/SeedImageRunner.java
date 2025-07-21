package data_generation.data_generation.DataGeneration.runner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import data_generation.data_generation.DataGeneration.config.DatabaseConnection;
import data_generation.data_generation.DataGeneration.db.DatabaseWriteService;
import data_generation.data_generation.DataGeneration.image.FetchImageService;

@Component
public class SeedImageRunner implements CommandLineRunner {

    private final FetchImageService fetchImageUseCase;
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

    public SeedImageRunner(FetchImageService fetchImageUseCase, DatabaseConnection databaseConnection, DatabaseWriteService databaseWriteService) {
        this.fetchImageUseCase = fetchImageUseCase;
        this.databaseConnection = databaseConnection;
        this.databaseWriteService = databaseWriteService;
    }

    @Override
    public void run(String... args) throws SQLException {
        Connection conn = databaseConnection.connect();

        System.out.println("🔍 Seeding image from Pixabay...");

        var image = fetchImageUseCase.fetchImage("drill tool");
        System.out.println("🖼 Image: " + image[0]);
        System.out.println("🔗 Page: " + image[1]);

        System.out.println("✅ Done seeding.");
    }
}