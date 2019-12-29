package com.waelalk.currencyexchanger.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.waelalk.currencyexchanger.Helper.AbstractProvider;
import com.waelalk.currencyexchanger.Helper.FirstProvider;
import com.waelalk.currencyexchanger.Helper.SecondProvider;
import com.waelalk.currencyexchanger.Helper.ThirdProvider;
import com.waelalk.currencyexchanger.Network.Request;
import com.waelalk.currencyexchanger.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
   private EditText amount;
   private AbstractProvider providerAPI;
   private String[]symbols=new String[]{"$","€","SYP"};
   private String[]provider_arr=new String[]{"Provider1","Provider2","Provider3"};
   private AppCompatSpinner providers;
   private AppCompatSpinner currencies;
   private Button convert_btn;
   private ProgressBar progressBar;
   private TextView convert1_txtview;
   private TextView convert2_txtview;
   private String amount_val=null;
   private int currency_selected=-1;
   private int provider_selected=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
    private List<String> getDestinationCurrencies(int position){
        List<String> dest_list=new ArrayList<>();
        for (int i=0;i<symbols.length;i++){
            if (i==position)continue;
            dest_list.add(symbols[i]);
        }
        return dest_list;
    }

    public TextView getCurrencyText1() {
        return convert1_txtview;
    }

    public TextView getCurrencyText2() {
        return convert2_txtview;
    }

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
         amount = (EditText) findViewById(R.id.amount_edt);
        amount.addTextChangedListener(new TextWatcher() {
            boolean hint;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                amount_val=s.toString().trim();
                if(s.length() == 0) {
                    // no text, hint is visible
                    hint = true;
                    amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                } else if(hint) {
                    // no hint, text is visible
                    hint = false;
                    amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        amount.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    clearFocus(amount);
                    return true;
                }
                return false;
            }
        });

        currencies=(AppCompatSpinner)findViewById(R.id.currencies_spn);
        final List<String> symbols_lst=new ArrayList<>(Arrays.asList(symbols));
        PrepareSpinner(currencies,symbols_lst,getString(R.string.select_currency));
        currencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=symbols.length) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    currency_selected=position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        providers=(AppCompatSpinner)findViewById(R.id.provider_spn);
        final List<String> provider_lst=new ArrayList<>(Arrays.asList(provider_arr));
        PrepareSpinner(providers,provider_lst,getString(R.string.select_provider));
        providers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=provider_arr.length) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    provider_selected=position;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        convert_btn=(Button)findViewById(R.id.convert_btn);
        convert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressBar.getVisibility() != View.VISIBLE) {
                    String message = validate();
                    if (message != null) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("provider_selected",provider_selected+"");
                        switch (provider_selected){
                            case 0:
                                providerAPI=new FirstProvider(Double.parseDouble(amount_val),symbols[currency_selected],getDestinationCurrencies(currency_selected));
                                break;
                            case 1:
                                providerAPI=new SecondProvider(Double.parseDouble(amount_val),symbols[currency_selected],getDestinationCurrencies(currency_selected));
                                break;
                            case 2:
                                providerAPI=new ThirdProvider(Double.parseDouble(amount_val),symbols[currency_selected],getDestinationCurrencies(currency_selected),0);
                                break;
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        convert1_txtview.setText("");
                        convert2_txtview.setText("");
                        Request request=new Request(MainActivity.this,providerAPI);
                        request.loadData();
                    }
                }
            }
        });
        convert1_txtview=(TextView)findViewById(R.id.convert1_txtview);
        convert2_txtview=(TextView)findViewById(R.id.convert2_txtview);
        progressBar=(ProgressBar)findViewById(R.id.progress_circular);


    }

    private String validate() {
        String regex = "^\\d*\\.\\d+|\\d+\\.\\d*|\\d+$";
        if (amount_val==null || amount_val.equals(""))
            return getString(R.string.empty_amount_error);
        else if(!amount_val.matches(regex))
               return getString(R.string.currency_input_error);
        else if (currency_selected==-1)
            return getString(R.string.select_currency_option);
        else if (provider_selected==-1)
            return getString(R.string.select_provider_option);
        else  if(!isNetworkConnected())
            return getString(R.string.connect_to_internet);
        return null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("focus", "touchevent");

                    clearFocus(v);

                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void clearFocus(View v) {
        v.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void PrepareSpinner(AppCompatSpinner spinner, List<String> lst,String hint) {
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,lst){
            @Override
            public int getCount() {
                int count= super.getCount();
                return count>0?count-1:count;
            }

        };
        spinnerArrayAdapter.add(hint);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.select_dialog_item);
        spinner.setPrompt(hint);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(spinnerArrayAdapter.getCount());


    }

}
