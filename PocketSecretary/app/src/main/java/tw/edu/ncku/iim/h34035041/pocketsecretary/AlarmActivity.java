package tw.edu.ncku.iim.h34035041.pocketsecretary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    TimePicker timePicker;
    Button setAlarm;
    Button unsetAlarm;
    AlarmManager alarmManager;
    static PendingIntent pendingIntent;
    Context context;
    static boolean isAlarmOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        this.context = this;

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        setAlarm = (Button) findViewById(R.id.setAlarm);
        unsetAlarm = (Button) findViewById(R.id.unsetAlarm);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();

        final Intent alarmIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);

        if (isAlarmOn) {
            unsetAlarm.setEnabled(true);
        } else {
            unsetAlarm.setEnabled(false);
        }

        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlarmOn = true;
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());

                Toast.makeText(getApplicationContext(), "Alarm set to " + timePicker.getHour() + ":" + timePicker.getMinute() + ".", Toast.LENGTH_SHORT).show();

                alarmIntent.putExtra("alarm", "true");

                pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                unsetAlarm.setEnabled(true);
            }
        });

        unsetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlarmOn = false;
                alarmManager.cancel(pendingIntent);
                Toast.makeText(getApplicationContext(), "Alarm cancelled.", Toast.LENGTH_SHORT).show();

                unsetAlarm.setEnabled(false);
            }
        });
    }
}
