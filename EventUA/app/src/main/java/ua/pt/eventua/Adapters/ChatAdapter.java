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

import java.util.List;

import ua.pt.eventua.Fragments.Chat;
import ua.pt.eventua.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{
    //Member variables.
    private List<String> usersData, users_emailData;
    private Context mContext;
    private Activity mActivity;

    public ChatAdapter(Context context, List<String> usersData, List<String> users_emailData, Activity mActivity) {
        this.usersData = usersData;
        this.users_emailData = users_emailData;
        this.mContext = context;
        this.mActivity = mActivity;
    }

    public void init(List<String> users, List<String> emails){
        this.usersData = users;
        this.users_emailData = emails;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false));
    }

    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
        // Get current sport.
        if (usersData != null && users_emailData != null){
            String receiver = usersData.get(position);
            String receiver_email = users_emailData.get(position);
            // Populate the textviews with data.
            holder.bindTo(receiver, receiver_email);
        }
    }

    public int getItemCount() {
        if(usersData != null)
            return usersData.size();
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
            mTitleText = itemView.findViewById(R.id.user_username);
            mInfoText = itemView.findViewById(R.id.user_email);
            //mEventImage = itemView.findViewById(R.id.schedule_image);

            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(String receiver, String receiver_email){
            // Populate the textviews with data.
            mTitleText.setText(receiver);
            mInfoText.setText(receiver_email);

        }



        public void onClick(View view)
        {
            String receiver = usersData.get(getAdapterPosition());

            Chat eventFrag = new Chat().initializeData(receiver);

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, eventFrag).addToBackStack(null).commit();
        }

    }
}
