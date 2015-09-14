package com.erikmanberger.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Erik on 2015-09-07.
 */
public class DetailFragment extends Fragment{

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.releaseDate)
    TextView mReleaseDate;
    @Bind(R.id.rating)
    TextView mVoteAverage;
    @Bind(R.id.overview)
    TextView mOverview;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_detail, container, false);

        int id = getArguments().getInt("id");

        List<Movie> movieList = MainActivityFragment.mMovieList;

        // Bind views with Butterknife
        ButterKnife.bind(this, ll);

        // Set data for views
        mTitle.setText(movieList.get(id).getTitle());
        String url = MainActivity.BASE_URL_IMAGE + movieList.get(id).getPosterPath();
        Picasso.with(getActivity()).load(url).into(mImage);
        mReleaseDate.setText(movieList.get(id).getReleaseDate());
        mVoteAverage.setText(movieList.get(id).getVoteAverage().toString() + "/10");
        mOverview.setText(movieList.get(id).getOverview());

        return ll;
    }
}
