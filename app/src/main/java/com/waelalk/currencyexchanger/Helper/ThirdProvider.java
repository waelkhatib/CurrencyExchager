package com.waelalk.currencyexchanger.Helper;

import android.text.TextUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ThirdProvider extends AbstractProvider {
    public ThirdProvider(double amount_val, String from, List<String> to, int active_item) {
        super(amount_val, from, to, active_item);
    }


    @Override
    public String BuildURL() {
        return String.format("https://fcsapi.com/api/forex/converter?symbol=%s&amount=%s&access_key=tVrZX23WJOH5CKGPkf9gvAsbXytg6WtuJGxpzoUbjxYNCqe3Mw",getRealNames().get(getFrom())+"/"+getRealNames().get(getTo().get(getActive_item())),""+getAmount_val());
    }

    @Override
    public boolean isSuccess(JSONObject jsonObject) {
        try {

            return jsonObject.getString("msg").equals("successfully");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void ParseJSON(JSONObject jsonObject) {
        try {
              getConvertResults().put(getRealNames().get(getTo().get(getActive_item())),Double.parseDouble( jsonObject.getJSONObject("response").getString("total")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
