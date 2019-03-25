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
import ua.pt.eventua.Entities.Talk;
import ua.pt.eventua.Fragments.TalkDetails;
import ua.pt.eventua.R;

public class MyScheduleAdapter extends RecyclerView.Adapter<MyScheduleAdapter.ViewHolder>
{
    //Member variables.
    private ArrayList<Talk> eventsData;
    private Context mContext;
    private Activity mActivity;

    public MyScheduleAdapter(Context context, ArrayList<Talk> eventsData, Activity mActivity) {
        this.eventsData = eventsData;
        this.mContext = context;
        this.mActivity = mActivity;
    }

    public void setData(ArrayList<Talk> eventsData){
        this.eventsData =eventsData;
    }

    @Override
    public MyScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.schedule_item, parent, false));
    }

    public void onBindViewHolder(MyScheduleAdapter.ViewHolder holder, int position) {
        // Get current sport.
        Talk currentEvent = eventsData.get(position);
        // Populate the textviews with data.
        holder.bindTo(currentEvent);
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
        private TextView mTitleText;
        private TextView mInfoText;
        private ImageView mEventImage;


        public ViewHolder(View itemView)
        {
            super(itemView);

            // Initialize the views.
            mTitleText = itemView.findViewById(R.id.schedule_title);
            mInfoText = itemView.findViewById(R.id.schedule_start_hours);
            mEventImage = itemView.findViewById(R.id.schedule_image);

            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Talk currentEvent){
            // Populate the textviews with data.
            mTitleText.setText(currentEvent.getTitle());
            mInfoText.setText(currentEvent.getHours());

            // Load the images into the ImageView using the Glide library.
            Glide.with(mContext).load(currentEvent.getImageResource()).into(mEventImage);
        }



        public void onClick(View view)
        {


            Talk currentEvent = eventsData.get(getAdapterPosition());
            Constants.CURRENT_TALK = currentEvent;
            TalkDetails eventFrag = new TalkDetails().initializeData(currentEvent);

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();

        }

    }
}
