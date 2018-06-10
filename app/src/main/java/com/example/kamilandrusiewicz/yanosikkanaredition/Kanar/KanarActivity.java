package com.example.kamilandrusiewicz.yanosikkanaredition.Kanar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.kamilandrusiewicz.yanosikkanaredition.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class KanarActivity extends AppCompatActivity {

    private ArrayList<String> target;
    private SimpleCursorAdapter adapter;
    final MySQLite db = new MySQLite(KanarActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanar);

        String[] valuse = new String[]{"Pies","Kot","Kon","Galab"};
        this.target = new ArrayList<String>();
        this.target.addAll(Arrays.asList(valuse));

        this.adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                db.lista(),
                new String[] {"_id", "gatunek"},
                new int[] {android.R.id.text1, android.R.id.text2},

                SimpleCursorAdapter.IGNORE_ITEM_VIEW_TYPE
        );

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(this.adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                Animal zwierz = db.pobierz(Integer.parseInt(name.getText().toString()));
                Intent intencja = new Intent(getApplicationContext(), DodajWpis.class);
                intencja.putExtra("element", zwierz);
                startActivityForResult(intencja, 2);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> _adapter, View view, int pos, long id) {
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                MySQLite db = new MySQLite(KanarActivity.this);
                Animal zwierz = db.pobierz(Integer.parseInt(name.getText().toString()));
                Integer zwierz_id_int = zwierz.get_id();
                String zwierz_id_string = zwierz_id_int.toString();
                db.usun(zwierz_id_string);

                adapter.changeCursor(db.lista());
                adapter.notifyDataSetChanged();

                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void nowyWpis(MenuItem mi)
    {
        Intent intencja = new Intent(this, DodajWpis.class);
        startActivityForResult(intencja, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Animal nowy = (Animal)
                    extras.getSerializable("nowy");
            db.dodaj(nowy);
            adapter.changeCursor(db.lista());
            adapter.notifyDataSetChanged();

        }

        if(requestCode==2 && resultCode==RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Animal nowy = (Animal)
                    extras.getSerializable("nowy");
            db.aktualizuj(nowy);
            adapter.changeCursor(db.lista());
            adapter.notifyDataSetChanged();
        }


        if(requestCode==3 && resultCode==RESULT_OK)
        {

            MySQLite db = new MySQLite(KanarActivity.this);
            adapter.changeCursor(db.lista());
            adapter.notifyDataSetChanged();
        }
    }

    //TODO cos do zriobienia
    //drugie TODO
}
