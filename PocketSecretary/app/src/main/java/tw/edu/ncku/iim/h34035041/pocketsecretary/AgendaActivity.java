package tw.edu.ncku.iim.h34035041.pocketsecretary;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;


import java.util.Calendar;

public class AgendaActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_VEHICLE_LOADER = 0;

    private EditText mTitleText;
    private TextView mDateText, mTimeText, mLocationText;
    private Calendar mCalendar;
    private Button mSetAgenda, mDltAgenda;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private String title, time, date, mlocation, mActive;
    private RelativeLayout mLocationLayout;

    private Uri mCurrentAgendaUri;
    private boolean mVehicleHasChanged = false;

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_LOCATION = "location_key";
    private static final String KEY_ACTIVE = "active_key";


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mVehicleHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        Intent intent = getIntent();
        mCurrentAgendaUri = intent.getData();

        //Initialize Views
        mTitleText = (EditText) findViewById(R.id.agendaName);
        mDateText = (TextView) findViewById(R.id.dateText);
        mTimeText = (TextView) findViewById(R.id.timeText);
        mLocationText = (TextView) findViewById(R.id.setlocation);
        mSetAgenda = (Button) findViewById(R.id.addAgendaBtn);
        mDltAgenda = (Button) findViewById(R.id.dltAgendaBtn);
        mLocationLayout = (RelativeLayout) findViewById(R.id.location);

        mActive = "true";

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) +1;
        mDay = mCalendar.get(Calendar.DATE);

        date = mMonth + "/" + mDay + "/" + mYear;
        time = mHour + ":" + mMinute;

        if (mCurrentAgendaUri == null) {
            mDltAgenda.setEnabled(false);
        } else {
            mDltAgenda.setEnabled(true);
            getLoaderManager().initLoader(EXISTING_VEHICLE_LOADER, null, this);
        }

        // Setup Agenda name
        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Setup TextViews
        mDateText.setText(date);
        mTimeText.setText(time);

        mLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });

        mSetAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleText.getText().toString().length() == 0) {
                    mTitleText.setError("Agenda cannot be blank!");
                } else {
                    saveAgenda();
                    finish();
                }
            }
        });

        mDltAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlarmScheduler().cancelAlarm(getApplicationContext(),mCurrentAgendaUri);
                deleteAgenda();
                finish();
            }
        });

    }

    private void deleteAgenda() {
        if (mCurrentAgendaUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentAgendaUri, null, null);

            if(rowsDeleted == 0) {
                Toast.makeText(this, "Delete Unsuccessful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Agenda successfully deleted.", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    private void saveAgenda() {
        ContentValues values = new ContentValues();

        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE, title);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_DATE, date);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TIME, time);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_LOCATION, mlocation);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE, mActive);


        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        long selectedTimestamp =  mCalendar.getTimeInMillis();


        if (mCurrentAgendaUri == null) {
            // This is a NEW reminder, so insert a new reminder into the provider,
            // returning the content URI for the new reminder.
            Uri newUri = getContentResolver().insert(AlarmReminderContract.AlarmReminderEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "OOPS! Something went wrong...",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Agenda added successfully!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentAgendaUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "OOPS! Something went wrong...",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Agenda updated!",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Create a new notification
        if (mActive.equals("true")) {
            new AlarmScheduler().setAlarm(this, selectedTimestamp, mCurrentAgendaUri);
            Toast.makeText(this, "Reminder Set", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                AlarmReminderContract.AlarmReminderEntry._ID,
                AlarmReminderContract.AlarmReminderEntry.KEY_TITLE,
                AlarmReminderContract.AlarmReminderEntry.KEY_DATE,
                AlarmReminderContract.AlarmReminderEntry.KEY_TIME,
                AlarmReminderContract.AlarmReminderEntry.KEY_LOCATION,
                AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentAgendaUri,         // Query the content URI for the current reminder
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE);
            int dateColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DATE);
            int timeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TIME);
            int locationColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_LOCATION);
            int activeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);
            String location = cursor.getString(locationColumnIndex);
            String active = cursor.getString(activeColumnIndex);



            // Update the views on the screen with the values from the database
            mTitleText.setText(title);
            mDateText.setText(date);
            mTimeText.setText(time);
            mLocationText.setText(location);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // PlacePicker
    public void setLocation() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.address_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(dialogView);
        final EditText location = dialogView.findViewById(R.id.locationInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLocationText.setText(location.getText());
                        if (location.getText().toString().length() == 0) {
                            mlocation = null;
                        } else {
                            mlocation = location.getText().toString();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // TimePicker
    public void setTime(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
    }

    // DatePicker
    public void setDate(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            time = hourOfDay + ":" + "0" + minute;
        }
        else {
            time = hourOfDay + ":" + minute;
        }
        mTimeText.setText(time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        date = monthOfYear + "/" + dayOfMonth + "/" + year;
        mDateText.setText(date);
    }
}
