package com.example.bacci.movieplots;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView txtgenere = (TextView) findViewById(R.id.etext);
        txtgenere.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    cercadaTastiera();// chiamo la funzione cercadatastiera che performerà la ricerca senza la pressione del tasto fisico nella schermata ma da tastiera
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void Clear(View v) {
        //implemento il tasto per pulire la edittext
        EditText svuota = (EditText) findViewById(R.id.etext);
        svuota.setText("");
    }
    public void svuotaedit(View v) {
        EditText filmtofind = (EditText) findViewById(R.id.etext);//controllo la edittext e la pulisco solo la prima volta così non la pulisco anche quando non voglio
        String film = filmtofind.getText().toString();
        if (film.equals("Wanna find a movie's plot?")) {
            EditText svuota = (EditText) findViewById(R.id.etext);
            svuota.setText("");
        }
    }
    public void cerca(View v) {

        EditText filmtofind = (EditText) findViewById(R.id.etext);
        String film = filmtofind.getText().toString();
        if (film.equals("Wanna find a movie's plot?") || film.equals("")) {
            Toast.makeText(getBaseContext(), "I need a title to perform a research", Toast.LENGTH_SHORT).show(); //Controllo che l'utente si abbia inserito qualcosa
        } else {
            String titolo = film;
            Intent passaggio = new Intent(this, ScrollingActivity.class);
            titolo = CamelCase(titolo);
            passaggio.putExtra("titolo", titolo);
            film = film.replace(" ", "+");
            // creo il valore da passare all'altra view che mi servirà poi per costruire l'indirizzo
            passaggio.putExtra("film", film);
            startActivity(passaggio);
            //ottengo i dati dall'edittext e li trasformo in modo da ottenere un link
        }
    }
    private String CamelCase(String input) //prima faccio la prima lettera maiuscola
    {
        return input.substring(0,1).toUpperCase()+input.substring(1);
    }
    public void cercadaTastiera()
    {
        EditText filmtofind = (EditText) findViewById(R.id.etext);
        //creo la stessa funzione ma questa volta per avere la ricerca da tastiera
        String film = filmtofind.getText().toString();
        if (film.equals("Wanna find a movie's plot?") || film.equals("")) {
            Toast.makeText(getBaseContext(), "I need a title to perform a research", Toast.LENGTH_SHORT).show();
        } else {
            String titolo = film;
            Intent passaggio = new Intent(this, ScrollingActivity.class);
            titolo = CamelCase(titolo);
            passaggio.putExtra("titolo", titolo);
            film = film.replace(" ", "+");
            passaggio.putExtra("film", film);
            startActivity(passaggio);
            //ottengo i dati dall'edittext e li trasformo in modo da ottenere un link
        }
    }
}
