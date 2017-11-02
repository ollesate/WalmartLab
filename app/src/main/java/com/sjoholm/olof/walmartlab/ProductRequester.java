package com.sjoholm.olof.walmartlab;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

class ProductRequester implements Response.ErrorListener, Response.Listener<ProductResult> {
    private final RequestQueue mQueue;
    private final Response.Listener<ProductResult> mListener;
    int mTotalProducts = Integer.MAX_VALUE;
    int mIndex = 1;
    private boolean mRequesting;

    public ProductRequester(@NonNull RequestQueue queue,
                            @NonNull Response.Listener<ProductResult> listener) {
        mQueue = queue;
        mListener = listener;
    }

    public void request(int count) {
        if (!mRequesting) {
            if (mIndex < mTotalProducts) {
                count = Math.min(count, mTotalProducts - mIndex);
                mQueue.add(new ProductsRequest(mIndex, count, this, this));
                mIndex += count;
                Log.d("Olof", "Request " + mIndex + ", " + count);
            }
        }
        mRequesting = true;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // We don't want no errors
        throw new RuntimeException(error.getMessage());
    }

    @Override
    public void onResponse(ProductResult result) {
        mRequesting = false;
        mListener.onResponse(result);
    }

    public void setTotalProducts(int totalProducts) {
        mTotalProducts = totalProducts;
    }
}
