package com1032.cw2.aa02350.cwmobile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

/**
 * Created by anthonyawobasivwe on 21/05/2017.
 */

public class AddActivity extends AppCompatActivity {


    //i feel this class doesnt neeed commenting as it is self explanatory
    private ArrayList<String> listArray;
    private ListView list;
    private ArrayAdapter<String> arrayAdapter;
    private String task;
    private int[] elements = {R.id.listViewtask};
    private String[] names= {"ORIGIN", "DESTINATION", "DISTANCE", "TIME", BaseColumns._ID};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_main);

        DatabaseHelper dh = new DatabaseHelper(this );
        SQLiteDatabase database = dh.getReadableDatabase();

        final Cursor cursor = database.rawQuery("SELECT * FROM Distance_table", null);
        final CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_main, cursor,
                names, elements, 0);

        list =(ListView)findViewById(R.id.listViewtask);

        listArray = new ArrayList<>();
        arrayAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listArray);
        list.setAdapter(arrayAdapter);
    }


    public void onClicked(View v){
        //goes to second activity when clicked
        Intent intent = new Intent( this,MainActivity.class);
        startActivityForResult(intent, CONSTANT.Request_Code);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if resultcode value and requestcode value match then the add activity has responded


        if(resultCode== CONSTANT.Request_Code){

            //stringextra called on key asscoiated with that value
            task=data.getStringExtra(CONSTANT.task_FIELD);
            listArray.add(task);
            arrayAdapter.notifyDataSetChanged();

        }
        else if(resultCode==CONSTANT.task_RequestCode){
            task=data.getStringExtra(CONSTANT.ALTERTASK);
            int position=data.getIntExtra(CONSTANT.item_position,-1);
            // This Removes the item within the array in a certain position
            listArray.remove(position);
            //adds the task in that position
            listArray.add(position,task);
            // notify Adapter of change
            arrayAdapter.notifyDataSetChanged();
        }
    }

}
