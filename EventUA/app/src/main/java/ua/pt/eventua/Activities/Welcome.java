package ua.pt.eventua.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.Fragments.ActualEvents;
import ua.pt.eventua.Fragments.BusinessCard;
import ua.pt.eventua.Fragments.Chat_PickUser;
import ua.pt.eventua.Fragments.EventPlant;
import ua.pt.eventua.Fragments.EventsMainPage;
import ua.pt.eventua.Fragments.FutureEvents;
import ua.pt.eventua.Fragments.GlobalSearch;
import ua.pt.eventua.Fragments.MySchedule;
import ua.pt.eventua.Fragments.MyTickets;
import ua.pt.eventua.Fragments.Participants;
import ua.pt.eventua.Fragments.Schedule;
import ua.pt.eventua.Fragments.Settings;
import ua.pt.eventua.Fragments.Speakers;
import ua.pt.eventua.Fragments.Location;
import ua.pt.eventua.Fragments.SuggestedEvents;
import ua.pt.eventua.Fragments.TalkRating;
import ua.pt.eventua.Fragments.Ticket;
import ua.pt.eventua.R;

public class Welcome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BeaconConsumer, RangeNotifier {

    double low_distance = 99;
    private String beacon_event = "", beacon_event_id, beacon_talk_id, beacon_title, beacon_hours, beacon_image;

    private BeaconManager mBeaconManager;

    public String current_img, current_username, current_email;
    public int current_id;

    public static String current_user = "";
    public static Event current_event = null;
    private int notification_counter = 0;
    DatabaseReference mRef;
    ChildEventListener c;

    private static boolean beacon_found = false;
    Timer timer;

    @Override
    protected void onResume() {
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry Eddystone-TLM frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        mBeaconManager.bind(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 10000);

        final Activity a =this;
        Thread t = new Thread() {
            @Override
            public void run() {
                Constants.checkIfTalkIsStarting(a);
            }
        };
        t.start();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });
                builder.show();
            }
        }

        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences myPrefs = Welcome.this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        current_img = myPrefs.getString("current_image", "null");
        current_username = myPrefs.getString("current_username", "null");
        current_user = current_username;
        current_email = myPrefs.getString("current_email", "null");
        current_id = myPrefs.getInt("current_id", -1);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = new ActualEvents();
        Bundle b = new Bundle();
        b.putString("current_img", current_img);
        b.putString("current_username", current_username);
        b.putString("current_email", current_email);
        f.setArguments(b);
        ft.replace(R.id.content_frame, f);
        ft.commit();

        View headerView = navigationView.getHeaderView(0);
        TextView t1 = (TextView) headerView.findViewById(R.id.username_menu);
        TextView t2 = (TextView) headerView.findViewById(R.id.email_menu);
        ImageView i1 = (ImageView) headerView.findViewById(R.id.photo_menu);
        t1.setText(current_username);
        t2.setText(current_email);

        Glide.with(this).load(current_img).into(i1);


        mRef =  FirebaseDatabase.getInstance().getReference().child("global_users").child(""+current_id).child("messages");
        c = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    try{
                        HashMap<String, String> mapa = (HashMap<String, String>) dataSnapshot.getValue();
                        notification(mapa.get("event"), mapa.get("from"), mapa.get("message"));
                        FirebaseDatabase.getInstance().getReference().child("global_users").child(""+current_id).child("messages").child(dataSnapshot.getKey().toString()).removeValue();
                    }catch (Exception e){}

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mRef.addChildEventListener(c);

    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        @Override
        public void run() {
            if (beacon_found == true){
                mRef =  FirebaseDatabase.getInstance().getReference().child("beacons");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.child("mac").getValue(String.class).trim().equals(beacon_event)){
                                final String event = data.child("event").getValue(String.class);
                                final String talk = data.child("talk").getValue(String.class);
                                DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference().child("events");
                                mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                        for (DataSnapshot d : dataSnapshot2.getChildren()){
                                            if(d.child("name").getValue(String.class).equals(event)){
                                                beacon_image = d.child("pic_url").getValue(String.class);
                                                mRef = FirebaseDatabase.getInstance().getReference().child("events").child(d.getKey()).child("talks");
                                                beacon_event_id = d.getKey();
                                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                                        for (DataSnapshot ds : dataSnapshot3.getChildren()){
                                                            if (ds.child("title").getValue(String.class).equals(talk)){
                                                                beacon_title = ds.child("title").getValue(String.class);
                                                                beacon_hours = ds.child("hours").getValue(String.class);
                                                                beacon_talk_id = ds.getKey();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu nav_menu = navigationView.getMenu();
                nav_menu.findItem(R.id.nav_form_events).setVisible(true);
                beacon_found = false;
                low_distance = 99;
            }else{
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu nav_menu = navigationView.getMenu();
                nav_menu.findItem(R.id.nav_form_events).setVisible(false);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("x", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    private void notification(String event, String from, String message){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "notify_001");

        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("NEW MESSAGE ON " + event);
        mBuilder.setContentText(from + " -> " + message);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);

        for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        int n = item.getItemId();

        if (n == R.id.menu_chat){
            Chat_PickUser eventFrag = new Chat_PickUser();
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();

        }
        if (n == R.id.menu_main_page){
            EventsMainPage eventFrag = new EventsMainPage().initializeData(current_event);
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();

        }
        if (n == R.id.menu_schedule){
            Schedule eventFrag = new Schedule().setInfo(current_event);
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }
        if (n == R.id.menu_speakers){
            Speakers eventFrag = new Speakers().setInfo(current_event);
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

        if (n == R.id.menu_participants){
            Participants eventFrag = new Participants().setInfo(current_event);
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

        if (n == R.id.menu_global_search){
            GlobalSearch eventFrag = new GlobalSearch().setInfo(current_event);
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

        if (n == R.id.menu_location){
            Location eventFrag = new Location().setInfo(current_event);
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

        if (n == R.id.menu_event_plant){
            EventPlant eventFrag = new EventPlant().setInfo(current_event);
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

        if (n == R.id.menu_ticket){
            Ticket eventFrag = new Ticket();
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

        if (n == R.id.menu_my_schedule){
            MySchedule eventFrag = new MySchedule().setInfo(current_event);
            //AppCompatActivity activity = (AppCompatActivity) getView().getContext();
            Welcome.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;
        Bundle b = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_form_events:
                fragment = new TalkRating().initializeData(beacon_event_id, beacon_talk_id, beacon_title, beacon_hours, beacon_image);
                break;
            case R.id.nav_business_card:
                b = new Bundle();
                fragment = new BusinessCard();
                b.putString("current_username", current_username);
                b.putString("current_email", current_email);
                fragment.setArguments(b);
                break;

            case R.id.nav_my_tickets:
                fragment = new MyTickets();
                break;

            case R.id.nav_actual_events:
                fragment = new ActualEvents();
                break;

            case R.id.nav_future_events:
                fragment = new FutureEvents();
                break;

            case R.id.nav_suggested_events:
                fragment = new SuggestedEvents();
                break;

            case R.id.nav_log_out:

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    startActivity(new Intent(Welcome.this, LoginActivity.class));
                                    finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(Welcome.this);
                    builder.setMessage(R.string.question_logout)
                            .setNegativeButton(getString(R.string.str_no), dialogClickListener)
                            .setPositiveButton(getString(R.string.str_yes), dialogClickListener).show();

                break;
            case R.id.nav_settings:
                fragment = new Settings();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRef.removeEventListener(c);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.addRangeNotifier(this);
    }


    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        for (Beacon beacon: collection) {
            if (beacon.getDistance() <= 1 && beacon.getDistance() <= low_distance){
                low_distance = beacon.getDistance();
                beacon_event = beacon.getBluetoothAddress();
                beacon_found = true;
            }
        }
    }
}

