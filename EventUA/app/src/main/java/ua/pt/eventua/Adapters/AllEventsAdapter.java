package ua.pt.eventua.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.HashMap;

import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Event;
import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.Entities.Talk;
import ua.pt.eventua.Fragments.EventsMainPage;
import ua.pt.eventua.Fragments.Ticket;
import ua.pt.eventua.R;
import ua.pt.eventua.Activities.Welcome;

public class AllEventsAdapter extends RecyclerView.Adapter<AllEventsAdapter.ViewHolder>
{
    //Member variables.
    private ArrayList<Event> eventsData;
    private Context mContext;
    private Activity mActivity;

    public AllEventsAdapter(Context context, ArrayList<Event> eventsData, Activity mActivity) {
        this.eventsData = eventsData;
        this.mContext = context;
        this.mActivity = mActivity;
    }

    @Override
    public AllEventsAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false));
    }

    public void setData(ArrayList<Event> eventsData){
        this.eventsData =eventsData;
    }

    public void onBindViewHolder(AllEventsAdapter.ViewHolder holder, int position) {
        if(eventsData != null) {
            Event currentEvent = eventsData.get(position);
            // Populate the textviews with data.
            holder.bindTo(currentEvent);
        }
    }

    public int getItemCount() {
        if(eventsData != null)
            return eventsData.size();
        else
            return 0;

    }



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Member Variables for the TextViews
        private TextView eventName;
        private TextView eventOrganizer;
        private TextView eventStart;
        private ImageView eventImage;


        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            eventName = itemView.findViewById(R.id.eventName);
            eventOrganizer = itemView.findViewById(R.id.eventOrganizer);
            eventStart = itemView.findViewById(R.id.eventStart);
            eventImage = itemView.findViewById(R.id.eventImage);

            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Event currentEvent) {
            // Populate the textviews with data.
            eventName.setText(currentEvent.getName());
            eventOrganizer.setText(currentEvent.getOrganizer());
            eventStart.setText(currentEvent.getStart_at());
            // Load the images into the ImageView using the Glide library
            Glide.with(mContext).load(currentEvent.getPic_url()).into(eventImage);
        }


        public void onClick(View view)
        {

            final Event currentEvent = eventsData.get(getAdapterPosition());

            Constants.CURRENT_EVENT_ID = currentEvent.getEventId();
            Constants.CURRENT_EVENT_NAME = currentEvent.getName();
            Constants.CURRENT_EVENT = currentEvent;

            Welcome.current_event = currentEvent;

            final View v = view;


            EventsMainPage eventFrag = new EventsMainPage().initializeData(currentEvent);

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

    }
}
