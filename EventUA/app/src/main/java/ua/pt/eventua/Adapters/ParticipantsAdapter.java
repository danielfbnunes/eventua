package ua.pt.eventua.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.Fragments.ParticipantFragment;
import ua.pt.eventua.Fragments.PersonFragment;
import ua.pt.eventua.R;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>
{
    //Member variables.
    private ArrayList<Person> eventsData;
    private Context mContext;
    private Activity mActivity;

    public ParticipantsAdapter(Context context, ArrayList<Person> eventsData, Activity mActivity) {
        this.eventsData = eventsData;
        this.mContext = context;
        this.mActivity = mActivity;
    }

    public void setData(ArrayList<Person> eventsData){
        this.eventsData =eventsData;
    }

    @Override
    public ParticipantsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.speaker_item, parent, false));
    }

    public void onBindViewHolder(ParticipantsAdapter.ViewHolder holder, int position) {
        // Get current sport.
        if(eventsData != null) {
            Person currentEvent = eventsData.get(position);
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


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // Member Variables for the TextViews
        private TextView textview;
        private ImageView imageView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            // Initialize the views.
            textview = itemView.findViewById(R.id.speaker);
            imageView = itemView.findViewById(R.id.speakerImage);

            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Person p){
            textview.setText(p.getName());
            Glide.with(mContext).load(p.getImageResource()).into(imageView);
        }

        public void onClick(View view)
        {
            Person p = eventsData.get(getAdapterPosition());
            ParticipantFragment eventFrag = new ParticipantFragment().initializeData(p);
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

    }
}
