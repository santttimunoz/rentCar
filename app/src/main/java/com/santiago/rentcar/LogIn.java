package com.santiago.rentcar;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class LogIn extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText user, password;
    TextView forgotPass, singUp;
    Button btnLogin;
    String rol = "Admin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        user = findViewById(R.id.etUserlg);
        password = findViewById(R.id.etPasswordlg);
        forgotPass = findViewById(R.id.tvForgotpass);
        singUp = findViewById(R.id.tvSingup);
        btnLogin = findViewById(R.id.btnLogin);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
            }
        });

        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){

                    Query queryUser = db.collection("users").whereEqualTo("Username", user.getText().toString());
                    Query queryPass = db.collection("users").whereEqualTo("Password", password.getText().toString());
                    Query queryRolAd = db.collection("users").whereEqualTo("Rol", "Admin")
                            .whereEqualTo("Username", user.getText().toString());
                    Query queryRolUs = db.collection("users").whereEqualTo("Rol", "User")
                            .whereEqualTo("Username", user.getText().toString());

                    Task<List<QuerySnapshot>> combinedTask = Tasks.whenAllSuccess(queryUser.get(), queryPass.get(), queryRolAd.get(), queryRolUs.get());
                    combinedTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            List<QuerySnapshot> querysResult = task.getResult();
                            QuerySnapshot resultUser = querysResult.get(0);
                            QuerySnapshot resultPass = querysResult.get(1);
                            QuerySnapshot resultRolAd= querysResult.get(2);
                            QuerySnapshot resultRolUs = querysResult.get(3);

                            if(!resultUser.isEmpty() && !resultPass.isEmpty() && !resultRolAd.isEmpty()){

                                Toast.makeText(LogIn.this, "Bienvenido admin", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Cars.class));

                            }else if(!resultUser.isEmpty() && !resultPass.isEmpty() && !resultRolUs.isEmpty()){

                                Toast.makeText(LogIn.this, "Bienvenido User", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Rent.class));


                            }else{
                                Toast.makeText(LogIn.this, "Usuario o contrasena incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LogIn.this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}