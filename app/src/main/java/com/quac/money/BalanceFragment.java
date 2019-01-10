package com.quac.money;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BalanceFragment extends Fragment {

    private TextView total;
    private TextView expense;
    private TextView income;

    private DigramView digram;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_balance,container,false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        total=view.findViewById(R.id.total);
        expense=view.findViewById(R.id.expense);
        income=view.findViewById(R.id.income);
        digram=view.findViewById(R.id.diagram);

        updateData();

    }

   /* @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isResumed()){
            updateData();
        }
    }*/

    private void updateData(){
        BalanceResult result=new BalanceResult();
        int S=0;
        int S1=0;

        SQLiteOpenHelper BankHelper=new DatabaseHelper(getActivity());

        try {
            SQLiteDatabase db=BankHelper.getReadableDatabase();

            Cursor cursor=db.rawQuery("SELECT PRICE  FROM Finance WHERE TYPE LIKE 'incomes' ;",null);
            while(cursor.moveToNext()){
                S+=cursor.getInt(0);

            }
            cursor=db.rawQuery("SELECT PRICE  FROM Finance WHERE TYPE LIKE 'expenses' ;",null);
            while(cursor.moveToNext()){
                S1+=cursor.getInt(0);

            }

            //in=cursor.getInt(0);

            Log.i("SUMeer", "updateData: "+S);


            cursor.close();
            db.close();

        }catch (SQLiteException e){
            Toast toast=Toast.makeText(getActivity(),"Database unavailable",Toast.LENGTH_SHORT);
        }
        result.expenses=S1;
        result.incomes=S;

        total.setText(getString(R.string.price,result.incomes-result.expenses));
        expense.setText(getString(R.string.price,result.expenses));
        income.setText(getString(R.string.price,result.incomes));
        digram.update(result.incomes,result.expenses);



    }
}
