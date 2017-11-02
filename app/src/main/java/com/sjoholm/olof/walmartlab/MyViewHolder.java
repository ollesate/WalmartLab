package com.sjoholm.olof.walmartlab;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView mProductName;
    private TextView mPrice;
    private TextView mReviewRating;
    private TextView mReviewCount;
    private TextView mInStock;

    public MyViewHolder(View itemView) {
        super(itemView);
        mProductName = (TextView) itemView.findViewById(R.id.product_name);
        mPrice = (TextView) itemView.findViewById(R.id.price);
        mReviewRating = (TextView) itemView.findViewById(R.id.review_rating);
        mReviewCount = (TextView) itemView.findViewById(R.id.review_count);
        mInStock = (TextView) itemView.findViewById(R.id.in_stock);
    }

    public void onBind(@NonNull Product data) {
        mProductName.setText(data.productName);
        mPrice.setText(data.price);
        mReviewRating.setText(data.reviewRating);
        mReviewCount.setText(data.reviewCount + " ratings");
        mInStock.setText(data.inStock ? "In stock" : "Not in stock");
    }
}
