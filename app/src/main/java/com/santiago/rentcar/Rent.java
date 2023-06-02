package com.santiago.rentcar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rent extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText etrEndDate, etrFirstDate;
    Button btnrSave, btnrCars;


    Spinner spinnerRent;

    //estas dos variables deben ser globales para que se puedan hacer las validaciones en los metodos mostrarDate1 y mostrarDate2
    Calendar selectedDate1, selectedDate2;

    TextView logOut, listAble;

    String rentNumber = "0" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);


        spinnerRent = findViewById(R.id.spinnerRent);
        etrFirstDate = findViewById(R.id.etrFirstDate);
        etrEndDate = findViewById(R.id.etrEndDate);
        btnrSave = findViewById(R.id.btnrSave);
        btnrCars = findViewById(R.id. btnrCars);
        logOut = findViewById(R.id.tvLogout);
        listAble = findViewById(R.id.tvList);

        btnrCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Cars.class));
            }
        });



        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });

        listAble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CarsList.class));
            }
        });


        //spinner para mostrar las placas(valores traidos desde firebase)
        db.collection("cars").whereEqualTo("State", "Able")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> opciones = new ArrayList<>();
                //agrega una opcion vacia al spinner
                opciones.add("");
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String opcion = documentSnapshot.getString("PlateNumber");
                    opciones.add(opcion);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Rent.this,
                        android.R.layout.simple_spinner_item, opciones);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerRent.setAdapter(adapter);

                //selecciona la primera opvion(en este vacion por opciones.add("");) del spinner
                spinnerRent.setSelection(0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el error si ocurre
            }
        });

        etrFirstDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDate1();
            }
        });

        etrEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDate2();
            }
        });


    btnrSave.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //con los spinners no se utilisa .getText() sino .getSelectedItem()
            if(!spinnerRent.getSelectedItem().toString().isEmpty() && !etrFirstDate.getText().toString().isEmpty()  && !etrEndDate.getText().toString().isEmpty()){

                db.collection("rents")
                        .whereEqualTo("plateNumber", spinnerRent.getSelectedItem().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                               if(task.isSuccessful()){
                                   QuerySnapshot queryS = task.getResult();
                                   if(task.getResult().isEmpty()){// si esta vacio el getResult es porque no se encontro alguna consulta y guarda los datos

                                       //campo autoincremental
                                       rentNumber = String.valueOf(Integer.parseInt(rentNumber) + 1);
                                       Map<String, Object> rent = new HashMap<>();
                                       rent.put("plateNumber", spinnerRent.getSelectedItem().toString());
                                       rent.put("rentDay", etrFirstDate.getText().toString());
                                       rent.put("returnDay", etrEndDate.getText().toString());
                                       rent.put("rentNumber", rentNumber);


                                       db.collection("rents")
                                               .add(rent)
                                               .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<DocumentReference> task) {
                                                       Toast.makeText(Rent.this, "Renta realizada exitosamente", Toast.LENGTH_SHORT).show();
                                                   }
                                               });

                                       //actualizar el stado del carro en la colleccion cars


                                   }else{
                                       Toast.makeText(Rent.this, "Vehiculo esta rentado, intente con otro", Toast.LENGTH_SHORT).show();
                                   }
                               }
                            }
                        });
            }else{
                Toast.makeText(Rent.this, "For favor llene todos los campos", Toast.LENGTH_SHORT).show();
            }

        }
    });







    }
    public void mostrarDate1() {
        // de esta forma se Obtiene la fecha actual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        //variable global(obetencion de valor global para usarse en el metodo mostrarDate2)
        selectedDate1 = Calendar.getInstance();

        // Crea una instancia del DatePickerDialog

        DatePickerDialog datePickerDialog1 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDayOfMonth) {

                selectedDate1.set(selectedYear, selectedMonth, selectedDayOfMonth);

                // Verifica si la fecha seleccionada es menor a la fecha actual
                if (selectedDate1.before(calendar)) {
                    // La fecha seleccionada es menor a la fecha actual, muestra un mensaje de error o realiza alguna acción
                    Toast.makeText(getApplicationContext(), "La fecha seleccionada no puede ser menor a la fecha actual", Toast.LENGTH_SHORT).show();
                    etrFirstDate.setText("");
                }else{
                   String fechaSeleccionada1 = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;

                    etrFirstDate.setText(fechaSeleccionada1);



                }
            }
        }, year, month, dayOfMonth);

        datePickerDialog1.show();

    }
    public void mostrarDate2() {
        // de esta forma se Obtiene la fecha actual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

         //variable global
         selectedDate2 = Calendar.getInstance();

        // Crea una instancia del DatePickerDialog

        DatePickerDialog datePickerDialog1 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDayOfMonth) {

                selectedDate2.set(selectedYear, selectedMonth, selectedDayOfMonth);

                // Verifica si la fecha seleccionada es menor a la fecha actual
                if(selectedDate2.before(calendar)){
                    Toast.makeText(getApplicationContext(), "La fecha seleccionada no puede ser menor a la fecha actual", Toast.LENGTH_SHORT).show();
                }
                //la variable selectedDate1 tiene obtiene su valor del primer metodo(mostrarDate1)
                else if(selectedDate2.before(selectedDate1)) {
                    // La fecha seleccionada es menor a la fecha actual, muestra un mensaje de error o realiza alguna acción
                    Toast.makeText(getApplicationContext(), "La fecha seleccionada no puede ser menor a la fecha inicial", Toast.LENGTH_SHORT).show();
                    etrEndDate.setText("");
                }else{
                    String fechaSeleccionada2= selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;

                    etrEndDate.setText(fechaSeleccionada2);

                }
            }
        }, year, month, dayOfMonth);

        datePickerDialog1.show();

    }
}