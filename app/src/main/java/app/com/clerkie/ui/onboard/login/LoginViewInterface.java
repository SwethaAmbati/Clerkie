package app.com.clerkie.ui.onboard.login;

import app.com.clerkie.ui.onboard.ViewInterface;

public interface LoginViewInterface extends ViewInterface {

    void showProgressBar();

    void hideProgressBar();

    void openChatHome();

    void onCredentialsFailure();
}
