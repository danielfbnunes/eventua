package ua.pt.eventua.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ua.pt.eventua.Constants;
import ua.pt.eventua.ConvertDate;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.R;

public class Ticket extends Fragment {

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

        View rootView;
        ImageView image;
        TextView title;
        TextView hours;
        TextView place;
        TextView description;
        TextView price;

        //Se o utilizador ja comprou ticket e faz parte do evento
        if (Constants.CURRENT_PERSON.getEvents().contains(Constants.CURRENT_EVENT_ID)) {
            rootView = inflater.inflate(R.layout.fragment_my_ticket, container, false);
            image = (ImageView) rootView.findViewById(R.id.eventImage);
            title = (TextView) rootView.findViewById(R.id.eventTitle);
            hours = (TextView) rootView.findViewById(R.id.eventHours);
            place = (TextView) rootView.findViewById(R.id.eventPlace);
            description = (TextView) rootView.findViewById(R.id.ticket);

            title.setText(Constants.CURRENT_EVENT.getName());

            String convertedHours = ConvertDate.dateToString_days(Constants.CURRENT_EVENT.getStart_at());
            convertedHours += (" - " + ConvertDate.dateToString_hours(Constants.CURRENT_EVENT.getStart_at()));

            hours.setText(convertedHours);
            place.setText(Constants.CURRENT_EVENT.getPlace());
            description.setText(getString(R.string.ticket_owned_by) + Constants.CURRENT_PERSON.getName());
            Glide.with(getActivity()).load(Constants.CURRENT_EVENT.getPic_url()).into(image);
        }
        else
        {
            rootView = inflater.inflate(R.layout.fragment_ticket, container, false);
            image = (ImageView) rootView.findViewById(R.id.eventImage);
            title = (TextView) rootView.findViewById(R.id.eventTitle);
            hours = (TextView) rootView.findViewById(R.id.eventHours);
            place = (TextView) rootView.findViewById(R.id.eventPlace);
            description = (TextView) rootView.findViewById(R.id.eventDescription);
            price = (TextView) rootView.findViewById(R.id.price_input);

            title.setText(Constants.CURRENT_EVENT.getName());
            String convertedHours = ConvertDate.dateToString_days(Constants.CURRENT_EVENT.getStart_at());
            convertedHours += (" - " + ConvertDate.dateToString_hours(Constants.CURRENT_EVENT.getStart_at()));
            hours.setText(convertedHours);
            place.setText(Constants.CURRENT_EVENT.getPlace());
            description.setText(Constants.CURRENT_EVENT.getDescription());
            Glide.with(getActivity()).load(Constants.CURRENT_EVENT.getPic_url()).into(image);
            price.setText(""+Constants.CURRENT_EVENT.getPrice()+" €");

            Button button = (Button) rootView.findViewById(R.id.buyTicket);
            button.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    final View v2 = v;
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:


                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("global_users");
                                    ref.addListenerForSingleValueEvent(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final String events =  dataSnapshot.child(Constants.CURRENT_PERSON.getId()+"").child("events").getValue(String.class);
                                            //atualiazar os eventos
                                            ref.child(Constants.CURRENT_PERSON.getId()+"").child("events").setValue(events + "," + Constants.CURRENT_EVENT_ID);

                                            Person p0 = Constants.CURRENT_PERSON;
                                            ArrayList<String> tmp = new ArrayList<>(p0.getEvents());
                                            tmp.add(Constants.CURRENT_EVENT_ID);
                                            Person p = new Person (p0.getId(), p0.getName(), p0.getBio(), p0.getImageResource());
                                            p.setEvents(tmp);
                                            p.setMail(p0.getMail());

                                            Constants.CURRENT_PERSON = p;

                                            Ticket eventFrag = new Ticket();
                                            AppCompatActivity activity = (AppCompatActivity) v2.getContext();
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();

                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //handle databaseError
                                        }
                                    });

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.wanna_buy_ticket)
                            .setNegativeButton(R.string.str_no, dialogClickListener)
                            .setPositiveButton(R.string.str_yes, dialogClickListener).show();


                    }

            });

        }
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
        mActivity.setTitle(getString(R.string.title_ticket));
    }


    public Ticket setInfo(Event e){
        this.event = e;
        return this;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }
}
