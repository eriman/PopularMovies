package com.erikmanberger.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=";
    public static final String BASE_URL_IMAGE = "http://image.tmdb.org/t/p/w185";
    public static final String EXTRA_MESSAGE = "com.erikmanberger.popularmovies.MESSAGE";

    private final String API_KEY = "INSERT API KEY HERE";

    public static final String JSON_STRING = "jsonString";
    private String jsonStringen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI(savedInstanceState);
    }

    private void initUI(Bundle savedInstanceState) {

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
        }

        // Fetch data if we don't have saved any in savedInstanceState
        if (savedInstanceState == null) {
            fetchData();
        }

        else {
            jsonStringen = savedInstanceState.getString(JSON_STRING);
            startMainFragment(jsonStringen);
        }
    }

    private void fetchData () {

        String stringUrl = BASE_URL + API_KEY;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new FetchMoviesTask().execute(stringUrl);
        } else {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putString(JSON_STRING, jsonStringen);
    }

    private void startMainFragment (String jsonString) {
        Bundle args = new Bundle();
        jsonStringen = jsonString;
        args.putString(JSON_STRING, jsonString);
        MainActivityFragment mf = new MainActivityFragment();
        mf.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, mf).commit();
    }

    // Fetch data from server in background thread with AsyncTask
    private class FetchMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadJsonString(urls[0]);
            } catch (IOException e) {
                return "";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if (!result.isEmpty())
                startMainFragment(result);
        }
    }

    // Download data returned as a JSON string
    private String downloadJsonString(String urlString) throws IOException {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try {
            // Construct the URL for the query
            URL url = new URL(urlString);

            // Create the request to server, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                jsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                jsonStr = null;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("MainActivity", "Error ", e);
            // If the code didn't successfully get the data, there's no point in attempting
            // to parse it.
            jsonStr = null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("MainActivity", "Error closing stream", e);
                }
            }
        }
        return jsonStr;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            //intent.putExtra(MainActivity.EXTRA_MESSAGE, position);
            startActivity(intent);

            return true;
        }
        else if (id == R.id.action_refresh) {
            fetchData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
