package com.sjoholm.olof.walmartlab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ProductDetailFragment extends Fragment {
    private static String EXTRA_PRODUCT = "PRODUCT";

    public static ProductDetailFragment newInstance(@NonNull Product product) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_PRODUCT, product);
        ProductDetailFragment fragment = new ProductDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.product_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView productImage = (ImageView) view.findViewById(R.id.product_image);
        TextView productName = (TextView) view.findViewById(R.id.product_name);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView price = (TextView) view.findViewById(R.id.price);
        TextView reviewRating = (TextView) view.findViewById(R.id.review_rating);
        TextView reviewCount = (TextView) view.findViewById(R.id.review_count);
        TextView inStock = (TextView) view.findViewById(R.id.in_stock);
        ViewGroup starHolder = (ViewGroup) view.findViewById(R.id.star_holder);

        Product product = getArguments().getParcelable(EXTRA_PRODUCT);

        if (product == null) {
            throw new IllegalArgumentException("No product provided");
        }

        // Bind views
        Picasso.with(getContext()).load(product.productImage).into(productImage);

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

        int resString = product.reviewCount == 1 ? R.string.product_rating_count_singular
                : R.string.product_rating_count_plural;
        reviewCount.setText(getString(resString, product.reviewCount));
        inStock.setText(product.inStock ? R.string.product_in_stock
                : R.string.product_out_of_stock);
    }
}
