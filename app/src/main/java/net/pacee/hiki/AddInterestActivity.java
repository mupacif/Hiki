package net.pacee.hiki;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import net.pacee.hiki.Model.Interest;
import net.pacee.hiki.Model.Interest_;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.query.Query;



public class AddInterestActivity extends AppCompatActivity implements Validator.ValidationListener, GoogleApiClient.OnConnectionFailedListener {
//region variable Declaration
    @NotEmpty
    @BindView(R.id.et_addinterest_title)
    TextView etAddInterestTitle;

    @BindView(R.id.et_addinterest_description)
    TextView etAddInterestNote;

    @BindView(R.id.tv_addInterest_chooseDate)
    TextView tvAddInterestDate;

    @NotEmpty
    @BindView(R.id.bt_addinterest_addLocation)
    TextView tvAddInterestLocation;

    @BindView(R.id.bt_addinterest_save)
    TextView saveBtn;

    @BindView(R.id.bt_addinterest_exit)
    TextView exitBtn;
    private Date date;

    Calendar calendar;

    private Place place;

    private SimpleDateFormat dateFormat;

    private GoogleApiClient mGoogleApiClient;

    private int PLACE_PICKER_REQUEST = 1;

    Box<Interest> interestBox;
    Query<Interest> interestQuery;

    private Validator validator;

    private long interestId;

    private Interest interest;

    private boolean isUpdating;

    @BindView(R.id.bt_addinterest_done)
    Button button;

    //endregion
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interest);
        ButterKnife.bind(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEEE, d MMM yyyy");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        interestBox = ((App) getApplication()).getBoxStore().boxFor(Interest.class);

        validator = new Validator(this);
        validator.setValidationListener(this);

        isUpdating = false;

        //récupérer le bundle envoyé à l'activité
        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            interestId = extra.getLong("interestId", 0);
            Log.i("AddInterestActivity","interestId:"+interestId);
            interestQuery = interestBox.query().equal(Interest_.__ID_PROPERTY, interestId).build();
            interest = interestQuery.findUnique();

            etAddInterestTitle.setText(interest.getName()+(interest.getDone()?"(Done)":""));
            etAddInterestNote.setText(interest.getComment());
            if(interest.getDate()!=null)
             tvAddInterestDate.setText(dateFormat.format(interest.getDate()));



            tvAddInterestLocation.setText(interest.getAdress());
            isUpdating = true;
            saveBtn.setText("UPDATE");

            //petits changement de l'ui quand l'utilisateur est en mode "done"
            if(interest.getDone()) {
                saveBtn.setText("");
                exitBtn.setText("BACK");
            }
        }
        //petits changement de l'ui quand l'utilisateur est en mode update
        if(isUpdating) {
            button.setVisibility(View.VISIBLE);
            if(interest.getDone()) {
                button.setClickable(interest.getDone());
                button.setVisibility(View.GONE);
            }
        }



    }

    /***
     * Triggered when the user click on "done"
     * @param view
     */
    @OnClick(R.id.bt_addinterest_done)
    public void done(View view)
    {

        FragmentManager fm = getSupportFragmentManager();
        InterestDoneFragment idf = InterestDoneFragment.newInstance();
        idf.setListener(new InterestDoneFragment.DoneDialogListener() {
            @Override
            public void result(String comment, float rating, boolean recommended) {
                interest.setDone(true);
                interestBox.put(interest);

                if(recommended) {
                    ParseObject recommanded = new ParseObject("interest");
                    recommanded.put("name",interest.getName());
                    recommanded.put("position",new ParseGeoPoint(interest.getLat(),interest.getLng()));
                    recommanded.put("rating",rating);
                    recommanded.put("comment",comment);
                  //TODO : user id   recommanded.put("userId",id);
                    recommanded.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null)
                            {
                                Log.i("Parse","recommendation save");
                            }
                        }
                    });
                }

                finish();
            }
        });
        idf.show(fm,"done");
    }
//region  save
    @OnClick(R.id.bt_addinterest_save)
    public void save(View view)
    {
        if(place!=null || isUpdating)
        validator.validate();
        else
            Toast.makeText(this, "please select a location", Toast.LENGTH_LONG).show();
    }
//endregion
//region pickDate
@OnClick(R.id.ll_addInterest_choseDate)
    public void pickDate(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDateSetListener(new DatePickerFragment.OnDateSetListener() {
            @Override
            public void dateSet(int year, int month, int dayOfMonth) {
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                tvAddInterestDate.setText(dateFormat.format(calendar.getTime()));
                date = calendar.getTime();
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");


    }
    //endregion
//region exit
    /***
     * triggered when user click on exit
     */
    @OnClick(R.id.bt_addinterest_exit)
    public void exit()
    {
        if(!interest.getDone() && !isUpdating) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).create().show();
        }else {
            finish();
        }
    }

//endregion
//region selection of location
    @OnClick(R.id.ll_addInterest_choseLocation)
    public void getLocation()
    {
    //TODO : si click qu'un choix a déjà été fait, renvoyer dans google maps
        //TODO : si pas internet modification manuelle
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
                place = PlacePicker.getPlace(data, this);

                CharSequence name = place.getName();
                String id = place.getId();
                LatLng latLng = place.getLatLng();
                Double lat = latLng.latitude;
                Double lng = latLng.longitude;
                CharSequence adress = place.getAddress();

                if(etAddInterestTitle.getText().toString().isEmpty() && name != null && !name.toString().isEmpty())
                    etAddInterestTitle.setText(name);


                if( adress != null && !adress.toString().isEmpty())
                    tvAddInterestLocation.setText(adress);


            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Impossible to select a location using place", Toast.LENGTH_LONG).show();
        //TODO : 1 if place fail because of limitation
        //Todo : adress manuellement

    }
    //endregion
//region onValidation of form
    @Override
    public void onValidationSucceeded() {
        if(!isUpdating) {
            CharSequence name = etAddInterestTitle.getText();
            String id = place.getId();
            LatLng latLng = place.getLatLng();
            Double lat = latLng.latitude;
            Double lng = latLng.longitude;
            CharSequence adress = place.getAddress();
            String note = etAddInterestNote.getText().toString();


            Interest i = new Interest(0, id, name.toString(), adress.toString(), lat, lng, note, date, false);
            interestBox.put(i);
        }
        else
        {
            interest.setName(etAddInterestTitle.getText().toString());
            //si l"utilisateur a modifié l'adresse 
            if(place!=null && place.getLatLng().latitude != interest.getLat())
            {
                LatLng latLng = place.getLatLng();
                Double lat = latLng.latitude;
                Double lng = latLng.longitude;
                CharSequence adress = place.getAddress();

                interest.setAdress(adress.toString());
                interest.setLat(lat);
                interest.setLng(lng);
            }

            String note = etAddInterestNote.getText().toString();
            interest.setComment(note);
            interestBox.put(interest);
        }
        finish();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }


    //endregion
//region DatePickerFragment
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public interface OnDateSetListener
        {
            void dateSet(int year, int month, int dayOfMonth);
        }

        OnDateSetListener dateSetListener;

        public void setDateSetListener(OnDateSetListener dateSetListener)
        {
            this.dateSetListener = dateSetListener;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        }


        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Log.i("DatePickerDialog", "date set");
            this.dateSetListener.dateSet(year,month,dayOfMonth);

        }
    }
//endregion
//region onPressBack
@Override
public void onBackPressed() {


    //prevent exit if not done
    if(!interest.getDone() || isUpdating) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            AddInterestActivity.super.onBackPressed();
                        }
                    }).create().show();
    }else {
        finish();
    }

}
//endregion
}
