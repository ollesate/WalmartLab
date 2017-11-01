package com.sjoholm.olof.walmartlab;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Response.Listener<List<Product>> {

    private StringRequester mStringRequester;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MyAdapter();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ProductRequester productRequester = new ProductRequester(requestQueue, this);
        productRequester.request(20);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("Olof", "onNewIntent");
    }

    @Override
    public void onResponse(List<Product> response) {

    }

    private static class ProductRequester implements Response.ErrorListener {
        private final RequestQueue mQueue;
        private final Response.Listener<List<Product>> mListener;
        int mIndex = 1;

        public ProductRequester(@NonNull RequestQueue queue,
                                @NonNull Response.Listener<List<Product>> listener) {
            mQueue = queue;
            mListener = listener;
        }

        public void request(int count) {
            mQueue.add(new ProductsRequest(mIndex, mIndex + count, mListener, this));
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            // We don't want no errors
            throw new RuntimeException(error.getMessage());
        }
    }

    private static class StringRequester implements Response.Listener<String>, Response.ErrorListener {
        private final TextView mTextView;
        private final RequestQueue mQueue;

        public StringRequester(@NonNull Context context, @NonNull TextView textView) {
            mTextView = textView;
            mQueue = Volley.newRequestQueue(context);
        }

        public void request(@NonNull String url) {
            mQueue.add(createRequest(url));
        }

        private StringRequest createRequest(String url) {
            return new StringRequest(Request.Method.GET, url, this, this);
        }

        @Override
        public void onResponse(String response) {
            mTextView.setText("Response \n" + response);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            mTextView.setText("Error " + error.getMessage());
        }
    }

    public static class Product {
        public final String productId;
        public final String productName;
        public final String description;
        public final String price;
        public final String reviewRating;
        public final String reviewCount;
        public final boolean inStock;

        public Product(String productId, String productName, String description, String price,
                       String reviewRating, String reviewCount, boolean inStock) {
            this.productId = productId;
            this.productName = productName;
            this.description = description;
            this.price = price;
            this.reviewRating = reviewRating;
            this.reviewCount = reviewCount;
            this.inStock = inStock;
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mProductName;
        private TextView mDescription;
        private TextView mPrice;
        private TextView mReviewRating;
        private TextView mReviewCount;
        private TextView mInStock;

        public MyViewHolder(View itemView) {
            super(itemView);
            mProductName = (TextView) itemView.findViewById(R.id.product_name);
            mDescription = (TextView) itemView.findViewById(R.id.description);
            mPrice = (TextView) itemView.findViewById(R.id.price);
            mReviewRating = (TextView) itemView.findViewById(R.id.review_rating);
            mReviewCount = (TextView) itemView.findViewById(R.id.review_count);
            mInStock = (TextView) itemView.findViewById(R.id.in_stock);
        }

        public void onBind(@NonNull Product data) {
            mProductName.setText(data.productName);

            if (data.description != null) {
                Spanned text;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    text = Html.fromHtml(data.description, Html.FROM_HTML_MODE_COMPACT);
                } else {
                    text= Html.fromHtml(data.description);
                }
                mDescription.setText(text);
            }
            mPrice.setText(data.price);
            mReviewRating.setText(data.reviewRating);
            mReviewCount.setText(data.reviewCount);
            mInStock.setText(data.inStock ? "In stock" : "Not in stock");
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<Product> mData = new ArrayList<>();

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MyViewHolder(inflater.inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.onBind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void setProducts(List<Product> response) {
            mData = response;
            notifyDataSetChanged();
        }
    }

    private static class ProductsRequest extends JsonRequest<List<Product>>{
        private static String API = "170c7f99-81b5-4904-99c6-a9000794f757";
        private static String BASE =
                "https://walmartlabs-test.appspot.com/_ah/api/walmart/v1/walmartproducts";

        public ProductsRequest(int page, int count, Response.Listener<List<Product>> listener,
                               Response.ErrorListener errorListener) {
            super(Method.GET, getUrl(page, count), null, listener, errorListener);
        }

        private static String getUrl(int page, int count) {
            return BASE + "/" + API + "/" + page + "/" + count;
        }

        @Override
        protected Response<List<Product>> parseNetworkResponse(NetworkResponse response) {
            JSONObject jsonObject;
            try {
                jsonObject = getJsonObject(response);
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException e) {
                return Response.error(new ParseError(e));
            }

            List<Product> products = new ArrayList<>();
            try {
                JSONArray productsArray = jsonObject.getJSONArray("products");
                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject jsonProduct = productsArray.getJSONObject(i);
                    String productId = jsonProduct.getString("productId");
                    String productName = jsonProduct.getString("productName");
                    String description = jsonProduct.has("shortDescription") ?
                            jsonProduct.getString("shortDescription") : null;
                    String price = jsonProduct.getString("price");
                    String reviewRating = jsonProduct.getString("reviewRating");
                    String reviewCount = jsonProduct.getString("reviewCount");
                    boolean inStock = jsonProduct.getBoolean("inStock");
                    products.add(new Product(productId, productName, description, price,
                            reviewRating, reviewCount, inStock));
                }

            } catch (JSONException e) {
                return Response.error(new ParseError(e));
            }
            return Response.success(products, HttpHeaderParser.parseCacheHeaders(response));
        }

        private JSONObject getJsonObject(NetworkResponse response)
                throws UnsupportedEncodingException, JSONException {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return new JSONObject(jsonString);
        }
    }
}
