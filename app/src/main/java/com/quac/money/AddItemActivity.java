package com.quac.money;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddItemActivity extends AppCompatActivity {
    //Инициализация
    public static final String TYPE_KEY="type";
    private EditText name;
    private EditText price;
    private Button addBtn;
    private boolean flag1=false;//Для  Watcher
    private boolean flag2=false;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);
        //Получение полей
        setTitle(R.string.AddItemTitle);
        name=(EditText)findViewById(R.id.name);
         price=(EditText)findViewById(R.id.price);
         addBtn=(Button)findViewById(R.id.addButton);
        type=getIntent().getStringExtra(TYPE_KEY);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.AddItemTitle);




        //Блокировка кнопки "добавить"

        TextWatcher generalTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (name.getText().hashCode() == editable.hashCode())
                {
                    if (!TextUtils.isEmpty(editable))
                        flag1=true;


                }
                else if (price.getText().hashCode() == editable.hashCode())
                {
                    if (!TextUtils.isEmpty(editable))
                        flag2=true;

                }


                Log.e("flag1",flag1+"");
                Log.e("flag2",flag2+"");
               if((flag1 & flag2)==true)
                addBtn.setEnabled(true);

            }
        };

        name.addTextChangedListener(generalTextWatcher);
        price.addTextChangedListener(generalTextWatcher);

        //Обработка кнопки добавить и отправка объекта
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Получить данные из полей
                String nameValue=name.getText().toString();
                String priceValue=price.getText().toString();

                Item item=new Item(nameValue,priceValue,type);
                Intent intent=new Intent();
                intent.putExtra("Item",item);


                setResult(RESULT_OK,intent);
                finish();
                /*String itemName=name.getText().toString();
                String itemPrice=price.getText().toString();*/

            }
        });


    }
    //??????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
