package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {
    private EditText username;
    private Button resetBtn;

    private TextView signin;

    private AlertDialog.Builder dialog;
    private AlertDialog OptionDialog;

    private FirebaseAuth fAuth;
    private String SUusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        fAuth = FirebaseAuth.getInstance();
        dialog = new AlertDialog.Builder(this);
        OptionDialog = dialog.create();


        forgotPass();
    }

    private void forgotPass(){
        username = findViewById(R.id.SignUpPage_un);
        resetBtn = findViewById(R.id.SignUpPage_SUbtn);
        signin = findViewById(R.id.SignUpPage_SignIn);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SUusername = username.getText().toString().trim();

                if(TextUtils.isEmpty(SUusername)){
                    username.setError("yêu cầu email!");
                    return;
                }else {
                    resetPassword();
                }

                OptionDialog.setMessage("Đang chạy..");
                OptionDialog.show();

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });
    }
    private void resetPassword(){

        fAuth.sendPasswordResetEmail(SUusername).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    OptionDialog.cancel();
                    Toast.makeText(getApplicationContext(), "Đường dẫn đặt lại mật khẩu đã được gửi đến email của bạn!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                }else {
                    OptionDialog.cancel();
                    Toast.makeText(getApplicationContext(), "Gửi đường dẫn thất bại..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}