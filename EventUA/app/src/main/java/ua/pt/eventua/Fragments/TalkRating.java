package ua.pt.eventua.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ua.pt.eventua.Constants;
import ua.pt.eventua.ConvertDate;
import ua.pt.eventua.R;

public class TalkRating extends Fragment {
    private String title, hours, talk_id, event_id ;
    private String imageResource;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_talks_rating, container, false);

        ImageView eventImage = (ImageView) view.findViewById(R.id.talkRatingImage);
        TextView eventTitle = (TextView) view.findViewById(R.id.talkRatingTitle);
        TextView eventHours = (TextView) view.findViewById(R.id.talkRatingHours);

        // Load the images into the ImageView using the Glide library.
        Glide.with(getContext().getApplicationContext()).load(imageResource).into(eventImage);
        // Load textviews
        eventTitle.setText(title);
        String convertedHours = ConvertDate.dateToString_days(hours);
        convertedHours += (" - " + ConvertDate.dateToString_hours(hours));
        eventHours.setText(convertedHours);

        Button submit = (Button) view.findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String rating_val = ""+((RatingBar) view.findViewById(R.id.ratingBar)).getRating();
                final String rating_com = ((EditText) view.findViewById(R.id.editText)).getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference().child("events").child(event_id).child("talks").child(talk_id).child("comments");
                                Query query = mRef.orderByKey().limitToLast(1);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(DataSnapshot dataSnapshot) {
                                         int val = -1;
                                         for (DataSnapshot child : dataSnapshot.getChildren()) {
                                             val = Integer.parseInt(child.getKey());
                                         }
                                         val++;

                                        mRef.child(""+val).child("user").setValue(Constants.CURRENT_PERSON.getMail());
                                        mRef.child(""+val).child("rating").setValue(rating_val);
                                        mRef.child(""+val).child("comment").setValue(rating_com);
                                        mRef.child(""+val).child("name").setValue(Constants.CURRENT_PERSON.getName());
                                        mRef.child(""+val).child("image").setValue(Constants.CURRENT_PERSON.getImageResource());

                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 });
                                Toast.makeText(getActivity(), "Thanks for the feedback!",
                                        Toast.LENGTH_LONG).show();
                                getActivity().onBackPressed();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });



        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(title);
    }


    public TalkRating initializeData(String event_id, String talk_id, String title, String hours, String imageResource)
    {
        this.event_id = event_id;
        this.talk_id = talk_id;
        this.title = title;
        this.hours = hours;
        this.imageResource = imageResource;
        return this;
    }


}
