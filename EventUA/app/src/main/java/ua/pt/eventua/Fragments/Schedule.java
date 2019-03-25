package ua.pt.eventua.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
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
import java.util.HashMap;

import ua.pt.eventua.Adapters.TalksAdapter;
import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.R;
import ua.pt.eventua.Entities.Talk;


public class Schedule extends Fragment
{
    private RecyclerView mRecyclerView;
    private ArrayList<Talk> talks;
    private TalksAdapter adapter;
    public Context mContext;
    private RecyclerView.LayoutManager mLayoutManager;
    private View currentView;
    private Event event;

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

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_schedule_list);

        final TalksAdapter adapter =  new TalksAdapter(getActivity(), talks, getActivity());
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);


        talks = new ArrayList<Talk>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("events");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot

                for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    String eventId = data.getKey().toString();

                    if (eventId.equals(Constants.CURRENT_EVENT_ID))
                    {
                        for (DataSnapshot ds : dataSnapshot.child(eventId).child("talks").getChildren())
                        {
                            String title = ds.child("title").getValue(String.class);
                            String hours = ds.child("hours").getValue(String.class);
                            String speakers_ids = ds.child("speakers_ids").getValue(String.class);
                            String description = ds.child("description").getValue(String.class);
                            String image_url = ds.child("image_url").getValue(String.class);

                            Talk t = new Talk(title, hours, speakers_ids, description, image_url);
                            t.setTalkId(Integer.parseInt(ds.getKey()));
                            talks.add(t);
                            adapter.setData(talks);
                            adapter.notifyDataSetChanged();
                        }
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
        this.currentView = view;
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(getString(R.string.title_schedule));
    }

    public Schedule setInfo(Event e){
        this.event = e;
        return this;
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

}
