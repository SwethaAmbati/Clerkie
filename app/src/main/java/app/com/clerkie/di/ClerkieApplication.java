package app.com.clerkie.di;

import android.app.Application;

import app.com.clerkie.di.component.ApplicationComponent;
import app.com.clerkie.di.component.DaggerApplicationComponent;
import app.com.clerkie.di.module.AppModule;

public class ClerkieApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}