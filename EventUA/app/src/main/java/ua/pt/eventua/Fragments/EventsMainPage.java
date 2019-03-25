package ua.pt.eventua.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ua.pt.eventua.Constants;
import ua.pt.eventua.ConvertDate;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.R;


public class EventsMainPage extends Fragment
{
    private String title, hours, description ;
    private Event e;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        final Toolbar toolbar = (Toolbar)  getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        View view = inflater.inflate(R.layout.fragment_event_main_page, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        ImageView eventImage = (ImageView)  view.findViewById(R.id.eventImage);
        TextView eventTitle = (TextView) view.findViewById(R.id.eventTitle);
        TextView eventHours = (TextView) view.findViewById(R.id.eventHours);
        TextView eventPlace = (TextView) view.findViewById(R.id.eventPlace);
        TextView eventDescription = (TextView) view.findViewById(R.id.eventDescription);

        // Load the images into the ImageView using the Glide library.
        Glide.with(getContext().getApplicationContext()).load(Constants.CURRENT_EVENT.getPic_url()).into(eventImage);
        // Load textviews
        eventTitle.setText(Constants.CURRENT_EVENT.getName());

        String convertedHours = ConvertDate.dateToString_days(Constants.CURRENT_EVENT.getStart_at());
        convertedHours += (" - " + ConvertDate.dateToString_hours(Constants.CURRENT_EVENT.getStart_at()));
        eventHours.setText(convertedHours);


        eventPlace.setText(Constants.CURRENT_EVENT.getPlace());
        eventDescription.setText(Constants.CURRENT_EVENT.getDescription());

        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(title);


    }

    @Override
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

    public EventsMainPage initializeData(Event e)
    {
        this.e = e;
        this.title = e.getName();
        this.description = e.getOrganizer();

        return this;
    }


}

