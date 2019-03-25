package ua.pt.eventua.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ua.pt.eventua.Constants;
import ua.pt.eventua.Entities.Person;
import ua.pt.eventua.R;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    // UI references.
    private AutoCompleteTextView mNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mRepitPasswordView;
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";
    private static final int SELECT_PHOTO = 100;
    private ImageView chooseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNameView = (AutoCompleteTextView) findViewById(R.id.register_name);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mRepitPasswordView = (EditText) findViewById(R.id.register_repit_password);

        mAuth = FirebaseAuth.getInstance();

        Button regist = (Button) findViewById(R.id.register_btn_confirm);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        chooseImage=(ImageView) findViewById(R.id.imageView2);
        Button pickImage = (Button) findViewById(R.id.pickImage);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage(v);
            }
        });
        // Don't show keyboard on beginning
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void ChooseImage(View v){
        openGallery();
    }

    private void openGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    if(selectedImage !=null){
                        chooseImage.setImageURI(selectedImage);
                    }
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }


    private void attemptRegister() {

        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRepitPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String name = mNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String repit_password = mRepitPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }else{
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }else{
                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password)) {
                    mPasswordView.setError(getString(R.string.error_field_required));
                    focusView = mPasswordView;
                    cancel = true;
                }else if (!isPasswordValid(password)){
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                    focusView = mPasswordView;
                    cancel = true;
                }else{
                    if (TextUtils.isEmpty(repit_password)) {
                        mRepitPasswordView.setError(getString(R.string.error_field_required));
                        focusView = mRepitPasswordView;
                        cancel = true;
                    }else if (!password.equals(repit_password)){
                        mRepitPasswordView.setError("Passwords must be equal");
                        focusView = mRepitPasswordView;
                        cancel = true;
                }
            }

        }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                mFirebaseUser = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Register.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference().child("global_users");
            Query query = mRef.orderByKey().limitToLast(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        int val = -1;
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            val = Integer.parseInt(child.getKey());
                        }
                        val++;
                        Constants.USER_MAIL=email;
                        DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference().child("global_users").child(""+val);
                        mRef.child("name").setValue(name);
                        mRef.child("email").setValue(email);
                        mRef.child("events").setValue("");

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        SharedPreferences sharedPref = Register.this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("current_image", Base64.encodeToString(bos.toByteArray(),0));
                        editor.putString("current_username",name);
                        editor.putString("current_email",email);
                        editor.putInt("current_id", val);
                        editor.commit();

                        String pw_enc = null;
                        try {
                            pw_enc = encrypt(password);
                            mRef.child("password").setValue(pw_enc);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(Register.this, pw_enc,
                        //       Toast.LENGTH_SHORT).show();


                        ((BitmapDrawable)chooseImage.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG,100,bos);
                        mRef.child("zimage").setValue(Base64.encodeToString(bos.toByteArray(),0));

                        final Person p = new Person(val,
                                name, " ",
                                Base64.encodeToString(bos.toByteArray(),0));
                        p.setEvents("");
                        p.setMail(email);

                        //guardar utilizador a usar o sistema
                        Constants.CURRENT_PERSON = p;

                        startActivity(new Intent(Register.this, Welcome.class));

                    

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //int val = Integer.parseInt(""+mRef.orderByKey().limitToLast(1).getRef()) + 1;
            /*
            mRef =  FirebaseDatabase.getInstance().getReference().child("users").child(mRef.orderByKey().limitToLast(1).toString()+"..");
            mRef.child("name").setValue(name);
            mRef.child("email").setValue(email);
            mRef.child("password").setValue(password);
            startActivity(new Intent(Register.this, Welcome.class));
            */
        }
    }

    public static String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(Register.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(Register.KEY.getBytes(),Register.ALGORITHM);
        return key;
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.endsWith("@ua.pt");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }
}
