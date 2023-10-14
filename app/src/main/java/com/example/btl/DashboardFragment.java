package com.example.btl;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import model.data;

public class DashboardFragment extends Fragment {

    private FloatingActionButton fab_main_btn, fab_income_btn, fab_expense_btn;
    private TextView fab_income_txt, fab_expense_txt;

    private Animation FadOpen, FadClose;

    private FirebaseAuth fAuth;
    private DatabaseReference dRIncomeDb, dRExpenseDb,dRBudgetDb;

    private boolean Open = false;
    private String income ="income";
    private String expense="expense";

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView= inflater.inflate(R.layout.fragment_dashboard, container, false);

        fAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=fAuth.getCurrentUser();
        String uid= mUser.getUid();

        dRIncomeDb= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        dRExpenseDb= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        dRBudgetDb= FirebaseDatabase.getInstance().getReference().child("BudgetData").child(uid);

        FadOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn=myView.findViewById(R.id.main_ft_btn);
        fab_income_btn=myView.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myView.findViewById(R.id.expense_ft_btn);

        fab_income_txt=myView.findViewById(R.id.income_ft_text);
        fab_expense_txt=myView.findViewById(R.id.expense_ft_text);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addData();

                if(Open){
                    fab_income_btn.startAnimation(FadClose);
                    fab_expense_btn.startAnimation(FadClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_expense_txt.startAnimation(FadClose);
                    fab_income_txt.startAnimation(FadClose);
                    fab_expense_txt.setClickable(false);
                    fab_income_txt.setClickable(false);
                    Open=false;
                }else {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.setClickable(true);
                    fab_income_txt.setClickable(true);
                    Open = true;
                }

            }
        });

        return myView;
    }

    private void ftAnimation(){
        if(Open){
            fab_income_btn.startAnimation(FadClose);
            fab_expense_btn.startAnimation(FadClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_expense_txt.startAnimation(FadClose);
            fab_income_txt.startAnimation(FadClose);
            fab_expense_txt.setClickable(false);
            fab_income_txt.setClickable(false);
            Open=false;
        }else {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.setClickable(true);
            fab_income_txt.setClickable(true);
            Open = true;
        }
    }

    private void addData(){
        fab_expense_btn.setOnClickListener(view -> DataInsert(expense));

        fab_income_btn.setOnClickListener(view -> DataInsert(income));

        fab_expense_txt.setOnClickListener(view -> DataInsert(expense));

        fab_income_txt.setOnClickListener(view -> DataInsert(income));

    }

    public void DataInsert(String dataType){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myView=inflater.inflate(R.layout.insert_data_layout, null);
        myDialog.setView(myView);
        AlertDialog dialog= myDialog.create();

        EditText editAmount = myView.findViewById(R.id.amount_id);
        EditText editType= myView.findViewById(R.id.type_id);
        EditText editNote=myView.findViewById(R.id.note_id);

        Button savebtn=myView.findViewById(R.id.save_button);
        Button cancelbtn=myView.findViewById(R.id.button_cancel);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String type= editType.getText().toString().trim();
                String amount= editAmount.getText().toString().trim();
                String note= editNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    editType.setError("Không được bỏ trống!");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    editAmount.setError("Không được bỏ trống!");
                    return;
                }
                float floatAmount= Float.valueOf(amount);
                if(TextUtils.isEmpty(note)){
                    editNote.setError("Không được bỏ trống!");
                    return;
                }

                LocalDate today= LocalDate.now();
                String mDate= formatDate(today);
                if(dataType.equals(income)) {

                    String id = dRIncomeDb.push().getKey();
                    data data = new data(floatAmount, type, note, id, mDate);
                    dRIncomeDb.child(id).setValue(data);
                }
                if ((dataType.equals(expense))){
                    String id=  dRExpenseDb.push().getKey();
                    data data = new data(floatAmount, type,note, id, mDate);
                    dRExpenseDb.child(id).setValue(data);
                }

                Toast.makeText(getActivity(), "Đã thêm dữ liệu!", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();

            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void updateBudget(){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatDate(LocalDate date){
        DateTimeFormatter formatters;
        formatters = DateTimeFormatter.ofPattern("d/MM/uuuu");
        return date.format(formatters);
    }
}