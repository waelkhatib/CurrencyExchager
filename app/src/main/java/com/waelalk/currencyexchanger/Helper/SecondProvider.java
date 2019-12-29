package com.waelalk.currencyexchanger.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SecondProvider extends AbstractProvider {
    public SecondProvider(double amount_val, String from, List<String> to) {
        super(amount_val, from, to);
    }

    @Override
    public String BuildURL() {

        return String.format("https://www1.oanda.com/rates/api/v2/rates/spot.json?api_key=ECKYytujKTuGDVYG01Qh2wHD&base=%s&quote=%s&quote=%s",getRealNames().get(getFrom()),getRealNames().get(getTo().get(0)),getRealNames().get(getTo().get(1)));
    }

    @Override
    public boolean isSuccess(JSONObject jsonObject) {

            return jsonObject.has("quotes");


    }

    @Override
    public void ParseJSON(JSONObject jsonObject)  {
        try {
            JSONArray jsonArray=jsonObject.getJSONArray("quotes");
            for(int i=0;i<jsonArray.length();i++){
                jsonObject=jsonArray.getJSONObject(i);
                getConvertResults().put(jsonObject.getString("quote_currency"),getAmount_val()* Double.parseDouble( jsonObject.getString("midpoint")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
