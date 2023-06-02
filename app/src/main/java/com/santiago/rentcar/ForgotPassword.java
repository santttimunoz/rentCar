package com.santiago.rentcar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText userFp, reservedWord, newPass;
    Button save;
    TextView logIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userFp = findViewById(R.id.Userfp);
        reservedWord = findViewById(R.id.Reservedw);
        newPass = findViewById(R.id.Newpass);
        save = findViewById(R.id.Savefp);
        logIn = findViewById(R.id.tvLogIn);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userFp.getText().toString().isEmpty() && !reservedWord.getText().toString().isEmpty() && !newPass.getText().toString().isEmpty()){

                    db.collection("users")
                            .whereEqualTo("Username", userFp.getText().toString())
                            .whereEqualTo("ReservedWord", reservedWord.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if(!task.getResult().isEmpty()){

                                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("Password", newPass.getText().toString());

                                            document.getReference().update(updates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                           if(task.isSuccessful()){
                                                             Toast.makeText(ForgotPassword.this, "Contrasena actualizada", Toast.LENGTH_SHORT).show();
                                                           }
                                                        }
                                                    });
                                        }else{
                                            Toast.makeText(ForgotPassword.this, "Usuario o palabra reservada incorrectos", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
}