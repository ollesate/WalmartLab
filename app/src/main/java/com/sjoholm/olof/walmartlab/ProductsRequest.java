package com.sjoholm.olof.walmartlab;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

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
        String jsonObject;
        try {
            jsonObject = getJsonObject(response);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }

        Gson gson = new Gson();
        ProductResult productResult = gson.fromJson(jsonObject, ProductResult.class);
        return Response.success(productResult, HttpHeaderParser.parseCacheHeaders(response));
    }

    private String getJsonObject(NetworkResponse response)
            throws UnsupportedEncodingException, JSONException {
        return new String(response.data,
                HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
    }
}
