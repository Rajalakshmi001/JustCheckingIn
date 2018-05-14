package edu.rosehulman.scottae.justcheckingin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.fragments.ReminderFragment;
import edu.rosehulman.scottae.justcheckingin.models.Reminder;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Reminder> mRemindersToday;
    private ArrayList<Reminder> mRemindersUpcoming;
    private boolean mIsToday;
    private DatabaseReference mRef;
    private Reminder mReminder;

    public ReminderListAdapter(Context context, String userPath, boolean isToday) {
        mContext = context;
        mRemindersToday = new ArrayList<>();
        mRemindersUpcoming = new ArrayList<>();
        mIsToday = isToday;
        mRef = FirebaseDatabase.getInstance().getReference().child(userPath).child(mContext.getString(R.string.reminders_text));
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
                    mRemindersToday.add(0, reminder);
                    Collections.sort(mRemindersToday);
                }
            } else if (!reminder.getDate(reminder.getDate()).equals(reminder.getDate(new Date()))) {
                mRemindersUpcoming.add(0, reminder);
                Collections.sort(mRemindersUpcoming);
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Reminder updateReminder = dataSnapshot.getValue(Reminder.class);
            if (updateReminder.getDate(updateReminder.getDate())
                    .equals(updateReminder.getDate(new Date()))) {
                for (Reminder r : mRemindersToday) {
                    if (r.getKey().equals(key)) {
                        r.setValues(updateReminder);
                        notifyDataSetChanged();
                        return;
                    }
                }
            } else {
                for (Reminder r : mRemindersUpcoming) {
                    if (r.getKey().equals(key)) {
                        r.setValues(updateReminder);
                        notifyDataSetChanged();
                        return;
                    }
                }
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
            mReminder = mRemindersToday.get(position);
        } else {
            mReminder = mRemindersUpcoming.get(position);
        }
        holder.mTitleView.setText(mReminder.getTitle());
        holder.mDateView.setText(mReminder.getDate().toString());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ReminderFragment.showAddEditReminderDialog(mReminder);
                return false;
            }
        });
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
        }
    }

//    // FIXME: not sending accurate Date object
//    private static Date convertStringToDate(String s) {
//        Log.e("AAA", "Current date: " + s);
//        DateFormat format = DateFormat.getDateInstance();
//        try {
//            Date date = format.parse(s);
//            Log.e("AAA", "Sent date: " + date.toString());
//            return date;
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public void addReminder(Reminder reminder) {
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
}
