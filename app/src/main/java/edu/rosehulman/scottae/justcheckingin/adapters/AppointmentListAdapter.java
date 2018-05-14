package edu.rosehulman.scottae.justcheckingin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import java.util.Collections;
import java.util.Date;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.fragments.AppointmentFragment;
import edu.rosehulman.scottae.justcheckingin.models.Appointment;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private ArrayList<Appointment> mAppointmentsToday;
    private ArrayList<Appointment> mAppointmentsUpcoming;
    private boolean mIsToday;
    private Context mContext;
    private DatabaseReference mRef;
    private  Appointment mAppointment;

    public AppointmentListAdapter(Context context, String userPath, boolean isToday) {
        mContext = context;
        mAppointmentsToday = new ArrayList<>();
        mAppointmentsUpcoming = new ArrayList<>();
        mIsToday = isToday;
        mRef = FirebaseDatabase.getInstance().getReference().child(userPath).child(mContext.getString(R.string.appointments));
        mRef.keepSynced(true);
        mRef.addChildEventListener(new AppointmentsChildEventListener());
    }

    private class AppointmentsChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Appointment appointment = dataSnapshot.getValue(Appointment.class);
            assert appointment != null;
            appointment.setKey(dataSnapshot.getKey());
            if (mIsToday) {
                if (appointment.getDate(appointment.getDate()).equals(appointment.getDate(new Date()))) {
                    mAppointmentsToday.add(0, appointment);
                    Collections.sort(mAppointmentsToday);
                }
            } else if (!appointment.getDate(appointment.getDate()).equals(appointment.getDate(new Date()))) {
                mAppointmentsUpcoming.add(0, appointment);
                Collections.sort(mAppointmentsUpcoming);
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Appointment updateAppointment = dataSnapshot.getValue(Appointment.class);
            if (updateAppointment.getDate(updateAppointment.getDate())
                    .equals(updateAppointment.getDate(new Date()))) {
                for (Appointment r : mAppointmentsToday) {
                    if (r.getKey().equals(key)) {
                        r.setValues(updateAppointment);
                        notifyDataSetChanged();
                        return;
                    }
                }
            } else {
                for (Appointment r : mAppointmentsUpcoming) {
                    if (r.getKey().equals(key)) {
                        r.setValues(updateAppointment);
                        notifyDataSetChanged();
                        return;
                    }
                }
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

    @NonNull
    @Override
    public AppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentListAdapter.ViewHolder holder, int position) {
        if (mIsToday) {
            mAppointment = mAppointmentsToday.get(position);
        } else {
            mAppointment = mAppointmentsUpcoming.get(position);
        }
        holder.mTitleView.setText(mAppointment.getTitle());
        holder.mDateView.setText(mAppointment.getDate().toString());
        holder.mCommentsView.setText(mAppointment.getComments());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AppointmentFragment.showAddEditAppointmentDialog(mAppointment);
                return false;
            }
        });
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

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.appointment_title);
            mDateView = itemView.findViewById(R.id.appointment_date_text);
            mCommentsView = itemView.findViewById(R.id.appointment_comments_text);

        }
    }

    public  void addAppointment(Appointment appointment) {
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
}
