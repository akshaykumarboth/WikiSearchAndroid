package com.akshayboth.wikisearch.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akshayboth.wikisearch.R;
import com.akshayboth.wikisearch.async.SearchAsync;
import com.akshayboth.wikisearch.util.HttpUtil;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AutoCompleteTextView.OnEditorActionListener, AutoCompleteTextView.OnFocusChangeListener {

    public AutoCompleteTextView inputField;
    public Button searchbtn;
    public TextView errorMessage;
    public GifImageView gifImageView;
    public RelativeLayout progresslayout;
    public LinearLayout ll_input_con;

    private SharedPreferences sharedpreferences;
    public ArrayAdapter<String> aAdapter;
    public List<String> suggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(getResources().getString(R.string.shared_preference_key), Context.MODE_PRIVATE);

        String prev_search = sharedpreferences.getString(getResources().getString(R.string.searchList), "");
        if (!prev_search.isEmpty()) {
            //prev_search = "";
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(getResources().getString(R.string.searchList), "");
            editor.apply();
            editor.commit();
        }



        inputField = (AutoCompleteTextView)findViewById(R.id.et_input);
        searchbtn = (Button)findViewById(R.id.btn_search);
        errorMessage = (TextView) findViewById(R.id.tv_error_msg);
        gifImageView = (GifImageView) findViewById(R.id.gifImageView);
        progresslayout = (RelativeLayout) findViewById(R.id.progresslayout);
        ll_input_con = (LinearLayout) findViewById(R.id.ll_input_con);

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open(getResources().getString(R.string.progressgif));
            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                //doSomething();
                String newText = inputField.getText().toString().trim();
                new getJson().execute(newText);
            }
        });

        suggest = new ArrayList<String>();


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
                //System.out.println(input);

                new SearchAsync(null, this, sharedpreferences).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,input + "&" +  getResources().getString(R.string.endurl));
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
            suggest = new ArrayList<>();
            errorMessage.setVisibility(View.GONE);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        // handle back button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            /*SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(ComplexObjectXMl);
                            editor.commit();*/
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        dialog.dismiss();
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //
    class getJson extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... key) {
            String newText = key[0];
            newText = newText.trim();
            newText = newText.replace(" ", "+");
            try{
                HttpUtil httpUtil = new HttpUtil("http://en.wikipedia.org/w/api.php?action=opensearch&search="+newText+"&limit=8&namespace=0&format=json", "GET", null, null);
                String jsonresponse = httpUtil.getStringResponse();
                System.out.println("jjj " + jsonresponse);
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
                    aAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.suggestion_item,suggest);
                    inputField.setAdapter(aAdapter);
                    aAdapter.notifyDataSetChanged();
                }
            });

            return null;
        }

    }
    //
}
