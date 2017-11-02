package com.sjoholm.olof.walmartlab;


import java.util.List;

public class ProductResult {
    public final List<Product> products;
    public final int totalProducts;

    public ProductResult(List<Product> products, int totalProducts) {
        this.products = products;
        this.totalProducts = totalProducts;
    }
}
