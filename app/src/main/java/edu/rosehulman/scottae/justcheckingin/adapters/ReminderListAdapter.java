package edu.rosehulman.scottae.justcheckingin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.models.Reminder;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ViewHolder> {

    private ArrayList<Reminder> mRemindersToday;
    private ArrayList<Reminder> mRemindersUpcoming;
    private boolean mIsToday;

    public ReminderListAdapter(Context context, boolean isToday) {
        mRemindersToday = new ArrayList<>();
        mRemindersUpcoming = new ArrayList<>();
        mIsToday = isToday;

        // FIXME: this is just ad-hoc test data
        // TODO: sort ArrayList data
//        mRemindersToday.add(new Reminder("test", new Date()));
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, r.nextInt(4));
            Date date = cal.getTime();

            if (date.after(new Date())) {
                mRemindersUpcoming.add(new Reminder("test", date));
            } else {
                mRemindersToday.add(new Reminder("test", date));
            }
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
        }
    }
}
