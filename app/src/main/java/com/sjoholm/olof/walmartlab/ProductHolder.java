package com.sjoholm.olof.walmartlab;


import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;

class ProductHolder extends RecyclerView.ViewHolder {
    private final TextView mProductName;
    private final TextView mPrice;
    private final TextView mReviewRating;
    private final TextView mReviewCount;
    private final TextView mInStock;
    private final ImageView mProductImage;
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

        Resources res = itemView.getContext().getResources();

        mReviewCount.setText(res.getQuantityString(R.plurals.rating_count, product.reviewCount,
                product.reviewCount));
        mInStock.setText(product.inStock ? R.string.product_in_stock
                : R.string.product_out_of_stock);
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
