package edu.rosehulman.scottae.justcheckingin.adapters;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.models.CheckIn;

public class CheckInListAdapter extends RecyclerView.Adapter<CheckInListAdapter.ViewHolder> {

    public ArrayList<CheckIn> mCheckIns;
    private Context mContext;
    private static final String KEY_CHECKIN_TITLE = "KEY_CHECKIN_TITLE";
    private DatabaseReference mCheckinRef;
    private static DatabaseReference mRef;
    public static String defaultMessage = "";
    private TextView todayCheckinStatusText;
    private boolean doesCheckinHasToday = false;

    public CheckInListAdapter(Context context, String userPath) {
        mContext = context;
        mCheckIns = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference().child(userPath).child("settings/Default message");
        mRef.addValueEventListener(new CheckinDefaultMessageEventListener());
        mRef.keepSynced(true);
        mCheckinRef = FirebaseDatabase.getInstance().getReference()
                .child(userPath).child("checkins");
        mCheckinRef.keepSynced(true);
        mCheckinRef.addChildEventListener(new CheckInChildEventListener());
    }

    private class  CheckInChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            CheckIn checkIn = dataSnapshot.getValue(CheckIn.class);
            mCheckIns.add(checkIn);
            Collections.sort(mCheckIns, Collections.<CheckIn>reverseOrder());
            for(CheckIn checkin : mCheckIns) {
                if (checkin.getDate(checkin.getDate()).equals(checkin.getDate(new Date()))) {
                    doesCheckinHasToday = true;
                    break;
                }
            }
            setSoonAlarm(defaultMessage);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            mCheckIns.remove(dataSnapshot.getValue(CheckIn.class));
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public static class CheckinDefaultMessageEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                defaultMessage = dataSnapshot.getValue().toString();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    @NonNull
    @Override
    public CheckInListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_in_row_view, parent, false);
        View checkinView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_main, parent, false);
        todayCheckinStatusText = checkinView.findViewById(R.id.check_in_status);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckInListAdapter.ViewHolder holder, int position) {
        final CheckIn checkin = mCheckIns.get(position);
        holder.mCommentView.setText(checkin.getComment());
        holder.mDateView.setText(checkin.getDate().toString());
        if(todayCheckinStatusText != null) {
            if (doesCheckinHasToday)
                todayCheckinStatusText.setText("You have checked in today!");
            else
                todayCheckinStatusText.setText("You have not yet checked-in today yet");
        }
    }

    @Override
    public int getItemCount() {
        return mCheckIns.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mCommentView;
        TextView mDateView;

        ViewHolder(View itemView) {
            super(itemView);
            mCommentView = itemView.findViewById(R.id.check_in_comment);
            mDateView = itemView.findViewById(R.id.check_in_date_text);
        }
    }

    public void add(CheckIn checkIn) {
        checkIn.setComment(defaultMessage);
        mCheckinRef.push().setValue(checkIn);
    }

    private void setSoonAlarm(String  defaultMessage) {
        Intent displayIntent = new Intent(mContext,
                DisplayCheckInNotification.class);
        displayIntent.putExtra(KEY_CHECKIN_TITLE,  defaultMessage);

        Notification notification = getNotification(displayIntent, defaultMessage);
        NotificationManager manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(1, notification);
    }

    private Notification getNotification(Intent intent, String message) {
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle(message);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        int unusedRequestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, unusedRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        builder.setLights(Color.RED, 3000, 3000);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        return builder.build();
    }

    public class DisplayCheckInNotification extends Activity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.checkin_notification);

            String title = getIntent().getStringExtra(KEY_CHECKIN_TITLE);

            TextView messageTextView = findViewById(R.id.checkin_title_notification);
            messageTextView.setText(title);
            messageTextView.setTextSize(32);

        }
    }
}
