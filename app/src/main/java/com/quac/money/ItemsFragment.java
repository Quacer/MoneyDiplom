package com.quac.money;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsFragment extends Fragment  {


    public static final String TYPE_KEY="type";

   private String type;

    public static final int ADD_ITEM_REQUEST_CODE=123;

    private FloatingActionButton fab;

    private RecyclerView recycler;
    private ItemsAdapter adapter;
    SwipeRefreshLayout refresh;
    private ActionMode actionMode=null;




    public static ItemsFragment CreateFr(String type){

        ItemsFragment fragment = new ItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ItemsFragment.TYPE_KEY, type);
        bundle.putBoolean("key",true);
        fragment.setArguments(bundle);
        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ItemsAdapter();
        adapter.setListener(new AdapterListener());

        Bundle bundle=getArguments();
        type=bundle.getString(TYPE_KEY,Item.TYPE_EXPENSES);
        Log.i("tw", "onCreate: "+type);
       // actionMode=null;





    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_items,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler =view.findViewById(R.id.list);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);



        refresh=view.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();

            }
        });
        loadItems();//Загружаем данные


    }

    private void loadItems() {
        //!!!!!!!!!
        Log.i("check", "Вызов метода loadItems");

        SQLiteOpenHelper BankHelper=new DatabaseHelper(getActivity());
        List<Item> data=new ArrayList<>();


        try {
            SQLiteDatabase db=BankHelper.getReadableDatabase();

            Cursor cursor=db.query("Finance",new String[]{"_id","NAME","PRICE","TYPE"},"TYPE=?",new String[]{type},null,null,null,null);

            while (cursor.moveToNext()){

                data.add(new Item(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
            }

            cursor.close();
            db.close();

        }catch (SQLiteException e){
            Toast toast=Toast.makeText(getActivity(),"Database unavailable",Toast.LENGTH_SHORT);
        }

        //new LoadDB().execute(data);
        adapter.setData(data);
        refresh.setRefreshing(false);

    }



    private void addItem(final Item item) {
        //item.id = result.id;
        adapter.addItem(item);
        SQLiteOpenHelper BankHelper=new DatabaseHelper(getActivity());
        try {
            ContentValues content=new ContentValues();
            content.put("NAME",item.name);
            content.put("PRICE",item.price);
            content.put("TYPE",item.type);
            SQLiteDatabase db = BankHelper.getReadableDatabase();
            db.insert("Finance",null,content);
            db.close();
        }catch (SQLiteException e){
            Toast toast=Toast.makeText(getActivity(),"Database unavailable",Toast.LENGTH_SHORT);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==ADD_ITEM_REQUEST_CODE && resultCode== Activity.RESULT_OK){
           Item item=data.getParcelableExtra("Item");
            Log.i("item.type", "item type:"+item.type+" type: "+type);
          if(item.type.equals(type)){
               addItem(item);
           }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }


/*ACTIONMODE*/


//Удаляет выделенные элементы(удаление с конца)
public void removeSelectedItems(){
    SQLiteOpenHelper BankHelper=new DatabaseHelper(getActivity());
    try {
        Item it;
        SQLiteDatabase db = BankHelper.getReadableDatabase();
        for (int i = adapter.getSelectedItems().size() - 1; i >= 0; i--) {
            it=adapter.remove(adapter.getSelectedItems().get(i));
            db.delete("Finance","_id=?",new String[]{Integer.toString(it.id)});
        }
        db.close();
    }catch (SQLiteException e){
        Toast toast=Toast.makeText(getActivity(),"Database unavailable",Toast.LENGTH_SHORT);
    }

    ///
    //new RemoveDB().execute();
    actionMode.finish();
    actionMode=null;
}




    private class AdapterListener implements ItemsAdapterListener{

        @Override
        public void onItemClick(Item item, int position) {


            if(isInActionMode()){
                Log.i("Click", "Click: "+isInActionMode());
                Log.i("Click", "Action: "+actionMode);
                toggleSelection(position);
            }
            Log.i("Click", "Action: "+actionMode);


        }

        @Override
        public void onItemLongClick(Item item, int position) {
            if(isInActionMode())
                return;
            actionMode=((AppCompatActivity)getActivity()).startSupportActionMode(actionModeCalbacck);
            toggleSelection(position);
        }

        private boolean isInActionMode(){
            return actionMode!=null;
        }

        private void toggleSelection(int position){
            adapter.toggleSelection(position);
        }




        private ActionMode.Callback actionModeCalbacck=new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater=new MenuInflater(getContext());
                inflater.inflate(R.menu.items_menu,menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.remove:
                        showDialog();
                    break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode a) {
                Log.i("Destroy", "onDestroyActionMode: ");
                adapter.clearSelections();
                actionMode=null;
                Log.i("Act", "onDestroyActionMode: "+actionMode);

            }
        };

        private void showDialog(){

            showDialog dialog=new showDialog();
            dialog.show(getFragmentManager(),"Dialog");
            dialog.setListener(new DialogListener() {
                @Override
                public void onPositiveBtnClicked() {
                    removeSelectedItems();
                    actionMode=null;
                }

                @Override
                public void onNegativeBtnClicked() {
                    actionMode.finish();
                    actionMode=null;
                }
            });

        }
    }
//1 Загрузка
    private class LoadDB extends AsyncTask<List<Item>,Void,Void>{

        @Override
        protected Void doInBackground(List<Item>... data) {
            SQLiteOpenHelper BankHelper=new DatabaseHelper(getActivity());

            try {
                SQLiteDatabase db=BankHelper.getReadableDatabase();

                Cursor cursor=db.query("Finance",new String[]{"_id","NAME","PRICE","TYPE"},"TYPE=?",new String[]{type},null,null,null,null);

                while (cursor.moveToNext()){

                    data[0].add(new Item(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
                }

                cursor.close();
                db.close();

            }catch (SQLiteException e){
                Toast toast=Toast.makeText(getActivity(),"Database unavailable",Toast.LENGTH_SHORT);
            }
            return null;
        }
    }

    //2.Добавление
    private class AddDB extends AsyncTask<List<Item>,Void,Void>{

        @Override
        protected Void doInBackground(List<Item>... data) {
            SQLiteOpenHelper BankHelper=new DatabaseHelper(getActivity());


            try {
                SQLiteDatabase db=BankHelper.getReadableDatabase();

                Cursor cursor=db.query("Finance",new String[]{"_id","NAME","PRICE","TYPE"},"TYPE=?",new String[]{type},null,null,null,null);

                while (cursor.moveToNext()){

                    data[0].add(new Item(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
                }

                cursor.close();
                db.close();

            }catch (SQLiteException e){
                Toast toast=Toast.makeText(getActivity(),"Database unavailable",Toast.LENGTH_SHORT);
            }
            return null;
        }
    }


    //3. Удаление
    private class RemoveDB extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... data) {
            SQLiteOpenHelper BankHelper=new DatabaseHelper(getActivity());
            try {
                Item it;
                SQLiteDatabase db = BankHelper.getReadableDatabase();
                for (int i = adapter.getSelectedItems().size() - 1; i >= 0; i--) {
                    it=adapter.remove(adapter.getSelectedItems().get(i));
                    db.delete("Finance","_id=?",new String[]{Integer.toString(it.id)});
                }
                db.close();
            }catch (SQLiteException e){
                Toast toast=Toast.makeText(getActivity(),"Database unavailable",Toast.LENGTH_SHORT);
            }
            return null;
        }
    }






}



