package com.example.comp3717project.comp3717project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String address = mainAddress.getText().toString().trim();
                StartMap(v, address);
            }
        });

        return rootView;
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

        Intent intent = new Intent(getActivity(), GoogleMapsActivity.class);
        intent.putExtra("DEST_ADDRESS_EXTRA", destnAddress);
        intent.putExtra("SELECTED_ACTION_EXTRA", mainSpinner.getSelectedItemPosition());
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
