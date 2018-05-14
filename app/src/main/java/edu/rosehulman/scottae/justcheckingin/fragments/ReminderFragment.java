package edu.rosehulman.scottae.justcheckingin.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private static Reminder reminderForUpdate;

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

    public static void showAddEditReminderDialog(final Reminder reminder) {
        reminderForUpdate = reminder;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle((reminder == null ? mContext.getString(R.string.new_reminder_title_text) : mContext.getString(R.string.edit_reminder_title_text)));
        builder.setMessage(R.string.enter_a_title_dialog_message);
        titleEditText = new EditText(mContext);
        titleEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builder.setView(titleEditText);

        if (reminder != null) {
            titleEditText.setText(reminder.getTitle());
        }
        builder.setPositiveButton(R.string.next_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCalendar = Calendar.getInstance();
                if (reminder != null) {
                    mCalendar.setTime(reminder.getDate());
                }
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new ReminderFragment(), mCalendar
                );
                dpd.setOkText(R.string.next_button);
                dpd.show(((MainActivity) mContext).getFragmentManager(), "Datepickerdialog");
            }
        });
        builder.setNeutralButton(R.string.delete_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (reminderForUpdate != null) {
                    if (reminderForUpdate.getDate(reminderForUpdate.getDate()).equals(reminderForUpdate.getDate(new Date()))) {
                        adapterToday.remove(reminderForUpdate);
                    } else {
                        adapterUpcoming.remove(reminderForUpdate);
                    }
                }else {
                    Toast.makeText(mContext, R.string.empty_reminder_deletion_warning_message, Toast.LENGTH_LONG).show();
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
        Reminder r = new Reminder(titleEditText.getText().toString(), mCalendar.getTime());
        if (r.getDate().before(new Date())) {
            Toast.makeText(mContext, R.string.invalid_date_time_toast_text, Toast.LENGTH_LONG).show();
        } else if (r.getDate(r.getDate()).equals(r.getDate(new Date()))) {
            if (reminderForUpdate != null)
                adapterToday.update(reminderForUpdate, r.getTitle(), r.getDate());
            else
                adapterToday.addReminder(r);
        } else {
            if (reminderForUpdate != null)
                adapterUpcoming.update(reminderForUpdate, r.getTitle(), r.getDate());
            else
                adapterUpcoming.addReminder(r);
        }
    }
}