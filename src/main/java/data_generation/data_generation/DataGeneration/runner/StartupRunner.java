package data_generation.data_generation.DataGeneration.runner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import data_generation.data_generation.DataGeneration.db.CsvInvoiceReader;
import data_generation.data_generation.DataGeneration.db.CsvProductReader;
import data_generation.data_generation.DataGeneration.db.CsvUserReader;
import data_generation.data_generation.DataGeneration.entity.Invoice;
import data_generation.data_generation.DataGeneration.entity.InvoiceItem;
import data_generation.data_generation.DataGeneration.entity.ProductCsvRecord;
import data_generation.data_generation.DataGeneration.entity.User;
import data_generation.data_generation.DataGeneration.image.FetchImageService;
import data_generation.data_generation.DataGeneration.utils.TestDataHelper;

@Component
public class StartupRunner implements CommandLineRunner {

    private final TestDataHelper testDataHelper;
    private final CsvProductReader csvProductReader;
    private final CsvUserReader csvUserReader;
    private final CsvInvoiceReader csvInvoiceReader;
    private final FetchImageService fetchImageService;
    private final ConfigurableApplicationContext context;

    public StartupRunner(TestDataHelper testDataHelper, CsvProductReader csvProductReader, CsvUserReader csvUserReader, CsvInvoiceReader csvInvoiceReader, FetchImageService fetchImageService, ConfigurableApplicationContext context) {
        this.testDataHelper = testDataHelper;
        this.csvProductReader = csvProductReader;
        this.csvUserReader = csvUserReader;
        this.csvInvoiceReader = csvInvoiceReader;
        this.fetchImageService = fetchImageService;
        this.context = context;
    }

    @Override
    public void run(String... args) throws SQLException, Exception {
        try (Connection conn = testDataHelper.getDatabaseConnection().connect()) {
            System.out.println("[STARTUP] Connected to DB.");

            Map<String, Integer> newCategoryMap = testDataHelper.getNewCategoryMap();
            Map<String, Integer> newBrandMap = testDataHelper.getNewBrandMap();
            List<Integer> newProductIds = testDataHelper.getNewProductIds();
            List<Integer> newImagesIds = testDataHelper.getNewImagesIds();
            List<Integer> newUserIds = testDataHelper.getNewUserIds();
            List<Integer> newInvoiceIds = testDataHelper.getNewInvoiceIds();
            List<Integer> newInvoiceItemIds = testDataHelper.getNewInvoiceItemIds();

            // Step 4: Insert users
            List<User> users = csvUserReader.readUsersFromCsv("static/users/sample-users.csv");
            for (User user : users) {
                int userId = testDataHelper.getDatabaseWriteService().insertUser(conn, user);
                testDataHelper.getNewUserIds().add(userId);
                System.out.println("Inserted user: " + user.getFirstName() + " " + user.getLastName());
                newUserIds.add(userId);
            }

            // Step 1: Insert categories
            for (String category : testDataHelper.getNewCategories()) {
                int categoryId = testDataHelper.getDatabaseWriteService().insertCategory(conn, category, now(), now());
                newCategoryMap.put(category, categoryId);
                System.out.println("Inserted category: " + category);
            }

            // Step 2: Insert brands
            for (String brand : testDataHelper.getNewBrands()) {
                int brandId = testDataHelper.getDatabaseWriteService().insertBrand(conn, brand);
                newBrandMap.put(brand, brandId);
                System.out.println("Inserted brand: " + brand);
            }

            // Step 3: Insert products from CSV
            List<ProductCsvRecord> products = csvProductReader.readProducts("static/products/sample-products.csv");
            Random random = testDataHelper.getRandom();
            int cur = 0;

            for (ProductCsvRecord product : products) {
                String name = product.getName();
                String description = product.getDescription();
                int stock = random.nextInt(100) + 1;
                double price = Math.round((random.nextDouble() * 500 + 50) * 100.0) / 100.0;

                String brand = testDataHelper.getNewBrands()[cur % 2];
                int brandId = newBrandMap.get(brand);

                String category = testDataHelper.getNewCategories()[cur / 30];
                int categoryId = newCategoryMap.get(category);

                String[] img = fetchImageService.fetchImage(name);
                int imgId = testDataHelper.getDatabaseWriteService().insertImage(conn, img[0], img[1], name);
                newImagesIds.add(imgId);

                int productId = testDataHelper.getDatabaseWriteService().insertProduct(
                    conn, name, description, stock, price, brandId, categoryId, imgId);

                newProductIds.add(productId);
                System.out.println("Inserted product: " + name);
                cur++;
            }

            // Insert invoices
            List<Invoice> invoices = csvInvoiceReader.readInvoices("static/invoices/sample-invoices.csv");
            for (Invoice invoice : invoices) {
                int userId = newUserIds.get(testDataHelper.getRandom().nextInt(newUserIds.size()));
                invoice.setUserId(userId);
                int invoiceId = testDataHelper.getDatabaseWriteService().insertInvoice(conn, invoice);
                System.out.println("Inserted invoice: " + invoice.getInvoiceNumber() + " with ID: " + invoiceId);
                newInvoiceIds.add(invoiceId);
            }

            // Insert invoice items
            List<InvoiceItem> invoiceItems = csvInvoiceReader.readInvoiceItems("static/invoices/sample-invoice-items.csv");
            for (InvoiceItem item : invoiceItems) {
                item.setInvoiceId(newInvoiceIds.get(testDataHelper.getRandom().nextInt(newInvoiceIds.size())));
                item.setProductId(newProductIds.get(testDataHelper.getRandom().nextInt(newProductIds.size())));
                int itemId = testDataHelper.getDatabaseWriteService().insertInvoiceItem(conn, item);
                System.out.println("Inserted invoice item for invoice ID: " + item.getInvoiceId());
                newInvoiceItemIds.add(itemId);
            }

            System.out.println("[STARTUP] Data generation completed.");
            System.out.println("Press ENTER to trigger cleanup and shut down...");
            System.in.read();

            context.close(); // triggers cleanup
        }
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
}