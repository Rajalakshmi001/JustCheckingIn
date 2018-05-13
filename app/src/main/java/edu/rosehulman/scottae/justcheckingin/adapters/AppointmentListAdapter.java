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
import edu.rosehulman.scottae.justcheckingin.models.Appointment;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private ArrayList<Appointment> mAppointmentsToday;
    private ArrayList<Appointment> mAppointmentsUpcoming;
    private boolean mIsToday;

    public AppointmentListAdapter(Context context, boolean isToday) {
        mAppointmentsToday = new ArrayList<>();
        mAppointmentsUpcoming = new ArrayList<>();
        mIsToday = isToday;

        // NOTE: this is just ad-hoc test data
        // TODO: sort ArrayList data
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, r.nextInt(4));
            Date date = cal.getTime();

            if (date.after(new Date())) {
                mAppointmentsUpcoming.add(new Appointment("test", date));
            } else {
                mAppointmentsToday.add(new Appointment("test", date));
            }
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
        Appointment appointment;
        if (mIsToday) {
            appointment = mAppointmentsToday.get(position);
        } else {
            appointment = mAppointmentsUpcoming.get(position);
        }
        holder.mTitleView.setText(appointment.getTitle());
        holder.mDateView.setText(appointment.getDate().toString());
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

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.appointment_title);
            mDateView = itemView.findViewById(R.id.appointment_date_text);

            // TODO: add OnLongClickListener
        }
    }
}
