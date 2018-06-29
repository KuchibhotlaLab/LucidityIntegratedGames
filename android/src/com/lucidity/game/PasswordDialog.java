package com.lucidity.game;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Popup dialog that prompts for the caregiver password
 */




public class PasswordDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;

    public PasswordDialog(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.password_dialog);

        EditText password = (EditText) findViewById(R.id.caregiver_password);

        /*Button bt_submit = (Button)findViewById(R.id.submit);
        Button bt_cancel = (Button)findViewById(R.id.cancel);


        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submit and go to caregiver page
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/
        show();


    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.submit:
                break;
            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }*/
        dismiss();
    }

}
