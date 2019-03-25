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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ua.pt.eventua.Adapters.ChatAdapter;
import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.R;
import ua.pt.eventua.Activities.Welcome;


public class Chat_PickUser extends Fragment
{
    private RecyclerView mRecyclerView;
    private List<String> usersData;
    private List<String> emailsData;
    private ChatAdapter adapter;
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


        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_users_list);

        ChatAdapter adapter =  new ChatAdapter(getActivity(), usersData, emailsData, getActivity());

        final ChatAdapter temp_adapter = adapter;

        mRecyclerView.setAdapter(temp_adapter);

        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);

        usersData = new ArrayList<String>();
        emailsData = new ArrayList<String>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("global_users");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    String userId = data.getKey().toString();
                    String userEvents =  dataSnapshot.child(userId).child("events").getValue(String.class);
                    // if the user is on the event
                    if (Arrays.asList(userEvents.trim().split(",")).contains(Constants.CURRENT_EVENT_ID))
                    {
                        if (!dataSnapshot.child(userId).child("name").getValue(String.class).equals(Welcome.current_user)){
                            usersData.add(dataSnapshot.child(userId).child("name").getValue(String.class));
                            emailsData.add(dataSnapshot.child(userId).child("email").getValue(String.class));
                        }
                    }
                }
                temp_adapter.init(usersData, emailsData);
                temp_adapter.notifyDataSetChanged();

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
        getActivity().setTitle(getString(R.string.title_chat_users));
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


    public void init(List<String> users, List<String> emails){
        this.usersData = users;
        this.emailsData = emails;
    }
}