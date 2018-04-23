package edu.rosehulman.scottae.justcheckingin.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.scottae.justcheckingin.CheckInListAdapter;
import edu.rosehulman.scottae.justcheckingin.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CheckInFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public CheckInFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CheckInFragment newInstance(int sectionNumber) {
        CheckInFragment fragment = new CheckInFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.check_in_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CheckInListAdapter adapter = new CheckInListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        return rootView;
    }
}