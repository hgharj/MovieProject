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

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /** Sample JSON response for a USGS query */
    private static final String SAMPLE_JSON_RESPONSE = "https://api.themoviedb.org/3/movie/popular?api_key=78f8b58674adbaa0bf92f4de4e9a6dc3&sort_by=popularity.desc";//http vs https

    /**
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private NetworkUtils() {
    }

    /**
     * Query the USGS dataset and return an {@link ArrayList <Movie>} object to represent a single movie.
     */
    public static ArrayList<Movie> fetchMovieData(String requestUrl) {
        // Create URL object
//        URL url = createUrl(requestUrl);

        HttpRedirect hr = new HttpRedirect();
        URL url = hr.main(createUrl(requestUrl));

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Movie> movie = extractMovies(jsonResponse);

        // Return the {@link Event}
        return movie;
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Movie> extractMovies(String movieJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding movies to
        ArrayList<Movie> movies = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject reader = new JSONObject(movieJSON);
            //JSONObject sys  = reader.getJSONObject("sys");
            JSONArray results = reader.getJSONArray("results");
            for (int i =0; i < results.length(); i++){
                JSONObject currentMovie = results.getJSONObject(i);
                int movieId = currentMovie.getInt("id");
                double voteAvgDouble = currentMovie.getDouble("vote_average");
                float voteAvg = (float)voteAvgDouble;
                String title = currentMovie.getString("title");
                double ratings = currentMovie.getDouble("popularity");
                String poster = currentMovie.getString("poster_path");
                String releaseDate = currentMovie.getString("release_date");
                String plot = currentMovie.getString("overview");
                Movie movie = new Movie(title,poster,releaseDate,voteAvg,plot);
                movies.add(movie);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movie JSON results", e);
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
            Log.e(LOG_TAG, "Error with creating URL ", e);
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
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200 || urlConnection.getResponseCode() == 301) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
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

        private URL main(URL url){
            String newUrl = "";
            URL outURL = url;

            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);


                System.out.println("Request URL ... " + url);

                boolean redirect = false;

                // normally, 3xx is redirect
                int status = conn.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP
                            || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true;
                    outURL=null;
                }

                System.out.println("Response Code ... " + status);


                if (redirect) {

                    // get redirect url from "location" header field
                    newUrl = conn.getHeaderField("Location");

                    // get the cookie if need, for login
                    String cookies = conn.getHeaderField("Set-Cookie");

                    // open the new connection again
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    conn.setRequestProperty("Cookie", cookies);
                    conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                    conn.addRequestProperty("User-Agent", "Mozilla");
                    conn.addRequestProperty("Referrer", "google.com");

                    System.out.println("Redirect to URL : " + newUrl);

                    outURL = createUrl(newUrl);
                }
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(conn.getInputStream()));
//                String inputLine;
//                StringBuffer html = new StringBuffer();
//
//                while ((inputLine = in.readLine()) != null) {
//                    html.append(inputLine);
//                }
//                in.close();
//
//                System.out.println("URL Content... \n" + html.toString());
//                System.out.println("Done");
            }
            catch (IOException e){
                e.printStackTrace();
            }

            return outURL;
        }
    }
}
