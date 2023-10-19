package com.example.btl;

import static com.example.btl.DashboardFragment.formatDate;

import android.app.AlertDialog;
import android.os.Build;
import  android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;

import model.budgetData;
import model.data;


public class ExpenseFragment extends Fragment {

    private DatabaseReference dRExpenseDb, dRBudgetDb;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseRecyclerOptions<data> options;

    private RecyclerView recyclerView;
    private TextView expenseTotalSum;

    private EditText editAmount, editType, editNote;
    private Button cancelButton, deleteButton, updateButton;

    private String type, note, post_key;
    private float amount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_expense, container, false);
        expenseTotalSum=myview.findViewById(R.id.expense_txt);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        FirebaseUser fUser= fAuth.getCurrentUser();
        String uid= fUser.getUid();

        dRExpenseDb= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        dRBudgetDb= FirebaseDatabase.getInstance().getReference().child("BudgetData").child(uid);

        recyclerView=myview.findViewById(R.id.recycler_expense_id);

        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        dRExpenseDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalExpense = 0;
                for (DataSnapshot myDataSnapShot: snapshot.getChildren()){
                    data data = myDataSnapShot.getValue(data.class);
                    assert data != null;
                    totalExpense += data.getAmount();

                    String totalE = String.valueOf(totalExpense);
                    expenseTotalSum.setText(totalE + "vnd");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<data>()
                .setQuery(dRExpenseDb, data.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<data, ExpenseFragment.MyViewHolder>(options) {

            @NonNull
            public ExpenseFragment.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ExpenseFragment.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_expense_data, parent, false));
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            protected void onBindViewHolder(ExpenseFragment.MyViewHolder holder, int position, @NonNull data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(view -> {

                    post_key=getRef(position).getKey();

                    type= model.getType();
                    note=model.getNote();
                    amount=model.getAmount();
                    updateDataItem();
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView= itemView;
        }

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.expense_type_txt);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.expense_note_txt);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.expense_date_txt);
            mDate.setText(date);
        }

        private void setAmount(float amount){
            TextView mAmount=mView.findViewById(R.id.expense_amount_txt);
            String stringAmount= String.valueOf(amount);
            stringAmount += "vnd";
            mAmount.setText(stringAmount);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDataItem(){

        AlertDialog.Builder myDialog= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.update_data, null);

        myDialog.setView(myview);

        editAmount=myview.findViewById(R.id.amount_id);
        editType=myview.findViewById(R.id.type_id);
        editNote=myview.findViewById(R.id.note_id);

        editType.setText(type);
        editType.setSelection(type.length());
        editNote.setText(note);
        editNote.setSelection(note.length());
        editAmount.setText(String.valueOf(amount));
        editAmount.setSelection(String.valueOf(amount).length());

        cancelButton=myview.findViewById(R.id.button_cancel);
        deleteButton=myview.findViewById(R.id.delete_button);
        updateButton=myview.findViewById(R.id.update_button);

        AlertDialog dialog= myDialog.create();

        updateButton.setOnClickListener(view -> {
            String id = post_key;

            type=editType.getText().toString().trim();
            note=editNote.getText().toString().trim();
            String stringAmount = editAmount.getText().toString().trim();
            amount=Float.parseFloat(stringAmount);
            String date = formatDate(LocalDate.now());

            data data = new data(amount, type, note, id, date);
            budgetData bData= new budgetData(id, -amount);

            dRExpenseDb.child(id).setValue(data);
            dRBudgetDb.child(id).setValue(bData);
            dialog.dismiss();


        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = post_key;
                dRExpenseDb.child(id).removeValue();
                dRBudgetDb.child(id).removeValue();
                Toast.makeText(getActivity(), "xóa thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}