package com.example.kamilandrusiewicz.yanosikkanaredition.Kanar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kamilandrusiewicz.yanosikkanaredition.R;

public class DodajWpis extends AppCompatActivity {

    private int modyfi_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_wpis);

        final Button przycisk = (Button) findViewById(R.id.button);
        przycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wyslij(v);
            }
        });

        ArrayAdapter gatunki = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[] {"1", "2", "3"});
        Spinner gatunek = (Spinner) findViewById
                (R.id.spinner);
        gatunek.setAdapter(gatunki);

        Bundle extras = getIntent().getExtras();
        try {
            if(extras.getSerializable("element") != null) {
                Animal zwierz = (Animal) extras.getSerializable("element");
                EditText kolor = (EditText) findViewById(R.id.editText);
                EditText wielkosc = (EditText) findViewById(R.id.editText2);
                EditText opis = (EditText) findViewById(R.id.editText3);
                kolor.setText(zwierz.getKolor());
                wielkosc.setText(Float.toString(zwierz.getWielkosc()));
                opis.setText(zwierz.getOpis());
                this.modyfi_id=zwierz.get_id();
            }
        }catch(Exception ex) {
            this.modyfi_id=0;
        }
    }

    public void wyslij (View view)
    {
        EditText kolor = (EditText) findViewById
                (R.id.editText);
        EditText wielkosc = (EditText)
                findViewById (R.id.editText2);
        EditText opis = (EditText) findViewById
                (R.id.editText3);
        Spinner gatunek = (Spinner) findViewById
                (R.id.spinner);
        Animal zwierze = new Animal(
                gatunek.getSelectedItem().toString(),
                kolor.getText().toString(),
                Float.valueOf(wielkosc.getText().toString()),
                opis.getText().toString()
        );

        zwierze.set_id(this.modyfi_id);

        Intent intencja = new Intent();
        intencja.putExtra("nowy", zwierze);
        setResult(RESULT_OK, intencja);
        finish();
    }

}
