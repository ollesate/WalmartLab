package com.sjoholm.olof.walmartlab;

import java.util.ArrayList;
import java.util.List;

public final class ProductsSingleton {
    private static ProductsSingleton sInstance;

    private ArrayList<Product> mProducts;

    private ProductsSingleton() {
    }

    public static ProductsSingleton getInstance() {
        if (sInstance == null) {
            sInstance = new ProductsSingleton();
        }
        return sInstance;
    }

    public void setProducts(ArrayList<Product> products) {
        mProducts = products;
    }

    public ArrayList<Product> getProducts() {
        return mProducts;
    }
}
