package com.sjoholm.olof.walmartlab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Listener<ProductResult>,
        ProductRecyclerViewAdapter.OnItemClickListener {
    private static final String KEY_TOTAL_PRODUCTS = "TOTAL_PRODUCTS";
    private static final String KEY_PRODUCTS = "PRODUCTS";

    private ProductRecyclerViewAdapter mAdapter;
    private ProductPageRequester mProductRequester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new ProductRecyclerViewAdapter(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        mProductRequester = new ProductPageRequester(15, requestQueue, this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mProductRequester.makeRequestsAtPage(layoutManager.findLastVisibleItemPosition());
            }
        });

        if (savedInstanceState != null) {
            ArrayList<Product> products = savedInstanceState.getParcelableArrayList(KEY_PRODUCTS);
            int totalProducts = savedInstanceState.getInt(KEY_TOTAL_PRODUCTS, Integer.MAX_VALUE);
            mAdapter.addProducts(products);
            mProductRequester.setTotalPages(totalProducts);
            mProductRequester.setCurrentPage(products.size());
        } else {
            mProductRequester.loadInitialPages();
        }
    }

    @Override
    public void onResponse(ProductResult result) {
        mProductRequester.setTotalPages(result.totalProducts);
        mAdapter.addProducts(result.products);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_PRODUCTS, mAdapter.getProducts());
        outState.putInt(KEY_TOTAL_PRODUCTS, mProductRequester.getTotalPages());
    }

    @Override
    public void onItemClicked(@NonNull Product product, int index) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(ProductDetailActivity.EXTRA_CURRENT_PRODUCT, index);
        extras.putParcelableArrayList(ProductDetailActivity.EXTRA_PRODUCTS, mAdapter.getProducts());
        extras.putInt(ProductDetailActivity.EXTRA_TOTAL_PRODUCTS,
                mProductRequester.getTotalPages());
        intent.putExtras(extras);
        startActivity(intent);
    }
}
