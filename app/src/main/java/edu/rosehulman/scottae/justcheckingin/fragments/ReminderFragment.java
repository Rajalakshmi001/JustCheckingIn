package edu.rosehulman.scottae.justcheckingin.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.ReminderListAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReminderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public ReminderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ReminderFragment newInstance(int sectionNumber) {
        ReminderFragment fragment = new ReminderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminders, container, false);

        //today recycler view code
//        RecyclerView recyclerViewToday = rootView.findViewById(R.id.reminders_today_recycler_view);
//        recyclerViewToday.setLayoutManager(new LinearLayoutManager(getContext()));
//        ReminderListAdapter adapter = new ReminderListAdapter(getContext());
//        recyclerViewToday.setAdapter(adapter);

        //upcoming recycler view code
        RecyclerView recyclerViewUpcoming = rootView.findViewById(R.id.reminders_upcoming_recycler_view);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        ReminderListAdapter adapter = new ReminderListAdapter(getContext());
        recyclerViewUpcoming.setAdapter(adapter);
        return rootView;
    }
}