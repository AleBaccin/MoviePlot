package com.example.bacci.movieplots;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
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
        aggiorna(); //durante il caricamento della view aggiorno i dati
    }

    public String cerca_informazioni() {
        String nomefilm = getIntent().getStringExtra("film"); //ottengo l'intent dall'altra activity con l'indirizzo del json per i dati del film

        String conn = "http://www.omdbapi.com/?t=" + nomefilm + "&y=&plot=short&r=json";
        return conn; //l'URL finale da utilizzare
    }

    public void aggiorna() { // creo la funzione che poi mi faraà il parsing e che a sua volta chiamerà la funzione per scaricare l'immagine
        RicercaCampo client = new RicercaCampo();
        client.execute(cerca_informazioni()); //chiamo cercainformazioni che quindi fornirà l'URL per la ricerca

    }

    public class RicercaCampo extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            // dichiarazione delle var/obj che mi serviranno
            final String[] informazioni = {"Genre", "Year", "Director", "Metascore", "Plot", "Poster"}; //inserisco in un array di stringhe i valori da cercare
            final String[] inforitorno = new String[6];//creo preventivamente l'array dei valori da restituire
            URL url;
            HttpURLConnection urlConnection = null;
            String responseString;
            // definizione di una costante
            try {
                // leggo i dati di risposta del web service nella pagina internet
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                responseString = readStream(urlConnection.getInputStream());
                // fine della lettura, i dati stanno nella variabile responseString

                //Trasformo la stringa in un oggetto di tipo JSON
                JSONObject jsonObj = new JSONObject(responseString);
                String controllo = jsonObj.getString("Response");
                // ottengo una stringa per poi controllare se il film effettivamente esiste
                if (controllo.equals("False")) { //dal momento che se il film non esiste il json mi restituisce una stringa "Response" = "False" io la controllo
                    for (int i = 0; i < inforitorno.length; i++) {
                        if( i== 0 )
                        inforitorno[i] = "Sorry, try with another film."; // se quindi non esiste sostituisco gli antiestetici "null" con delle frasi comprensibile dall'utenza
                        else
                            inforitorno[i] = "Movie Not Found!";
                    }
                } else {
                    for (int i = 0; i < informazioni.length; i++) { //se il film esiste quindi carico il mio array(inforitorno) con le stringje uttenute compreso l'indirizzo per scaricare l'immagine
                        String campoattuale = informazioni[i];
                        String film = jsonObj.getString(campoattuale);
                        inforitorno[i] = film; //carico l'array inforitorno
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return inforitorno;

        }

        protected void onPostExecute(String[] inforitorno) {
            String titolofilm = getIntent().getStringExtra("titolo"); //Inserisco il titolo
            TextView txttitolo = (TextView) findViewById(R.id.title);
            txttitolo.setText("Title: " + titolofilm);
         // con l'array inforitorno riempito e il titolo passatomi dall'altra activity carico quindi le textbox e scarico l'immagine da il link ottenuto
            for (int i = 0; i < inforitorno.length; i++) {
                if (i == 0) {
                    TextView txtgenere = (TextView) findViewById(R.id.Genre);
                    txtgenere.setText("Genre: " + inforitorno[i]);
                } else if (i == 1) {
                    TextView txtgenere = (TextView) findViewById(R.id.Year);
                    txtgenere.setText("Year: " + inforitorno[i]);
                } else if (i == 2) {
                    TextView txtgenere = (TextView) findViewById(R.id.Director);
                    txtgenere.setText("Director: " + inforitorno[i]);
                } else if (i == 3) {
                    TextView txtgenere = (TextView) findViewById(R.id.Metascore);
                    txtgenere.setText("Metascore: " + inforitorno[i]);
                } else if (i == 4) {
                    TextView txtgenere = (TextView) findViewById(R.id.Plot);
                    txtgenere.setText("Plot: " + inforitorno[i]);
                }
                else
                {
                    ImageDownloader id = new ImageDownloader();
                    id.execute(inforitorno[i]); //chiamo la funzione che scarica l'immagine
                }
            }
        }

        private String readStream(InputStream in) { // readstream dei pdf
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

    private class ImageDownloader extends AsyncTask<String, Integer, Bitmap> { //asynctask per il download dell'immagine
        ProgressDialog pDialog;

        protected void onPreExecute() { //creo il messaggio che segnala se l'immagine sta scaricando
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
            ImageView iv = (ImageView) findViewById(R.id.locandina);
            if (iv != null && img != null) {
                iv.setImageBitmap(img); //imposto l'immagine scaricata
            }
        }
    }

}
