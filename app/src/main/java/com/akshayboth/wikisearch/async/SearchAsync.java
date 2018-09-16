package com.akshayboth.wikisearch.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.akshayboth.wikisearch.R;
import com.akshayboth.wikisearch.activity.MainActivity;
import com.akshayboth.wikisearch.activity.ResultsActivity;
import com.akshayboth.wikisearch.pojo.Root;
import com.akshayboth.wikisearch.pojo.WikiPage;
import com.akshayboth.wikisearch.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Created by akshayboth on 31/01/18.
 */

public class SearchAsync extends AsyncTask<String, Integer, String> {
    private HashMap<String, String> param;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    public SearchAsync(HashMap<String, String> param, Context context) {
        this.param = param;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        ((MainActivity) context).searchbtn.setEnabled(false);
    }

    @Override
    protected String doInBackground(String... params) {
        return postData(param, params[0]);
    }

    @Override
    protected void onPostExecute(String jsonresponse) {
        ((MainActivity) context).searchbtn.setEnabled(true);
        ((MainActivity) context).ll_input_con.setVisibility(View.VISIBLE);
        ((MainActivity) context).progresslayout.setVisibility(View.GONE);
        if (!jsonresponse.equalsIgnoreCase("null") && !jsonresponse.equalsIgnoreCase("[]") && !jsonresponse.equalsIgnoreCase("")) {
            Intent i = null;
            try {
                if (context instanceof MainActivity)
                    ((MainActivity) context).gifImageView.stopAnimation();
                i = new Intent(context, ResultsActivity.class);
                i.putExtra(context.getString(R.string.searchList), jsonresponse);
                context.startActivity(i);
            } catch (Exception e) {
                setErrorMessage("Oops! something went wrong.");
            }
        } else {
            setErrorMessage("Please check your internet connection.");
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        if (context instanceof MainActivity) {
            ((MainActivity) context).ll_input_con.setVisibility(View.GONE);
            ((MainActivity) context).progresslayout.setVisibility(View.VISIBLE);
            ((MainActivity) context).gifImageView.startAnimation();
            ((MainActivity) context).errorMessage.setVisibility(View.GONE);
        }

    }

    private void setErrorMessage(String message) {
        if (context instanceof MainActivity) {
            if (!message.equalsIgnoreCase("")) {
                ((MainActivity) context).errorMessage.setText(message);
                ((MainActivity) context).errorMessage.setVisibility(View.VISIBLE);
            }
            ((MainActivity) context).gifImageView.stopAnimation();
            ((MainActivity) context).ll_input_con.setVisibility(View.VISIBLE);
            ((MainActivity) context).progresslayout.setVisibility(View.GONE);
            ((MainActivity) context).searchbtn.setEnabled(true);
        }
    }


    private String postData(HashMap<String, String> param, String url) {
        publishProgress(5);
        String midurl = context.getResources().getString(R.string.midurl);
        HttpUtil httpUtil = new HttpUtil(context.getResources().getString(R.string.serverip) + midurl + url, "GET", null, null);
        String jsonresponse = httpUtil.getStringResponse();

        return jsonresponse;
    }
}