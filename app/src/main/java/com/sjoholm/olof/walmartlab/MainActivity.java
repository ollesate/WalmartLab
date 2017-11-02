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

public class MainActivity extends AppCompatActivity implements Listener<ProductResult>,MyAdapter.OnItemClickListener {
    private static final String KEY_TOTAL_PRODUCTS = "TOTAL_PRODUCTS";
    private static final String KEY_PRODUCTS = "PRODUCTS";

    private MyAdapter mAdapter;
    private ProductRequester mProductRequester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MyAdapter(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        mProductRequester = new ProductRequester(requestQueue, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
                int scrollMax = recyclerView.computeVerticalScrollRange();

                float scale = currentScroll / (scrollMax * 1.0f);
                if (dy > 0 && scale > 0.75) {
                    mProductRequester.request(30);
                }
//                Log.d("Olof", "Scroll " + currentScroll + ", " + scrollMax + ", " + scale);
            }
        });


        if (savedInstanceState != null) {
            ArrayList<Product> products = savedInstanceState.getParcelableArrayList(KEY_PRODUCTS);
            int totalProducts = savedInstanceState.getInt(KEY_TOTAL_PRODUCTS, Integer.MAX_VALUE);
            mAdapter.addProducts(products);
            mProductRequester.setTotalProducts(totalProducts);
        } else {
            mProductRequester.request(20);
        }

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onResponse(ProductResult result) {
        mProductRequester.setTotalProducts(result.totalProducts);
        mAdapter.addProducts(result.products);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_PRODUCTS, mAdapter.getProducts());
        outState.putInt(KEY_TOTAL_PRODUCTS, mProductRequester.getTotalProducts());
    }

    @Override
    public void onItemClicked(@NonNull Product product, int index) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(ProductDetailActivity.EXTRA_CURRENT_PRODUCT, index);
        extras.putParcelableArrayList(ProductDetailActivity.EXTRA_PRODUCTS, mAdapter.getProducts());
        extras.putInt(ProductDetailActivity.EXTRA_TOTAL_PRODUCTS,
                mProductRequester.getTotalProducts());
        intent.putExtras(extras);
        startActivity(intent);
    }
}
