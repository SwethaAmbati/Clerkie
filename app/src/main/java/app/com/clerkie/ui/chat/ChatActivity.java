package app.com.clerkie.ui.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import app.com.clerkie.R;
import app.com.clerkie.model.Message;
import app.com.clerkie.model.User;
import app.com.clerkie.ui.chart.ChartActivity;
import app.com.clerkie.ui.onboard.login.LoginActivity;
import app.com.clerkie.utils.RandomSentenceGenerator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chat_constraint_layout)
    ConstraintLayout chatRelativeLayout;
    @BindView(R.id.chatRecyclerView)
    RecyclerView chatRecyclerView;
    @BindView(R.id.chatProgressBar)
    ProgressBar chatProgressBar;
    @BindView(R.id.messageEditText)
    MaterialEditText messageEditText;
    @BindView(R.id.photoPickerButton)
    ImageButton photoPickerButton;
    @BindView(R.id.sendMessageButton)
    ImageButton sendMessageButton;
    @BindView(R.id.label_bot_typing)
    TextView labelBotTyping;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, databaseUserReference;
    private ChildEventListener childEventListener;

    String botReply = "";
    private String currentUsername, userId;
    private static final String ANONYMOUS = "Anonymous";

    private static final int RC_PHOTO_PICKER = 1;
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ChatRecyclerAdapter chatRecyclerAdapter = new ChatRecyclerAdapter();
    private ArrayList<Message> listOfMessages = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        ButterKnife.bind(this);

        currentUsername = ANONYMOUS;

        showProgress();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseUserReference = firebaseDatabase.getReference().child("user");
        storageReference = firebaseStorage.getReference().child("chat_media");

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    navigateToLogin();
                }
                else {
                    hideProgress();
                    databaseReference = databaseUserReference.child(user.getUid())
                            .child("messages");
                    databaseUserReference.child(user.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    User user = dataSnapshot.getValue(User.class);
                                    currentUsername = user.getNameOfUser();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w("Failure: ", "failed to read date from server",
                                            databaseError.toException());
                                }
                            });
                    userId = user.getUid();
                    attachDatabaseReadListener();
                }
            }
        };

        messageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendMessageButton.setEnabled(true);
                }
                else {
                    sendMessageButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        messageEditText.setFilters(new InputFilter[]{new InputFilter.
                LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
    }

    @OnClick({R.id.photoPickerButton, R.id.sendMessageButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.photoPickerButton:
                pickImageFromGallery();
                break;
            case R.id.sendMessageButton:
                sendChatMessage();
                break;
        }
    }





    private void sendChatMessage() {
        Message message = new Message();
        message.setUserId(userId);
        message.setMessageBody(messageEditText.getText().toString());
        message.setSenderName(currentUsername);
        message.setTimeStamp(message.getFormattedTime());
        message.setPhotoUrl(null);

        if (!messageEditText.getText().toString().isEmpty()) {
            databaseReference.push().setValue(message);
            labelBotTyping.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(this, R.string.string_empty_message, Toast.LENGTH_SHORT).show();
        }

        generateBotReply();

        messageEditText.setText("");
    }

    private void generateBotReply() {
        RandomSentenceGenerator randomSentenceGenerator = new RandomSentenceGenerator();
        randomSentenceGenerator.setUserInputText(messageEditText.getText().toString());

        botReply = randomSentenceGenerator.generateSentence(currentUsername);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setBotReply(botReply);
            }
        }, 1500);
    }

    private void setBotReply(String botReply) {
        labelBotTyping.setVisibility(View.GONE);
        Message message = new Message();
        message.setUserId("bot");
        message.setSenderName("Clerkie");
        message.setMessageBody(botReply);
        message.setPhotoUrl(null);
        message.setTimeStamp(message.getFormattedTime());
        databaseReference.push().setValue(message);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/* video/* gif/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser
                (intent, "Complete Action using "), RC_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            StorageReference photoRef = storageReference.child(selectedImageUri
                    .getLastPathSegment());

            // Upload file to firebase storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Message message = new Message();
                            message.setUserId(userId);
                            message.setMessageBody(null);
                            message.setSenderName(currentUsername);
                            message.setTimeStamp(message.getFormattedTime());
                            message.setPhotoUrl(downloadUrl.toString());
                            databaseReference.push().setValue(message);
                        }
                    });
        }
    }

    private void attachDatabaseReadListener() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.getValue(Message.class);
                listOfMessages.add(message);
                setUpRecyclerView(listOfMessages);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    private void detachDatabaseReadListener() {
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }


    private void setUpRecyclerView(ArrayList<Message> listOfMessages) {
        hideProgress();
        this.listOfMessages = listOfMessages;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        chatRecyclerAdapter.setMessagesList(this, userId, this.listOfMessages);
        chatRecyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setHasFixedSize(true);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                createLogoutDialog(R.string.logout);
                return true;
            case R.id.chart:
                navigateToChartScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToChartScreen() {
        startActivity(new Intent(this, ChartActivity.class));
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void createLogoutDialog(int title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(getString(R.string.are_you_sure));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.logout,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        signOut();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void signOut() {
        firebaseAuth.signOut();
        listOfMessages.clear();
        chatRecyclerAdapter.notifyDataSetChanged();
        detachDatabaseReadListener();
        navigateToLogin();
    }

    public void showProgress() {
        chatProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        chatProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachDatabaseReadListener();
        listOfMessages.clear();
        chatRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
