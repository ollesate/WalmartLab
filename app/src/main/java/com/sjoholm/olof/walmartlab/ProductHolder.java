package com.sjoholm.olof.walmartlab;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static android.R.attr.rating;

class ProductHolder extends RecyclerView.ViewHolder {
    private TextView mProductName;
    private TextView mPrice;
    private TextView mReviewRating;
    private TextView mReviewCount;
    private TextView mInStock;
    private ImageView mProductImage;
    private final ViewGroup mStarHolder;

    public ProductHolder(View itemView) {
        super(itemView);
        mProductName = (TextView) itemView.findViewById(R.id.product_name);
        mPrice = (TextView) itemView.findViewById(R.id.price);
        mReviewRating = (TextView) itemView.findViewById(R.id.review_rating);
        mReviewCount = (TextView) itemView.findViewById(R.id.review_count);
        mInStock = (TextView) itemView.findViewById(R.id.in_stock);
        mProductImage = (ImageView) itemView.findViewById(R.id.product_image);
        mStarHolder = (ViewGroup) itemView.findViewById(R.id.star_holder);
    }

    public void onBind(@NonNull Product product) {
        mProductName.setText(product.productName);
        mPrice.setText(product.price);

        mReviewCount.setText(product.reviewCount + " ratings");
        mInStock.setText(product.inStock ? "In stock" : "Not in stock");
        Picasso.with(itemView.getContext()).load(product.productImage).into(mProductImage);

        double rating = Double.valueOf(product.reviewRating);
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        mReviewRating.setText(df.format(rating));

        long stars = Math.round(rating);
        for (int i = 0; i < 5; i++) {
            ImageView starImage = (ImageView) mStarHolder.getChildAt(i);
            int image = i < stars ? R.drawable.ic_star_full : R.drawable.ic_star_empty;
            starImage.setImageDrawable(itemView.getContext().getResources().getDrawable(image));
        }
    }
}
