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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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


import net.pacee.hiki.Adapters.CustomInterestAdapter;
import net.pacee.hiki.Model.Interest;
import net.pacee.hiki.Model.Interest_;


import java.util.ArrayList;
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

    CustomInterestAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        adapter = new CustomInterestAdapter(getApplicationContext());
        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.setAdapter(adapter);


        adapter.setEventListener(new CustomInterestAdapter.EventListener() {
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

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_activity_main);
        if(viewPager != null)
        {
            setupViewPager(viewPager);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new InterestLocalListFragment(), "Places");
        viewPager.setAdapter(adapter);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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


}
