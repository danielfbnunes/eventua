package ua.pt.eventua.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.R;


public class ParticipantFragment extends Fragment
{
    private String name, bio;
    private String imageResource;
    private Person p;


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

        View view = inflater.inflate(R.layout.fragment_participant, container, false);

        ImageView personImage = (ImageView)  view.findViewById(R.id.personImage);
        TextView personName = (TextView) view.findViewById(R.id.personName);
        TextView personBio = (TextView) view.findViewById(R.id.personBio);

        // Load the images into the ImageView using the Glide library.
        Glide.with(getActivity()).load(p.getImageResource()).into(personImage);

        // Load textviews
        personName.setText(name);
        personBio.setText(bio);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(name);
    }


    public ParticipantFragment initializeData(Person p)
    {

        this.p = p;
        this.name = p.getName();
        this.bio=p.getBio();
        this.imageResource = p.getImageResource();
        return this;


    }
}