package com.example.comp3717project.comp3717project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public enum JsonTypeTag {PARKING, PARKS, SHOPPING, ADDRESS};
    private TextView mTextMessage;
    private Spinner mainSpinner;
    private EditText mainAddress;
    private Button btnSubmit;
    private JsonTypeTag purpose = JsonTypeTag.PARKING;

    public static List<FavoritePlace> favoriteList = new ArrayList<>();

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
//                String msg = item.toString() + " " + position;     //prints the text in spinner item.
//                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                if(position == 1){
//                    mainAddress.setVisibility(View.VISIBLE);
//                } else {
//                    mainAddress.setVisibility(View.INVISIBLE)
                System.out.println(item.toString() + " " + position);     //prints the text in spinner item.
                switch (position) {
                    case 1:
                        mainAddress.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mainAddress.setVisibility(View.INVISIBLE);
                        // do something for shopping
                        purpose = JsonTypeTag.SHOPPING;
                        break;
                    case 3:
                        mainAddress.setVisibility(View.INVISIBLE);
                        // do something for parks
                        purpose = JsonTypeTag.PARKS;
                        break;
                    default:
                        mainAddress.setVisibility(View.INVISIBLE);
                        purpose = JsonTypeTag.PARKING;
                        break;
                }


//                if(position == 1){
//                    mainAddress.setVisibility(View.VISIBLE);
//                } else {
//                    mainAddress.setVisibility(View.INVISIBLE);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                System.out.println("Nothing selected");     //prints the text in spinner item.
            }

        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String address = mainAddress.getText().toString().trim();
                StartMap(v, address);
            }
        });

        FloatingActionButton FAB = (FloatingActionButton)findViewById(R.id.locate_me_fab);
        FAB.setOnClickListener(new tempFABListener());
    }

    public void StartMap(View view, String destnAddress){
        switch (purpose) {
            case SHOPPING:
                // call JSONHandler constructor for shopping
                break;
            case PARKS:
                // call JSONHandler constructor for parks
                break;
        }

        Intent intent = new Intent(this, GoogleMapsActivity.class);
        intent.putExtra("DEST_ADDRESS_EXTRA", destnAddress);
        intent.putExtra("SELECTED_ACTION_EXTRA", mainSpinner.getSelectedItemPosition());
        startActivity(intent);
    }

    private class tempFABListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, GoogleMapsActivity.class);
            intent.putExtra("DEST_ADDRESS_EXTRA", "");
            intent.putExtra("SELECTED_ACTION_EXTRA", 4); //exceptional case
            startActivity(intent);
        }
    }
}
