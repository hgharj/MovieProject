package com.example.android.movieproject.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.movieproject.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String POSTER_SIZE = "w342/";
    private static final String RESULTS_ARRAY = "results";
    private static final String MOVIE_ID = "id";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String RELEASE_DATE = "release_date";
    private static final String OVERVIEW = "overview";
    private static final String FETCH_MOVIE_DATA_ERROR_MSG = "Error closing input stream: ";
    private static final String EXTRACT_MOVIES_ERROR_MSG = "Problem parsing the movie JSON results: ";
    private static final String CREATE_URL_ERROR_MSG = "Error with creating URL: ";
    private static final String GET = "GET";
    private static final String ERROR_RESPONSE_CODE = "Error response code: ";
    private static final String MAKE_HTTP_REQUEST_ERROR_MSG = "Problem retrieving the movie JSON results.";
    private static final String UTF8 = "UTF-8";
    private static final String REQUEST_URL = "Request URL ... ";
    private static final String RESPONSE_CODE = "Response Code ... ";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";
    private static final String ACCEPT_LANGUAGE = "Accept-Language";
    private static final String ACCEPT_LANGUAGE_2 = "en-US,en;q=0.8";
    private static final String USER_AGENT = "User-Agent";
    private static final String USER_AGENT_2 = "Mozilla";
    private static final String REFERRER = "Referrer";
    private static final String REFERRER_2 = "google.com";
    private static final String REDIRECT_URL = "Redirect to URL : ";

    /**
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name NetworkUtils (and an object instance of NetworkUtils is not needed).
     */
    private NetworkUtils() {
    }

    /**
     * Query the MovieDB API and return an {@link ArrayList <Movie>} object to represent a single movie.
     */
    public static ArrayList<Movie> fetchMovieData(String requestUrl) {
        // Create URL object
        HttpRedirect hr = new HttpRedirect();
        URL url = hr.main(createUrl(requestUrl));

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, FETCH_MOVIE_DATA_ERROR_MSG, e);
        }

        // Extract relevant fields from the JSON response, create an return {@link Event} object
        return extractMovies(jsonResponse);
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<Movie> extractMovies(String movieJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding movies to
        ArrayList<Movie> movies = new ArrayList<>();

        // Try to parse the JSON response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject reader = new JSONObject(movieJSON);
            JSONArray results = reader.getJSONArray(RESULTS_ARRAY);
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentMovie = results.getJSONObject(i);
                int movieId = currentMovie.getInt(MOVIE_ID);
                String voteAvgDouble = currentMovie.getString(VOTE_AVERAGE);
                float voteAvg = Float.parseFloat(voteAvgDouble);
                String title = currentMovie.getString(TITLE);
                String posterPathExtension = currentMovie.getString(POSTER_PATH);
                String poster = POSTER_BASE_URL + POSTER_SIZE + posterPathExtension;
                String releaseDate = currentMovie.getString(RELEASE_DATE);
                String plot = currentMovie.getString(OVERVIEW);
                Movie movie = new Movie(movieId, title, poster, releaseDate, voteAvg, plot);
                movies.add(movie);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, EXTRACT_MOVIES_ERROR_MSG, e);
        }

        // Return the list of movies
        return movies;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, CREATE_URL_ERROR_MSG, e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setInstanceFollowRedirects(true);
            HttpURLConnection.setFollowRedirects(true);
            urlConnection.setReadTimeout(10000); /* milliseconds */
            urlConnection.setConnectTimeout(15000); /* milliseconds */
            urlConnection.setRequestMethod(GET);

            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200 || urlConnection.getResponseCode() == 301) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, ERROR_RESPONSE_CODE + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, MAKE_HTTP_REQUEST_ERROR_MSG, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(UTF8));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static class HttpRedirect {

        private URL main(URL url) {
            String newUrl = "";
            URL outURL = url;

            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);

                System.out.println(REQUEST_URL + url);

                boolean redirect = false;

                // normally, 3xx is redirect
                int status = conn.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP
                            || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true;
                    outURL = null;
                }

                System.out.println(RESPONSE_CODE + status);

                if (redirect) {

                    // get redirect url from "location" header field
                    newUrl = conn.getHeaderField(LOCATION);

                    // get the cookie if need, for login
                    String cookies = conn.getHeaderField(SET_COOKIE);

                    // open the new connection again
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    conn.setRequestProperty(COOKIE, cookies);
                    conn.addRequestProperty(ACCEPT_LANGUAGE, ACCEPT_LANGUAGE_2);
                    conn.addRequestProperty(USER_AGENT, USER_AGENT_2);
                    conn.addRequestProperty(REFERRER, REFERRER_2);

                    System.out.println(REDIRECT_URL + newUrl);

                    outURL = createUrl(newUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return outURL;
        }
    }
}
