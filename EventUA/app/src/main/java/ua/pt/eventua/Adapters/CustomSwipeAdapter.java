package ua.pt.eventua.Adapters;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ua.pt.eventua.R;

public class CustomSwipeAdapter extends PagerAdapter {
    private Context ctx;
    private ArrayList<String> floors;
    private LayoutInflater layoutInflater;

    public CustomSwipeAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() { return floors.size();  }

    public CustomSwipeAdapter setInfo(ArrayList<String> floors){
        this.floors = floors;
        return this;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (ScrollView)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        ImageView imageview = (ImageView) item_view.findViewById(R.id.image_view);
        TextView textView = (TextView)  item_view.findViewById(R.id.image_count);
        Glide.with(ctx).load(floors.get(position)).into(imageview);
        textView.setText(ctx.getString(R.string.swipe_adpter_floor) + " " + position);
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ScrollView)object);
    }

}
