package edu.rosehulman.scottae.justcheckingin.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.activities.MainActivity;
import edu.rosehulman.scottae.justcheckingin.adapters.ReminderListAdapter;
import edu.rosehulman.scottae.justcheckingin.models.Reminder;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReminderFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static Context mContext;
    private static String mUserPath;
    private static ReminderListAdapter adapterToday;
    private static ReminderListAdapter adapterUpcoming;
    private static EditText titleEditText;
    private static Calendar mCalendar;

    public ReminderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ReminderFragment newInstance(Context context, int sectionNumber, String userPath) {
        mContext = context;
        mUserPath = userPath;
        adapterToday = new ReminderListAdapter(mContext, mUserPath, true);
        adapterUpcoming = new ReminderListAdapter(mContext, mUserPath, false);

        ReminderFragment fragment = new ReminderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminders, container, false);

        // today recycler view
        RecyclerView recyclerViewToday = rootView.findViewById(R.id.reminders_today_recycler_view);
        recyclerViewToday.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewToday.setAdapter(adapterToday);

        // upcoming recycler view
        RecyclerView recyclerViewUpcoming = rootView.findViewById(R.id.reminders_upcoming_recycler_view);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUpcoming.setAdapter(adapterUpcoming);
        return rootView;
    }

    public static void showAddEditReminderDialog(String title, final Date date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("New Reminder");
        builder.setMessage("Enter a title");
        titleEditText = new EditText(mContext);
        titleEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        titleEditText.setText(title == null ? "" : title);
        builder.setView(titleEditText);
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCalendar = Calendar.getInstance();
                if (date != null)
                    mCalendar.setTime(date);
                Log.e("AAA", "Recieved date: " + mCalendar.getTime().toString());
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new ReminderFragment(), mCalendar
                );
                dpd.setOkText("Next");
                dpd.setMinDate(mCalendar);
                dpd.show(((MainActivity) mContext).getFragmentManager(), "Datepickerdialog");
            }
        });
        // TODO: add delete button
        builder.setNeutralButton("Delete", null);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Log.e("AAA", "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, false);
        mCalendar.set(year, monthOfYear, dayOfMonth);
        tpd.setInitialSelection(mCalendar.get(Calendar.HOUR), mCalendar.get(Calendar.MINUTE));
        tpd.setOkText("Done");
        tpd.show(view.getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Log.e("AAA", "You picked the following time: " + hourOfDay + ":" + minute);
        mCalendar.set(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                hourOfDay,
                minute,
                0);
        Reminder r = new Reminder(titleEditText.getText().toString(), mCalendar.getTime());
        if (r.getDate().before(new Date())) {
            Toast.makeText(mContext, "Invalid date and time!", Toast.LENGTH_LONG).show();
        } else if (r.getDate(r.getDate()).equals(r.getDate(new Date()))) {
            adapterToday.addReminder(r);
        } else {
            adapterUpcoming.addReminder(r);
        }
    }
}