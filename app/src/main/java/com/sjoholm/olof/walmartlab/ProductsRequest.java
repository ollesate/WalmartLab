package com.sjoholm.olof.walmartlab;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

class ProductsRequest extends JsonRequest<ProductResult> {
    private static final String API = "170c7f99-81b5-4904-99c6-a9000794f757";
    private static final String BASE =
            "https://walmartlabs-test.appspot.com/_ah/api/walmart/v1/walmartproducts";

    public ProductsRequest(int page, int count, Response.Listener<ProductResult> listener,
                           Response.ErrorListener errorListener) {
        super(Method.GET, getUrl(page, count), null, listener, errorListener);
    }

    private static String getUrl(int page, int count) {
        return BASE + "/" + API + "/" + page + "/" + count;
    }

    @Override
    protected Response<ProductResult> parseNetworkResponse(NetworkResponse response) {
        JSONObject jsonObject;
        try {
            jsonObject = getJsonObject(response);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }

        List<Product> products = new ArrayList<>();
        int totalProducts;
        try {
            totalProducts = jsonObject.getInt("totalProducts");
            JSONArray productsArray = jsonObject.getJSONArray("products");
            for (int i = 0; i < productsArray.length(); i++) {
                JSONObject jsonProduct = productsArray.getJSONObject(i);
                String productId = jsonProduct.getString("productId");
                String productName = jsonProduct.getString("productName");
                String shortDescription = jsonProduct.has("shortDescription")
                        ? jsonProduct.getString("shortDescription") : null;
                String longDescription = jsonProduct.has("longDescription")
                        ? jsonProduct.getString("longDescription") : null;
                String price = jsonProduct.getString("price");
                String reviewRating = jsonProduct.getString("reviewRating");
                int reviewCount = jsonProduct.getInt("reviewCount");
                String image = jsonProduct.getString("productImage");
                boolean inStock = jsonProduct.getBoolean("inStock");
                products.add(new Product(productId, productName, shortDescription, longDescription,
                        price, reviewRating, reviewCount, image, inStock));
            }
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
        ProductResult productResult = new ProductResult(products, totalProducts);
        return Response.success(productResult, HttpHeaderParser.parseCacheHeaders(response));
    }

    private JSONObject getJsonObject(NetworkResponse response)
            throws UnsupportedEncodingException, JSONException {
        String jsonString = new String(response.data,
                HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
        return new JSONObject(jsonString);
    }
}
