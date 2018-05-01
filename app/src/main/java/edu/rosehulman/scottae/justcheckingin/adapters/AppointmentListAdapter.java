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

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.models.Appointment;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private ArrayList<Appointment> mAppointments;
    private boolean mIsToday;

    public AppointmentListAdapter(Context context, boolean isToday) {
        mAppointments = new ArrayList<>();
        if (isToday) {
            for (int i = 0; i < 3; i++) {
                mAppointments.add(new Appointment("test", new Date()));
            }
        } else {
            for (int i = 0; i < 4; i++) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DATE, -1);
                Date date = cal.getTime();
                mAppointments.add(new Appointment("test", date));
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
        final Appointment appointment = mAppointments.get(position);
        holder.mTitleView.setText(appointment.getTitle());
        holder.mDateView.setText(appointment.getDate().toString());
    }

    @Override
    public int getItemCount() {
        return mAppointments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleView;
        TextView mDateView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.appointment_title);
            mDateView = itemView.findViewById(R.id.appointment_date_text);
        }
    }
}
