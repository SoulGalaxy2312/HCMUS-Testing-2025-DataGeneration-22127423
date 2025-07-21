package data_generation.data_generation.DataGeneration.db;

import java.sql.*;

import org.springframework.stereotype.Service;

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
}
