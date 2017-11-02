package com.sjoholm.olof.walmartlab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Listener<ProductResult> {
    private MyAdapter mAdapter;
    private ProductRequester mProductRequester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MyAdapter();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        mProductRequester = new ProductRequester(requestQueue, this);
        mProductRequester.request(20);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
                int scrollMax = recyclerView.computeVerticalScrollRange();

                float scale = currentScroll / (scrollMax * 1.0f);
                if (dy > 0 && scale > 0.75) {
                    mProductRequester.request(30);
                }
//                Log.d("Olof", "Scroll " + currentScroll + ", " + scrollMax + ", " + scale);
            }
        });


    }

    @Override
    public void onResponse(ProductResult result) {
        mProductRequester.setTotalProducts(result.totalProducts);
        mAdapter.addProducts(result.products);
    }

    private static class MyAdapter extends RecyclerView.Adapter<ProductHolder> {
        private List<Product> mData = new ArrayList<>();

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ProductHolder(inflater.inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, int position) {
            holder.onBind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void addProducts(List<Product> products) {
            mData.addAll(products);
            notifyItemRangeInserted(mData.size(), products.size());
        }
    }

}
