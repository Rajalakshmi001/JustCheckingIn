package edu.rosehulman.scottae.justcheckingin.fragments;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.activities.DisplayReminderNotification;
import edu.rosehulman.scottae.justcheckingin.adapters.CheckInListAdapter;
import edu.rosehulman.scottae.justcheckingin.models.Reminder;
import edu.rosehulman.scottae.justcheckingin.utils.NotificationBroadcastReceiver;

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
    private static DatabaseReference mRef;
    public static final String KEY_REMINDER_TITLE = "KEY_REMINDER_TITLE";
    public static final String KEY_NOTIFICATION = "KEY_NOTIFICATION";
    public static final String KEY_SOON_NOTIFICATION_ID = "KEY_SOON_NOTIFICATION_ID";

    public CheckInFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CheckInFragment newInstance(Context context, int sectionNumber, String userpath) {
        mContext = context;
        CheckInFragment fragment = new CheckInFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        mRef = FirebaseDatabase.getInstance().getReference().child(userpath).child("settings");
        mRef.addChildEventListener(new CheckinChildEventListener());
        mRef.keepSynced(true);
        fragment.setArguments(args);
        return fragment;
    }

    public static class CheckinChildEventListener implements ChildEventListener{

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot != null) {
                dataSnapshot.getValue();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button sendNowButton = rootView.findViewById(R.id.send_now_button);
        sendNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSoonAlarm("Are you ok?");
            }
        });
        RecyclerView recyclerView = rootView.findViewById(R.id.check_in_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CheckInListAdapter adapter = new CheckInListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    private void setSoonAlarm(String  defaultMessage) {
        Intent displayIntent = new Intent(mContext,
                DisplayReminderNotification.class);
        displayIntent.putExtra(KEY_REMINDER_TITLE,  defaultMessage);

        Notification notification = getNotification(displayIntent, defaultMessage);
        NotificationManager manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(1, notification);
    }

    private Notification getNotification(Intent intent, String message) {
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle("Just Checking-in reminder");
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        int unusedRequestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, unusedRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }
}