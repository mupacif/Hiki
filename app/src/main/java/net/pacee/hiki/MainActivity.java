package net.pacee.hiki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.query.Query;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import android.support.v4.app.FragmentActivity;

import net.pacee.hiki.Model.Interest;
import net.pacee.hiki.Model.Interest_;

import java.util.Date;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity implements OnConnectionFailedListener {

    @BindView(R.id.activity_main_fab)
    FloatingActionButton fab;
    @BindView(R.id.rv_main_main)
    RecyclerView rc;

    List<Interest> interests;

    private GoogleApiClient mGoogleApiClient;

    int PLACE_PICKER_REQUEST = 1;

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
                i.setDone(true);
                interestBox.put(i);
                adapter.notifyDataSetChanged();
            }
        });
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        interestBox = ((App) getApplication()).getBoxStore().boxFor(Interest.class);
        boolean hide_sync = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.hide_sync), false);


        if(hide_sync)
        interestQuery = interestBox.query().equal(Interest_.done, false).order(Interest_.date).build();
        else
        interestQuery = interestBox.query().order(Interest_.done).build();


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
        Toast.makeText(this,"Click",Toast.LENGTH_SHORT).show();
        Log.e("Main","Whyyy");

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                CharSequence name = place.getName();
                String id = place.getId();
                LatLng latLng = place.getLatLng();
                Double lat = latLng.latitude;
                Double lng = latLng.longitude;
                CharSequence adress = place.getAddress();

                Interest i = new Interest(0,id,name.toString(),adress.toString(),lat,lng,null,new Date(),false);
                interestBox.put(i);
                updateNotes();
            }
        }
    }

    public void updateNotes()
    {
        this.interests = interestQuery.find();
        adapter.setInterests(interests);
        for(Interest i : interests)
            Log.e("main","text:"+i);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MainActivity", "Connection failed");
    }
}
