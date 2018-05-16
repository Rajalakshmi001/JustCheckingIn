package edu.rosehulman.scottae.justcheckingin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Date;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.adapters.CheckInListAdapter;
import edu.rosehulman.scottae.justcheckingin.models.CheckIn;

/**
 * A placeholder fragment containing a simple view.
 */
public class CheckInFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static Context mContext;
    private CheckInListAdapter checkInListAdapter;
    private static String mUserPath;

    public CheckInFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CheckInFragment newInstance(Context context, int sectionNumber, String userpath) {
        mContext = context;
        mUserPath = userpath;
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
        Button sendNowButton = rootView.findViewById(R.id.send_now_button);
        RecyclerView recyclerView = rootView.findViewById(R.id.check_in_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        checkInListAdapter = new CheckInListAdapter(getContext(), mUserPath);
        recyclerView.setAdapter(checkInListAdapter);
        sendNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIn checkIn = new CheckIn("Default comment", new Date());
                checkInListAdapter.add(checkIn);
            }
        });
        return rootView;
    }
}