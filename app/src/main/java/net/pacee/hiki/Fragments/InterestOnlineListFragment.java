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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import net.pacee.hiki.Adapters.CustomInterestAdapter;
import net.pacee.hiki.Adapters.CustomParseObjectAdapter;
import net.pacee.hiki.Adapters.EventListener;
import net.pacee.hiki.AddInterestActivity;
import net.pacee.hiki.App;
import net.pacee.hiki.Model.Interest;
import net.pacee.hiki.Model.Interest_;
import net.pacee.hiki.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.query.LazyList;
import io.objectbox.query.Query;



public class InterestOnlineListFragment extends Fragment {

    @BindView(R.id.rv_fragment_interest_list)
    RecyclerView rc;

    List<Interest> interests;

    CustomParseObjectAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_interest_list,container,false);
        ButterKnife.bind(this,view);
        interests = new ArrayList<>();
        setupRecyclerView(rc);
        return view;
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        rc.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CustomParseObjectAdapter(context);
        adapter.setEventListener(new EventListener() {
            @Override
            public void onInterestClick(int position) {
                Log.e("Main","clicked on position:"+position);
                Interest i = interests.get(position);
                long id = i.getId();
//                Intent goToAddInterest = new Intent(getActivity(),AddInterestActivity.class);
//                goToAddInterest.putExtra("interestId",id);
//                startActivity(goToAddInterest);
                //TODO : custom add interest for recommendation

            }
        });
        findAll();
        rc.setAdapter(adapter);

    }

    public InterestOnlineListFragment() {
    }


    public void  findAll()
    {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("interest");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null )
                {
                    if(objects.size() > 0)
                    {
                        for(ParseObject interest : objects)
                        {
                            String name = interest.getString("name");
                            Double rating = interest.getDouble("rating");
                            ParseGeoPoint position = interest.getParseGeoPoint("position");
                            String comment = interest.getString("comment");

                            Interest i = new Interest(0,null,name, null,position.getLatitude(),position.getLongitude(),comment,interest.getCreatedAt(), true);
                            interests.add(i);
                            Log.i("online", i.toString()+"\n");
                        }
                        adapter.setInterests(interests);
                    }
                }
            }
        });

    }



}
