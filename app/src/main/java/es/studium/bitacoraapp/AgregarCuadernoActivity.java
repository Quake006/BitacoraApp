package es.studium.bitacoraapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import javax.net.ssl.HttpsURLConnection;

public class AgregarCuadernoActivity extends AppCompatActivity
{
    private Button btnAgregarCuaderno, btnCancelarNuevoCuaderno;
    private EditText etTexto;
    AltaRemota  alta;
    //ModificacionRemota modifica;
    ConsultaRemota acceso;
    JSONArray result;
    JSONObject jsonobject;
    int idCuaderno;
    String nombreCuaderno = "";
    private Cuaderno cuaderno;//el cuaderno que vamos a estar editando
    int posicion;
    ArrayList<Cuaderno> listaDeCuadernos;
    private RecyclerView recyclerView;
    private CuadernosAdapter cuadernosAdapter;
   // TextView txtId;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cuaderno);
        // Instanciar vistas
        etTexto = findViewById(R.id.txtTexto);
       //editarCuaderno = findViewById(R.id.txtEditar);
        btnAgregarCuaderno = findViewById(R.id.btnAgregarCuaderno);
        btnCancelarNuevoCuaderno = findViewById(R.id.btnCancelarNuevoCuaderno);
        //btnModificarCuaderno = findViewById(R.id.btnModificarCuaderno);
        //txtId.getText(idCuaderno);
        // Crear el controlador
        // Agregar listener del botón de guardar
        btnAgregarCuaderno.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {                   //queAccion = 0;
                alta = new AltaRemota(etTexto.getText().toString());
                alta.execute();
                acceso = new ConsultaRemota();
                acceso.execute();
                etTexto.setFocusable(false);
                btnAgregarCuaderno.setEnabled(false);
                // Alta
                //Toast.makeText(AgregarCuadernoActivity.this, "Alta datos...",
                        //Toast.LENGTH_SHORT).show();
            }
        });
        // El de cancelar simplemente cierra la actividad
        btnCancelarNuevoCuaderno.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class AltaRemota extends AsyncTask<Void, Void, String> {
        // Atributos
        String nombreCuaderno;

        // Constructor
        public AltaRemota(String nombre) {
            this.nombreCuaderno = nombre;
        }

        // Inspectoras
        protected void onPreExecute() {
            Toast.makeText(AgregarCuadernoActivity.this, "Alta..." + this.nombreCuaderno,
                    Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://192.168.0.27/ApiRestBitacora/cuadernos.php");
                //URL url = new URL("http://192.168.0.11/ApiRestBitacora/cuadernos.php");
                //URL url = new URL("http://192.168.1.21/ApiRestBitacora/cuadernos.php");
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                // Establecer método de comunicación.
                myConnection.setRequestMethod("POST");
                // Conexión exitosa
                HashMap<String, String> postDataParams = new HashMap<>();
                postDataParams.put("nombreCuaderno", this.nombreCuaderno);
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
            //Toast.makeText(AgregarCuadernoActivity.this, "Alta Correcta...",
                    //Toast.LENGTH_SHORT).show();
            etTexto = findViewById(R.id.txtTexto);
            etTexto.setText(nombreCuaderno);
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
           // Toast.makeText(AgregarCuadernoActivity.this, "Obteniendo datos...",
                  //  Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://192.168.0.27/ApiRestBitacora/cuadernos.php");
                //URL url = new URL("http://192.168.0.11/ApiRestBitacora/cuadernos.php");
                //URL url = new URL("http://192.168.1.21/ApiRestBitacora/cuadernos.php");
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
                    idCuaderno = jsonobject.getInt("idCuaderno");
                    nombreCuaderno = jsonobject.getString("nombreCuaderno");
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
            //Toast.makeText(AgregarCuadernoActivity.this, nombreCuaderno,Toast.LENGTH_LONG).show();
            listaDeCuadernos = new ArrayList<Cuaderno>();
            try {
                for (int i=0;i<result.length();i++){
                    JSONObject jsonObject=result.getJSONObject(i);
                    idCuaderno = jsonObject.getInt("idCuaderno");
                    nombreCuaderno = jsonObject.getString("nombreCuaderno");
                    Cuaderno cuaderno=new Cuaderno(nombreCuaderno, idCuaderno);
                    listaDeCuadernos.add(cuaderno);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Por defecto es una lista vacía,
            // se la ponemos al adaptador y configuramos el recyclerView
            recyclerView = findViewById(R.id.recyclerViewCuadernos);
            cuadernosAdapter = new CuadernosAdapter(listaDeCuadernos);
            RecyclerView.LayoutManager mLayoutManager =
                    new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(cuadernosAdapter);
            recyclerView.setHasFixedSize(true);
        }
        }




    }

