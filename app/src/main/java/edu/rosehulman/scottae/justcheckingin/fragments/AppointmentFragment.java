package edu.rosehulman.scottae.justcheckingin.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.scottae.justcheckingin.AppointmentListAdapter;
import edu.rosehulman.scottae.justcheckingin.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class AppointmentFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public AppointmentFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AppointmentFragment newInstance(int sectionNumber) {
        AppointmentFragment fragment = new AppointmentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments, container, false);

        // today recycler view code
        RecyclerView recyclerViewToday = rootView.findViewById(R.id.appointments_today_recycler_view);
        recyclerViewToday.setLayoutManager(new LinearLayoutManager(getContext()));
        AppointmentListAdapter adapter = new AppointmentListAdapter(getContext(), true);
        recyclerViewToday.setAdapter(adapter);

        // upcoming recycler view code
        RecyclerView recyclerViewUpcoming = rootView.findViewById(R.id.appointments_upcoming_recycler_view);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        AppointmentListAdapter adapter1 = new AppointmentListAdapter(getContext(), false);
        recyclerViewUpcoming.setAdapter(adapter1);
        return rootView;
    }
}