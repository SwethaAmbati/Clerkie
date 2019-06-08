package app.com.clerkie.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

        private String userName;
        private String userEmailId;

        public String getNameOfUser() {
        return userName;
    }

        public void setNameOfUser(String userName) {
        this.userName = userName;
    }

        public String getUserEmailId() {
        return userEmailId;
    }

        public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }


        @Override
        public int describeContents() {
        return 0;
    }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.userEmailId);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.userName = in.readString();
        this.userEmailId = in.readString();
    }

        public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
            @Override
            public User createFromParcel(Parcel source) {
                return new User(source);
            }

            @Override
            public User[] newArray(int size) {
                return new User[size];
            }
        };
    }