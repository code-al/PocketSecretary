package tw.edu.ncku.iim.h34035041.pocketsecretary;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Random;

public class RingtoneService extends Service {
    MediaPlayer mediaPlayer;
    private boolean isRunning;
    private int startId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startActivity(new Intent(this, AlarmControl.class));

        String state = intent.getExtras().getString("alarm");

        switch (state) {
            case "true":
                startId = 1;
                break;
            case "false":
                startId = 0;
                break;

                default:
                    startId = 0;
        }

        Random rn = new Random();

        if (!this.isRunning && startId == 1) {
            switch (rn.nextInt(3) + 1) {
                case 1:
                    mediaPlayer = MediaPlayer.create(RingtoneService.this, R.raw.ringtone);
                    break;
                case 2:
                    mediaPlayer = MediaPlayer.create(RingtoneService.this, R.raw.ringtone2);
                    break;
                case 3:
                    mediaPlayer = MediaPlayer.create(RingtoneService.this, R.raw.ringtone3);
                    break;
            }
            mediaPlayer.start();
            this.isRunning = true;
            this.startId = 0;
        }

        else if (this.isRunning && startId == 0) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            this.isRunning = false;
            this.startId = 0;
            startActivity(new Intent(this, MainActivity.class));
        }


        return START_NOT_STICKY;
    }
}
