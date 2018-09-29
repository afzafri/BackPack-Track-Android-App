package com.afifzafri.backpacktrack;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyItineraryFragment extends Fragment {


    public MyItineraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_itinerary, container, false);

        // handle FAB button
        FloatingActionButton createFab = (FloatingActionButton) view.findViewById(R.id.createFab);
        createFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Create new itinerary", Toast.LENGTH_SHORT).show();

            }
        });


        return view;
    }

}
