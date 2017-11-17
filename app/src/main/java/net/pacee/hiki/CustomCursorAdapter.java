package net.pacee.hiki;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import net.pacee.hiki.Model.Interest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by mupac_000 on 11-11-17.
 */

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.VH>{
    private Cursor cursor;
    List<Interest> interests;
    private Context context;
    private EventListener listener;
    public void setEventListener(EventListener listener)
    {
        this.listener = listener;
    }
    public interface EventListener
    {
        public void onInterestClick(int position);
    }

    public CustomCursorAdapter(Context context)
    {
        this.context = context;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.places_list,parent,false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Interest interest = interests.get(position);
        holder.name.setText(interest.getName());
        holder.name.setChecked(interest.getDone());

        holder.itemView.setTag(interest.getId());
        Log.e("adapter","name:"+interest.getName());
    }

    public void setInterests(List<Interest> interests)
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

    public class VH extends ViewHolder implements View.OnClickListener {
        @BindView(R.id.place_name)
        CheckedTextView name;


        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onInterestClick(getAdapterPosition());
        }
    }
}
