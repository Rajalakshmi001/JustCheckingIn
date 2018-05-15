package edu.rosehulman.scottae.justcheckingin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.adapters.ReminderListAdapter;
import edu.rosehulman.scottae.justcheckingin.fragments.ReminderFragment;
import edu.rosehulman.scottae.justcheckingin.models.Reminder;

public class DisplayReminderNotification extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_notification);

        Reminder reminder = getIntent().getParcelableExtra(ReminderListAdapter.KEY_REMINDER_TITLE);

        TextView messageTextView = findViewById(R.id.reminder_title_notification);
        messageTextView.setText(reminder.getTitle());
        messageTextView.setTextSize(32);
    }
}
