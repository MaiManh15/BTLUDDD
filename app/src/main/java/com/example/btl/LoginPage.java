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

public class LoginPage extends AppCompatActivity {


    protected EditText username, password;
    protected Button LogInBtn;
    protected TextView fgpass, signUp;
    private AlertDialog.Builder dialog;
    private AlertDialog OptionDialog;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainPage.class));
        }

        dialog = new AlertDialog.Builder(this);
        OptionDialog = dialog.create();

        Login();

    }

    public void Login(){
        username= findViewById(R.id.LoginPage_un);
        password= findViewById(R.id.LoginPage_pw);
        LogInBtn= findViewById(R.id.LoginPage_LIbtn);
        fgpass = findViewById(R.id.LoginPage_FGPW);
        signUp = findViewById(R.id.LoginPage_SU);

        LogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String LIusername = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if(TextUtils.isEmpty(LIusername)){
                    username.setError("yêu cầu email!");
                    return;
                }
                if(pass.length()<6){
                    password.setError("mật khẩu phải lớn hơn hoặc bằng 6 ký tự!");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    password.setError("yêu cầu mật khẩu!");
                    return;
                }
                OptionDialog.setMessage("Đang chạy..");
                OptionDialog.show();

                fAuth.signInWithEmailAndPassword(LIusername, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            OptionDialog.cancel();
                            startActivity(new Intent(getApplicationContext(), MainPage.class));
                            Toast.makeText(getApplicationContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainPage.class));
                        }else {
                            OptionDialog.cancel();
                            Toast.makeText(getApplicationContext(), "email hoặc mật khẩu không đúng..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterPage.class));
            }
        });

        fgpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPass.class));
            }
        });
    }

}