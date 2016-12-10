package com.developement.app.hap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    private TextView register;

    //Google--------------------------------------------------------------------------------------------
    public static final String TAG = Login.class.getSimpleName();
    private Button mGoogleBtn;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    //Google---------------------------------------------------------------------------------------------

    //Email----------------------------------------------------------------------------------------------
    private EditText email_id,pass;
    private Button signIn_Button;
    //Email-----------------------------------------------------------------------------------------------

    /*//Facebook--------------------------------------------------------------------------------------------
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    public int fb=0;
    *///Facebook--------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        //General---------------------------------------------------------------------------------------------
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Working");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(Login.this,Home.class));
                    finish();
                }
            }
        };
        register =(TextView) findViewById(R.id.registerbtn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
                finish();
            }
        });
        //General--------------------------------------------------------------------------------------------

        //Email-------------------------------------------------------------------------------------------
        email_id =(EditText) findViewById(R.id.email_login);
        pass = (EditText) findViewById(R.id.password_login);
        signIn_Button = (Button) findViewById(R.id.loginbtn);
        signIn_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_id.getText().toString();
                final String password = pass.getText().toString();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    email_id.setError("Cannot Leave Blank");
                    pass.setError("Cannot Leave Blank");
                }
                if (TextUtils.isEmpty(email)) {
                    email_id.setError("Cannot Leave Blank");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    pass.setError("Cannot Leave Blank");
                    return;
                }
                progressDialog.setMessage("Logging In, Please Wait...");
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if(!task.isSuccessful()){
                            if(password.length()<6) {
                                pass.setError("Should be More than 6 Characters");
                            }
                            Toast.makeText(Login.this,"Authentication Failed",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });

        //Email-----------------------------------------------------------------------------------------------

        //Google----------------------------------------------------------------------------------------------
        mGoogleBtn = (Button) findViewById(R.id.googlebtn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(Login.this,"Connection Error",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        //Google------------------------------------------------------------------------------------------------

        /*//Facebook----------------------------------------------------------------------------------------------
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.Facebook_loginButton);
        if(loginButton.isActivated()) fb=1;
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
        *///Facebook----------------------------------------------------------------------------------------------
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    //Google----------------------------------------------------------------------------------------------------
    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // callbackManager.onActivityResult(requestCode, resultCode, data);        Facebook

        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        } else{
            Toast.makeText(Login.this,"Google LogIn Failed",Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        Log.d(TAG,"SignInWithCredential: onComplete:"+ task.isSuccessful());
                        if(!task.isSuccessful()){
                            Log.w(TAG,"SignInWithCredential",task.getException());
                            Toast.makeText(Login.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //Google---------------------------------------------------------------------------------------------------------

    //Facebook-------------------------------------------------------------------------------------------------------
   /* private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/
    //Facebook---------------------------------------------------------------------------------------------------------
}
