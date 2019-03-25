package ua.pt.eventua.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.R;


public class Location extends Fragment implements OnMapReadyCallback {

    protected FragmentActivity mActivity;
    private MapView mMapView;
    private GoogleMap googleMap;
    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final Menu men = menu;

        inflater.inflate(R.menu.event_menu, men);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("global_users").child(Constants.CURRENT_PERSON.getId()+"");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                if (map.get("events").contains(Constants.CURRENT_EVENT.getEventId())) {
                    men.findItem(R.id.menu_chat).setVisible(true);
                    men.findItem(R.id.menu_participants).setVisible(true);
                    men.findItem(R.id.menu_my_schedule).setVisible(true);
                    men.findItem(R.id.menu_global_search).setVisible(true);
                }else{
                    men.findItem(R.id.menu_chat).setVisible(false);
                    men.findItem(R.id.menu_participants).setVisible(false);
                    men.findItem(R.id.menu_my_schedule).setVisible(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });

        super.onCreateOptionsMenu(men,inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity.setTitle(getString(R.string.title_location));
        mMapView = (MapView) mActivity.findViewById(R.id.mapView);
        if (mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("events");

        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot

                for(DataSnapshot data: dataSnapshot.getChildren()) {
                    String eventId = data.getKey().toString();

                    if (eventId.equals(Constants.CURRENT_EVENT_ID)) {
                        HashMap<String, Object> mapa = (HashMap<String, Object>) dataSnapshot.child(eventId).getValue();
                        SharedPreferences pref = mActivity.getSharedPreferences("myPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putFloat("latitude", Float.valueOf(String.valueOf(mapa.get("lat"))));
                        editor.putFloat("longitude", Float.valueOf(String.valueOf(mapa.get("long"))));
                        editor.putString("event_name", (String) mapa.get("name"));
                        editor.commit();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public Location setInfo(Event e){
        this.event = e;
        return this;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(mActivity.getApplicationContext());

        SharedPreferences sharedPref = mActivity.getSharedPreferences("location", Context.MODE_PRIVATE);

        LatLng eventPlace = new LatLng(sharedPref.getFloat("latitude", -1),sharedPref.getFloat("longitude", -1));
        googleMap.addMarker(new MarkerOptions().position(eventPlace).title(sharedPref.getString("event_name", null)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventPlace, 15));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.getSharedPreferences("localiton", Context.MODE_PRIVATE).edit().clear();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("events");

        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot

                for(DataSnapshot data: dataSnapshot.getChildren()) {
                    String eventId = data.getKey().toString();

                    if (eventId.equals(Constants.CURRENT_EVENT_ID)) {
                        HashMap<String, Object> mapa = (HashMap<String, Object>) dataSnapshot.child(eventId).getValue();
                        SharedPreferences pref = mActivity.getSharedPreferences("location", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putFloat("latitude", Float.valueOf(String.valueOf(mapa.get("lat"))));
                        editor.putFloat("longitude", Float.valueOf(String.valueOf(mapa.get("long"))));
                        editor.putString("event_name", (String) mapa.get("name"));
                        editor.commit();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}