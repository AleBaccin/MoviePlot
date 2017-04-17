package com.example.bacci.movieplots;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        update(); //While the view is loading I update the data
    }

    public String lookForInformations() {
        String filmName = getIntent().getStringExtra("film"); //with the intent from the previous view I create the URL.

        String connection = "http://www.omdbapi.com/?t=" + filmName + "&y=&plot=short&r=json";
        return connection; //final URL
    }

    public void update() {
        searchFields client = new searchFields(); //Calling for the parsing
        client.execute(lookForInformations()); //execute

    }

    public class searchFields extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //var and objects that I will need
            final String[] information = {"Genre", "Year", "Director", "Metascore", "Plot", "Poster"}; //values to look for
            final String[] results = new String[6];//array to return
            URL url;
            HttpURLConnection urlConnection = null;
            String responseString;
            //Costant
            try {
                //get the data from the json
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                responseString = readStream(urlConnection.getInputStream());
                //data stored in response String

                //String into jason object
                JSONObject jsonObj = new JSONObject(responseString);
                String control = jsonObj.getString("Response");
                //check if the movie exist
                if (control.equals("False")) { //if the movie doesn't exist the json will give "Response=false"
                    for (int i = 0; i < results.length; i++) {
                        if( i== 0 )
                        results[i] = "Sorry, try with another film.";
                        else
                            results[i] = "Movie Not Found!";
                    }
                } else {
                    for (int i = 0; i < information.length; i++) { //If the movie exist I will fill the array with the values and get the link for the poster
                        String actualField = information[i];
                        String film = jsonObj.getString(actualField);
                        results[i] = film; //load the results array
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return results;

        }

        protected void onPostExecute(String[] resultsInfo) {
            String movieTitle = getIntent().getStringExtra("title"); //Show the title
            TextView textTitle = (TextView) findViewById(R.id.title);
            textTitle.setText("Title: " + movieTitle);
         // with the results in my array I can now show them to the user
            for (int i = 0; i < resultsInfo.length; i++) {
                if (i == 0) {
                    TextView txtgenere = (TextView) findViewById(R.id.Genre);
                    txtgenere.setText("Genre: " + resultsInfo[i]);
                } else if (i == 1) {
                    TextView txtgenere = (TextView) findViewById(R.id.Year);
                    txtgenere.setText("Year: " + resultsInfo[i]);
                } else if (i == 2) {
                    TextView txtgenere = (TextView) findViewById(R.id.Director);
                    txtgenere.setText("Director: " + resultsInfo[i]);
                } else if (i == 3) {
                    TextView txtgenere = (TextView) findViewById(R.id.Metascore);
                    txtgenere.setText("Metascore: " + resultsInfo[i]);
                } else if (i == 4) {
                    TextView txtgenere = (TextView) findViewById(R.id.Plot);
                    txtgenere.setText("Plot: " + resultsInfo[i]);
                }
                else
                {
                    ImageDownloader id = new ImageDownloader();
                    id.execute(resultsInfo[i]); //method to download the image
                }
            }
        }

        private String readStream(InputStream in) { // readstream
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }
    }

    private class ImageDownloader extends AsyncTask<String, Integer, Bitmap> { //asynctask to download the image
        ProgressDialog pDialog;

        protected void onPreExecute() { //while the image is loading
            pDialog = new ProgressDialog(ScrollingActivity.this);
            pDialog.setMessage("Download Image ....");
            pDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                if (httpCon.getResponseCode() != 200)
                    throw new Exception("Failed to connect");
                InputStream is = httpCon.getInputStream();
                return BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                Log.e("Image", "Failed to download image", e);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... params) {
        }

        protected void onPostExecute(Bitmap img) {
            pDialog.dismiss();
            ImageView iv = (ImageView) findViewById(R.id.Poster);
            if (iv != null && img != null) {
                iv.setImageBitmap(img); //show the downloaded image
            }
        }
    }

}
