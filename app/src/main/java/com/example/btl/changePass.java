package com.example.btl;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePass extends AppCompatActivity {

    private EditText oldPass, pass, rePass;
    private Button cancel, change;
    private FirebaseAuth fAuth;
    private AlertDialog.Builder dialog;
    private AlertDialog OptionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        fAuth = FirebaseAuth.getInstance();
        dialog = new AlertDialog.Builder(this);
        OptionDialog = dialog.create();

        changePass();
    }

    public void changePass(){
        oldPass=findViewById(R.id.change_pass_old_pw);
        pass = findViewById(R.id.change_pass_pw);
        rePass=findViewById(R.id.change_pass_cp);
        cancel=findViewById(R.id.button_cancel);
        change=findViewById(R.id.change_button);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword= oldPass.getText().toString().trim();
                String password = pass.getText().toString().trim();
                String rePassword= rePass.getText().toString().trim();
                if(TextUtils.isEmpty(oldPassword)){
                    oldPass.setError("yêu cầu mật khẩu cũ!");
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    pass.setError("yêu cầu mật khẩu mới!");
                    return;
                }
                if(TextUtils.isEmpty(rePassword)){
                    rePass.setError("yêu cầu nhập lại mật khẩu!");
                    return;
                }
                if(!password.equals(rePassword)){
                    rePass.setError("mật khẩu không khớp!");
                    return;
                }

                OptionDialog.setMessage("Đang chạy..");
                OptionDialog.show();

                FirebaseUser mUser=fAuth.getCurrentUser();

                AuthCredential authCredential= EmailAuthProvider.getCredential(mUser.getEmail(),oldPassword);
                mUser.reauthenticate(authCredential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                OptionDialog.cancel();
                                mUser.updatePassword(password)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), MainPage.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                OptionDialog.cancel();
                                Toast.makeText(getApplicationContext(), "mật khẩu cũ không đúng..", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainPage.class));
            }
        });
    }
}