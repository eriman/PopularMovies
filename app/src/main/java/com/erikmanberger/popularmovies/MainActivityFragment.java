package com.erikmanberger.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Main fragment containing a grid view.
 */
public class MainActivityFragment extends Fragment {

    public static List<Movie> mMovieList = new ArrayList<Movie>();
    private MovieAdapter mMovieAdapter;
    private GridView mGridView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Setup UI
        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rl.findViewById(R.id.grid);

        // Parse JSON with GSON
        Gson gson = new Gson();
        try {
            JSONObject jsonResult = new JSONObject(getArguments().getString(MainActivity.JSON_STRING));
            JSONArray jsonMovies = jsonResult.getJSONArray("results");
            String movies = jsonMovies.toString();
            Type listType = new TypeToken<List<Movie>>(){}.getType();
            mMovieList = (List<Movie>) gson.fromJson(movies, listType);
        }
        catch (JSONException e) {
            Log.e("MainActivityFragment", "Error when parsing JSON!", e);
        }

        // Sort movie list and bind views to GridView with MovieAdapter
        sortMovieList();
        mMovieAdapter = new MovieAdapter(getActivity(), mMovieList);
        mGridView.setAdapter(mMovieAdapter);

        return rl;
    }

    // Sort movie list according to user preference
    private void sortMovieList () {

        Context context = getActivity();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Get sort order from SharedPreferences
        String sortOrder = prefs.getString(getString(R.string.key_sort_by), getString(R.string.key_popularity));

        // Sort by highest popularity
        if (sortOrder.equals(getString(R.string.key_popularity))) {
            Collections.sort(mMovieList, new Comparator<Movie>() {
                @Override
                public int compare(Movie m1, Movie m2) {
                    return Double.compare(m2.getPopularity(), m1.getPopularity());
                }
            });
        }

        // Sort by highest vote average
        else if (sortOrder.equals(getString(R.string.key_highest_rated))) {
            Collections.sort(mMovieList, new Comparator<Movie>() {
                @Override
                public int compare(Movie m1, Movie m2) {
                    return Double.compare(m2.getVoteAverage(), m1.getVoteAverage());
                }
            });
        }
    }

}
