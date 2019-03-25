package ua.pt.eventua.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ua.pt.eventua.Adapters.AllEventsAdapter;
import ua.pt.eventua.Adapters.MyTicketAdapter;
import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.R;


public class MyTickets extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<Event> eventsData;
    private AllEventsAdapter adapter;
    public Context mContext;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.setDrawerListener(toggle);

        View view = inflater.inflate(R.layout.activity_actual_events, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_all_events_list);

        final MyTicketAdapter adapter =  new MyTicketAdapter(getActivity(), eventsData, getActivity());

        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        eventsData = new ArrayList<Event>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("events");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot

                for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    String eventId = data.getKey().toString();
                    if (Constants.CURRENT_PERSON.getEvents().contains(eventId)) {
                        String organizer = data.child("organizer").getValue().toString();
                        String name = data.child("name").getValue().toString();
                        ArrayList<String> floors = (ArrayList<String>) data.child("floors").getValue();
                        String place = data.child("place").getValue().toString();
                        String pic_url = data.child("pic_url").getValue().toString();
                        String start_at = data.child("start_at").getValue().toString();
                        String finish_at = data.child("finish_at").getValue().toString();
                        String latitute = data.child("lat").getValue().toString();
                        String longitude = data.child("long").getValue().toString();
                        String description = data.child("description").getValue().toString();
                        Float price = Float.parseFloat(data.child("price").getValue().toString());

                        Event e = new Event(eventId, name, organizer, place, floors, pic_url, start_at, finish_at, latitute, longitude, description, price);
                        eventsData.add(e);
                        adapter.setData(eventsData);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(getString(R.string.title_my_tickets));
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
    }

}
