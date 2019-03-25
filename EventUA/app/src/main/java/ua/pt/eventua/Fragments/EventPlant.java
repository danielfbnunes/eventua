package ua.pt.eventua.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;

import ua.pt.eventua.Adapters.CustomSwipeAdapter;
import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.R;

public class EventPlant extends Fragment {

    protected FragmentActivity mActivity;
    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        final Toolbar toolbar = (Toolbar)  mActivity.findViewById(R.id.toolbar);
        ((AppCompatActivity)mActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_event_plant, container, false);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        CustomSwipeAdapter adapter = new CustomSwipeAdapter(this.getActivity()).setInfo(event.getFloors());
        viewPager.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mActivity.setTitle(getString(R.string.title_plants));

    }


    public EventPlant setInfo(Event e){
        this.event = e;
        return this;
    }


    @Override
    public void onDetach() {
        super.onDetach();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }
}
