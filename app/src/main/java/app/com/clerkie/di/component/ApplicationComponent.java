package app.com.clerkie.di.component;

import javax.inject.Singleton;

import app.com.clerkie.di.module.AppModule;
import app.com.clerkie.ui.onboard.login.LoginActivity;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface ApplicationComponent {

    void injectLogin(LoginActivity loginActivity);
}
