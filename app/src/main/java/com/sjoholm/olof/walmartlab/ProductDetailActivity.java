package com.sjoholm.olof.walmartlab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity
        implements Response.Listener<ProductResult>, Response.ErrorListener {
    private static final String KEY_PRODUCTS = "PRODUCTS";

    public static final String EXTRA_CURRENT_PRODUCT = "CURRENT_PRODUCT";
    public static final String EXTRA_TOTAL_PRODUCTS = "TOTAL_PRODUCTS";

    private static final String LOG_TAG = "ProductDetailActivity";
    private static final int PAGES_TO_LOAD_AHEAD = 5;

    private ProductPagerAdapter mAdapter;
    private ProductPageRequester mPageRequester;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mAdapter = new ProductPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                mPageRequester.makeRequestsAtPage(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        List<Product> products;
        if (savedInstanceState != null) {
            products = savedInstanceState.getParcelableArrayList(KEY_PRODUCTS);
        } else {
            products = ProductsSingleton.getInstance().getProducts();
            // Clean up
            ProductsSingleton.getInstance().setProducts(null);
        }

        Bundle extras = getIntent().getExtras();
        int currentProduct = extras.getInt(EXTRA_CURRENT_PRODUCT);
        int totalProducts = extras.getInt(EXTRA_TOTAL_PRODUCTS);

        if (products == null || products.isEmpty()) {
            Log.e(LOG_TAG, "No products provided");
            finish();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        mPageRequester = new ProductPageRequester(requestQueue, this, this);
        mPageRequester.setPagesToLoadAhead(PAGES_TO_LOAD_AHEAD);
        mPageRequester.setTotalPages(totalProducts);
        mPageRequester.setCurrentPage(products.size());

        mAdapter.addProducts(products);
        mAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(currentProduct);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_PRODUCTS, mAdapter.getProducts());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResponse(ProductResult response) {
        mPageRequester.setTotalPages(response.totalProducts);
        mAdapter.addProducts(Arrays.asList(response.products));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(LOG_TAG, error.getMessage(), error);
    }

    private static class ProductPagerAdapter extends FragmentStatePagerAdapter {
        private final ArrayList<Product> mProducts = new ArrayList<>();

        public ProductPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addProducts(List<Product> products) {
            mProducts.addAll(products);
        }

        @Override
        public Fragment getItem(int position) {
            return ProductDetailFragment.newInstance(mProducts.get(position));
        }

        @Override
        public int getCount() {
            return mProducts.size();
        }

        public ArrayList<Product> getProducts() {
            return mProducts;
        }
    }
}
