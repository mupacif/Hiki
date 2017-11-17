package net.pacee.hiki;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mupac_000 on 17-11-17.
 */

public class InterestDoneFragment extends DialogFragment implements TextView.OnEditorActionListener{
    @BindView(R.id.et_dialog_interest_finished_comment)
    EditText comment;
    @BindView(R.id.rb_dialog_interest_finished_comment)
    RatingBar rb;
    @BindView(R.id.ctv_dialog_interest_finished_comment)
    CheckedTextView recommend;
    @BindView(R.id.bt__dialog_interest_finished_comment)
    Button done;

    DoneDialogListener listener;
    public interface DoneDialogListener
    {
        public void result(String comment, float rating, boolean recommended);
    }


    public void setListener(DoneDialogListener listener) {
        this.listener = listener;
    }

    public InterestDoneFragment() {
    }

    public static InterestDoneFragment newInstance()
    {
        InterestDoneFragment idf = new InterestDoneFragment();
        return idf;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialog_interest_finished,container);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comment.requestFocus();
        // Show soft keyboard automatically and request focus to field
        comment.setOnEditorActionListener(this);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @OnClick(R.id.bt__dialog_interest_finished_comment)
    public void finish()
    {
        listener.result(comment.getText().toString(),rb.getRating(),recommend.isChecked());
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    // Fires whenever the textfield has an action performed
    // In this case, when the "Done" button is pressed
    // REQUIRES a 'soft keyboard' (virtual keyboard)

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
                //comment okay
                // Close the dialog and return back to the parent activity
                //dismiss();
            return true;
        }
        return false;

    }
}
