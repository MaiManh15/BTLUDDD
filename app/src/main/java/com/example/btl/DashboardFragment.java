package com.example.btl;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import model.budgetData;
import model.data;

public class DashboardFragment extends Fragment {

    private FloatingActionButton fab_main_btn, fab_income_btn, fab_expense_btn;
    private TextView fab_income_txt, fab_expense_txt;
    private TextView incomeTotalSum, expenseTotalSum, budgetTotalSum,suggest;

    private Animation FadOpen, FadClose;

    private FirebaseAuth fAuth;
    private DatabaseReference dRIncomeDb, dRExpenseDb,dRBudgetDb;

    private boolean Open = false;
    private String income ="income";
    private String expense="expense";

    private RecyclerView recyclerIncome, recyclerExpense;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView= inflater.inflate(R.layout.fragment_dashboard, container, false);

        incomeTotalSum = myView.findViewById(R.id.income_set_result);
        expenseTotalSum=myView.findViewById(R.id.expense_set_result);
        budgetTotalSum=myView.findViewById(R.id.budget_set_result);
        suggest=myView.findViewById(R.id.suggest_set_result);

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

        recyclerIncome= myView.findViewById(R.id.recycler_income);
        recyclerExpense=myView.findViewById(R.id.recycler_expense);

        dRIncomeDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalIncome = 0;
                for (DataSnapshot myDataSnapShot: snapshot.getChildren()){
                    data data = myDataSnapShot.getValue(data.class);
                    assert data != null;
                    totalIncome += data.getAmount();

                    String totalI = String.valueOf(totalIncome);
                    incomeTotalSum.setText(totalI +"vnd");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dRExpenseDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalExpense = 0;
                for (DataSnapshot myDataSnapShot: snapshot.getChildren()){
                    data data = myDataSnapShot.getValue(data.class);
                    assert data != null;
                    totalExpense += data.getAmount();

                    String totalE = String.valueOf(totalExpense);
                    expenseTotalSum.setText(totalE +"vnd");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dRBudgetDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalBudget = 0;
                for (DataSnapshot myDataSnapShot: snapshot.getChildren()){
                    data data = myDataSnapShot.getValue(data.class);
                    assert data != null;
                    totalBudget += data.getAmount();

                    String totalB = String.valueOf(totalBudget);
                    budgetTotalSum.setText(totalB +"vnd");
                }
                if(totalBudget> 0){
                    suggest.setText("Chi tiêu trong tầm kiểm soát");
                    budgetTotalSum.setBackgroundResource(R.drawable.floating_income_textbox);
                }
                if(totalBudget< 0){
                    suggest.setText("Chi tiêu vượt ngoài ngân sách");
                    budgetTotalSum.setBackgroundResource(R.drawable.floating_expense_textbox);
                }
                if(totalBudget== 0){
                    suggest.setText("Bạn đã tiêu sạch tiền");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        recyclerIncome.setHasFixedSize(true);
        recyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        recyclerExpense.setHasFixedSize(true);
        recyclerExpense.setLayoutManager(layoutManagerExpense);


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
                budgetData budgetdata;
                if(dataType.equals(income)) {
                    String id = dRIncomeDb.push().getKey();
                    data data = new data(floatAmount, type, note, id, mDate);
                    dRIncomeDb.child(id).setValue(data);
                    budgetdata = new budgetData(id, floatAmount);
                    dRBudgetDb.child(id).setValue(budgetdata);
                }
                if ((dataType.equals(expense))){
                    String id=  dRExpenseDb.push().getKey();
                    data data = new data(floatAmount, type,note, id, mDate);
                    dRExpenseDb.child(id).setValue(data);
                    budgetdata = new budgetData(id, -floatAmount);
                    dRBudgetDb.child(id).setValue(budgetdata);
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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<data> IncomeOptions=
                new FirebaseRecyclerOptions.Builder<data>()
                        .setQuery(dRIncomeDb,data.class)
                        .build();

        FirebaseRecyclerOptions<data> ExpenseOptions=
                new FirebaseRecyclerOptions.Builder<data>()
                        .setQuery(dRExpenseDb,data.class)
                        .build();
        FirebaseRecyclerAdapter<data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<data, IncomeViewHolder>(IncomeOptions) {
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income,parent,false));
            }
            @Override
            protected void onBindViewHolder(IncomeViewHolder holder, int position, @NonNull data model) {

                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());

            }
        };
        FirebaseRecyclerAdapter<data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<data, ExpenseViewHolder>(ExpenseOptions) {
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense,parent,false));
            }
            @Override
            protected void onBindViewHolder(ExpenseViewHolder holder, int position, @NonNull data model) {

                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseType(model.getType());
                holder.setExpenseDate(model.getDate());

            }
        };
        expenseAdapter.startListening();
        incomeAdapter.startListening();
        recyclerIncome.setAdapter(incomeAdapter);
        recyclerExpense.setAdapter(expenseAdapter);
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View incomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeView= itemView;
        }

        private void setIncomeType(String type){
            TextView mType=incomeView.findViewById(R.id.type_income_dashboard);
            mType.setText(type);
        }

        private void setIncomeDate(String date){
            TextView mDate=incomeView.findViewById(R.id.date_income_dashboard);
            mDate.setText(date);
        }

        private void setIncomeAmount(float amount){
            TextView mAmount=incomeView.findViewById(R.id.amount_income_dashboard);
            String stringAmount= String.valueOf(amount) ;
            stringAmount += "vnd";
            mAmount.setText(stringAmount);
        }
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View expenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseView= itemView;
        }

        private void setExpenseType(String type){
            TextView mType=expenseView.findViewById(R.id.type_expense_dashboard);
            mType.setText(type);
        }

        private void setExpenseDate(String date){
            TextView mDate=expenseView.findViewById(R.id.date_expense_dashboard);
            mDate.setText(date);
        }

        private void setExpenseAmount(float amount){
            TextView mAmount=expenseView.findViewById(R.id.amount_expense_dashboard);
            String stringAmount= String.valueOf(amount) ;
            stringAmount += "vnd";
            mAmount.setText(stringAmount);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatDate(LocalDate date){
        DateTimeFormatter formatters;
        formatters = DateTimeFormatter.ofPattern("d/MM/uuuu");
        return date.format(formatters);
    }
}