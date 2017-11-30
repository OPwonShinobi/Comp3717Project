package com.example.comp3717project.comp3717project;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

//    private OnFragmentInteractionListener mListener;

    private Spinner mainSpinner;
    private EditText mainAddress;
    private Button btnSubmit;
    private ImageButton mallButton;
    private ImageButton parkButton;
    private ImageButton ownAddressButton;
    private MainActivity.JsonTypeTag purpose = MainActivity.JsonTypeTag.PARKING;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(Bundle bundle) {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set background image opacity (between 0~255)
        MainActivity.mainBgImg.setImageAlpha(100);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mainSpinner = rootView.findViewById(R.id.main_spinner);
        mainAddress = rootView.findViewById(R.id.main_addressEntry);
        btnSubmit = rootView.findViewById(R.id.main_btn_Submit);

        mallButton = (ImageButton) rootView.findViewById(R.id.shopIcon);

        mallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                purpose = MainActivity.JsonTypeTag.SHOPPING;
                String address = mainAddress.getText().toString().trim();
                StartMap(v, address);
            }
        });

        parkButton = (ImageButton) rootView.findViewById(R.id.parkIcon);

        parkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                purpose = MainActivity.JsonTypeTag.PARKS;
                String address = mainAddress.getText().toString().trim();
                StartMap(v, address);
            }
        });

        ownAddressButton = (ImageButton) rootView.findViewById(R.id.ownAddressIcon);

        ownAddressButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                purpose = MainActivity.JsonTypeTag.PARKING;
                String address = mainAddress.getText().toString().trim();
                StartMap(v, address);            }
        });

        mainSpinner.setVisibility(View.INVISIBLE);
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Object item = parentView.getItemAtPosition(position);
                switch (position) {
                    case 1:
                        mainAddress.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mainAddress.setVisibility(View.INVISIBLE);
                        // do something for shopping
                        purpose = MainActivity.JsonTypeTag.SHOPPING;
                        break;
                    case 3:
                        mainAddress.setVisibility(View.INVISIBLE);
                        // do something for parks
                        purpose = MainActivity.JsonTypeTag.PARKS;
                        break;
                    default:
                        mainAddress.setVisibility(View.INVISIBLE);
                        purpose = MainActivity.JsonTypeTag.PARKING;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                System.out.println("Nothing selected");     //prints the text in spinner item.
            }

        });

        btnSubmit.setVisibility(View.INVISIBLE);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String address = mainAddress.getText().toString().trim();
                StartMap(v, address);
            }
        });

        return rootView;
    }

    public void StartMap(View view, String destnAddress){

        int position = mainSpinner.getSelectedItemPosition();

        switch (purpose) {
            case SHOPPING:
                // handle JSON file for shopping mall
                position = 2;
                break;
            case PARKS:
                // handle JSON file for parks
                position = 3;
                break;
            case PARKING:
                position = 1;
                break;
            default:
                // go to my location
                position = 4;
        }

        Intent intent = new Intent(getActivity(), GoogleMapsActivity.class);
        intent.putExtra("DEST_ADDRESS_EXTRA", destnAddress);
        //intent.putExtra("SELECTED_ACTION_EXTRA", mainSpinner.getSelectedItemPosition());
        intent.putExtra("SELECTED_ACTION_EXTRA", position);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
