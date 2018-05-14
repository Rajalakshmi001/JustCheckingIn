package edu.rosehulman.scottae.justcheckingin.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.activities.MainActivity;
import edu.rosehulman.scottae.justcheckingin.adapters.AppointmentListAdapter;
import edu.rosehulman.scottae.justcheckingin.models.Appointment;

/**
 * A placeholder fragment containing a simple view.
 */
public class AppointmentFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static Context mContext;
    private static String mUserPath;
    private static AppointmentListAdapter adapterToday;
    private static AppointmentListAdapter adapterUpcoming;
    private static Appointment appointmentForUpdate;
    private static EditText titleEditText;
    private static EditText commentEditText;
    private static Calendar mCalendar;
    private static LayoutInflater mInflater;

    public AppointmentFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AppointmentFragment newInstance(Context context, int sectionNumber, String  userPath) {
        mContext = context;
        mUserPath = userPath;
        adapterToday = new AppointmentListAdapter(mContext, mUserPath, true);
        adapterUpcoming = new AppointmentListAdapter(mContext, mUserPath, false);

        AppointmentFragment fragment = new AppointmentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_appointments, container, false);

        // today recycler view code
        RecyclerView recyclerViewToday = rootView.findViewById(R.id.appointments_today_recycler_view);
        recyclerViewToday.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewToday.setAdapter(adapterToday);

        // upcoming recycler view code
        RecyclerView recyclerViewUpcoming = rootView.findViewById(R.id.appointments_upcoming_recycler_view);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUpcoming.setAdapter(adapterUpcoming);
        return rootView;
    }

    public static void showAddEditAppointmentDialog(final Appointment appointment) {
        appointmentForUpdate = appointment;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle((appointment == null ? mContext.getString(R.string.add_appointment_title_text) : mContext.getString(R.string.edit_appointment_title_text)));
        builder.setMessage(R.string.enter_a_title_dialog_message);
        View view =  mInflater.inflate(R.layout.add_appointment_dialog, null, false);
        builder.setView(view);

        titleEditText = view.findViewById(R.id.dialog_add_appointment_title_text);
        commentEditText = view.findViewById(R.id.dialog_add_comments_text);

        if (appointment != null) {
            titleEditText.setText(appointment.getTitle());
            commentEditText.setText(appointment.getComments());
        }
        builder.setPositiveButton(R.string.next_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCalendar = Calendar.getInstance();
                if (appointment != null) {
                    mCalendar.setTime(appointment.getDate());
                }
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new AppointmentFragment(), mCalendar
                );
                dpd.setOkText(R.string.next_button);
                dpd.show(((MainActivity) mContext).getFragmentManager(), "Datepickerdialog");
            }
        });
        builder.setNeutralButton(R.string.delete_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (appointmentForUpdate != null) {
                    if (appointmentForUpdate.getDate(appointmentForUpdate.getDate()).equals(appointmentForUpdate.getDate(new Date()))) {
                        adapterToday.remove(appointmentForUpdate);
                    } else {
                        adapterUpcoming.remove(appointmentForUpdate);
                    }
                }else {
                    Toast.makeText(mContext, R.string.empty_appointment_delete_warning_message, Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, false);
        mCalendar.set(year, monthOfYear, dayOfMonth);
        tpd.setInitialSelection(mCalendar.get(Calendar.HOUR), mCalendar.get(Calendar.MINUTE));
        tpd.setOkText(R.string.done_button_text);
        tpd.show(view.getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        mCalendar.set(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                hourOfDay,
                minute,
                0);
        Appointment a = new Appointment(titleEditText.getText().toString(), mCalendar.getTime(), commentEditText.getText().toString());
        if (a.getDate().before(new Date())) {
            Toast.makeText(mContext, R.string.invalid_date_time_toast_text, Toast.LENGTH_LONG).show();
        } else if (a.getDate(a.getDate()).equals(a.getDate(new Date()))) {
            if (appointmentForUpdate != null)
                adapterToday.update(appointmentForUpdate, a.getTitle(), a.getDate(), a.getComments());
            else
                adapterToday.addAppointment(a);
        } else {
            if (appointmentForUpdate != null)
                adapterUpcoming.update(appointmentForUpdate, a.getTitle(), a.getDate(), a.getComments());
            else
                adapterUpcoming.addAppointment(a);
        }
    }
}