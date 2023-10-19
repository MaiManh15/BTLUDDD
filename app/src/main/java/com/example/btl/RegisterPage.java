package com.example.btl;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage extends AppCompatActivity {

    private EditText username, password, confirmPass;
    private Button signUpBtn;

    private TextView signin;

    private AlertDialog.Builder dialog;
    private AlertDialog OptionDialog;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        fAuth = FirebaseAuth.getInstance();
        dialog = new AlertDialog.Builder(this);
        OptionDialog = dialog.create();


        register();
    }

    private void register(){
        username = findViewById(R.id.SignUpPage_un);
        password = findViewById(R.id.SignUpPage_pw);
        confirmPass = findViewById(R.id.SignUpPage_cp);
        signUpBtn = findViewById(R.id.SignUpPage_SUbtn);
        signin = findViewById(R.id.SignUpPage_SignIn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String SUusername = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String cfpass = confirmPass.getText().toString().trim();

                if(TextUtils.isEmpty(SUusername)){
                    username.setError("yêu cầu email!");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    password.setError("yêu cầu mật khẩu!");
                    return;
                }

                if(pass.equals(cfpass)){
                    OptionDialog.setMessage("Đang chạy..");
                    OptionDialog.show();
                    fAuth.createUserWithEmailAndPassword(SUusername, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                OptionDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                FirebaseUser mUser=fAuth.getCurrentUser();
                                String uid= mUser.getUid();
                                String uEmail= mUser.getEmail();
                                FirebaseDatabase.getInstance().getReference().child("UserID").child(uid).setValue(uEmail);
                                startActivity(new Intent(getApplicationContext(), MainPage.class));
                            }else {
                                OptionDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Đăng ký thất bại..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else if(TextUtils.isEmpty(cfpass)){
                    confirmPass.setError("yêu cầu nhập lại mật khẩu!");
                }else {
                    confirmPass.setError("mật khẩu không khớp!");
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });
    }
}