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

import java.util.ArrayList;
import java.util.Date;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.models.Reminder;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ViewHolder> {

    private ArrayList<Reminder> mRemindersToday;
    private ArrayList<Reminder> mRemindersUpcoming;
    private boolean mIsToday;
    private DatabaseReference mRef;

    public ReminderListAdapter(Context context, String userPath, boolean isToday) {
        mRemindersToday = new ArrayList<>();
        mRemindersUpcoming = new ArrayList<>();
        mIsToday = isToday;
        mRef = FirebaseDatabase.getInstance().getReference().child(userPath).child("reminders");
        mRef.keepSynced(true);
        mRef.addChildEventListener(new RemindersChildEventListener());

        // FIXME: this is just ad-hoc test data
        // TODO: sort ArrayList data
//        Random r = new Random();
//        for (int i = 0; i < 5; i++) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(new Date());
//            cal.add(Calendar.DATE, r.nextInt(4));
//            Date date = cal.getTime();
//
//            if (date.after(new Date())) {
//                Reminder reminder = new Reminder("test", date);
//                mRemindersUpcoming.add(reminder);
//                mRef.push().setValue(reminder);
//            } else {
//                Reminder reminder = new Reminder("test", date);
//                mRemindersToday.add(reminder);
//                mRef.push().setValue(reminder);
//            }
//        }
    }

    @NonNull
    @Override
    public ReminderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderListAdapter.ViewHolder holder, int position) {
        Reminder reminder;
        if (mIsToday) {
            reminder = mRemindersToday.get(position);
        } else {
            reminder = mRemindersUpcoming.get(position);
        }
        holder.mCommentView.setText(reminder.getTitle());
        holder.mDateView.setText(reminder.getDate().toString());
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

        TextView mCommentView;
        TextView mDateView;

        ViewHolder(View itemView) {
            super(itemView);
            mCommentView = itemView.findViewById(R.id.reminder_comment);
            mDateView = itemView.findViewById(R.id.reminder_date_text);

            // TODO: add OnLongClickListener
        }
    }

    private class RemindersChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Reminder reminder = dataSnapshot.getValue(Reminder.class);
            assert reminder != null;
            reminder.setKey(dataSnapshot.getKey());
            if (mIsToday) {
                if (reminder.getDate(reminder.getDate()).equals(reminder.getDate(new Date())))
                    mRemindersToday.add(0, reminder);
            } else if (!reminder.getDate(reminder.getDate()).equals(reminder.getDate(new Date()))) {
                mRemindersUpcoming.add(0, reminder);
            }
            notifyItemInserted(0);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Reminder updateReminder = dataSnapshot.getValue(Reminder.class);
//            for (Reminder r : mMyPics) {
//                if (r.getKey().equals(key)) {
//                    r.setValues(updateReminder);
//                    notifyDataSetChanged();
//                }
//            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
//            for (Reminder r : mMyPics) {
//                if (r.getKey().equals(key)) {
//                    mMyPics.remove(r);
//                    notifyDataSetChanged();
//                }
//            }
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
}
