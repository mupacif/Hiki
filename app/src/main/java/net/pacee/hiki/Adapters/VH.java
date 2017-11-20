package net.pacee.hiki.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;

import net.pacee.hiki.R;

import butterknife.BindView;
import butterknife.ButterKnife;



public class VH extends RecyclerView.ViewHolder   implements View.OnClickListener {
        @BindView(R.id.place_name)
        CheckedTextView name;
        private EventListener listener;


        public VH(View itemView,EventListener listener)
        {
            super(itemView);
             this.listener = listener;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onInterestClick(getAdapterPosition());
        }
    }

