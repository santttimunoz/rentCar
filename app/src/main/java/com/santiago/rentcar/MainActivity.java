package com.santiago.rentcar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText etUsername, etName, etPassword, etReservedw;
    Button btnSave, btnLogin;

    RadioButton rbUser, rbAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etUsername = findViewById(R.id.etUserName);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);
        etReservedw = findViewById(R.id.etReservedw);
        rbUser = findViewById(R.id.rbUser);
        rbAdmin = findViewById(R.id.rbAdmin);
        btnLogin = findViewById(R.id.btnLoginUs);

      Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etUsername.getText().toString().isEmpty() && !etName.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
                    // Búsqueda del usuario en la colección users
                    db.collection("users")
                            .whereEqualTo("Username", etUsername.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {

                                            Map<String, Object> user = new HashMap<>();
                                            user.put("Username", etUsername.getText().toString());
                                            user.put("Name", etName.getText().toString());
                                            user.put("Password", etPassword.getText().toString());
                                            user.put("ReservedWord", etReservedw.getText().toString());
                                            user.put("Rol", rbUser.isChecked() ? "User" : "Admin");

                                            db.collection("users")
                                                    .add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getApplicationContext(), "Usuario guardado", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "Error al guardar usuario" + e, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Username existente, intente otro...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
