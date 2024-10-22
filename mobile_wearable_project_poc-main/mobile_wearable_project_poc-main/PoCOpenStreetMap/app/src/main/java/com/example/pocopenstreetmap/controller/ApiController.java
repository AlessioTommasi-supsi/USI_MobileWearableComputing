package com.example.pocopenstreetmap.controller;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pocopenstreetmap.model.ResponsePosts;
import com.google.gson.Gson;
import android.util.Log;

public class ApiController {
    private static final String URL = "http://10.0.2.2/view/genericAPI.php?name=Mario%20Rossi";
    private RequestQueue requestQueue;
    private Gson gson;

    public ApiController(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        gson = new Gson();
    }

    public void fetchPosts(final ApiCallback callback) {

        try{
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ResponsePosts responseData = gson.fromJson(response, ResponsePosts.class);
                            callback.onSuccess(responseData);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onError(error);
                }
            });

            requestQueue.add(stringRequest);
        }catch (Exception e){
            Log.e("ApiController", "Error fetching data: " + e.getMessage());
        }


    }

    public interface ApiCallback {
        void onSuccess(ResponsePosts responsePosts);
        void onError(VolleyError error);
    }
}