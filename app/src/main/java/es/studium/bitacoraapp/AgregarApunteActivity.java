package es.studium.bitacoraapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AgregarApunteActivity extends AppCompatActivity
{
    private Button btnAgregarApunte, btnCancelarNuevoApunte;
    private EditText etTexto, etFecha;
    AltaRemota  alta;
    ConsultaRemota acceso;
    JSONArray result;
    JSONObject jsonobject;
    int idApunte;
    String textoApunte = "";
    String fechaApunte = "";
    int posicion;
    int idFK;
    ArrayList<Apunte> listaDeApuntes;
    private RecyclerView recyclerView;
    private ApuntesAdapter apuntesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_apunte);

        Bundle extras = getIntent().getExtras();
        // Si no hay datos, salimos
        if (extras == null)
        {
            finish();
            return;
        }
        // Instanciar el controlador de las frases
        // Rearmar la frase
        // Nota: igualmente solamente podríamos mandar el id y recuperar la frase de la BD
        idFK = extras.getInt("idCuaderno");
        //idFK = String.valueOf(idCuaderno);
        Toast.makeText(AgregarApunteActivity.this, ""+idFK,
          Toast.LENGTH_SHORT).show();
        //*idFK = (int) idCuaderno;

        // Instanciar vistas
        etTexto = findViewById(R.id.txtTexto);
        etFecha = findViewById(R.id.txtFecha);
        btnAgregarApunte = findViewById(R.id.btnAgregarApunte);
        btnCancelarNuevoApunte= findViewById(R.id.btnCancelarNuevoApunte);

        // Crear el controlador
        // Agregar listener del botón de guardar
        btnAgregarApunte.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {                   //queAccion = 0;
                recyclerView = findViewById(R.id.recyclerViewApuntes);
                alta = new AltaRemota(etFecha.getText().toString(),etTexto.getText().toString(), idFK);
                alta.execute();
                acceso = new ConsultaRemota();
                acceso.execute();
                etTexto.setFocusable(false);
                etFecha.setFocusable(false);
                btnAgregarApunte.setEnabled(false);
                // Alta
                //Toast.makeText(AgregarCuadernoActivity.this, "Alta datos...",
                        //Toast.LENGTH_SHORT).show();
            }
        });
        // El de cancelar simplemente cierra la actividad
        btnCancelarNuevoApunte.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class AltaRemota extends AsyncTask<Void, Void, String> {
        // Atributos
        String textoApunte;
        String fechaApunte;
        int idFK;
        // Constructor
        public AltaRemota(String texto, String fecha, int idFK) {
            this.textoApunte= texto;
            this.fechaApunte = fecha;
           this.idFK = idFK;
        }

        // Inspectoras
        protected void onPreExecute() {
            Toast.makeText(AgregarApunteActivity.this, "Alta..." + this.idFK,
                    Toast.LENGTH_LONG).show();

           // Toast.makeText(AgregarApunteActivity.this, " " +this.idFK,
                    //Toast.LENGTH_LONG).show();
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://192.168.0.27/ApiRestBitacora/apuntes.php?idCuaderno="+idFK);
               // URL url = new URL("http://192.168.0.11/ApiRestBitacora/apuntes.php");
                //URL url = new URL("http://192.168.1.21/ApiRestBitacora/apuntes.php?idCuaderno="+idFK);
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                // Establecer método de comunicación.
                myConnection.setRequestMethod("POST");
                // Conexión exitosa
                HashMap<String, String> postDataParams = new HashMap<>();
                postDataParams.put("textoApunte", this.textoApunte);
                postDataParams.put("fechaApunte", this.fechaApunte);
                postDataParams.put("idFK", String.valueOf(idFK));
                myConnection.setDoInput(true);
                myConnection.setDoOutput(true);
                OutputStream os = myConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,
                        StandardCharsets.UTF_8));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                myConnection.getResponseCode();
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    myConnection.disconnect();
                } else {
                    // Error handling code goes here
                    Log.println(Log.ASSERT, "Error", "Error");
                }
            } catch (Exception e) {
                Log.println(Log.ASSERT, "Excepción", e.getMessage());
            }
            return (null);
        }

        protected void onPostExecute(String mensaje) {
           // Toast.makeText(AgregarApunteActivity.this, "A" +idFK,
                   // Toast.LENGTH_SHORT).show();
            idFK=1;
            etTexto = findViewById(R.id.txtTexto);
            etTexto.setText(textoApunte);
            etFecha = findViewById(R.id.txtFecha);
            etFecha.setText(fechaApunte);

        }

        private String getPostDataString(HashMap<String, String> params)
                throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        }
    }
    private class ConsultaRemota extends AsyncTask<Void, Void, String> {
        // Inspectores
        protected void onPreExecute() {
            //Toast.makeText(MainActivity.this, "Obteniendo datos...",
            //  Toast.LENGTH_SHORT).show();
            idFK=1;
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://192.168.0.27/ApiRestBitacora/apuntes.php?idCuaderno=1");
                //URL url = new URL("http://192.168.0.11/ApiRestBitacora/apuntes.php?idCuaderno=1");
                //URL url = new URL("http://192.168.1.21/ApiRestBitacora/apuntes.php?idCuaderno=1");

                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                // Establecer método de comunicación. Por defecto GET.
                myConnection.setRequestMethod("GET");
                if (myConnection.getResponseCode() == 200) {
                    // Conexión exitosa
                    // Creamos Stream para la lectura de datos desde el servidor
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                    // Creamos Buffer de lectura
                    BufferedReader bR = new BufferedReader(responseBodyReader);
                    String line;
                    StringBuilder responseStrBuilder = new StringBuilder();
                    // Leemos el flujo de entrada
                    while ((line = bR.readLine()) != null) {
                        responseStrBuilder.append(line);
                    }
                    // Parseamos respuesta en formato JSON
                    result = new JSONArray(responseStrBuilder.toString());//////////
                    // Nos quedamos solamente con la primera
                    posicion = 0;
                    jsonobject = result.getJSONObject(posicion);
                    // Sacamos dato a dato obtenido
                    idApunte = jsonobject.getInt("idApunte");
                    fechaApunte = jsonobject.getString("fechaApunte");
                    textoApunte = jsonobject.getString("textoApunte");
                    responseBody.close();
                    responseBodyReader.close();
                    myConnection.disconnect();
                } else {
                    // Error en la conexión
                    Log.println(Log.ERROR, "Error", "¡Conexión fallida!");
                }
            } catch (Exception e) {
                Log.println(Log.ERROR, "Error", "¡Conexión fallida!");
            }
            return (null);
        }

        protected void onPostExecute(String mensaje) {
            // Actualizamos los cuadros de texto
            //Toast.makeText(MainActivity.this, nombreCuaderno,Toast.LENGTH_LONG).show();

            //recorrer el result y crear un cuaderno por cada elemento
            //y meter en lista de cuaderno
            listaDeApuntes = new ArrayList<Apunte>();
            try {

                for (int i=0;i<result.length();i++){

                    JSONObject jsonObject=result.getJSONObject(i);
                    idApunte = jsonObject.getInt("idApunte");
                    fechaApunte = jsonObject.getString("fechaApunte");
                    textoApunte = jsonObject.getString("textoApunte");
                    //Toast.makeText(MainActivity.this, nombreCuaderno,Toast.LENGTH_LONG).show();
                    Apunte apunte=new Apunte(fechaApunte, textoApunte, idFK);
                    listaDeApuntes.add(apunte);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Por defecto es una lista vacía,
            // se la ponemos al adaptador y configuramos el recyclerView
            apuntesAdapter = new ApuntesAdapter(listaDeApuntes);
            RecyclerView.LayoutManager mLayoutManager =
                    new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(apuntesAdapter);
            recyclerView.setHasFixedSize(true);
        }
    }
    }

