package net.pacee.hiki.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import net.pacee.hiki.Model.Interest;
import net.pacee.hiki.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.query.LazyList;


public class CustomParseObjectAdapter extends RecyclerView.Adapter<VH> implements InterestAdapter {

    LazyList<Interest> interests;
    private Context context;
    private EventListener listener;
    public void setEventListener(EventListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void setInterests(List<Interest> interests) {

    }

    public CustomParseObjectAdapter(Context context)
    {
        this.context = context;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.places_list,parent,false);
        return new VH(view,listener);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Interest interest = interests.get(position);
        holder.name.setText(interest.getName());
        holder.name.setChecked(interest.getDone());
        holder.itemView.setTag(interest.getId());
        Log.e("adapter","name:"+interest.getName());
    }

    public void setInterests(LazyList<Interest> interests)
    {
        this.interests = interests;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(interests == null)
            return 0;
        return interests.size();
    }


}
