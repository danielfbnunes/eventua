package ua.pt.eventua;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.Entities.Talk;

public class Constants
{
    public static String CURRENT_EVENT_ID = "";
    public static String CURRENT_EVENT_NAME = "";
    public static Event CURRENT_EVENT;
    public static String USER_MAIL = "";
    public static Talk CURRENT_TALK;
    public static int notification_counter = 0;
    public static Person CURRENT_PERSON = null;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //private static  MyFirebaseMessagingService ms = new MyFirebaseMessagingService();

    public static void sendNotificationData(Activity a, String title, String body){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(a, "notify_001");

        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(body);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager =
                (NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(notification_counter, mBuilder.build());
        notification_counter++;
    }

    public static void checkIfTalkIsStarting(Activity a) {
        final Activity activity = a;
        while (true)
        {
            final Date currDate = Calendar.getInstance().getTime();

            ArrayList<Talk> talks;
            talks = new ArrayList<Talk>();

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Get map of users in datasnapshot

                    for (DataSnapshot data : dataSnapshot.child("events").getChildren())
                    {
                        String eventId = data.getKey().toString();

                        //get user events:
                        for(DataSnapshot usersData: dataSnapshot.child("global_users").getChildren())
                            if(usersData.child("email").getValue(String.class).equals(USER_MAIL))
                            {

                                String userEvents = usersData.child("events").getValue(String.class);
                                if (Arrays.asList(userEvents.trim().split(",")).contains(eventId))
                                {
                                    for (DataSnapshot ds : dataSnapshot.child("events").child(eventId).child("talks").getChildren()) {
                                        String subscribers = ds.child("subscribers").getValue(String.class);
                                        if (Arrays.asList(subscribers.trim().split(",")).contains(USER_MAIL))
                                        {
                                            Date talkDate =null;
                                            String talkHours = ds.child("hours").getValue(String.class);
                                            try {talkDate = formatter.parse(talkHours);} catch (ParseException e)
                                            {e.printStackTrace();}

                                            if(talkDate != null)
                                            {
                                                Date currentDate = new Date();
                                                long diff = talkDate.getTime() - currentDate.getTime();
                                                long minutes = diff / (60 * 1000);

                                                if (minutes < 10 && minutes >=0)
                                                {
                                                    String title = ds.child("title").getValue(String.class);
                                                    sendNotificationData(activity,"\"" + title + "\" is starting in a few minutes!" , "\"" + title + "\" starts at " +talkHours);
                                                    try {
                                                        Thread.sleep(1000 );
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //handle databaseError
                }
            });
            try {
                Thread.sleep(1000 * 10 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
