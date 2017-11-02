package com.sjoholm.olof.walmartlab;

public class Product {
    public final String productId;
    public final String productName;
    public final String description;
    public final String price;
    public final String reviewRating;
    public final String reviewCount;
    public final String productImage;
    public final boolean inStock;

    public Product(String productId, String productName, String description, String price,
                   String reviewRating, String reviewCount, String productImage, boolean inStock) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.reviewRating = reviewRating;
        this.reviewCount = reviewCount;
        this.inStock = inStock;
        this.productImage = productImage;
    }
}