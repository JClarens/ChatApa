package com.example.jclarens.chatapa.View;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jclarens.chatapa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.zip.Inflater;

public class DialogEditProfileActivity extends DialogFragment {
    private static final String TAG = "DialogEditProfileActivi";
//1
    public interface OnInputListener {
        void sendInput(String input);
    }
    //2
    public OnInputListener mOnInputListener;

    private EditText mInputDisplay;
    private TextView mActionOk, mActionCancel;

    private FirebaseAuth mAuth;
    private DatabaseReference changeProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View _view = inflater.inflate(R.layout.activity_dialogedit_profile,container,false);


        mActionOk = (TextView) _view.findViewById(R.id.action_ok);
        mActionCancel = (TextView) _view.findViewById(R.id.action_cancel);
        mInputDisplay = (EditText) _view.findViewById(R.id.input_dialog);

        mAuth = FirebaseAuth.getInstance();
        String current_user = mAuth.getCurrentUser().getUid();
        changeProfile = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Closing Dialog");
                getDialog().dismiss();
            }
        });


        final int statusName = getArguments().getInt("status_name");
        final int statusStatus = getArguments().getInt("status_status");
        final int statusPhone = getArguments().getInt("status_phone");
        final int statusWork = getArguments().getInt("status_work");

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = mInputDisplay.getText().toString();
//                if (!(input.isEmpty())){
//
////                    ((AccountSettingActivity)getActivity()).settingName.setText(input));
//                }
                //4


                if (statusName == 1){
                    ChangeName(input);
                }
                if (statusStatus == 2){
                    ChangeStatus(input);
                }
                if (statusPhone == 3){
                    ChangePhone(input);
                }
                if (statusWork == 4){
                    ChangeWork(input);
                }

                mOnInputListener.sendInput(input);

                Intent i = new Intent(_view.getContext(),AccountSettingActivity.class);
                startActivity(i);
            }
        });
        return _view;
    }

    private void ChangeName(String input) {
        changeProfile.child("user_name").setValue(input);
    }
    private void ChangeStatus(String input) {
        changeProfile.child("user_status").setValue(input);
    }

    private void ChangePhone(String input) {
        changeProfile.child("user_phone").setValue(input);
    }

    private void ChangeWork(String input) {
        changeProfile.child("user_current_work").setValue(input);
    }


    //3
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener)getActivity();
        }catch (ClassCastException e ){
            Log.e(TAG, "onAttach: ClassCastEcxeption"+e.getMessage() );
        }
    }


}
