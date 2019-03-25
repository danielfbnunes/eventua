package ua.pt.eventua.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;

import ua.pt.eventua.Adapters.CommentsAdapter;
import ua.pt.eventua.Adapters.SpeakersAdapter;
import ua.pt.eventua.Constants;
import ua.pt.eventua.ConvertDate;
import ua.pt.eventua.Entities.Comment;
import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.R;
import ua.pt.eventua.Entities.Talk;

public class TalkDetails extends Fragment
{
    private String title, hours, description ;
    private String imageResource;
    private String[] speakersIDs;
    private ArrayList<Person> speakers;

    private Talk e;

    private Button btn;

    private TextView rating;
    private TextView comments;
    private  RecyclerView commentsRecycler;
    private ArrayList<Comment> all_comments;


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

        View view = inflater.inflate(R.layout.fragment_talks, container, false);

        rating = (TextView) view.findViewById(R.id.talkRating);
        comments = (TextView) view.findViewById(R.id.talkComment);

        ImageView eventImage = (ImageView)  view.findViewById(R.id.talkImage);
        TextView eventTitle = (TextView) view.findViewById(R.id.talkTitle);
        TextView eventHours = (TextView) view.findViewById(R.id.talkHours);
        TextView eventDescription = (TextView) view.findViewById(R.id.talkDescription);

        // Load the images into the ImageView using the Glide library.
        Glide.with(getContext().getApplicationContext()).load(e.getImageResource()).into(eventImage);
        // Load textviews
        eventTitle.setText(title);
        String convertedHours = ConvertDate.dateToString_days(Constants.CURRENT_TALK.getHours());
        convertedHours += (" - " + ConvertDate.dateToString_hours(Constants.CURRENT_TALK.getHours()));
        eventHours.setText(convertedHours);
        eventDescription.setText(description);

        commentsRecycler = (RecyclerView) view.findViewById(R.id.comments_list);

        while (commentsRecycler.getItemDecorationCount() > 0) {
            commentsRecycler.removeItemDecorationAt(0);
        }

        final CommentsAdapter adapter_comments =  new CommentsAdapter(getActivity(), all_comments, getActivity());

        commentsRecycler.setAdapter(adapter_comments);

        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(getActivity());

        commentsRecycler.setLayoutManager(layoutManager);

        all_comments = new ArrayList<Comment>();

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.speakers_list);

        while (mRecyclerView.getItemDecorationCount() > 0) {
            mRecyclerView.removeItemDecorationAt(0);
        }


        final SpeakersAdapter adapter =  new SpeakersAdapter(getActivity(), speakers, getActivity());

        mRecyclerView.setAdapter(adapter);

        layoutManager =  new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);

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
                        if (data.child("talks").child(""+e.getTalkId()).hasChild("comments")){
                            float sum = 0, counter = 0;
                            for (DataSnapshot snap : data.child("talks").child(""+e.getTalkId()).child("comments").getChildren())
                            {
                                counter++;
                                sum += Float.parseFloat(snap.child("rating").getValue(String.class));

                                Comment c = new Comment(snap.child("image").getValue(String.class), snap.child("name").getValue(String.class), snap.child("comment").getValue(String.class), snap.child("rating").getValue(String.class));
                                all_comments.add(c);

                                adapter_comments.setData(all_comments);
                                adapter_comments.notifyDataSetChanged();
                            }
                            setRatingCommentOn(sum/counter);
                        }else{
                            setRatingCommentOff();
                        }

                        for (DataSnapshot ds : dataSnapshot.child(eventId).child("speakers").getChildren())
                        {

                            String name = ds.child("name").getValue(String.class);
                            String id = ds.child("id").getValue(String.class);
                            String talkSpeakers = Constants.CURRENT_TALK.getSpeakers();
                            if(Arrays.asList(talkSpeakers.split(",")).contains(id))
                            {
                                String description = ds.child("description").getValue(String.class);
                                String imageUrl = ds.child("imageUrl").getValue(String.class);

                                Person s = new Person(Integer.parseInt(id), name, description, imageUrl);
                                speakers.add(s);

                                adapter.setData(speakers);
                                adapter.notifyDataSetChanged();
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


        btn = (Button) view.findViewById(R.id.fav_button);
        btn.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    if (ds.getKey().equals(""+e.getTalkId())){

                                        String temp;
                                        String[] array = ds.child("subscribers").getValue().toString().trim().split(",");
                                        if (Arrays.asList(array).contains(Constants.CURRENT_PERSON.getMail())){
                                            temp = ds.child("subscribers").getValue().toString().replace(Constants.CURRENT_PERSON.getMail(), "");
                                            setBtnOff();
                                        }else{
                                            temp = ds.child("subscribers").getValue().toString() + "," + Constants.CURRENT_PERSON.getMail();
                                            setBtn();
                                        }
                                        FirebaseDatabase.getInstance().getReference().child("events").child(eventId).child("talks").child(""+e.getTalkId()).child("subscribers").setValue(temp);
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
            }
        });

        ref = FirebaseDatabase.getInstance().getReference().child("events");
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
                            if (ds.getKey().equals(""+e.getTalkId())){
                                String[] array = ds.child("subscribers").getValue().toString().trim().split(",");
                                if (Arrays.asList(array).contains(Constants.CURRENT_PERSON.getMail())){
                                    setBtn();
                                }else{
                                    setBtnOff();
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

        ref = FirebaseDatabase.getInstance().getReference().child("global_users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                if (d.getKey().contains(""+Constants.CURRENT_PERSON.getId())){
                                    String[] array = d.child("events").getValue().toString().trim().split(",");
                                    if (Arrays.asList(array).contains(Constants.CURRENT_EVENT.getEventId()))
                                        setVisible();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        return view;
    }

    public void setBtn(){
        btn.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fav));
    }

    public void setBtnOff(){
        btn.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_fav));
    }

    public void setRatingCommentOn(Float media){
        rating.setText(rating.getText() + "" + media);
        rating.setVisibility(View.VISIBLE);
        commentsRecycler.setVisibility(View.VISIBLE);
        comments.setVisibility(View.VISIBLE);
    }

    public void setRatingCommentOff(){
        rating.setVisibility(View.GONE);
        commentsRecycler.setVisibility(View.GONE);
        comments.setVisibility(View.GONE);
    }

    public void setVisible(){
        btn.setVisibility(View.VISIBLE);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(title);
    }


    public TalkDetails initializeData(Talk e)
    {
        this.e = e;
        //this.image = e.getImageResource();
        this.speakersIDs = e.getSpeakers().trim().split(",");
        this.title = e.getTitle();
        this.hours = e.getHours();
        this.description = e.getDescription();
        this.imageResource = e.getImageResource();

        return this;
    }

    public void imageClick(View view) {
        /*
        Talk currentTalk = eventsData.get(getAdapterPosition());

        TalkDetails eventFrag = new PersonFragment().initializeData(currentTalk);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        //Implement image click function
        */
    }
}
