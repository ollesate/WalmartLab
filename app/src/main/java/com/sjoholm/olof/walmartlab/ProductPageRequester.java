package com.sjoholm.olof.walmartlab;


import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ProductPageRequester implements Response.ErrorListener {
    private static final String LOG_TAG = "ProductPageRequester";

    private static final int MIN_AMOUNT_PAGE_REQUESTS = 5;
    private static final int THRESHOLD_FOR_PAGE_REQUESTS = 5;

    private final int mInitialPages;
    private final RequestQueue queue;
    private final Response.Listener<ProductResult> listener;

    private int mMinAmountPageRequests = MIN_AMOUNT_PAGE_REQUESTS;
    private int mThresholdForPageRequests = THRESHOLD_FOR_PAGE_REQUESTS;

    private int mTotalPages = Integer.MAX_VALUE;
    private int mCurrentPage;

    public ProductPageRequester(int initialPages, @NonNull RequestQueue queue,
                                @NonNull Response.Listener<ProductResult> listener) {
        mInitialPages = initialPages;
        this.queue = queue;
        this.listener = listener;
    }

    public void setMinAmountRequest(int minAmountPageRequests) {
        mMinAmountPageRequests = minAmountPageRequests;
    }

    public void setThresholdForPageRequests(int thresholdForPageRequests) {
        mThresholdForPageRequests = thresholdForPageRequests;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(int totalPages) {
        mTotalPages = totalPages;
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }

    public void loadInitialPages() {
        mCurrentPage = 1;
        requestPages(mInitialPages);
    }

    public void makeRequestsAtPage(int page) {
        if (mCurrentPage == mTotalPages) {
            // We can't load more
            return;
        }

        if (page + mThresholdForPageRequests <= mCurrentPage) {
            return;
        }

        int overshoot = page + mThresholdForPageRequests - mCurrentPage;
        int pagesToLoad = Math.max(mMinAmountPageRequests, overshoot);

        // Don't load more than allowed
        pagesToLoad = Math.min(pagesToLoad, mTotalPages - mCurrentPage);

        requestPages(pagesToLoad);
    }

    private void requestPages(int count) {
        Log.d(LOG_TAG, "Request pages from " + mCurrentPage + " to " + (mCurrentPage + count));
        queue.add(new ProductsRequest(mCurrentPage, count, listener, this));
        mCurrentPage += count;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // We don't want no errors
        throw new RuntimeException(error.getMessage());
    }
}
