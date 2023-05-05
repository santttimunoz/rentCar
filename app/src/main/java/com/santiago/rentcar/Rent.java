package com.santiago.rentcar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rent extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText etRentNumber, etUserName, etPlateNumber, etDate;
    Button btnrSave, btnrCars, btnrUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        etRentNumber = findViewById(R.id.etrRentNumber);
        etUserName = findViewById(R.id.etrUserName);
        etPlateNumber = findViewById(R.id.etrPlateNumber);
        etDate = findViewById(R.id.etrDate);
        btnrSave = findViewById(R.id.btnrSave);
        btnrCars = findViewById(R.id. btnrCars);
        btnrUsers = findViewById(R.id.btnrUsers);

        btnrCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Cars.class));
            }
        });

        btnrUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


       btnrSave.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (!etRentNumber.getText().toString().isEmpty() && !etUserName.getText().toString().isEmpty()) {

                   Query queryUsers = db.collection("users")
                           .whereEqualTo("Username", etUserName.getText().toString());

                   Query queryCars = db.collection("cars")
                           .whereEqualTo("PlateNumber", etPlateNumber.getText().toString());

                   Query queryCarsable = db.collection("cars")
                           .whereEqualTo("State", "Disable").whereEqualTo("PlateNumber", etPlateNumber.getText().toString());


                   Task<List<QuerySnapshot>> combinedTask = Tasks.whenAllSuccess(queryUsers.get(), queryCars.get(), queryCarsable.get());

                   combinedTask.addOnCompleteListener(task -> {
                       if(task.isSuccessful()){
                           List<QuerySnapshot> querySnapshots = task.getResult();
                           QuerySnapshot usersSnapshot = querySnapshots.get(0);
                           QuerySnapshot carssSnapshot = querySnapshots.get(1);
                           QuerySnapshot carableSnapshot = querySnapshots.get(2);


                           if(usersSnapshot.isEmpty() || carssSnapshot.isEmpty()){
                               Toast.makeText(getApplicationContext(), "usuario o carro no existen", Toast.LENGTH_SHORT).show();

                           }
                           //si la consulta encuentra algo entra en esta condicion
                           else if(!carableSnapshot.isEmpty()){
                               Toast.makeText(getApplicationContext(), "carro no disponible", Toast.LENGTH_SHORT).show();
                           }

                           else{
                               db.collection("rents")
                                       .whereEqualTo("rentNumber", etRentNumber.getText().toString())
                                       .get()
                                       .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   if (task.getResult().isEmpty()) {
                                                       // No Encontró el documento con el username específico
                                                       // Create a new user with a first and last name
                                                       Map<String, Object> rent = new HashMap<>();
                                                       rent.put("rentNumber", etRentNumber.getText().toString());
                                                       rent.put("userName", etUserName.getText().toString());
                                                       rent.put("plateNumber", etPlateNumber.getText().toString());
                                                       rent.put("date", etDate.getText().toString());

                                                       // Add a new document with a generated ID
                                                       db.collection("rents")
                                                               .add(rent)
                                                               .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                   @Override
                                                                   public void onSuccess(DocumentReference documentReference) {
                                                                       Toast.makeText(getApplicationContext(), "rent creado correctamente... ", Toast.LENGTH_SHORT).show();
                                                                       etRentNumber.setText("");
                                                                       etUserName.setText("");
                                                                       etPlateNumber.setText("");
                                                                       etDate.setText("");
                                                                   }
                                                               })
                                                               .addOnFailureListener(new OnFailureListener() {
                                                                   @Override
                                                                   public void onFailure(@NonNull Exception e) {
                                                                       Toast.makeText(getApplicationContext(), "Error al crear el rent: " + e, Toast.LENGTH_SHORT).show();
                                                                   }
                                                               });

                                                   } else {
                                                       Toast.makeText(getApplicationContext(), "rent Existente. Inténelo con otro ...", Toast.LENGTH_SHORT).show();
                                                   }
                                               }

                                           }
                                       });
                           }

                       }
                   });

               }
           }
       });



    }

}