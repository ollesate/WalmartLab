package com.sjoholm.olof.walmartlab;


public class ProductResult {
    public final Product[] products;
    public final int totalProducts;

    public ProductResult(Product[] products, int totalProducts) {
        this.products = products;
        this.totalProducts = totalProducts;
    }
}
