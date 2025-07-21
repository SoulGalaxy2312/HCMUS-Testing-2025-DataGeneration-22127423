package data_generation.data_generation.DataGeneration.db;

import java.sql.*;
import java.util.*;

import org.springframework.stereotype.Service;

import data_generation.data_generation.DataGeneration.utils.SlugUtil;

@Service
public class DatabaseWriteService {

    private final SlugUtil slugUtil;
    
    public DatabaseWriteService(SlugUtil slugUtil) {
        this.slugUtil = slugUtil;
    } 

    public Map<String, Integer> loadMap(Connection conn, String query) throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("name"), rs.getInt("id"));
            }
        }
        return map;
    }

    public int getMaxId(Connection conn, String table, int defaultVal) throws SQLException {
        String sql = "SELECT COALESCE(MAX(id), ?) FROM " + table;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, defaultVal);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    public String insertCategory(Connection conn, String name, String slug, Timestamp createdAt, Timestamp updatedAt) throws SQLException {
        if (slug == null || slug.isEmpty()) {
            slug = slugUtil.slugify(name);
        }
        String categoryId = UUID.randomUUID().toString().replace("-", "").substring(0, 26);
        String sql = "INSERT INTO categories (id, name, slug, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryId);
            ps.setString(2, name);
            ps.setString(3, slug);
            ps.setTimestamp(4, createdAt);
            ps.setTimestamp(5, updatedAt);
            ps.executeUpdate();
            return categoryId;
        }
    }

    public String insertBrand(Connection conn, String name, String slug) throws SQLException {
        if (slug == null || slug.isEmpty()) {
            slug = slugUtil.slugify(name);
        }

        String brandId = UUID.randomUUID().toString().replace("-", "").substring(0, 26);
        String sql = "INSERT INTO brands (id, name, slug, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, brandId);
            ps.setString(2, name);
            ps.setString(3, slug);
            ps.executeUpdate();
            return brandId;
        }
    }

    public String insertImage(Connection conn, String url, String srcUrl, String fname, String title) throws SQLException {
        String imageId = UUID.randomUUID().toString().replace("-", "").substring(0, 26);
        String sql = "INSERT INTO product_images (id, by_name, by_url, source_name, source_url, file_name, title, created_at, updated_at) " +
                    "VALUES (?, NULL, ?, 'API', ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imageId);
            ps.setString(2, url);
            ps.setString(3, srcUrl);
            ps.setString(4, fname);
            ps.setString(5, title);
            ps.executeUpdate();
            return imageId;
        }
    }


    public String insertProduct(Connection conn, String name, String desc, int stock, double price,
                          String brandName, String categoryName, String imgId) throws SQLException {
        // Step 1: Lookup brand_id
        String brandId = getIdByName(conn, "brands", brandName);
        if (brandId == null) {
            throw new SQLException("Brand not found: " + brandName);
        }

        // Step 2: Lookup category_id
        String catId = getIdByName(conn, "categories", categoryName);
        if (catId == null) {
            throw new SQLException("Category not found: " + categoryName);
        }

        String productId = UUID.randomUUID().toString().replace("-", "").substring(0, 26);
        String sql = "INSERT INTO products (id, name, description, stock, price, is_location_offer, is_rental, " +
                     "brand_id, category_id, product_image_id, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, 0, 0, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            ps.setString(2, name);
            ps.setString(3, desc);
            ps.setInt(4, stock);
            ps.setDouble(5, price);
            ps.setString(6, brandId);
            ps.setString(7, catId);
            ps.setString(8, imgId);
            ps.executeUpdate();
            return productId;
        }
    }

    private String getIdByName(Connection conn, String table, String name) throws SQLException {
        String sql = "SELECT id FROM " + table + " WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
            }
        }
        return null; // not found
    }
}
