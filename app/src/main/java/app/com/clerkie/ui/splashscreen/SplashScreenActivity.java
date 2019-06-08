package app.com.clerkie.ui.splashscreen;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.com.clerkie.R;
import app.com.clerkie.ui.chat.ChatActivity;
import app.com.clerkie.ui.onboard.login.LoginActivity;
import app.com.clerkie.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {

    @BindView(R.id.splash_progress_bar)
    ProgressBar splashProgressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        splashProgressBar.setVisibility(View.VISIBLE);

        if (ViewUtils.isInternetAvailable(this)) {
            firebaseAuth = FirebaseAuth.getInstance();
            getSessionDetails();
        }
        else {
            ViewUtils.createNoInternetDialog(this, R.string.network_error);
        }
    }

    private void getSessionDetails() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                System.out.println("Current User"+firebaseAuth.getCurrentUser());
                if (currentUser != null) {
                    navigateToChatScreen();
                }
                else {
                    navigateToLogin();
                }
            }
        };
    }

    private void navigateToChatScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashProgressBar.setVisibility(View.GONE);
                startActivity(new Intent(SplashScreenActivity.this, ChatActivity.class));
                finish();

            }
        }, 2000);
    }

    private void navigateToLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashProgressBar.setVisibility(View.GONE);
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
        // minsdk = 21 -> sdk check not needed
        View statusBarView = getWindow().getDecorView();
        statusBarView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

}
