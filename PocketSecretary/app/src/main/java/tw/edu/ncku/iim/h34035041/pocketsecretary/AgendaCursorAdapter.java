package tw.edu.ncku.iim.h34035041.pocketsecretary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class AgendaCursorAdapter extends CursorAdapter {
    private TextView mTitle, mDateTime, mLocation;

    public AgendaCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.agenda_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mTitle = (TextView) view.findViewById(R.id.agendaTitleView);
        mDateTime = (TextView) view.findViewById(R.id.agendaDateTime);
        mLocation = (TextView) view.findViewById(R.id.agendaLocation);

        int titleColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE);
        int dateColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DATE);
        int timeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TIME);
        int locationColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_LOCATION);

        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String location = cursor.getString(locationColumnIndex);

        String dateTime = date + " " + time;

        if(location == null) {
            mLocation.setText("Location Not Provided");
        } else {
            setAgendaLocation(location);
        }

        setAgendaTitle(title);
        setAgendaDateTime(dateTime);
    }

    public void setAgendaTitle(String t) {
        mTitle.setText(t);
    }

    public void setAgendaDateTime(String dt) {
        mDateTime.setText(dt);
    }

    public void setAgendaLocation(String l) {
        mLocation.setText(l);
    }
}
