package com.akshayboth.wikisearch.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akshayboth.wikisearch.R;
import com.akshayboth.wikisearch.async.SearchAsync;
import com.akshayboth.wikisearch.util.HttpUtil;
import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AutoCompleteTextView.OnEditorActionListener, AutoCompleteTextView.OnFocusChangeListener {

    public AutoCompleteTextView inputField;
    public Button searchbtn;
    public TextView errorMessage;
    public GifImageView gifImageView;
    public RelativeLayout progresslayout;
    public LinearLayout ll_input_con;

    public ArrayAdapter<String> searchAdapter;
    public List<String> suggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = (AutoCompleteTextView)findViewById(R.id.et_input);
        searchbtn = (Button)findViewById(R.id.btn_search);
        errorMessage = (TextView) findViewById(R.id.tv_error_msg);
        gifImageView = (GifImageView) findViewById(R.id.gifImageView);
        progresslayout = (RelativeLayout) findViewById(R.id.progresslayout);
        ll_input_con = (LinearLayout) findViewById(R.id.ll_input_con);

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open(getResources().getString(R.string.gd));
            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setListeners();
        suggest = new ArrayList<String>();
        setTypeface();

    }

    private void setTypeface() {
        final Typeface abel = Typeface.createFromAsset(getResources().getAssets(), "font2/abel-regular.ttf");

        inputField.setTypeface(abel);
        searchbtn.setTypeface(abel);
        errorMessage.setTypeface(abel);
    }

    private void setListeners() {
        inputField.setOnEditorActionListener(this);
        inputField.setOnFocusChangeListener(this);
        searchbtn.setOnClickListener(this);
        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = inputField.getText().toString().trim();
                new GetJson().execute(input);
            }
        });
    }

    private boolean validate() {
        boolean valid = true;
        String input = inputField.getText().toString();

        if (input.isEmpty()) {
            String error_message = "";
            error_message = "* Input field is empty";
            errorMessage.setText(error_message);
            errorMessage.setVisibility(View.VISIBLE);

            return false;
        }
        errorMessage.setVisibility(View.GONE);

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                hideKeyboard(v);
                inputField.clearFocus();
                if (!validate()) {
                    return;
                }
                String input = inputField.getText().toString().trim().replaceAll(" ", "+");
                new SearchAsync(null, this).execute(input + "&" +  getResources().getString(R.string.endurl));
                break;

        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard(v);
            searchbtn.performClick();

            return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideKeyboard(v);
        } else {
            errorMessage.setVisibility(View.GONE);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        // handle back button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_text)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    class GetJson extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... key) {
            String input = key[0];
            input = input.trim();
            input = input.replace(" ", "+");
            try{
                HttpUtil httpUtil = new HttpUtil(getResources().getString(R.string.wiki_search_base_url) + input+ getResources().getString(R.string.wiki_url_param), "GET", null, null);
                String jsonresponse = httpUtil.getStringResponse();

                suggest = new ArrayList<String>();
                JSONArray jArray = new JSONArray(jsonresponse);
                for(int i=0;i<jArray.getJSONArray(1).length();i++){
                    String SuggestKey = jArray.getJSONArray(1).getString(i);
                    suggest.add(SuggestKey);
                }
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }
            runOnUiThread(new Runnable(){
                public void run(){
                    searchAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.suggestion_item,suggest);
                    inputField.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();
                }
            });

            return null;
        }

    }

}
