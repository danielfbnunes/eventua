package ua.pt.eventua.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ua.pt.eventua.Activities.LoginActivity;
import ua.pt.eventua.Adapters.AllEventsAdapter;
import ua.pt.eventua.Constants;
import ua.pt.eventua.R;
import ua.pt.eventua.Activities.Welcome;


public class Chat extends Fragment {

    protected FragmentActivity mActivity;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    String current_message = "";
    String send = Welcome.current_user;
    String rec = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        final Toolbar toolbar = (Toolbar)  mActivity.findViewById(R.id.toolbar);
        ((AppCompatActivity)mActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        View view = inflater.inflate(R.layout.chat_room, container, false);

        layout = (LinearLayout) view.findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)view.findViewById(R.id.layout2);
        sendButton = (ImageView)view.findViewById(R.id.sendButton);
        messageArea = (EditText)view.findViewById(R.id.messageArea);
        scrollView = (ScrollView)view.findViewById(R.id.scrollView);


        return view;
    }

    public Chat initializeData(String receiver){
        this.rec = receiver;
        return this;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        mActivity.setTitle(getString(R.string.title_chat));


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    current_message = messageText;
                    saveMessage(send, rec, messageText);
                    messageArea.setText("");
                }
            }
        });

        String[] test = new String[] {send, rec};
        Arrays.sort(test);
        final String u1 = test[0];
        final String u2 = test[1];
        DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference().child("events").child(Constants.CURRENT_EVENT_ID).child("event_messages").child(u1 + "_" + u2);
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    try{
                        HashMap<String, String> mapa = (HashMap<String, String>) dataSnapshot.getValue();

                        if (mapa.size() == 3) {
                            if (mapa.get("sender").equals(send))
                                addMessageBox(mapa.get("sender") + ":\n\t" + mapa.get("message"), 0);
                            else
                                addMessageBox(mapa.get("sender") + ":\n\t" + mapa.get("message"), 1);
                        }
                    }catch (Exception e){}
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                HashMap<String, String> mapa = (HashMap<String, String>) dataSnapshot.getValue();
                if (dataSnapshot.getValue() != null) {
                    if (mapa.size() == 3){
                        if (mapa.get("sender").equals(send))
                            addMessageBox(mapa.get("sender") + ":\n\t" + mapa.get("message"), 0);
                        else
                            addMessageBox(mapa.get("sender") + ":\n\t" + mapa.get("message"), 1);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(mActivity);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
        }
        else{
            lp2.gravity = Gravity.RIGHT;
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }


    public void saveMessage(final String sender, final String receiver, final String message){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mFirebaseUser = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        String[] test = new String[] {sender, receiver};
        Arrays.sort(test);
        final String final_sender = sender;
        final String final_rec = receiver;
        final String u1 = test[0];
        final String u2 = test[1];

        DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference().child("events").child(Constants.CURRENT_EVENT_ID).child("event_messages").child(u1+"_"+u2);
        Query query = mRef.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                int val = -1;
                for (com.google.firebase.database.DataSnapshot child: dataSnapshot.getChildren()) {
                    val = Integer.parseInt(child.getKey());
                }
                val++;
                DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference().child("events").child(Constants.CURRENT_EVENT_ID).child("event_messages").child(u1+"_"+u2).child(""+val);
                mRef.child("sender").setValue(final_sender);
                mRef.child("receiver").setValue(final_rec);
                mRef.child("message").setValue(message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("global_users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot d : dataSnapshot.getChildren())
                            {
                                try {
                                    if(d.child("name").getValue().toString().equals(final_rec)){
                                        final int receiver_id = Integer.parseInt(d.getKey().toString());
                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("global_users").child(d.getKey().toString()).child("messages");
                                        Query x = db.orderByKey().limitToLast(1);
                                        x.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                                int val = -1;
                                                for (com.google.firebase.database.DataSnapshot child : dataSnapshot.getChildren()) {
                                                    val = Integer.parseInt(child.getKey());
                                                }
                                                val++;
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("global_users").child(""+receiver_id).child("messages").child(""+val);
                                                db.child("event").setValue(Constants.CURRENT_EVENT_NAME);
                                                db.child("from").setValue(sender);
                                                db.child("message").setValue(message);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

}