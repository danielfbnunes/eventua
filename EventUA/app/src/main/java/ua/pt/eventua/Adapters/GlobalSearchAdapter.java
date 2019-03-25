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

import java.util.ArrayList;

import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.Entities.Talk;
import ua.pt.eventua.Fragments.ParticipantFragment;
import ua.pt.eventua.Fragments.TalkDetails;
import ua.pt.eventua.R;

public class GlobalSearchAdapter extends RecyclerView.Adapter<GlobalSearchAdapter.ViewHolder>
{
    //Member variables.
    private ArrayList<Object> eventsData;
    private Context mContext;
    private Activity mActivity;

    public GlobalSearchAdapter(Context context, ArrayList<Object> eventsData, Activity mActivity) {
        this.eventsData = eventsData;
        this.mContext = context;
        this.mActivity = mActivity;
    }

    public void setData(ArrayList<Object> eventsData){
        this.eventsData =eventsData;
    }

    @Override
    public GlobalSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.speaker_item, parent, false));
    }

    public void onBindViewHolder(GlobalSearchAdapter.ViewHolder holder, int position) {
        // Get current sport.
        if(eventsData != null) {
            Object currentEvent = eventsData.get(position);
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
        private ImageView typeView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            // Initialize the views.
            textview = itemView.findViewById(R.id.speaker);
            imageView = itemView.findViewById(R.id.speakerImage);
            typeView = itemView.findViewById(R.id.type);

            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Object o){
            if(o instanceof Person)
            {
                textview.setText(((Person)o).getName());
                Glide.with(mContext).load(((Person)o).getImageResource()).into(imageView);

                if(((Person)o).getPersonType() == Person.PersonType.SPEAKER) {
                    typeView.setImageResource(R.drawable.ic_speaker_person);
                }
                else
                    typeView.setImageResource(R.drawable.ic_participant);
            }
            else if(o instanceof Talk)
            {
                textview.setText(((Talk)o).getTitle());
                textview.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                Glide.with(mContext).load(((Talk)o).getImageResource()).into(imageView);
                typeView.setImageResource(R.drawable.ic_talk);
            }
        }

        public void onClick(View view)
        {
            Object o =  eventsData.get(getAdapterPosition());

            if( o instanceof Person)
            {
                Person p = (Person) eventsData.get(getAdapterPosition());
                ParticipantFragment eventFrag = new ParticipantFragment().initializeData(p);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
            }


            else if( o instanceof Talk)
            {
                Talk currentEvent = (Talk) eventsData.get(getAdapterPosition());
                Constants.CURRENT_TALK = currentEvent;
                TalkDetails eventFrag = new TalkDetails().initializeData(currentEvent);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();

            }
        }

    }
}
