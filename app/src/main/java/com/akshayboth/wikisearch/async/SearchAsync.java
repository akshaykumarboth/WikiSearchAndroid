package com.akshayboth.wikisearch.async;

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
    private Context context;
    private SharedPreferences sharedpreferences;

    //private final Gson gson = new Gson();

    public SearchAsync(HashMap<String, String> param, Context context, SharedPreferences sharedpreferences) {
        this.param = param;
        this.context = context;
        this.sharedpreferences = sharedpreferences;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        return postData(param, params[0]);
    }


    @Override
    protected void onPostExecute(String jsonresponse) {

        if (!jsonresponse.equalsIgnoreCase("null") && !jsonresponse.equalsIgnoreCase("[]") && !jsonresponse.equalsIgnoreCase("") && !jsonresponse.contains("istarViksitProComplexKey")) {
            //dialog.show();
            Intent i = null;

            try {
                /*Gson gson = new Gson();
                Root root = gson.fromJson(jsonresponse, Root.class);

                List<WikiPage> pages = root.getQuery().getPages();
                Type type = new TypeToken<List<WikiPage>>() {}.getType();
                jsonresponse = gson.toJson(pages, type);
                //jsonresponse = gson.toJson(pages);*/
                //System.out.println(jsonresponse);
                if (context instanceof MainActivity)
                    ((MainActivity) context).gifImageView.stopAnimation();

                i = new Intent(context, ResultsActivity.class);
                //i.putExtra("jsonresponse", jsonresponse);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                ((Activity)context).finish();

            } catch (Exception e) {
                setErrorMessage("Oops! something went wrong.");
            }

        } else if (!jsonresponse.equalsIgnoreCase("null") && jsonresponse.contains("istarViksitProComplexKey")) {
            setErrorMessage(jsonresponse.replaceAll("istarViksitProComplexKey", "").replaceAll("\"", ""));
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

        String midurl = "&format=json&prop=pageimages%7Cpageterms&generator=prefixsearch&redirects=1&formatversion=2&piprop=thumbnail&pithumbsize=50&pilimit=10&wbptterms=description&gpssearch=";
        HttpUtil httpUtil = new HttpUtil(context.getResources().getString(R.string.serverip) + midurl + url, "GET", null, null);
        String jsonresponse = httpUtil.getStringResponse();
        System.out.println("jjj " + jsonresponse);

       if (!jsonresponse.equalsIgnoreCase("null") && !jsonresponse.equalsIgnoreCase("[]") && !jsonresponse.equalsIgnoreCase("")) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(context.getResources().getString(R.string.searchList), jsonresponse);
            editor.apply();
            editor.commit();
        }

        return jsonresponse;
    }
}