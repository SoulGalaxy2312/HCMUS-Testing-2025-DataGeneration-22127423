package data_generation.data_generation.DataGeneration.db;

import java.sql.*;

import org.springframework.stereotype.Service;

import data_generation.data_generation.DataGeneration.entity.Invoice;
import data_generation.data_generation.DataGeneration.entity.InvoiceItem;
import data_generation.data_generation.DataGeneration.entity.User;
import data_generation.data_generation.DataGeneration.utils.SlugUtil;

@Service
public class DatabaseWriteService {

    private final SlugUtil slugUtil;
    
    public DatabaseWriteService(SlugUtil slugUtil) {
        this.slugUtil = slugUtil;
    }

    public int insertCategory(Connection conn, String name, Timestamp createdAt, Timestamp updatedAt) throws SQLException {
        String slug = slugUtil.slugify(name);

        String sql = "INSERT INTO categories (name, slug, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, slug);
            ps.setTimestamp(3, createdAt);
            ps.setTimestamp(4, updatedAt);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // returns the auto-incremented ID
                }
            }
        }

        return -1; // In case no key is generated
    }


    public String deleteCategory(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0 ? "Category deleted successfully" : "Category not found";
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Cannot delete category with existing products";
        }
    }

    public int insertBrand(Connection conn, String name) throws SQLException {
        String slug = slugUtil.slugify(name);

        String sql = "INSERT INTO brands (name, slug, created_at, updated_at) VALUES (?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, slug);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // returns the auto-incremented ID
                }
            }
        }
        return -1;
    }

    public String deleteBrand(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM brands WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0 ? "Brand deleted successfully" : "Brand not found";
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Cannot delete brand with existing products";
        }
    }

    public int insertImage(Connection conn, String url, String srcUrl, String name) throws SQLException {
        String fName = url.split("/")[url.split("/").length - 1];

        String sql = "INSERT INTO product_images (by_name, by_url, source_name, source_url, file_name, title, created_at, updated_at) " +
                    "VALUES (0, ?, 'API', ?, ?, ?, NOW(), NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, url);     // webformatURL from Pixabay
            ps.setString(2, srcUrl);  // pageURL from Pixabay
            ps.setString(3, fName);   // file name extracted from URL
            ps.setString(4, name);     // title of the image
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return auto-increment ID
                }
            }
        }

        return -1;
    }


    public String deleteImage(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM product_images WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0 ? "Image deleted successfully" : "Image not found";
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Cannot delete image with existing products";
        }
    }

    public int insertProduct(Connection conn, String name, String desc, int stock, double price,
                          int brandId, int categoryId, int imgId) throws SQLException {
        String sql = "INSERT INTO products (name, description, stock, price, is_location_offer, is_rental, " +
                     "brand_id, category_id, product_image_id, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, 0, 0, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setInt(3, stock);
            ps.setDouble(4, price);
            ps.setInt(5, brandId);
            ps.setInt(6, categoryId);
            ps.setInt(7, imgId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // returns the auto-incremented ID
                }
            }
        }

        return -1; // In case no key is generated
    }

    public String deleteProduct(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0 ? "Product deleted successfully" : "Product not found";
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Cannot delete product with existing orders";
        }
    }

    public int insertUser(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO users (first_name, last_name, address, city, state, country, postcode, phone, dob, email, password, role, enabled, failed_login_attempts, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getAddress());
            ps.setString(4, user.getCity());
            ps.setString(5, user.getState());
            ps.setString(6, user.getCountry());
            ps.setString(7, user.getPostcode());
            ps.setString(8, user.getPhone());
            ps.setDate(9, Date.valueOf(user.getDob()));
            ps.setString(10, user.getEmail());
            ps.setString(11, user.getPassword());
            ps.setString(12, user.getRole());
            ps.setBoolean(13, user.isEnabled());
            ps.setInt(14, user.getFailedLoginAttempts());
            ps.setTimestamp(15, user.getCreatedAt());
            ps.setTimestamp(16, user.getUpdatedAt());
            
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return the generated user ID
                }
            }
        }

        return -1;
    }

    public String deleteUser(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0 ? "User deleted successfully" : "User not found";
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Cannot delete user due to existing dependencies";
        }
    }

    public int insertInvoice(Connection conn, Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (user_id, invoice_date, invoice_number, billing_address, billing_city, billing_state, billing_country, billing_postcode, total, payment_method, payment_account_name, payment_account_number, status, status_message) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, invoice.getUserId());
            ps.setTimestamp(2, invoice.getInvoiceDate() == null ? null : Timestamp.valueOf(invoice.getInvoiceDate()));
            ps.setString(3, invoice.getInvoiceNumber());
            ps.setString(4, invoice.getBillingAddress());
            ps.setString(5, invoice.getBillingCity());
            ps.setString(6, invoice.getBillingState());
            ps.setString(7, invoice.getBillingCountry());
            ps.setString(8, invoice.getBillingPostcode());
            ps.setDouble(9, invoice.getTotal());
            ps.setString(10, invoice.getPaymentMethod());
            ps.setString(11, invoice.getPaymentAccountName());
            ps.setString(12, invoice.getPaymentAccountNumber());
            ps.setString(13, invoice.getStatus());
            ps.setString(14, invoice.getStatusMessage());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return the generated invoice ID
                }
            }
        }
        return -1;
    }

    public String deleteInvoice(Connection conn, int invoiceId) throws SQLException {
        String sql = "DELETE FROM invoices WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0 ? "Invoice deleted successfully" : "Invoice not found";
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Cannot delete invoice due to existing dependencies";
        }
    }

    public int insertInvoiceItem(Connection conn, InvoiceItem item) throws SQLException {
        String sql = "INSERT INTO invoice_items (invoice_id, product_id, unit_price, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getInvoiceId());
            ps.setInt(2, item.getProductId());
            ps.setDouble(3, item.getUnitPrice());
            ps.setInt(4, item.getQuantity());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return the generated invoice item ID
                }
            }
        }
        return -1;
    }

    public String deleteInvoiceItem(Connection conn, int invoiceItemId) throws SQLException {
        String sql = "DELETE FROM invoice_items WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceItemId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0 ? "Invoice item deleted successfully" : "Invoice item not found";
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Cannot delete invoice item due to existing dependencies";
        }
    }

}
