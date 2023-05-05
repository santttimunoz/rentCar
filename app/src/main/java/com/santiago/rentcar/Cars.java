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


public class Cars extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText etPlateNumber, etBrand;
    RadioButton rbAble, rbDisable;
    Button btncSave, btncUsers, btncRent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        etPlateNumber = findViewById(R.id.etcPlateNumber);
        etBrand = findViewById(R.id.etcBrand);
        rbAble = findViewById(R.id.rbAble);
        rbDisable = findViewById(R.id.rbDisable);
        btncSave = findViewById(R.id.btncSave);
        btncRent = findViewById(R.id.btncRent);
        btncUsers = findViewById(R.id.btncUsers);

        btncRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Rent.class));
            }
        });
        btncUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


        btncSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar que el nombre del usuario se haya digitado
                if (!etPlateNumber.getText().toString().isEmpty() && !etBrand.getText().toString().isEmpty()) {
                    // Búsqueda del usuario en la colección users
                    db.collection("cars")
                            .whereEqualTo("PlateNumber", etPlateNumber.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            // No Encontró el documento con el username específico
                                            // Create a new user with a first and last name
                                            Map<String, Object> car = new HashMap<>();
                                            car.put("PlateNumber", etPlateNumber.getText().toString());
                                            car.put("Brand", etBrand.getText().toString());
                                            car.put("State", rbAble.isChecked() ? "Able" : "Disable");

                                            // Add a new document with a generated ID
                                            db.collection("cars")
                                                    .add(car)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getApplicationContext(), "Vehiculo  creado correctamente... ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "Error al crear el vehiculo: " + e, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Vehiiculo Existente. Inténelo con otro ...", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Debe ingresar todos los datos ...", Toast.LENGTH_SHORT).show();
                }


            }

        });
    }
}
