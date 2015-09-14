package com.erikmanberger.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Erik on 2015-09-06.
 */
public class MovieAdapter extends BaseAdapter {

    public static Context mContext;
    public static List<Movie> mMovies;

    public MovieAdapter () {

    }

    public MovieAdapter (Context context, List<Movie> movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Object getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView==null){

            // Inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.movie_main, parent, false);

            // Set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.imgView = (ImageView) convertView.findViewById(R.id.image);

            // Store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // Use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Load image inte ImageView with Picasso
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185" + mMovies.get(position).getPosterPath()).into(viewHolder.imgView);

        // Set click listener to ImageView to start DetailActivity when clicked
        viewHolder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE, position);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView imgView;
    }

}
