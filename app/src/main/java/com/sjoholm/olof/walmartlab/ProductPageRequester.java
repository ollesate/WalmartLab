package com.sjoholm.olof.walmartlab;


import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

public class ProductPageRequester {
    private static final String LOG_TAG = "ProductPageRequester";

    private static final int DEFAULT_PAGES_TO_LOAD_AHEAD = 5;

    private final RequestQueue mQueue;
    private final Response.Listener<ProductResult> mListener;
    private final Response.ErrorListener mErrorListener;

    private int mPagesToLoadAhead = DEFAULT_PAGES_TO_LOAD_AHEAD;

    private int mTotalPages = Integer.MAX_VALUE;
    private int mCurrentPage = 1;

    public ProductPageRequester(@NonNull RequestQueue queue,
                                @NonNull Response.Listener<ProductResult> listener,
                                @NonNull Response.ErrorListener errorListener) {
        mQueue = queue;
        mListener = listener;
        mErrorListener = errorListener;
    }

    public void setPagesToLoadAhead(int pagesToLoadAhead) {
        mPagesToLoadAhead = pagesToLoadAhead;
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

    public void makeRequestsAtPage(int page) {
        if (mCurrentPage == mTotalPages) {
            // We can't load more
            return;
        }

        if (page + mPagesToLoadAhead <= mCurrentPage) {
            return;
        }
        requestPages(mPagesToLoadAhead);
    }

    public void requestPages(int count) {
        // Don't load more than allowed
        count = Math.min(count, mTotalPages - mCurrentPage);
        Log.d(LOG_TAG, "Request pages from " + mCurrentPage + " to " + (mCurrentPage + count));
        mQueue.add(new ProductsRequest(mCurrentPage, count, mListener, mErrorListener));
        mCurrentPage += count;
    }
}
