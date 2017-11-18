package net.pacee.hiki;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.preference.PreferenceManager;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.query.LazyList;
import io.objectbox.query.Query;


import net.pacee.hiki.Model.Interest;
import net.pacee.hiki.Model.Interest_;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_fab)
    FloatingActionButton fab;
    @BindView(R.id.rv_main_main)
    RecyclerView rc;

    LazyList<Interest> interests;
    private Paint p = new Paint();



    private Box<Interest> interestBox;
    private Query<Interest> interestQuery;

    CustomCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        adapter = new CustomCursorAdapter(getApplicationContext());
        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.setAdapter(adapter);


        adapter.setEventListener(new CustomCursorAdapter.EventListener() {
            @Override
            public void onInterestClick(int position) {
                Log.e("Main","clicked on position:"+position);
                Interest i = interests.get(position);
                long id = i.getId();
                Intent goToAddInterest = new Intent(MainActivity.this,AddInterestActivity.class);
                goToAddInterest.putExtra("interestId",id);
                startActivity(goToAddInterest);

            }
        });
        interestBox = ((App) getApplication()).getBoxStore().boxFor(Interest.class);
        updateNotes();

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
                            updateNotes();
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

    @Override
    protected void onResume() {
        super.onResume();
        updateNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_setting:
                Intent s = new Intent(this, PrefActivity.class);
                startActivity(s);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.activity_main_fab)
    public void click()
    {
        Intent goToAddInterest = new Intent(this,AddInterestActivity.class);
        startActivity(goToAddInterest);
    }



    public void updateNotes()
    {
        boolean hide_sync = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.hide_sync), false);


        if(hide_sync)
            interestQuery = interestBox.query().equal(Interest_.done, false).order(Interest_.date).build();
        else
            interestQuery = interestBox.query().order(Interest_.done).build();


        this.interests = interestQuery.findLazy();
        adapter.setInterests(interests);
        for(Interest i : interests)
            Log.e("main","text:"+i);
    }

}
