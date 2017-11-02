package com.sjoholm.olof.walmartlab;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class MyAdapter extends RecyclerView.Adapter<ProductHolder> implements View.OnClickListener {
    private final ArrayList<Product> mProducts = new ArrayList<>();
    private final OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {

        void onItemClicked(@NonNull Product product, int index);
    }

    public MyAdapter(@NonNull OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ProductHolder(inflater.inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position) {
        Product product = mProducts.get(position);
        holder.onBind(mProducts.get(position));
        holder.itemView.setTag(product);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public void addProducts(List<Product> products) {
        mProducts.addAll(products);
        notifyItemRangeInserted(mProducts.size(), products.size());
    }

    public ArrayList<Product> getProducts() {
        return mProducts;
    }

    @Override
    public void onClick(View v) {
        Product product = (Product) v.getTag();
        int index = mProducts.indexOf(product);
        mOnItemClickListener.onItemClicked(product, index);
    }
}
