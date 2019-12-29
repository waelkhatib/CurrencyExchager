package com.waelalk.currencyexchanger.Network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.waelalk.currencyexchanger.Helper.AbstractProvider;
import com.waelalk.currencyexchanger.R;
import com.waelalk.currencyexchanger.View.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Request {
    private Context context;
    private AbstractProvider provider;

    public Request(Context context, AbstractProvider provider) {
        this.context = context;
        this.provider = provider;
    }
    public void loadData(){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
       String url= provider.BuildURL();
        Log.d("provider.BuildURL()",url);
        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                com.android.volley.  Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(provider.isSuccess(response)){
                            provider.ParseJSON(response);
                            if(provider.getConvertResults().size()==provider.getTo().size()) {

                                int i = 0;
                                for (Map.Entry<String, Double> mapElement : provider.getConvertResults().entrySet()) {
                                    if (i == 0) {
                                        ((MainActivity) context).getCurrencyText1().setText(String.format("%.2f %s", mapElement.getValue(), mapElement.getKey()));
                                    } else {
                                        ((MainActivity) context).getCurrencyText2().setText(String.format("%.2f %s", mapElement.getValue(), mapElement.getKey()));
                                    }
                                    i++;
                                }
                                ((MainActivity)context).hideProgressBar();
                            }else {
                                provider.Next();
                                loadData();
                            }
                        }
                        else {
                            Toast.makeText(context, R.string.some_error_occur, Toast.LENGTH_SHORT).show();
                            ((MainActivity)context).hideProgressBar();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        ((MainActivity)context).hideProgressBar();
                        Toast.makeText(context,R.string.some_error_occur,Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add JsonObjectRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
}
