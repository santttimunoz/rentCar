package com.santiago.rentcar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ReturnCar extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText rcPlate, returnDate;

    Button save, btnRents, btnCars;

    TextView logOut;

    Spinner spinnerRc;

    Calendar selectedDate;

    String returCarNumber = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_car);

        rcPlate = findViewById(R.id.etRcPlateNumber);
        returnDate = findViewById(R.id.etRcDate);
        save = findViewById(R.id.btnSave);
        logOut = findViewById(R.id.tvLogout);
        spinnerRc = findViewById(R.id.spinnerRc);
        btnCars = findViewById(R.id.btnCars);
        btnRents = findViewById(R.id.btnRents);


        //codigo para realizar el spinner que muestra el numero de la renta
        db.collection("rents")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> opciones = new ArrayList<>();
                //agrega una opcion vacia al spinner
                    opciones.add("");
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    //areglar error en la linea siguiente
                    String opcion = documentSnapshot.getString("rentNumber");
                        opciones.add(opcion);

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ReturnCar.this,
                        android.R.layout.simple_spinner_item, opciones);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerRc.setAdapter(adapter);
                spinnerRc.setSelection(0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el error si ocurre
            }
        });//fin del codigo del spinner


        //evento que muestra el calendario con el metodo llamado mostrarDate()
        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDate();
            }
        });//fin del spinner

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });

        btnCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Cars.class));
            }
        });
        btnRents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Rent.class));
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!spinnerRc.getSelectedItem().toString().isEmpty() && !rcPlate.getText().toString().isEmpty() && !returnDate.getText().toString().isEmpty()){

                    Query query = db.collection("rents").whereEqualTo("rentNumber", spinnerRc.getSelectedItem().toString())
                            .whereEqualTo("plateNumber", rcPlate.getText().toString())
                            .whereEqualTo("returnDay", returnDate.getText().toString());

                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot queryS = task.getResult();

                                    if(!task.getResult().isEmpty()){

                                        //campo autoincremental
                                        returCarNumber = String.valueOf(Integer.parseInt(returCarNumber));

                                        Map<String, Object> returnCar = new HashMap<>();
                                        returnCar.put("rentNumber", spinnerRc.getSelectedItem().toString());
                                        returnCar.put("plateNumber", rcPlate.getText().toString());
                                        returnCar.put("returnDay", returnDate.getText().toString());
                                        returnCar.put("returnCarNumber", returCarNumber);
                                                db.collection("returnCars")
                                                        .add(returnCar)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Toast.makeText(ReturnCar.this, "Devolucion exitosa", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                        String deleteRent = queryS.getDocuments().get(0).getId();
                                        db.collection("rents")
                                                .document(deleteRent)
                                                .delete();

                                    }else{
                                        Toast.makeText(ReturnCar.this, "Los datos ingresados no existen", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        }
                    });

                }else{
                    Toast.makeText(ReturnCar.this, "Por favor llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    public void mostrarDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        selectedDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                selectedDate.set(year, month, dayOfMonth);

                if(selectedDate.before(calendar)){
                    Toast.makeText(ReturnCar.this, "la fecha seleccionada no puede ser inferior a la actual", Toast.LENGTH_SHORT).show();
                }else{
                    String fechaResultante = dayOfMonth + "/" + (month + 1) + "/" + year;
                    returnDate.setText(fechaResultante);
                }
            }
        }, year, month, dayOfMonth);

        datePickerDialog.show();

    }
}