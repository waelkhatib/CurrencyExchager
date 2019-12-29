package com.waelalk.currencyexchanger.Helper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractProvider {
    private double amount_val;
    private String from;
    private List<String> to;
    private Map<String,String> realNames=new HashMap<>();
    private Map<String,Double> convertResults=new HashMap<>();

    public int getActive_item() {
        return active_item;
    }

    public AbstractProvider(double amount_val, String from, List<String> to, int active_item) {
        this(amount_val,from,to);
        this.active_item=active_item;
    }

    private int active_item;

    public AbstractProvider(double amount_val, String from, List<String> to) {
        this.amount_val = amount_val;
        this.from = from;
        this.to = to;
        realNames.put("$","USD");
        realNames.put("€","EUR");
        realNames.put("SYP","SYP");
        active_item=to.size();

    }
    public void Next(){active_item++;}


    public double getAmount_val() {
        return amount_val;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getTo() {
        return to;
    }

    public Map<String, String> getRealNames() {
        return realNames;
    }

    public Map<String, Double> getConvertResults() {
        return convertResults;
    }
    public abstract String BuildURL();
    public abstract boolean isSuccess(JSONObject jsonObject);
    public abstract void ParseJSON(JSONObject jsonObject);
}
