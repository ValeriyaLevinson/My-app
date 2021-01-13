package valeriya.levinson.favorsbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class OpeningActivity extends AppCompatActivity {
    Button button_continue;
    ImageView picture;
    Button take_picture;
    Bitmap bitmap;
    SharedPreferences spb;
    ByteArrayOutputStream baos;
    String encoded ;
    TextView battery_show;
    BroadCastBattery broadCastBattery;
   // FavorsOpenHelper foh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        // יצירת טבלה בsharedpreference כדי לאחסן בה את התמונה
        spb = getSharedPreferences("details_bitmap", 0);
        //in order to convert image into byte array Base64
        baos = new ByteArrayOutputStream();

        picture = findViewById(R.id.iv);
        take_picture = findViewById(R.id.take_picture);
        button_continue = findViewById(R.id.opening_button);
        battery_show = findViewById(R.id.battery_textview);
       //יצירת מופע כדי להעיר את הברודקאסט
        broadCastBattery=new BroadCastBattery();

//this intent activate notificationservice
        Intent intent = new Intent(OpeningActivity.this, NotificationService.class);
        startService(intent);



        //הוצאת התמונה מ sharedpreference לאחר שהיא הומרה לstring
        String check = spb.getString("picture_bitmap",null);
        //בדיקה אם יש תמונה שמורה
          if(check!=null)
          {
              picture.setBackgroundColor(Color.WHITE);
              //  המרת התמונה מstring למערך של bytes ואז הופכים את המערך לתמונה ושמים בתמונת הפרופיל
              byte[] imageAsBytes = Base64.decode(check.getBytes(), Base64.DEFAULT);
             picture.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
          }
         //בלחיצה עוברים למסך המצלמה, ובעזרת intent נשמרת התמונה שצולמה בbitmap והפונקציה onActivityResult מחכה לתוצאה בחזרה ממסך המצלמה
        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpeningActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
//פונקציה המגיבה למידע שמגיע ממסכים אחרים במקרה זה ממסך המצלמה (פעולות אלו קורות מיד לאחר צילום תמונה)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCodeאם מדובר בפעולה ממסך המצלמה לפי ה
        if(requestCode==0)
        {
            //ואם הפעולה בוצעה בהצלחה
            if(resultCode==RESULT_OK)
            {
                //שולפים את התמונה מהמידע המועבר בעזרת אינטנט והופכים את התמונה לאובייקט מסוג bitmap
                bitmap= (Bitmap)data.getExtras().get("data");
                picture.setImageBitmap(bitmap);
                picture.setBackgroundColor(Color.WHITE);
               //  ממירים את התמונה למערך של bytes ואז לstring
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                encoded = Base64.encodeToString(b, Base64.DEFAULT);
                // שמירת התמונה שכבר הומרה לstring

                SharedPreferences.Editor editor = spb.edit();
                editor.putString("picture_bitmap", encoded);
                editor.commit();
            }
        }
    }
    //broadcast שמאזין לשינויים של הבטריה
    private class BroadCastBattery extends BroadcastReceiver
    {
     //חובה לממש את הפונקציה הזו
        public void onReceive(Context context, Intent intent) {
            int battery = intent.getIntExtra("level",0);
            battery_show.setText("battery :"+String.valueOf(battery) + "%");
        }

    }

//בעזרת הפונקציה הזו תמיד כשנכנסים למסך הזה נרשמים לreciver
    //מעבירים את הקלאס של הברודקאסט ולאיזה פילטר הוא מאזין
    protected void onResume() {
        super.onResume();
        registerReceiver(broadCastBattery,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }
//פועלת בכדי שבעזיבת המסך ההאזנה של הברודקאסט לשינוי הסוללה תפסק
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadCastBattery);
    }




    //-------------------------------------------------------------------------------------------------------
}


