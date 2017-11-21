package net.pacee.hiki.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.pacee.hiki.Adapters.CustomInterestAdapter;
import net.pacee.hiki.Adapters.EventListener;
import net.pacee.hiki.AddInterestActivity;
import net.pacee.hiki.App;
import net.pacee.hiki.Model.Interest;
import net.pacee.hiki.Model.Interest_;
import net.pacee.hiki.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.query.LazyList;
import io.objectbox.query.Query;

/**
 * Created by mupac_000 on 20-11-17.
 */

public class InterestLocalListFragment extends Fragment {

    @BindView(R.id.rv_fragment_interest_list)
    RecyclerView rc;

    LazyList<Interest> interests;

    CustomInterestAdapter adapter;
    Paint p = new Paint();
    private Box<Interest> interestBox;
    private Query<Interest> interestQuery;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_interest_list,container,false);
        ButterKnife.bind(this,view);
        setupRecyclerView(rc);
        return view;
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        rc.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CustomInterestAdapter(context);
        adapter.setEventListener(new EventListener() {
            @Override
            public void onInterestClick(int position) {
                Log.e("Main","clicked on position:"+position);
                Interest i = interests.get(position);
                long id = i.getId();
                Intent goToAddInterest = new Intent(getActivity(),AddInterestActivity.class);
                goToAddInterest.putExtra("interestId",id);
                startActivity(goToAddInterest);

            }
        });
        interests = lazyFind();
        adapter.setInterests(interests);
        adapter.notifyDataSetChanged();
        rc.setAdapter(adapter);
        /**
         * Swipe to delete animations + toast
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                boolean notDeleted = false;
                Snackbar snackbar = Snackbar
                        .make(rc, "EVENT REMOVED", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                adapter.notifyDataSetChanged();
                            }
                        });
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            long id = (long) viewHolder.itemView.getTag();

                            interestBox.remove(id);
                            adapter.setInterests(lazyFind());
                        }
                    }
                });
                snackbar.show();


            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.argb(255, 170, 255, 0));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                   /*     icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);*/
                    } else {

                        int tmpColor = Math.round((int) ((dX / -2000.0) * 100));
                        p.setColor(Color.argb(255, 172 + tmpColor, 162 - tmpColor, 162 - tmpColor));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        }).attachToRecyclerView(rc);
    }

    public InterestLocalListFragment() {
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.setInterests(lazyFind());
    }

    public LazyList<Interest> lazyFind()
    {
        boolean hide_sync = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.hide_sync), false);

        interestBox = ((App) getActivity().getApplication()).getBoxStore().boxFor(Interest.class);

        if(hide_sync)
            interestQuery = interestBox.query().equal(Interest_.done, false)/*.order(Interest_.date)*/.build();
        else
            interestQuery = interestBox.query()./*order(Interest_.done).*/build();

        interests = interestQuery.findLazy();
        Log.i("lazyFind","lazyList size:"+interests.size());
        return interests;
    }



}
