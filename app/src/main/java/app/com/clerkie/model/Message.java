package app.com.clerkie.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Message implements Parcelable

    {

        private String userId;
        private String messageBody;
        private String senderName;
        private String photoUrl;
        private String timeStamp;

        public String getMessageBody() {
        return messageBody;
    }

        public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

        public String getSenderName() {
        return senderName;
    }

        public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

        public String getPhotoUrl() {
        return photoUrl;
    }

        public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

        public String getUserId() {
        return userId;
    }

        public void setUserId(String userId) {
        this.userId = userId;
    }

        public String getTimeStamp() {
        return timeStamp;
    }

        public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

        @Override
        public int describeContents() {
        return 0;
    }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.userId);
            parcel.writeString(this.messageBody);
            parcel.writeString(this.senderName);
            parcel.writeString(this.photoUrl);
            parcel.writeString(this.timeStamp);
    }

    public Message() {
        }

    protected Message(Parcel parcel) {
        this.userId = parcel.readString();
        this.messageBody = parcel.readString();
        this.senderName = parcel.readString();
        this.photoUrl = parcel.readString();
        this.timeStamp = parcel.readString();
    }

        public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
            @Override
            public Message createFromParcel(Parcel source) {
                return new Message(source);
            }

            @Override
            public Message[] newArray(int size) {
                return new Message[size];
            }
        };


        public String getFormattedTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date dateNow = Calendar.getInstance().getTime();
        return dateFormat.format(dateNow);
    }

        public String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        Date dateNow = Calendar.getInstance().getTime();
        return dateFormat.format(dateNow);
    }
}
