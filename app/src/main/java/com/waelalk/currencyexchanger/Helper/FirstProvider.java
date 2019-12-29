package com.waelalk.currencyexchanger.Helper;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FirstProvider extends AbstractProvider {
    private List<String> key_lst=new ArrayList<>();
    public FirstProvider(double amount_val, String from, List<String> to) {
        super(amount_val, from, to);
        for (int i=0;i<getTo().size();i++) {
            key_lst.add( getRealNames().get(getFrom()) +"_"+ getRealNames().get(getTo().get(i)));
        }
    }

    @Override
    public String BuildURL() {
        String url_to=TextUtils.join(",",key_lst);
        return String.format("https://free.currconv.com/api/v7/convert?q=%s&compact=ultra&apiKey=6a54611af9c5482eb23c",url_to);
    }

    @Override
    public boolean isSuccess(JSONObject jsonObject) {
        boolean find=true;

            for(String key:key_lst){
                if(!jsonObject.has(key)){
                    find=false;
                    break;
                }
            }

        return find;
    }

    @Override
    public void ParseJSON(JSONObject jsonObject)  {
        try {
            for(String key:key_lst){
                getConvertResults().put(key.substring(4),getAmount_val()* Double.parseDouble( jsonObject.getString(key)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
