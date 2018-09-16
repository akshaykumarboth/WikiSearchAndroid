package com.akshayboth.wikisearch.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akshayboth.wikisearch.R;
import com.akshayboth.wikisearch.activity.WikiWebActivity;
import com.akshayboth.wikisearch.pojo.WikiPage;
import com.bumptech.glide.Glide;

import java.util.List;

public class ResultsAdapter extends
        RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private List<WikiPage> wikiList;
    private Context context;

    /**
     * View holder class
     * */
    public class ResultsViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView image;
        public LinearLayout main_layout;

        public ResultsViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            image = (ImageView)view.findViewById(R.id.item_image);
            main_layout = (LinearLayout)view.findViewById(R.id.main_layout);

        }
    }

    public ResultsAdapter(List<WikiPage> wikiList, Context context) {
        this.wikiList = wikiList;
        this.context = context;
    }

    private void setTypeface(ResultsViewHolder holder) {
        final Typeface abel = Typeface.createFromAsset(context.getResources().getAssets(), "font2/abel-regular.ttf");

        holder.description.setTypeface(abel);
        holder.title.setTypeface(abel);
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder holder, int position) {
        setTypeface(holder);
        final WikiPage item = wikiList.get(position);
        String description = "";
        holder.title.setText(item.getTitle());
        if (item != null) {
            if (item.getTerms() != null) {
                if (!item.getTerms().getDescription().isEmpty() && item.getTerms().getDescription() != null && item.getTerms().getDescription().size() != 0)
                    description = String.valueOf(item.getTerms().getDescription());
                    holder.description.setText(description.substring(1, description.length() - 1)); //;
            }

            if (item.getThumbnail()!= null) {
                if (item.getThumbnail().getSource() != "" && item.getThumbnail().getSource() != null) {
                    String s1 =item.getThumbnail().getSource().substring(item.getThumbnail().getSource().lastIndexOf("/"));
                    s1 = s1.substring(1,s1.indexOf('-'));

                    s1 = item.getThumbnail().getSource().replace(s1, "100px");
                    System.out.println("aa ------------------------------------------------------       " + s1);

                    Glide.with(context)
                            .load(s1)
                            .placeholder(R.drawable.wk2)
                            .into(holder.image);


                }
            }
        }

       holder.main_layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String title = item.getTitle().trim().replaceAll(" ", "_");
               Intent i = new Intent(context, WikiWebActivity.class);
               i.putExtra(context.getResources().getString(R.string.title), title);
               context.startActivity(i);

           }
       });


    }



    @Override
    public int getItemCount() {
        return wikiList.size();
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_view_holder,parent, false);
        return new ResultsViewHolder(v);
    }
}