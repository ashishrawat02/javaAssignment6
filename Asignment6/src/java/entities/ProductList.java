/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author c0649005
 */
@ApplicationScoped
public class ProductList {

    private List<Product> productlist;

    public ProductList() {
        productlist = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM product";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getInt("Quantity"));
                productlist.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public JsonArray toJSON() {
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Product p : productlist) {
            json.add(p.toJSON());
        }
        return json.build();

    }

    public Product get(int productId) {
        Product result = null;
        for (int i = 0; i < productlist.size() && result == null; i++) {
            Product p = productlist.get(i);
            if (p.getProductId() == productId) {
                result = p;
            }
        }
        return result;

    }

    public void add(Product p) throws Exception {
        int result = doUpdate("INSERT INTO product (ProductID, Name, Description, Quantity) VALUES (?, ?, ?, ?)",
                String.valueOf(p.getProductId()),
                p.getName(),
                p.getDescription(),
                String.valueOf(p.getQuantity()));
        if (result > 0) {
            productlist.add(p);
        } else {
            throw new Exception("Error Inserting value");
        }
    }

    public void remove(Product p) throws Exception {
        remove(p.getProductId());
    }

    public void remove(int productId) throws Exception {
        int result = doUpdate("DELETE FROM product WHERE ProductID = ?",
                String.valueOf(productId));
        if (result > 0) {
            Product original = get(productId);
            productlist.remove(original);
        } else {
            throw new Exception("Error Deleting value");
        }
    }

    public void set(int productId, Product product) {
        Product original = get(productId);
        original.setName(product.getName());
        original.setDescription(product.getDescription());
        original.setQuantity(product.getQuantity());

    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String jdbc = "jdbc:mysql://localhost/ashish";
            String user = "root";
            String pass = "";
            conn = DriverManager.getConnection(jdbc, user, pass);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

//    private String getResult(String query, String... params) throws SQLException {
//        StringBuilder sb = new StringBuilder();
//        try (Connection conn = getConnection()) {
//            PreparedStatement pstmt = conn.prepareStatement(query);
//            for (int i = 1; i <= params.length; i++) {
//                pstmt.setString(i, params[i - 1]);
//            }
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                sb.append(String.format("%s\t%s\t%s\t%s\n", rs.getInt("id"), rs.getString("name"),
//                        rs.getString("description"), rs.getInt("quantity")));
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
//
//        }
//        return sb.toString();
//    }

    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
}
