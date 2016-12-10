package com.developement.app.hap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileEdit extends AppCompatActivity {
      private FirebaseUser user;
      private UserProfileChangeRequest upcp;
      private EditText displayname;
    private Button change;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        displayname = (EditText) findViewById(R.id.displayname_edit);
       user = FirebaseAuth.getInstance().getCurrentUser();
        change = (Button) findViewById(R.id.editbtn);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upcp = new UserProfileChangeRequest.Builder()
                        .setDisplayName((displayname.getText()).toString())
                        .build();
                 displayname.setText(user.getDisplayName());
                Toast.makeText(ProfileEdit.this,"Action Performed",Toast.LENGTH_LONG).show();
            }
        });

    }
}
