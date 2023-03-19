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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class EditarCuadernoActivity extends AppCompatActivity
{
    private EditText editarCuaderno;
    TextView txtId;
    private Button btnGuardarCambios, btnCancelarEdicion;
    private Cuaderno cuaderno;//el cuaderno que vamos a estar editando
    ConsultaRemota acceso;
    ModificacionRemota modifica;
    JSONArray result;
    JSONObject jsonobject;
    int idCuaderno;
    String id;
    String nombreCuaderno = "";
    int posicion;
    ArrayList<Cuaderno> listaDeCuadernos;
    private RecyclerView recyclerView;
    private CuadernosAdapter cuadernosAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cuaderno);
        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        // Si no hay datos, salimos
        if (extras == null)
        {
            finish();
            return;
        }

        // Nota: igualmente solamente podríamos mandar el id y recuperar la frase de la BD
        long idCuaderno = extras.getLong("idCuaderno");
        String textoCuaderno = extras.getString("textoCuaderno");
        Toast.makeText(EditarCuadernoActivity.this, ""+idCuaderno,
                Toast.LENGTH_SHORT).show();

        cuaderno = new Cuaderno(textoCuaderno, idCuaderno);
        // Ahora declaramos las vistas
        txtId = findViewById(R.id.editTextId);
        editarCuaderno = findViewById(R.id.etEditarCuaderno);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicionCuaderno);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambiosCuaderno);
        // Rellenar los EditText con los datos de la frase
        editarCuaderno.setText(textoCuaderno);
        id = String.valueOf(idCuaderno);
        txtId.setText(id);

        //recyclerView = findViewById(R.id.recyclerViewCuadernos);
        // Listener del click del botón que guarda cambios
        btnGuardarCambios.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {                   //queAccion = 0;
                //txtId = findViewById(R.id.editTextId);
                //editarCuaderno = findViewById(R.id.etEditarCuaderno);
                recyclerView = findViewById(R.id.recyclerViewCuadernos);
                modifica = new ModificacionRemota(id,editarCuaderno.getText().toString());
                modifica.execute();
                acceso = new ConsultaRemota();
                acceso.execute();
                txtId.setFocusable(false);
                editarCuaderno.setFocusable(false);
                btnGuardarCambios.setEnabled(false);
                //Toast.makeText(EditarCuadernoActivity.this, ""+id, Toast.LENGTH_LONG).show();
                //Toast.makeText(EditarCuadernoActivity.this, ""+editarCuaderno.getText().toString(), Toast.LENGTH_LONG).show();
                // Terminar
               // finish();
            }
        });
        // El de cancelar simplemente cierra la actividad
        btnCancelarEdicion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
            private class ModificacionRemota extends AsyncTask<Void, Void, String> {
                // Atributos
                String idCuaderno;
                String nombreCuaderno;

                // Constructor
                public ModificacionRemota(String id, String nombre) {
                    this.idCuaderno = id;
                    this.nombreCuaderno = nombre;
                }

                // Inspectores
                protected void onPreExecute() {
                   // Toast.makeText(EditarCuadernoActivity.this, " "+ this.nombreCuaderno,
                        // Toast.LENGTH_SHORT).show();
                }

                protected String doInBackground(Void... voids) {
                    try {
                        StringBuilder response = new StringBuilder();
                        Uri uri = new Uri.Builder()
                                .scheme("http")
                                .authority("192.168.0.27")
                                //.authority("192.168.0.11")
                                //.authority("192.168.1.21")
                                .path("/ApiRestBitacora/cuadernos.php")
                                .appendQueryParameter("idCuaderno", this.idCuaderno)
                                .appendQueryParameter("nombreCuaderno", this.nombreCuaderno)

                                .build();
                        // Create connection
                        URL url = new URL(uri.toString());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setReadTimeout(15000);
                        connection.setConnectTimeout(15000);
                        connection.setRequestMethod("PUT");
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                            String line;
                            BufferedReader br = new BufferedReader(new
                                    InputStreamReader(connection.getInputStream()));
                            while ((line = br.readLine()) != null) {
                                response.append(line);
                            }
                        } else {
                            response = new StringBuilder();
                        }
                        connection.getResponseCode();
                        if (connection.getResponseCode() == 200) {
                            // Success
                            Log.println(Log.ASSERT, "Resultado", "Registro modificado:" + response);
                            connection.disconnect();
                        } else {
                            Log.println(Log.ASSERT, "Error", "Error");
                        }
                    } catch (Exception e) {
                        Log.println(Log.ASSERT, "Excepción", e.getMessage());
                    }
                    return null;
                }

                protected void onPostExecute(String mensaje) {
                   // Toast.makeText(EditarCuadernoActivity.this, "Actualizando datos...",
                           // Toast.LENGTH_SHORT).show();

                    txtId = findViewById(R.id.editTextId);
                    txtId.setText(idCuaderno);
                    editarCuaderno = findViewById(R.id.etEditarCuaderno);
                    editarCuaderno.setText(nombreCuaderno);
                   // Toast.makeText(EditarCuadernoActivity.this, "  "+nombreCuaderno,
                          //  Toast.LENGTH_LONG).show();

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
                    idCuaderno = Integer.parseInt(String.valueOf(jsonobject.getInt("idCuaderno")));
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

