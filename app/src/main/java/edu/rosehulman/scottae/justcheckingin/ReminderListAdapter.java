package edu.rosehulman.scottae.justcheckingin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ViewHolder> {

    private ArrayList<Reminder> mReminders;

    public ReminderListAdapter(Context context, boolean isToday) {
        mReminders = new ArrayList<>();

        if (isToday) {
            for (int i = 0; i < 3; i++) {
                mReminders.add(new Reminder("test", new Date(), true));
            }
        } else {
            for (int i = 0; i < 4; i++) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DATE, -1);
                Date date = cal.getTime();
                mReminders.add(new Reminder("test", date, true));
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
        final Reminder reminder = mReminders.get(position);
        holder.mCommentView.setText(reminder.getTitle());
        holder.mDateView.setText(reminder.getDate().toString());
        if (!reminder.isIsRecurring()) {
            holder.mImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mReminders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mCommentView;
        TextView mDateView;
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mCommentView = itemView.findViewById(R.id.reminder_comment);
            mDateView = itemView.findViewById(R.id.reminder_date_text);
            mImageView = itemView.findViewById(R.id.reminder_recurrence_icon);
        }
    }
}
