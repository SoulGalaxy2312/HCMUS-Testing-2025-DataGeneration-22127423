package data_generation.data_generation.DataGeneration.runner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import data_generation.data_generation.DataGeneration.utils.TestDataHelper;
import jakarta.annotation.PreDestroy;

@Component
public class CleanupRunner {

    private final TestDataHelper testDataHelper;

    public CleanupRunner(TestDataHelper testDataHelper) {
        this.testDataHelper = testDataHelper;
    }

    @PreDestroy
    public void onShutdown() {
        try (Connection conn = testDataHelper.getDatabaseConnection().connect()) {
            System.out.println("[CLEANUP] Application is shutting down. Cleaning up test data...");

            List<Integer> newProductIds = testDataHelper.getNewProductIds();
            List<Integer> newImagesIds = testDataHelper.getNewImagesIds();
            Map<String, Integer> newBrandMap = testDataHelper.getNewBrandMap();
            Map<String, Integer> newCategoryMap = testDataHelper.getNewCategoryMap();

            // Delete products
            for (Integer productId : newProductIds) {
                testDataHelper.getDatabaseWriteService().deleteProduct(conn, productId);
                System.out.println("Deleted product with ID: " + productId);
            }

            // Delete images
            for (Integer imageId : newImagesIds) {
                testDataHelper.getDatabaseWriteService().deleteImage(conn, imageId);
                System.out.println("Deleted image with ID: " + imageId);
            }

            // Delete brands
            for (Integer brandId : newBrandMap.values()) {
                testDataHelper.getDatabaseWriteService().deleteBrand(conn, brandId);
                System.out.println("Deleted brand with ID: " + brandId);
            }

            // Delete categories
            for (Integer categoryId : newCategoryMap.values()) {
                testDataHelper.getDatabaseWriteService().deleteCategory(conn, categoryId);
                System.out.println("Deleted category with ID: " + categoryId);
            }

            System.out.println("[CLEANUP] Completed.");
        } catch (SQLException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}