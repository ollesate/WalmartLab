package com.sjoholm.olof.walmartlab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Listener<List<Product>> {
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MyAdapter();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final ProductRequester productRequester = new ProductRequester(requestQueue, this);
        productRequester.request(20);

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
                    productRequester.request(30);
                }
                Log.d("Olof", "Scroll " + currentScroll + ", " + scrollMax + ", " + scale);
            }
        });


    }

    @Override
    public void onResponse(List<Product> result) {
        mAdapter.addProducts(result);
    }

    private static class ProductRequester implements Response.ErrorListener, Listener<List<Product>> {
        private final RequestQueue mQueue;
        private final Listener<List<Product>> mListener;
        int mIndex = 1;
        private boolean requesting;

        public ProductRequester(@NonNull RequestQueue queue,
                                @NonNull Listener<List<Product>> listener) {
            mQueue = queue;
            mListener = listener;
        }

        public void request(int count) {
            if (!requesting) {
                Log.d("Olof", "Request " + count);
                mQueue.add(new ProductsRequest(mIndex, mIndex + count, this, this));
            }
            requesting = true;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            // We don't want no errors
            throw new RuntimeException(error.getMessage());
        }

        @Override
        public void onResponse(List<Product> response) {
            requesting = false;
            mListener.onResponse(response);
        }
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

    private static class ProductsRequest extends JsonRequest<List<Product>>{
        private static String API = "170c7f99-81b5-4904-99c6-a9000794f757";
        private static String BASE =
                "https://walmartlabs-test.appspot.com/_ah/api/walmart/v1/walmartproducts";

        public ProductsRequest(int page, int count, Listener<List<Product>> listener,
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
                    String image = jsonProduct.getString("productImage");
                    boolean inStock = jsonProduct.getBoolean("inStock");
                    products.add(new Product(productId, productName, description, price,
                            reviewRating, reviewCount, image, inStock));
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
