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

import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import ua.pt.eventua.Adapters.SpeakersAdapter;
import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.R;

import static java.util.Locale.filter;


public class Speakers extends Fragment
{
    private RecyclerView mRecyclerView;
    private ArrayList<Person> speakers;
    private SpeakersAdapter mAdapter;
    public Context mContext;
    private RecyclerView.LayoutManager mLayoutManager;
    private View currentView;
    private Event event;

    //Search view
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {


        //create comparator
        class SpeakerComparator implements Comparator<Person> {
            @Override
            public int compare(Person p1, Person p2) {return p1.getName().compareTo(p2.getName());}
        }

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        final Toolbar toolbar = (Toolbar)  getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //Search
        setHasOptionsMenu(true);



        View view = inflater.inflate(R.layout.fragment_show_people, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.speaker_list);

        mAdapter = new SpeakersAdapter(getActivity(), speakers, getActivity());

        final SpeakersAdapter adapter =  mAdapter;

        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);

        //Update all the information
        speakers = new ArrayList<Person>();
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
                        for (DataSnapshot ds : dataSnapshot.child(eventId).child("speakers").getChildren())
                        {

                            String id = ds.child("id").getValue(String.class);

                            String description = ds.child("description").getValue(String.class);
                            String imageUrl = ds.child("imageUrl").getValue(String.class);
                            String name = ds.child("name").getValue(String.class);

                            Person s = new Person(Integer.parseInt(id), name, description, imageUrl);
                            speakers.add(s);

                            Collections.sort(speakers, new SpeakerComparator());

                            adapter.setData(speakers);
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
        getActivity().setTitle(getString(R.string.title_speakers));
    }

    public Speakers setInfo(Event e){
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

        searchView = (SearchView) getActivity().findViewById(R.id.search_person);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.equals("") || s.equals(" "))
                {
                    mAdapter.setData(speakers);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    ArrayList<Person> temp = new ArrayList<>();
                    for (Person p : speakers)
                        if (p.getName().toLowerCase().startsWith(s.toLowerCase()))
                            temp.add(p);

                    mAdapter.setData(temp);
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("") || s.equals(" "))
                {
                    mAdapter.setData(speakers);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    ArrayList<Person> temp = new ArrayList<>();
                    for (Person p : speakers)
                        if (p.getName().toLowerCase().startsWith(s.toLowerCase()))
                            temp.add(p);

                    mAdapter.setData(temp);
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

    }


}
