package com.doubleclick.chatappnew.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.doubleclick.chatappnew.Adapters.GroupMessagesAdapter;
import com.doubleclick.chatappnew.Models.Message;
import com.doubleclick.chatappnew.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;

    GroupMessagesAdapter adapter;
    ArrayList<Message> messages;

    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog dialog;

    String senderUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("Group Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        senderUid = FirebaseAuth.getInstance().getUid();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        messages = new ArrayList<>();
        adapter = new GroupMessagesAdapter(this, messages);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        binding.recyclerView.setHasFixedSize(true);
//        binding.recyclerView.scrollToPosition(messages.size());
        binding.recyclerView.setAdapter(adapter);

//        database.getReference().child("public").get()
//                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DataSnapshot> task) {
//                        messages.clear();
//                        for (DataSnapshot snapshot1 : task.getResult().getChildren()) {
//                            Message message = snapshot1.getValue(Message.class);
//                            message.setMessageId(snapshot1.getKey());
//                            messages.add(message);
//                        }
//
//                        adapter.notifyDataSetChanged();
//
//                    }
//                });

//        FileInputStream serviceAccount =
//                null;
//        try {
//            serviceAccount = new FileInputStream("path/to/serviceAccountKey.json");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setDatabaseUrl("https://chatappnew-b18a4-default-rtdb.firebaseio.com")
//                .build();
//
//        FirebaseApp.initializeApp(options);
        database.getReference().child("public").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Message message = snapshot1.getValue(Message.class);
                    message.setMessageId(snapshot1.getKey());
                    messages.add(message);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.sendBtn.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        String messageTxt = binding.messageBox.getText().toString();

        Date date = new Date();
        Message message = new Message(messageTxt, senderUid, date.getTime());
        binding.messageBox.setText("");

        database.getReference().child("public")
                .push()
                .setValue(message);
    }
    });

        binding.attachment.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 25);
    }
    });
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();

                                        String messageTxt = binding.messageBox.getText().toString();

                                        Date date = new Date();
                                        Message message = new Message(messageTxt, senderUid, date.getTime());
                                        message.setMessage("photo");
                                        message.setImageUrl(filePath);
                                        binding.messageBox.setText("");

                                        database.getReference().child("public")
                                                .push()
                                                .setValue(message);
                                        //Toast.makeText(ChatActivity.this, filePath, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}