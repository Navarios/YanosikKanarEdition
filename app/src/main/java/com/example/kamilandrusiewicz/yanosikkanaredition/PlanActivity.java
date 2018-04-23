package com.example.kamilandrusiewicz.yanosikkanaredition;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.kamilandrusiewicz.yanosikkanaredition.Models.PlanModel;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlanActivity extends AppCompatActivity {

   private ListView planListView;
   private EditText searchLine;
   private EditText searchStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        planListView=(ListView)findViewById(R.id.planListView);
        searchLine=(EditText)findViewById(R.id.searchLine);
        searchStop=(EditText)findViewById(R.id.searchStop);


    }

    public void updateJsonData(View view) {
        new JSONTask().execute("https://www.zditm.szczecin.pl/json/pojazdy.inc.php");
    }


    public class JSONTask extends AsyncTask<String, String,  List<PlanModel>> {

        @Override
        protected List<PlanModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONArray parentArray = new JSONArray(finalJson);

                List<PlanModel> planModelList = new ArrayList<>();

                StringBuffer finalBufferedData=new StringBuffer();

                for (int i = 0; i < parentArray.length(); i++) {
                    if(searchLine.getText().toString().length()==0 && searchStop.getText().toString().length()==0) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        PlanModel planModel = new PlanModel();
                        planModel.setLinia(finalObject.getString("linia"));
                        planModel.setTrasa(finalObject.getString("trasa"));
                        planModel.setZ(finalObject.getString("z"));
                        planModel.setD(finalObject.getString("do"));
                        planModel.setPunktualnosc1(finalObject.getString("punktualnosc1"));

                        planModelList.add(planModel);
                    }
                    else{
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        PlanModel planModel = new PlanModel();
                        if(searchLine.getText().toString().equals(finalObject.getString("linia")) || (searchStop.getText().toString().equals(finalObject.getString("do"))) && searchStop.getText().toString().length()!=0) {
                            planModel.setLinia(finalObject.getString("linia"));
                            planModel.setTrasa(finalObject.getString("trasa"));
                            planModel.setZ(finalObject.getString("z"));
                            planModel.setD(finalObject.getString("do"));
                            planModel.setPunktualnosc1(finalObject.getString("punktualnosc1"));

                            planModelList.add(planModel);
                        }
                    }

                }

                return planModelList;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute( List<PlanModel> result) {
            super.onPostExecute(result);
            PlanAdapter adapter=new PlanAdapter(getApplicationContext(),R.layout.row,result);
            planListView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new JSONTask().execute("https://www.zditm.szczecin.pl/json/pojazdy.inc.php");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PlanAdapter extends ArrayAdapter{
        public List<PlanModel> planModelList;
        private int resource;
        private LayoutInflater inflater;
        public PlanAdapter(@NonNull Context context, int resource, List<PlanModel> objects) {
            super(context, resource, objects);
            planModelList=objects;
            this.resource=resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=inflater.inflate(resource,null);
            }

            TextView linia;
            TextView trasa;
            TextView z;
            TextView d;
            TextView punktualnosc1;

            linia=(TextView)convertView.findViewById(R.id.linia);
            trasa=(TextView)convertView.findViewById(R.id.trasa);
            z=(TextView)convertView.findViewById(R.id.z);
            d=(TextView)convertView.findViewById(R.id.d);
            punktualnosc1=(TextView)convertView.findViewById(R.id.punktualnosc1);

            linia.setText("Linia: " + planModelList.get(position).getLinia());
            trasa.setText("Trasa: "+planModelList.get(position).getTrasa());
            z.setText("Z: "+planModelList.get(position).getZ());
            d.setText("Do: "+planModelList.get(position).getD());
            punktualnosc1.setText("Punktualność: "+ planModelList.get(position).getPunktualnosc1());
            return convertView;
        }
    }

}

