package app.com.clerkie.ui.onboard;

public interface Presenter<T extends ViewInterface> {

    void attachView(T t);

    void detachView();
}
