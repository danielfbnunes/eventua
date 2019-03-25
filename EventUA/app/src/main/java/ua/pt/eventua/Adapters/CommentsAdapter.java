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

import ua.pt.eventua.Entities.Comment;
import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.Fragments.PersonFragment;
import ua.pt.eventua.R;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>
{
    //Member variables.
    private ArrayList<Comment> comments;
    private Context mContext;
    private Activity mActivity;

    public CommentsAdapter(Context context, ArrayList<Comment> comments, Activity mActivity) {
        this.comments = comments;
        this.mContext = context;
        this.mActivity = mActivity;
    }

    public void setData(ArrayList<Comment> comments){
        this.comments =comments;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false));
    }

    public void onBindViewHolder(CommentsAdapter.ViewHolder holder, int position) {
        // Get current sport.
        if(comments != null) {
            Comment comment = comments.get(position);
            // Populate the textviews with data.
            holder.bindTo(comment);
        }
    }


    public int getItemCount() {
        if(comments != null)
            return comments.size();
        else
            return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // Member Variables for the TextViews
        private TextView textview1;
        private TextView textview2;
        private ImageView imageView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            // Initialize the views.
            textview1 = itemView.findViewById(R.id.user);
            textview2 = itemView.findViewById(R.id.comment);
            imageView = itemView.findViewById(R.id.userImage);

            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Comment c){
            textview1.setText(c.getUsername());
            textview2.setText(c.getText());
            Glide.with(mContext).load(c.getImage_resource()).into(imageView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
