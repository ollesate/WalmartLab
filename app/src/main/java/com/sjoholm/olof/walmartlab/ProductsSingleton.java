package com.sjoholm.olof.walmartlab;

import java.util.ArrayList;
import java.util.List;

public final class ProductsSingleton {
    private static ProductsSingleton sInstance;

    private List<Product> mProducts;

    private ProductsSingleton() {
    }

    public static ProductsSingleton getInstance() {
        if (sInstance == null) {
            sInstance = new ProductsSingleton();
        }
        return sInstance;
    }

    public void setProducts(List<Product> products) {
        mProducts = products;
    }

    public List<Product> getProducts() {
        return mProducts;
    }
}
