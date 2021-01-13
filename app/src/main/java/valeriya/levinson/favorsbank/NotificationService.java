package valeriya.levinson.favorsbank;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class NotificationService extends Service {
    public NotificationService() {
    }

    //הפונקציה שמשגרת את ההודעה
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1,getNotification());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
//הפונקציה שאחראית לבניית ההודעה
    public Notification getNotification ()
    {
        FavorsOpenHelper foh = new FavorsOpenHelper(this);
        foh.open();
        int expired = foh.getExpired(foh.getAllFavors());
        foh.close();

        String result = String.valueOf(expired);
//כל המאפיינים/הגדרות של ההודעה phase 1
        int icon = android.R.drawable.star_on;
        String ticket = " this is ticket message";
        //מתי ההודעה מופיע - בזה הרגע
        long when = System.currentTimeMillis();
        String title = "pay attention";
        String ticker = "warning";
        String text= result+" "+"favors had expired";
        //phase 2 בלחיצה על ההודעה מועברים למסך הראשי
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
      //  NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "M_CH_ID");
//בדיקה אם הsdk מעל version oreo אז צריך לעשות את השלבים הבאים להחלפת channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title",NotificationManager.IMPORTANCE_DEFAULT);
           // notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        //phase 3 בניית ההודעה עצמה
        //אומר לאיזה מסך יגיע בלחיצה על ההודעה padingintent
        Notification notification = builder.setContentIntent(pendingIntent)
                .setSmallIcon(icon).setTicker(ticker).setWhen(when)
                .setAutoCancel(true).setContentTitle(title)
                .setContentText(result+" "+"favors expired").build();
       // notificationManager.notify(1, notification);

        return  notification;

    }

}
