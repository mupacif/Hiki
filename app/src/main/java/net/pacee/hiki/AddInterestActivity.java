package net.pacee.hiki;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mupac_000 on 17-11-17.
 */

public class AddInterestActivity extends AppCompatActivity {

    @BindView(R.id.et_addinterest_title)
    TextView etAddInterestTitle;
    @BindView(R.id.et_addinterest_description)
    TextView etAddInterestNode;
    @BindView(R.id.tv_addInterest_chooseDate)
    TextView tvAddInterestDate;
    @BindView(R.id.bt_addinterest_addLocation)
    TextView getTvAddInterestLocation;

    Calendar calendar;
    Place place;

    SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interest);
        ButterKnife.bind(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEEE, d MMM yyyy");
    }

    @OnClick(R.id.bt_addinterest_save)
    public void save(View view)
    {

    }




    @Override
    public void onBackPressed() {


        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        AddInterestActivity.super.onBackPressed();
                    }
                }).create().show();

    }


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
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");


    }

    @OnClick(R.id.bt_addinterest_exit)
    public void exit()
    {

    }
    @OnClick(R.id.ll_addInterest_choseLocation)
    public void getLocation()
    {

    }

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


}
