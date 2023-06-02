package com.santiago.rentcar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CarsList extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btnBack;
    ListView listcarscl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_list);

        listcarscl = findViewById(R.id.listCars);

        db.collection("cars").whereEqualTo("State", "Able")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<String> itemList = new ArrayList<>();
                            itemList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String item = "Placa: " + document.getString("PlateNumber") + " | Rol: " + document.getString("State");
                                itemList.add(item);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(CarsList.this, android.R.layout.simple_list_item_1, itemList);
                            adapter.notifyDataSetChanged();
                            listcarscl.setAdapter(adapter);
                        }
                    }
                });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Cars.class));
            }
        });
    }
}