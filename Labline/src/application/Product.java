package application;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String productCode;
    private String productList;
    private BigDecimal price;
    private String category; // ✅ New field

    public Product(int id, String productCode, String productList, BigDecimal price, String category) {
        this.id = id;
        this.productCode = productCode;
        this.productList = productList;
        this.price = price;
        this.category = category;
    }

    // Overloaded constructor if you don't always include category
    public Product(int id, String productCode, String productList, BigDecimal price) {
        this(id, productCode, productList, price, null);
    }

    // Getters
    public int getId() { return id; }
    public String getProductCode() { return productCode; }
    public String getProductList() { return productList; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; } // ✅ Getter

    // Setters
    public void setId(int id) { this.id = id; }

    public void setProductCode(String productCode) {
        if (productCode != null && !productCode.trim().isEmpty()) {
            this.productCode = productCode;
        }
    }

    public void setProductList(String productList) {
        this.productList = productList;
    }

    public void setPrice(BigDecimal price) {
        if (price == null) {
            this.price = null;
            return;
        }

        if (price.compareTo(BigDecimal.ZERO) >= 0) {
            this.price = price;
        } else {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    public void setCategory(String category) { // ✅ Setter
        this.category = category;
    }

    @Override
    public String toString() {
        String priceStr = (price != null) ? String.format("$%.2f", price) : "null";
        return String.format("%d - %s - %s (%s) [%s]", id, productCode, productList, priceStr, category);
    }
}
