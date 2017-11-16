package com.example.comp3717project.comp3717project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String EXTRA_MESSAGE = "Hello World";
    private TextView mTextMessage;
    private Spinner mainSpinner;
    private EditText mainAddress;
    private Button btnSubmit;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.main_title);
        mainSpinner = (Spinner)findViewById(R.id.main_spinner);
        mainAddress = (EditText) findViewById(R.id.main_addressEntry);
        btnSubmit = (Button) findViewById(R.id.main_btn_Submit);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Object item = parentView.getItemAtPosition(position);
                System.out.println(item.toString() + " " + position);     //prints the text in spinner item.
                if(position == 1){
                    mainAddress.setVisibility(View.VISIBLE);
                } else {
                    mainAddress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                System.out.println("Nothing selected");     //prints the text in spinner item.
            }

        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EXTRA_MESSAGE = mainAddress.getText().toString();
                StartMap(v);
            }
        });

        FloatingActionButton FAB = (FloatingActionButton)findViewById(R.id.locate_me_fab);
        FAB.setOnClickListener(new tempFABListener());
    }

    public void StartMap(View view){
//        double latitude = 40.714728;
//        double longitude = -73.998672;
//        String label = "ABC Label";
//        String uriBegin = "geo:" + latitude + "," + longitude;
//        String query = latitude + "," + longitude + "(" + label + ")";
//        String encodedQuery = Uri.encode(query);
//        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        //Uri uri = Uri.parse(uriString);
        //Intent intent = new Intent(android.activity, uri);
        //startActivity(intent);
        Intent intent = new Intent(this, GoogleMapsActivity.class);
        EditText editText = (EditText) findViewById(R.id.main_addressEntry);
        String message = editText.getText().toString();
        intent.putExtra("MyMessage", message);
        startActivity(intent);
    }

    private class tempFABListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String msg = "Placeholder text.";
            Snackbar allahu = Snackbar.make(findViewById(R.id.container), msg, Snackbar.LENGTH_LONG);
            allahu.setAction("Undo", new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Toast burnt = Toast.makeText(MainActivity.this, "Undid.", Toast.LENGTH_LONG);
                    burnt.show();
                }
            });
            allahu.show();
        }
    }
}
