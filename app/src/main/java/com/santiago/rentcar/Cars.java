package com.santiago.rentcar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Cars extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText etPlateNumber, etBrand, etDvalue;
    RadioButton rbAble, rbDisable;
    Button btncSave, btnDelete, btnUpdate, btnSearch, btnReturn, btnRent;

    TextView logOut, listCars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        etPlateNumber = findViewById(R.id.etcPlateNumber);
        etBrand = findViewById(R.id.etcBrand);
        etDvalue = findViewById(R.id.etDvalue);
        rbAble = findViewById(R.id.rbAble);
        rbDisable = findViewById(R.id.rbDisable);
        btncSave = findViewById(R.id.btncSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSearch = findViewById(R.id.btnSearch);
        btnRent = findViewById(R.id.btnRent);
        logOut = findViewById(R.id.tvLogout);
        listCars = findViewById(R.id.tvListCars);
        btnReturn = findViewById(R.id.btnReturn);

        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Rent.class));
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ReturnCar.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });

        listCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CarsList.class));
            }
        });


        btncSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar que el nombre del usuario se haya digitado
                if (!etPlateNumber.getText().toString().isEmpty() && !etBrand.getText().toString().isEmpty() && !etDvalue.getText().toString().isEmpty()) {
                    // Búsqueda del usuario en la colección users
                    db.collection("cars")
                            .whereEqualTo("PlateNumber", etPlateNumber.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot queryS = task.getResult();
                                        if (task.getResult().isEmpty()) {
                                            // No Encontró el documento con el username específico
                                            // Create a new user with a first and last name
                                            Map<String, Object> car = new HashMap<>();
                                            car.put("PlateNumber", etPlateNumber.getText().toString());
                                            car.put("Brand", etBrand.getText().toString());
                                            car.put("State", rbAble.isChecked() ? "Able" : "Disable");
                                            car.put("DailyValue", etDvalue.getText().toString());

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
                                            Toast.makeText(getApplicationContext(), "Vehiculo Existente. Inténelo con otro ...", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Debe ingresar todos los datos ...", Toast.LENGTH_SHORT).show();
                }


            }

        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference deleteCar = db.collection("cars");

                Query query = deleteCar.whereEqualTo("PlateNumber", etPlateNumber.getText().toString())
                        .whereEqualTo("Brand", etBrand.getText().toString());

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                String UserId = querySnapshot.getDocuments().get(0).getId();
                                db.collection("cars").document(UserId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Cars.this, "Documento borrado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Cars.this, "Error al borrar", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    }
                });

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etPlateNumber.getText().toString().isEmpty() && !etBrand.getText().toString().isEmpty() && !etDvalue.getText().toString().isEmpty()) {

                    CollectionReference cars = db.collection("cars");
                    String idUser = cars.getId();

                    Query query = cars.whereEqualTo("PlateNumber", etPlateNumber.getText().toString());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    // Obtener el primer documento que cumpla con los criterios de búsqueda
                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                                    // Crear un objeto Map con los campos a actualizar y sus nuevos valores
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("PlateNumber", etPlateNumber.getText().toString());
                                    updates.put("Brand", etBrand.getText().toString());
                                    updates.put("DailyValue", etDvalue.getText().toString());
                                    updates.put("State", rbAble.isChecked() ? "Able" : "Disable");

                                    // Actualizar los campos del documento
                                    document.getReference().update(updates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Actualización exitosa
                                                        Toast.makeText(getApplicationContext(), "Documento actualizado correctamente", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Ocurrió un error al actualizar el documento
                                                        Toast.makeText(getApplicationContext(), "Error al actualizar el documento", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }


                            }
                        }
                    });

                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etPlateNumber.getText().toString().isEmpty()){
                    // Búsqueda del usuario en la colección users
                    db.collection("cars")
                            .whereEqualTo("PlateNumber",etPlateNumber.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (!task.getResult().isEmpty()){
                                            // Encontró el documento con el username específico
                                            for (QueryDocumentSnapshot document : task.getResult()){

                                                // Asignar el comtenido de cada campo a su control respectivo
                                                etBrand.setText(document.getString("Brand"));
                                                etDvalue.setText(document.getString("DailyValue"));
                                               Query state = db.collection("cars").whereEqualTo("State", rbAble.isChecked()? "Able" : "Disable");
                                               if(state.toString() == "Able"){
                                                   rbAble.setChecked(true);
                                               }else{
                                                   rbDisable.setChecked(true);
                                               }

                                            }
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"vehiculo NO existe. Inténelo con otro...",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            });
                }
            }
        });
    }
}
