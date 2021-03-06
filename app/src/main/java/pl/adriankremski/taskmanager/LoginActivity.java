package pl.adriankremski.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.username)
    TextView usernameLabel;

    @Bind(R.id.password)
    TextView passwordLabel;

    @Bind(R.id.progress)
    View progressView;

    @Bind(R.id.facebook_login)
    LoginButton loginButton;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        if (SyncUser.currentUser() != null) {
            showMainScreen();
        }

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logOut();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                SyncCredentials credentials = SyncCredentials.facebook(loginResult.getAccessToken().getToken());
                SyncUser.loginAsync(credentials, RealmTaskManagerApplication.AUTH_URL, new SyncUser.Callback() {
                    @Override
                    public void onSuccess(SyncUser user) {
                        showMainScreen();
                        Toast.makeText(getBaseContext(), "You are logged in", Toast.LENGTH_SHORT).show();
                        progressView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ObjectServerError error) {
                        Toast.makeText(getBaseContext(), "Log in error", Toast.LENGTH_SHORT).show();
                        progressView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
//
//        String serverId = null;
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestIdToken(serverId)
//                .build();
//
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//                    }
//                })
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

    }

    @OnClick(R.id.login)
    public void login() {
        final String email = usernameLabel.getText().toString();
        final String password = passwordLabel.getText().toString();

        progressView.setVisibility(View.VISIBLE);

        SyncUser.loginAsync(SyncCredentials.usernamePassword(email, password, false), RealmTaskManagerApplication.AUTH_URL, new SyncUser.Callback() {

            @Override
            public void onSuccess(SyncUser user) {
                showMainScreen();
                Toast.makeText(getBaseContext(), "You are logged in", Toast.LENGTH_SHORT).show();
                progressView.setVisibility(View.GONE);
            }

            @Override
            public void onError(ObjectServerError error) {
                Toast.makeText(getBaseContext(), "Log in error", Toast.LENGTH_SHORT).show();
                progressView.setVisibility(View.GONE);
            }
        });
    }

    private void showMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.register)
    public void register() {
        final String email = usernameLabel.getText().toString();
        final String password = passwordLabel.getText().toString();

        progressView.setVisibility(View.VISIBLE);

        SyncUser.loginAsync(SyncCredentials.usernamePassword(email, password, true), RealmTaskManagerApplication.AUTH_URL, new SyncUser.Callback() {

            @Override
            public void onSuccess(SyncUser user) {
                Toast.makeText(getBaseContext(), "Account created", Toast.LENGTH_SHORT).show();
                progressView.setVisibility(View.GONE);
            }

            @Override
            public void onError(ObjectServerError error) {
                Toast.makeText(getBaseContext(), "Registration error", Toast.LENGTH_SHORT).show();
                progressView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
