package app.com.clerkie.ui.onboard.login;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import javax.inject.Inject;

import app.com.clerkie.R;
import app.com.clerkie.di.ClerkieApplication;
import app.com.clerkie.di.component.ApplicationComponent;
import app.com.clerkie.model.User;
import app.com.clerkie.ui.chat.ChatActivity;
import app.com.clerkie.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginViewInterface {

    // login
    @BindView(R.id.username_login)
    MaterialEditText username_login;
    @BindView(R.id.password_login)
    MaterialEditText password_login;
    @BindView(R.id.button_login)
    Button button_login;
    @BindView(R.id.login_progress_bar)
    ProgressBar login_progress_bar;
    @BindView(R.id.login_relative_layout)
    RelativeLayout login_relative_layout;

    //forgot password
    @BindView(R.id.label_forgot_password)
    TextView forgotPasswordTextView;

    //onboard
    @BindView(R.id.x_in_circle)
    TextView xCircle;
    @BindView(R.id.button_circle)
    ImageView buttonCircle;
    @BindView(R.id.login_layout)
    View loginLayout;
    @BindView(R.id.label_app_name)
    TextView appName;

    //Register
    @BindView(R.id.register_layout)
    View registerLayout;
    @BindView(R.id.username_register)
    MaterialEditText username_register;
    @BindView(R.id.email_register)
    MaterialEditText email_register;
    @BindView(R.id.phone_register)
    MaterialEditText phone_register;
    @BindView(R.id.password_register)
    MaterialEditText password_register;
    @BindView(R.id.confirmpassword_register)
    MaterialEditText confirmpassword_register;
    @BindView(R.id.button_register)
    Button button_register;


    @BindView(R.id.relative_layout1)
    View rLayout1;
    @BindView(R.id.relative_layout2)
    View rLayout2;

    private String username = "", password = "";
    private String userNameRegister, emailAddressRegister, userPhoneRegister, passwordRegister, confirmPasswordRegister;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Inject
    LoginPresenter loginPresenter;

    public static final String EMAIL_ADDRESS = "email";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getApplicationComponent().injectLogin(this);
        initializeView();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        username_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    button_login.setEnabled(true);
                }
                else {
                    button_login.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        password_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0
                        && username_login.getText().length() > 0) {
                    button_login.setEnabled(true);
                }
                else {
                    button_login.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initializeView() {
        loginPresenter.attachView(this);
    }

    private ApplicationComponent getApplicationComponent() {
        return ((ClerkieApplication) getApplication()).getApplicationComponent();
    }


  @OnClick({R.id.button_login,R.id.x_in_circle,R.id.button_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                if (ViewUtils.isInternetAvailable(this)) {
                    if (ViewUtils.checkPasswordValidity(password_login)
                            && ViewUtils.checkEmailValidity(username_login)) {
                        username = username_login.getText().toString();
                        password = password_login.getText().toString();
                        loginPresenter.checkCredentialsValidity(firebaseAuth, username, password);
                    }
                    else {
                        if (!ViewUtils.checkEmailValidity(username_login)) {
                            username_login.setError(getString(R.string.invalid_email));
                        }

                        if (!ViewUtils.checkPasswordValidity(password_login)) {
                            password_login.setError(getString(R.string.invalid_password_details));
                        }
                    }
                }
                else {
                    ViewUtils.createNoInternetDialog(this, R.string.network_error);
                }
                break;

            case R.id.x_in_circle:
                System.out.println("click 1 success");
                if (registerLayout.getVisibility() == View.INVISIBLE) {

                    displayRegisterForm();
                } else {
                    hideRegisterForm();
                }
                break;
            case R.id.button_register:
                registerNewUser();
                break;
        }

    }

    private void registerNewUser() {
        userNameRegister = username_register.getText().toString();
        emailAddressRegister = email_register.getText().toString();
        userPhoneRegister = phone_register.getText().toString();
        passwordRegister = password_register.getText().toString();
        confirmPasswordRegister = confirmpassword_register.getText().toString();

        if (userNameRegister == null) {
            userNameRegister = "";
        }

        if (!TextUtils.isEmpty(userNameRegister) && ViewUtils.checkEmailValidity(email_register)
                && ViewUtils.checkPhoneValidity(phone_register)
                && ViewUtils.checkPasswordValidity(password_register)
                && ViewUtils.checkPasswordValidity(confirmpassword_register)) {
            if (TextUtils.equals(passwordRegister, confirmPasswordRegister)) {

                registerUser();
            } else {
                confirmpassword_register.setError(getString(R.string.passwords_no_match));
            }

        } else {
            if (userNameRegister.length() == 0) {
                username_register.setError(getString(R.string.empty_name));
            }
            if (!ViewUtils.checkEmailValidity(email_register)) {
                email_register.setError(getString(R.string.invalid_email));
            }
            if (!ViewUtils.checkPhoneValidity(phone_register)) {
                phone_register.setError(getString(R.string.invalid_phone));
            }
            if (!ViewUtils.checkPasswordValidity(password_register)) {
                password_register.setError(getString(R.string.invalid_password_details));
            }
            if (!ViewUtils.checkPasswordValidity(confirmpassword_register)) {
                confirmpassword_register.setError(getString(R.string.invalid_password));
            }
        }
    }


    private void registerUser() {
        firebaseAuth.createUserWithEmailAndPassword(emailAddressRegister, passwordRegister)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            createNewUserDetails(task.getResult().getUser());
                        }
                        else {
                     //       displaySnackBar(getString(R.string.auth_failed));
                        }
                    }
                });
    }

    private void createNewUserDetails(FirebaseUser user) {
        userNameRegister = username_register.getText().toString();
        createNewUser(user.getUid(), userNameRegister, user.getEmail());
        createLRegistrationDialog(R.string.registration_successful);
    }

    private void createNewUser(String userId, String nameOfUser, String email) {
        User user = new User();
        user.setNameOfUser(nameOfUser);
        user.setUserEmailId(email);

        databaseReference.child("user").child(userId).setValue(user);
    }

    private void navigateToChatScreen() {
        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void createLRegistrationDialog(int title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        navigateToChatScreen();
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


        private void displayRegisterForm(){
        reveal_forward = true;
        buttonToCornerAnimations.clear();
        buttonToCenterAnimations.clear();
        runForwardAnimations();
    }


    private void hideRegisterForm(){
        reveal_forward = false;
        runBackwardAnimations();
    }

    private void setRegisterFieldVisibilities(boolean visible){

        registerLayout.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);

    }

    private boolean reveal_forward;
    private ArrayList<ObjectAnimator> buttonToCornerAnimations = new ArrayList<>();
    private ArrayList<ObjectAnimator> buttonToCenterAnimations = new ArrayList<>();

    private void runForwardAnimations(){
        ArrayList<ObjectAnimator> circleToCenterAnimations = getCircleToCenterAnimations();
        ArrayList<ObjectAnimator> loginToBackAnimations = getLoginToBackAnimations();

        final float cornerX = xCircle.getX();
        final float cornerY = xCircle.getY();

        buttonToCenterAnimations.addAll(circleToCenterAnimations);
        buttonToCenterAnimations.addAll(loginToBackAnimations);

        for(ObjectAnimator objectAnimator: buttonToCenterAnimations) objectAnimator.start();

        ArrayList<ObjectAnimator> xToCornerAnimations = getXtoCornerAnimations(cornerX, cornerY);
        buttonToCornerAnimations.addAll(xToCornerAnimations);

        revealRegisterFormForward(xToCornerAnimations);
    }


    private void runBackwardAnimations(){
        // Send the 'X' to the center first by reversing these animations
        for(ObjectAnimator objectAnimator : buttonToCornerAnimations) objectAnimator.reverse();
        revealRegisterFormBackward();
    }


    private ArrayList<ObjectAnimator> getCircleToCenterAnimations(){
        ArrayList<ObjectAnimator> arr = new ArrayList<>();

        ObjectAnimator animationCircleX = ObjectAnimator.ofFloat(buttonCircle, "x",
                loginLayout.getX()+(loginLayout.getWidth()-buttonCircle.getWidth())/2);
        animationCircleX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                buttonCircle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ObjectAnimator animationCircleY = ObjectAnimator.ofFloat(buttonCircle, "y",
                loginLayout.getY()+(loginLayout.getHeight()-buttonCircle.getHeight())/2);
        ObjectAnimator animationXinCircleX = ObjectAnimator.ofFloat(buttonCircle, "x",
                loginLayout.getX()+(loginLayout.getWidth()-buttonCircle.getMeasuredWidth())/2);
        ObjectAnimator animationXinCircleY = ObjectAnimator.ofFloat(buttonCircle, "y",
                loginLayout.getY()+(loginLayout.getHeight()-buttonCircle.getMeasuredHeight())/2);

        arr.add(animationCircleX);
        arr.add(animationCircleY);
        arr.add(animationXinCircleX);
        arr.add(animationXinCircleY);

        return arr;
    }


    private ArrayList<ObjectAnimator> getLoginToBackAnimations(){
        ArrayList<ObjectAnimator> arr = new ArrayList<>();

        ObjectAnimator animationY = ObjectAnimator.ofFloat(loginLayout, "y",
                loginLayout.getY()-35);
        ObjectAnimator animationAlpha = ObjectAnimator.ofFloat(loginLayout, "alpha",
                loginLayout.getAlpha()/2);
        ObjectAnimator animationScaleX = ObjectAnimator.ofFloat(loginLayout, "scaleX", 0.95f);
        ObjectAnimator animationScaleY = ObjectAnimator.ofFloat(loginLayout, "scaleY", 0.95f);

        arr.add(animationY);
        arr.add(animationAlpha);
        arr.add(animationScaleX);
        arr.add(animationScaleY);

        return arr;
    }


    private ArrayList<ObjectAnimator> getXtoCornerAnimations(float x, float y){
        ArrayList<ObjectAnimator> arr = new ArrayList<>();

        ObjectAnimator animationXinCircleX = ObjectAnimator.ofFloat(xCircle, "x", x);
        ObjectAnimator animationXinCircleY = ObjectAnimator.ofFloat(xCircle, "y", y);
        ObjectAnimator animationXinCircleRotation = ObjectAnimator.ofFloat(xCircle, "rotation", 45);

        arr.add(animationXinCircleX);
        arr.add(animationXinCircleY);
        arr.add(animationXinCircleRotation);

        return arr;
    }


    private void revealRegisterFormForward(final ArrayList<ObjectAnimator> xToCornerAnimations){
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                registerLayout,
                registerLayout.getWidth()/2,
                registerLayout.getHeight()/2,
                buttonCircle.getWidth()/2,
                registerLayout.getWidth()/2);

        revealAnimator.setStartDelay(250);

        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(reveal_forward) buttonCircle.setVisibility(View.INVISIBLE);
                registerLayout.setVisibility(View.VISIBLE);
                setRegisterFieldVisibilities(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                registerLayout.setVisibility(View.VISIBLE);
                setRegisterFieldVisibilities(true);
                setRegisterFieldVisibilities(true);
                for(ObjectAnimator objectAnimator : xToCornerAnimations) objectAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        revealAnimator.start();
    }


    private void revealRegisterFormBackward(){
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                registerLayout,
                registerLayout.getWidth()/2,
                registerLayout.getHeight()/2,
                registerLayout.getWidth()/2,
                buttonCircle.getWidth()/2);

        revealAnimator.setStartDelay(250);

        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setRegisterFieldVisibilities(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                registerLayout.setVisibility(View.INVISIBLE);
                for(ObjectAnimator objectAnimator : buttonToCenterAnimations) objectAnimator.reverse();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        revealAnimator.start();
    }


    private ObjectAnimator currentInstructionAnimation = null;
    private void showInstructions(final int message){
        if(currentInstructionAnimation != null) currentInstructionAnimation.end();

        final ObjectAnimator animationShow = ObjectAnimator.ofFloat(appName, "alpha", 1);
        animationShow.setStartDelay(1000);
        animationShow.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                currentInstructionAnimation = animationShow;
                appName.setText(message);
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        final ObjectAnimator animationHide = ObjectAnimator.ofFloat(appName, "alpha", 0);
                        animationHide.setDuration(500);
                        animationHide.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                currentInstructionAnimation = animationHide;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                appName.setText("");
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        animationHide.start();
                    }
                }, 10000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animationShow.start();
    }

    @Override
    public Context getContext() {
        return LoginActivity.this;
    }

    @Override
    public void showProgressBar() {
        login_progress_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        login_progress_bar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void openChatHome() {
        startActivity(new Intent(LoginActivity.this, ChatActivity.class));
        finish();
    }

    @Override
    public void onCredentialsFailure() {
        displayLogInFailure(getString(R.string.login_failure));
    }

    private void displayLogInFailure(String title) {
        Snackbar.make(login_relative_layout, title, Snackbar.LENGTH_LONG)
                .show();
    }

    @OnClick(R.id.label_forgot_password)
    public void onViewClicked() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        if (ViewUtils.checkEmailValidity(username_login) ||
                username_login.getText().toString().equals("")) {
            intent.putExtra(EMAIL_ADDRESS, username_login.getText().toString());
            startActivity(intent);
        }
        else {
            username_login.setError(getString(R.string.invalid_email));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
