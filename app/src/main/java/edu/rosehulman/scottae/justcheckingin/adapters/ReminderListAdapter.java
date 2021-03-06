package edu.rosehulman.scottae.justcheckingin.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.fragments.ReminderFragment;
import edu.rosehulman.scottae.justcheckingin.models.Reminder;
import edu.rosehulman.scottae.justcheckingin.utils.Constants;
import edu.rosehulman.scottae.justcheckingin.utils.NotificationBroadcastReceiver;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Reminder> mRemindersToday;
    private ArrayList<Reminder> mRemindersUpcoming;
    private boolean mIsToday;
    private DatabaseReference mRef;

    public ReminderListAdapter(Context context, String userPath, boolean isToday) {
        mContext = context;
        mRemindersToday = new ArrayList<>();
        mRemindersUpcoming = new ArrayList<>();
        mIsToday = isToday;
        mRef = FirebaseDatabase.getInstance().getReference()
                .child(userPath).child(mContext.getString(R.string.reminders_text));
        mRef.keepSynced(true);
        mRef.addChildEventListener(new RemindersChildEventListener());
    }

    private class RemindersChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Reminder reminder = dataSnapshot.getValue(Reminder.class);
            assert reminder != null;
            reminder.setKey(dataSnapshot.getKey());
            if (mIsToday) {
                if (reminder.getDate(reminder.getDate()).equals(reminder.getDate(new Date()))) {
                    mRemindersToday.add(reminder);
                    Collections.sort(mRemindersToday, Collections.<Reminder>reverseOrder());
                }
            } else if (!reminder.getDate(reminder.getDate()).equals(reminder.getDate(new Date()))) {
                mRemindersUpcoming.add(reminder);
                Collections.sort(mRemindersUpcoming, Collections.<Reminder>reverseOrder());
            }
            setSoonAlarm(reminder);
            notifyDataSetChanged();
        }

        // FIXME: notification timing might get messed up after edit/delete
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Reminder updateReminder = dataSnapshot.getValue(Reminder.class);
            if (updateReminder.getDate(updateReminder.getDate())
                    .equals(updateReminder.getDate(new Date()))) {
                for (Reminder r : mRemindersToday) {
                    if (r.getKey().equals(key)) {
                        r.setValues(updateReminder);
                        setSoonAlarm(updateReminder);
                        notifyDataSetChanged();
                        return;
                    }
                }
                mRemindersToday.add(updateReminder);
                for (Reminder r : mRemindersUpcoming) {
                    if (r.getKey().equals(key)) {
                        mRemindersUpcoming.remove(r);
                    }
                }
                setSoonAlarm(updateReminder);
                notifyDataSetChanged();
            } else {
                for (Reminder r : mRemindersUpcoming) {
                    if (r.getKey().equals(key)) {
                        r.setValues(updateReminder);
                        setSoonAlarm(updateReminder);
                        notifyDataSetChanged();
                        return;
                    }
                }
                mRemindersUpcoming.add(updateReminder);
                for (Reminder r : mRemindersToday) {
                    if (r.getKey().equals(key)) {
                        mRemindersToday.remove(r);
                    }
                }
                setSoonAlarm(updateReminder);
                notifyDataSetChanged();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            Reminder toBeDeletedReminder = dataSnapshot.getValue(Reminder.class);
            assert toBeDeletedReminder != null;
            if (toBeDeletedReminder.getDate(toBeDeletedReminder.getDate())
                    .equals(toBeDeletedReminder.getDate(new Date()))) {
                for (Reminder r : mRemindersToday) {
                    if (r.getKey().equals(key)) {
                        mRemindersToday.remove(r);
                        notifyDataSetChanged();
                        return;
                    }
                }
            } else {
                for (Reminder r : mRemindersUpcoming) {
                    if (r.getKey().equals(key)) {
                        mRemindersUpcoming.remove(r);
                        notifyDataSetChanged();
                        return;
                    }
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            // empty
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("Database error", databaseError.getMessage());
        }
    }

    @NonNull
    @Override
    public ReminderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderListAdapter.ViewHolder holder, int position) {
        if (mIsToday) {
            holder.mTitleView.setText(mRemindersToday.get(position).getTitle());
            holder.mDateView.setText(mRemindersToday.get(position).getDate().toString());
        } else {
            holder.mTitleView.setText(mRemindersUpcoming.get(position).getTitle());
            holder.mDateView.setText(mRemindersUpcoming.get(position).getDate().toString());
        }
    }

    @Override
    public int getItemCount() {
        if (mIsToday) {
            return mRemindersToday.size();
        } else {
            return mRemindersUpcoming.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleView;
        TextView mDateView;

        ViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.reminder_comment);
            mDateView = itemView.findViewById(R.id.reminder_date_text);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Reminder r;
                    if (mIsToday) {
                        r = mRemindersToday.get(getAdapterPosition());
                    } else {
                        r = mRemindersUpcoming.get(getAdapterPosition());
                    }
                    ReminderFragment.showAddEditReminderDialog(r);
                    return false;
                }
            });
        }
    }

    public void add(Reminder reminder) {
        mRef.push().setValue(reminder);
    }

    public void remove(Reminder reminder) {
        mRef.child(reminder.getKey()).removeValue();
    }

    public void update(Reminder reminder, String title, Date date) {
        reminder.setTitle(title);
        reminder.setDate(date);
        mRef.child(reminder.getKey()).setValue(reminder);
    }

    private void setSoonAlarm(Reminder r) {
        Intent displayIntent = new Intent(mContext,
                DisplayReminderNotification.class);
        displayIntent.putExtra(Constants.KEY_REMINDER_TITLE, r.getTitle());

        Notification notification = getNotification(displayIntent, r);

        Intent notificationIntent = new Intent(mContext, NotificationBroadcastReceiver.class);
        notificationIntent.putExtra(Constants.KEY_NOTIFICATION, notification);
        notificationIntent.putExtra(Constants.KEY_SOON_NOTIFICATION_ID, 1);
        int unusedRequestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, unusedRequestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long secondsUntilAlarm = findDifference(r);
        if (secondsUntilAlarm > 0) {
            long futureInMills = SystemClock.elapsedRealtime() + secondsUntilAlarm * 1000;
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMills, pendingIntent);
        }
    }

    private Notification getNotification(Intent intent, Reminder r) {
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle(r.getTitle());
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        int unusedRequestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, unusedRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        return builder.build();
    }

    private long findDifference(Reminder r) {
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        Date d2 = r.getDate();
        long diff = d2.getTime() - d1.getTime();
        return (diff / (60 * 1000) % 60) * 60;
    }

    public class DisplayReminderNotification extends Activity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reminder_notification);

            Reminder reminder = getIntent().getParcelableExtra(Constants.KEY_REMINDER_TITLE);

            TextView messageTextView = findViewById(R.id.reminder_title_notification);
            messageTextView.setText(reminder.getTitle());
            messageTextView.setTextSize(32);
        }
    }
}
