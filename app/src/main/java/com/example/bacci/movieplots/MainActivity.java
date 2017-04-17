package com.example.bacci.movieplots;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
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
        TextView txtGenre = (TextView) findViewById(R.id.etext);
        txtGenre.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();//Perform the research with the search button on the keyboard
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
        //Clear the editText
        EditText empty = (EditText) findViewById(R.id.etext);
        empty.setText("");
    }
    public void clearEditText(View v) {
        EditText filmToFind = (EditText) findViewById(R.id.etext);//Clear the editText the first time I type in.
        String film = filmToFind.getText().toString();
        if (film.equals("Want to look for a Movie?")) {
            filmToFind.setText("");
        }
    }
    public void searchGo(View v) {
        search();
    }
    private String CamelCase(String input) //Set the String to Camel Case
    {
        return input.substring(0,1).toUpperCase()+input.substring(1);
    }
    public void search()    //method to look for the Movie
    {
        EditText filmtofind = (EditText) findViewById(R.id.etext);
        String film = filmtofind.getText().toString();
        if (film.equals("Want to look for a Movie?") || film.equals("")) {
            Toast.makeText(getBaseContext(), "I need a title to perform a research", Toast.LENGTH_SHORT).show();
        } else {
            String titolo = film;
            Intent passaggio = new Intent(this, ScrollingActivity.class);
            titolo = CamelCase(titolo);
            passaggio.putExtra("title", titolo);
            film = film.replace(" ", "+");
            passaggio.putExtra("film", film);
            startActivity(passaggio);
            //Obtain data from the edittext to trasmit it to the next view
        }
    }
}
