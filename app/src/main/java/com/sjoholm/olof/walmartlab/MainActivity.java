package com.sjoholm.olof.walmartlab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Listener<ProductResult>,
        ProductRecyclerViewAdapter.OnItemClickListener, Response.ErrorListener {
    private static final String LOG_TAG = "MainActivity";

    private static final String KEY_TOTAL_PRODUCTS = "TOTAL_PRODUCTS";
    private static final String KEY_PRODUCTS = "PRODUCTS";
    private static final int INITIAL_LOADED_PAGES = 20;
    private static final int MIN_AMOUNT_REQUEST = 5;
    private static final int PAGES_TO_LOAD_AHEAD = 20;

    private ProductRecyclerViewAdapter mAdapter;
    private ProductPageRequester mPageRequester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new ProductRecyclerViewAdapter(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        mPageRequester = new ProductPageRequester(requestQueue, this, this);
        mPageRequester.setMinAmountRequest(MIN_AMOUNT_REQUEST);
        mPageRequester.setPagesToLoadAhead(PAGES_TO_LOAD_AHEAD);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mPageRequester.makeRequestsAtPage(layoutManager.findLastVisibleItemPosition());
            }
        });

        if (savedInstanceState != null) {
            ArrayList<Product> products = savedInstanceState.getParcelableArrayList(KEY_PRODUCTS);
            int totalProducts = savedInstanceState.getInt(KEY_TOTAL_PRODUCTS, Integer.MAX_VALUE);
            if (products != null) {
                mAdapter.addProducts(products);
                mPageRequester.setTotalPages(totalProducts);
                mPageRequester.setCurrentPage(products.size());
            }
        } else {
            mPageRequester.requestPages(INITIAL_LOADED_PAGES);
        }
    }

    @Override
    public void onResponse(ProductResult result) {
        mPageRequester.setTotalPages(result.totalProducts);
        mAdapter.addProducts(result.products);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_PRODUCTS, mAdapter.getProducts());
        outState.putInt(KEY_TOTAL_PRODUCTS, mPageRequester.getTotalPages());
    }

    @Override
    public void onItemClicked(@NonNull Product product, int index) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(ProductDetailActivity.EXTRA_CURRENT_PRODUCT, index);
        extras.putParcelableArrayList(ProductDetailActivity.EXTRA_PRODUCTS, mAdapter.getProducts());
        extras.putInt(ProductDetailActivity.EXTRA_TOTAL_PRODUCTS,
                mPageRequester.getTotalPages());
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(LOG_TAG, error.getMessage(), error);
    }
}
