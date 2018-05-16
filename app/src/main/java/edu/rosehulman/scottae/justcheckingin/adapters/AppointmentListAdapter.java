package edu.rosehulman.scottae.justcheckingin.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.fragments.AppointmentFragment;
import edu.rosehulman.scottae.justcheckingin.models.Appointment;
import edu.rosehulman.scottae.justcheckingin.utils.Constants;
import edu.rosehulman.scottae.justcheckingin.utils.NotificationBroadcastReceiver;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private ArrayList<Appointment> mAppointmentsToday;
    private ArrayList<Appointment> mAppointmentsUpcoming;
    private boolean mIsToday;
    private Context mContext;
    private DatabaseReference mRef;
    private DatabaseReference mDefaultReminderRef;
    public static String defaultReminderTime = "";
//    private HashMap<String, Integer> hashMap = new HashMap<>();

    public AppointmentListAdapter(Context context, String userPath, boolean isToday) {
        mContext = context;
        mAppointmentsToday = new ArrayList<>();
        mAppointmentsUpcoming = new ArrayList<>();
        mIsToday = isToday;
        mDefaultReminderRef = FirebaseDatabase.getInstance().getReference().child(userPath).child("settings/Default reminder time");
        mDefaultReminderRef.addValueEventListener(new AppointmentDefaultReminderTimeEventListener());
        mDefaultReminderRef.keepSynced(true);
        mRef = FirebaseDatabase.getInstance().getReference().child(userPath).child(mContext.getString(R.string.appointments));
        mRef.keepSynced(true);
        mRef.addChildEventListener(new AppointmentsChildEventListener());
//        hashMap.put("At time of event", 0);
//        hashMap.put("1 minute before", )
    }

    private class AppointmentsChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Appointment appointment = dataSnapshot.getValue(Appointment.class);
            assert appointment != null;
            appointment.setKey(dataSnapshot.getKey());
            if (mIsToday) {
                if (appointment.getDate(appointment.getDate())
                        .equals(appointment.getDate(new Date()))) {
                    mAppointmentsToday.add(0, appointment);
                    Collections.sort(mAppointmentsToday, Collections.<Appointment>reverseOrder());
                }
            } else if (!appointment.getDate(appointment.getDate())
                    .equals(appointment.getDate(new Date()))) {
                mAppointmentsUpcoming.add(0, appointment);
                Collections.sort(mAppointmentsUpcoming, Collections.<Appointment>reverseOrder());
            }
            setSoonAlarm(appointment);
            notifyDataSetChanged();
        }

        // FIXME: notification timing might get messed up after edit/delete
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Appointment updateAppointment = dataSnapshot.getValue(Appointment.class);
            if (updateAppointment.getDate(updateAppointment.getDate())
                    .equals(updateAppointment.getDate(new Date()))) {
                for (Appointment a : mAppointmentsToday) {
                    if (a.getKey().equals(key)) {
                        a.setValues(updateAppointment);
                        setSoonAlarm(updateAppointment);
                        notifyDataSetChanged();
                        return;
                    }
                }
                mAppointmentsToday.add(updateAppointment);
                for (Appointment a : mAppointmentsUpcoming) {
                    if (a.getKey().equals(key)) {
                        mAppointmentsUpcoming.remove(a);
                    }
                }
                setSoonAlarm(updateAppointment);
                notifyDataSetChanged();
            } else {
                for (Appointment a : mAppointmentsUpcoming) {
                    if (a.getKey().equals(key)) {
                        a.setValues(updateAppointment);
                        setSoonAlarm(updateAppointment);
                        notifyDataSetChanged();
                        return;
                    }
                }
                mAppointmentsUpcoming.add(updateAppointment);
                for (Appointment a : mAppointmentsToday) {
                    if (a.getKey().equals(key)) {
                        mAppointmentsToday.remove(a);
                    }
                }
                setSoonAlarm(updateAppointment);
                notifyDataSetChanged();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            Appointment toBeDeletedAppointment = dataSnapshot.getValue(Appointment.class);
            assert toBeDeletedAppointment != null;
            if (toBeDeletedAppointment.getDate(toBeDeletedAppointment.getDate())
                    .equals(toBeDeletedAppointment.getDate(new Date()))) {
                for (Appointment r : mAppointmentsToday) {
                    if (r.getKey().equals(key)) {
                        mAppointmentsToday.remove(r);
                        notifyDataSetChanged();
                        return;
                    }
                }
            } else {
                for (Appointment r : mAppointmentsUpcoming) {
                    if (r.getKey().equals(key)) {
                        mAppointmentsUpcoming.remove(r);
                        notifyDataSetChanged();
                        return;
                    }
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class AppointmentDefaultReminderTimeEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                defaultReminderTime = dataSnapshot.getValue().toString();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
    @NonNull
    @Override
    public AppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentListAdapter.ViewHolder holder, int position) {
        if (mIsToday) {
            holder.mTitleView.setText(mAppointmentsToday.get(position).getTitle());
            holder.mDateView.setText(mAppointmentsToday.get(position).getDate().toString());
            holder.mCommentsView.setText(mAppointmentsToday.get(position).getComments());
        } else {
            holder.mTitleView.setText(mAppointmentsUpcoming.get(position).getTitle());
            holder.mDateView.setText(mAppointmentsUpcoming.get(position).getDate().toString());
            holder.mCommentsView.setText(mAppointmentsUpcoming.get(position).getComments());
        }
    }

    @Override
    public int getItemCount() {
        if (mIsToday) {
            return mAppointmentsToday.size();
        } else {
            return mAppointmentsUpcoming.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleView;
        TextView mDateView;
        TextView mCommentsView;

        ViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.appointment_title);
            mDateView = itemView.findViewById(R.id.appointment_date_text);
            mCommentsView = itemView.findViewById(R.id.appointment_comments_text);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Appointment a;
                    if (mIsToday) {
                        a = mAppointmentsToday.get(getAdapterPosition());
                    } else {
                        a = mAppointmentsUpcoming.get(getAdapterPosition());
                    }
                    AppointmentFragment.showAddEditAppointmentDialog(a);
                    return false;
                }
            });
        }
    }

    public void addAppointment(Appointment appointment) {
        mRef.push().setValue(appointment);
    }

    public void remove(Appointment appointment) {
        mRef.child(appointment.getKey()).removeValue();
    }

    public void update(Appointment appointment, String title, Date date, String comments) {
        appointment.setTitle(title);
        appointment.setDate(date);
        appointment.setComments(comments);
        mRef.child(appointment.getKey()).setValue(appointment);
    }

    private void setSoonAlarm(Appointment a) {
        Intent displayIntent = new Intent(mContext,
                DisplayAppointmentNotification.class);
        displayIntent.putExtra(Constants.KEY_REMINDER_TITLE, a.getTitle());

        Notification notification = getNotification(displayIntent, a);

        Intent notificationIntent = new Intent(mContext, NotificationBroadcastReceiver.class);
        notificationIntent.putExtra(Constants.KEY_NOTIFICATION, notification);
        notificationIntent.putExtra(Constants.KEY_SOON_NOTIFICATION_ID, 1);
        int unusedRequestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, unusedRequestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long secondsUntilAlarm = findDifference(a);
        if (secondsUntilAlarm > 0) {
            long futureInMills = SystemClock.elapsedRealtime() + secondsUntilAlarm * 1000;
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMills, pendingIntent);
        }
    }

    private Notification getNotification(Intent intent, Appointment a) {
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle(a.getTitle());
        builder.setContentText(a.getComments());
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        int unusedRequestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, unusedRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    private long findDifference(Appointment a) {
        Calendar calendar = Calendar.getInstance();
        Date d1 = a.getDate();
        calendar.setTime(d1);
        Date d2;
        long diff = 0;
        switch (defaultReminderTime) {
            case "At  time of event":
                d2 = calendar.getTime();
                diff = d1.getTime()-d2.getTime();
                break;
            case  "1 minute before":
                calendar.add(Calendar.MINUTE, -1);
                d2 = calendar.getTime();
                diff = d1.getTime() - d2.getTime();
                break;
            case "30 minutes before":
                calendar.add(Calendar.MINUTE, -30);
                d2 = calendar.getTime();
                diff = d1.getTime() - d2.getTime();
                break;
            case "1 hour before":
                calendar.add(Calendar.HOUR, -1);
                d2 = calendar.getTime();
                diff = d1.getTime() - d2.getTime();
                break;
            case "1 day before":
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                d2 = calendar.getTime();
                diff = d1.getTime() - d2.getTime();
                break;
        }
        Log.d("AAA", "Time difference is " + diff);
        return (diff / (60 * 1000) % 60) * 60;
    }

    public class DisplayAppointmentNotification extends Activity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reminder_notification);

            Appointment appointment = getIntent().getParcelableExtra(Constants.KEY_REMINDER_TITLE);

            TextView messageTextView = findViewById(R.id.reminder_title_notification);
            messageTextView.setText(appointment.getTitle());
            messageTextView.setTextSize(32);
        }
    }
}
