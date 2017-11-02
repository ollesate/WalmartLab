package com.sjoholm.olof.walmartlab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCTS = "PRODUCTS";
    public static final String EXTRA_CURRENT_PRODUCT = "CURRENT_PRODUCT";
    public static final String EXTRA_TOTAL_PRODUCTS = "TOTAL_PRODUCTS";
    private ProductPagerAdapter mAdapter;

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

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Bundle extras = getIntent().getExtras();
        List<Product> products = extras.getParcelableArrayList(EXTRA_PRODUCTS);
        int currentProduct = extras.getInt(EXTRA_CURRENT_PRODUCT);
        int totalProducts = extras.getInt(EXTRA_TOTAL_PRODUCTS);

        if (products == null) {
            throw new IllegalArgumentException("No products provided");
        }

        mAdapter.addProducts(products);
        mAdapter.notifyDataSetChanged();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
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

    private static class ProductPagerAdapter extends FragmentStatePagerAdapter {
        private List<Product> mProducts = new ArrayList<>();

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
    }
}
