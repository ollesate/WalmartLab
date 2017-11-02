package com.sjoholm.olof.walmartlab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import static android.R.attr.data;

public class ProductDetailActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCTS = "PRODUCTS";
    public static final String EXTRA_CURRENT_PRODUCT = "CURRENT_PRODUCT";
    public static final String EXTRA_TOTAL_PRODUCTS = "TOTAL_PRODUCTS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView productImage = (ImageView) findViewById(R.id.product_image);
        TextView productName = (TextView) findViewById(R.id.product_name);
        TextView description = (TextView) findViewById(R.id.description);
        TextView price = (TextView) findViewById(R.id.price);
        TextView reviewRating = (TextView) findViewById(R.id.review_rating);
        TextView reviewCount = (TextView) findViewById(R.id.review_count);
        TextView inStock = (TextView) findViewById(R.id.in_stock);
        ViewGroup starHolder = (ViewGroup) findViewById(R.id.star_holder);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            List<Product> products = extras.getParcelableArrayList(EXTRA_PRODUCTS);
            int currentProduct = extras.getInt(EXTRA_CURRENT_PRODUCT);
            int totalProducts = extras.getInt(EXTRA_TOTAL_PRODUCTS);

            if (products == null) {
                throw new IllegalArgumentException("No products provided");
            }

            Product product = products.get(currentProduct);

            // Bind views
            Picasso.with(this).load(product.productImage).into(productImage);

            productName.setText(product.productName);

            String descr = product.longDescription != null ? product.longDescription
                    : product.shortDescription;
            if (descr != null) {
                description.setText(Html.fromHtml(descr));
            }
            price.setText(product.price);


            double rating = Double.valueOf(product.reviewRating);
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            reviewRating.setText(df.format(rating));

            long stars = Math.round(rating);
            for (int i = 0; i < 5; i++) {
                ImageView starImage = (ImageView) starHolder.getChildAt(i);
                int image = i < stars ? R.drawable.ic_star_full : R.drawable.ic_star_empty;
                starImage.setImageDrawable(getResources().getDrawable(image));
            }

            reviewCount.setText(product.reviewCount + " ratings");
            inStock.setText(product.inStock ? "Currently in stock" : "Currently out of stock");
        }

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
}
