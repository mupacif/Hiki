package net.pacee.hiki;

import android.content.Intent;

import android.preference.PreferenceManager;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.query.Query;


import net.pacee.hiki.Model.Interest;
import net.pacee.hiki.Model.Interest_;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_fab)
    FloatingActionButton fab;
    @BindView(R.id.rv_main_main)
    RecyclerView rc;

    List<Interest> interests;




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


        this.interests = interestQuery.find();
        adapter.setInterests(interests);
        for(Interest i : interests)
            Log.e("main","text:"+i);
    }

}
