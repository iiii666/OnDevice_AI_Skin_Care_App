package com.example.RNU;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

public class delete_record extends AppCompatActivity {


    ArrayAdapter<String> adapter;
    List<String> date_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        ImageButton delete=(ImageButton) findViewById(R.id.deleted);
        ImageButton home=(ImageButton) findViewById(R.id.home);
        ListView del_list=(ListView)findViewById(R.id.rv_view);

        UserDateDatabase db=UserDateDatabase.getDatabase(this);
        date_list=db.getUserDateDao().getdateAll();

        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_multiple_choice,date_list);
        del_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        del_list.setAdapter(adapter);

        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SparseBooleanArray checkedItems = del_list.getCheckedItemPositions();
                int count = del_list.getCount();

                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        db.getUserDateDao().delDate(date_list.get(i));
                        date_list.remove(i) ;
                    }
                }
                del_list.setAdapter(adapter);
                del_list.clearChoices() ;
                adapter.notifyDataSetChanged();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(delete_record.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}