package com.creiss.job_schedulers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import com.creiss.MainActivity;
import com.creiss.R;
import com.creiss.model.TripScanResponse;
import com.creiss.utility.CreissPreferences;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SyncFiles extends JobService {

    String fileKeyName = "processed_";
    ArrayList<TripScanResponse> processedFiles = new ArrayList<TripScanResponse> ();
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
//        if (isProcessedFilesAvailable ()) {
//            //api call
//        } else {
        if (CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH) != null) {
            Boolean temp = isProcessedFilesAvailable ();
            createNotification (getString(R.string.notification_title), getString(R.string.notification_message), this);
        }
//        }
        return true;
    }

    private Boolean isProcessedFilesAvailable() {
        String path = CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH);
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if ((files[i].getName().substring(0, 10)).equals(fileKeyName)) {
                try {
                    TripScanResponse tempData = new TripScanResponse();
                    tempData.setName(files[i].getName());
                    tempData.setBytes(pdfToByte(CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH) + "/" + files[i].getName()).toString());
                    processedFiles.add(tempData);
                } catch (Exception e) {

                }
                return true;
            }
        }
        return false;
    }

    public byte[] pdfToByte(String filePath) {
        File file = new File(filePath);
        FileInputStream fileInputStream;
        byte[] data = null;
        byte[] finalData = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            data = new byte[(int)file.length()];
            finalData = new byte[(int)file.length()];
            byteArrayOutputStream = new ByteArrayOutputStream();

            fileInputStream.read(data);
            byteArrayOutputStream.write(data);
            finalData = byteArrayOutputStream.toByteArray();

            fileInputStream.close();

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        return finalData;
    }

    public void createNotification(String title, String message, Context mContext)
    {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotificationManager;
        Intent resultIntent = new Intent(mContext , MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_ONE_SHOT);
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.drawable.app_icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(ContextCompat.getColor(mContext, R.color.secondary_text));
        }
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 , mBuilder.build());
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
