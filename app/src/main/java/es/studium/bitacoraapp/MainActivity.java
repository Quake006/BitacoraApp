package es.studium.bitacoraapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity  extends AppCompatActivity {
    ArrayList<Cuaderno> listaDeCuadernos;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAgregarCuaderno;
    ConsultaRemota acceso;
    BajaRemota baja;
    JSONArray result;
    JSONObject jsonobject;
    int idCuaderno;
    String nombreCuaderno = "";
    int posicion;
    private CuadernosAdapter cuadernosAdapter;
     Button btnModificar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        acceso = new ConsultaRemota();
        acceso.execute();
        // Instanciar vistas
        recyclerView = findViewById(R.id.recyclerViewCuadernos);
        btnModificar = findViewById(R.id.btnEditarCuaderno);
        fabAgregarCuaderno = findViewById(R.id.floatingActionButton);
        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        // Listener de los clicks en la lista, o sea el RecyclerView
        recyclerView.addOnItemTouchListener(new
                RecyclerTouchListener(getApplicationContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                // Pasar a la actividad MainActivityApuntes.java
                Cuaderno cuadernoSeleccionado = listaDeCuadernos.get(position);
                Intent intent = new Intent(MainActivity.this,
                       MainActivityApuntes.class);
                intent.putExtra("idCuaderno", cuadernoSeleccionado.getId());
                intent.putExtra("textoCuaderno", cuadernoSeleccionado.getTexto("nombreCuaderno"));
                startActivity(intent);
               // Toast.makeText(MainActivity.this, " "+listaDeCuadernos.get(position),
                     //  Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(View view, int position) {
                final Cuaderno cuadernoParaEliminar = listaDeCuadernos.get(position);
                AlertDialog dialog = new AlertDialog
                        .Builder(MainActivity.this)
                        .setPositiveButton("Sí, eliminar",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {
                                        //Toast.makeText(MainActivity.this, " "+cuadernoParaEliminar.getId(),
                                              //  Toast.LENGTH_SHORT).show();
                                        baja = new BajaRemota(cuadernoParaEliminar.getId());
                                        baja.execute();
                                        acceso = new ConsultaRemota();
                                        acceso.execute();
                                    }
                                })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {
                                        dialog.dismiss();
                                    }
                                })
                        .setTitle("Confirmar")
                        .setMessage("¿Eliminar el Cuaderno " +
                               cuadernoParaEliminar.getTexto(nombreCuaderno) + "?")
                        .create();
                dialog.show();
            }
        }));

        // Listener del FAB
        fabAgregarCuaderno.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Simplemente cambiamos de actividad
                Intent intent = new Intent(MainActivity.this,
                        AgregarCuadernoActivity.class);
                startActivity(intent);
            }
        });
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cuaderno cuadernoSeleccionado = listaDeCuadernos.get(posicion);
                Intent intent = new Intent(MainActivity.this,
                        EditarCuadernoActivity.class);
                intent.putExtra("idCuaderno", cuadernoSeleccionado.getId());
                intent.putExtra("textoCuaderno", cuadernoSeleccionado.getTexto("nombreCuaderno"));
                startActivity(intent);
            }
        });
    }

    private class ConsultaRemota extends AsyncTask<Void, Void, String> {
        // Inspectores
        protected void onPreExecute() {
            //Toast.makeText(MainActivity.this, "Obteniendo datos...",
                  //  Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(Void... argumentos) {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://192.168.0.27/ApiRestBitacora/cuadernos.php");
                //URL url = new URL("http://192.168.0.11/ApiRestBitacora/cuadernos.php");
               // URL url = new URL("http://192.168.1.21/ApiRestBitacora/cuadernos.php");
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
            //Toast.makeText(MainActivity.this, nombreCuaderno,Toast.LENGTH_LONG).show();

            //recorrer el result y crear un cuaderno por cada elemento
            //y meter en lista de cuaderno
            listaDeCuadernos = new ArrayList<Cuaderno>();
            try {

                for (int i=0;i<result.length();i++){

                    JSONObject jsonObject=result.getJSONObject(i);
                    idCuaderno = jsonObject.getInt("idCuaderno");
                    nombreCuaderno = jsonObject.getString("nombreCuaderno");
                   // Toast.makeText(MainActivity.this, ""+idCuaderno,Toast.LENGTH_LONG).show();
                    Cuaderno cuaderno=new Cuaderno(nombreCuaderno, idCuaderno);
                    listaDeCuadernos.add(cuaderno);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Por defecto es una lista vacía,
            // se la ponemos al adaptador y configuramos el recyclerView
            cuadernosAdapter = new CuadernosAdapter(listaDeCuadernos);
            RecyclerView.LayoutManager mLayoutManager =
                    new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(cuadernosAdapter);
            recyclerView.setHasFixedSize(true);
        }
    }

    private class BajaRemota extends AsyncTask<Void, Void, String> {
        // Atributos
        String idCuaderno;

        // Constructor
        public BajaRemota(long id) {
            this.idCuaderno = String.valueOf(id);
        }

        // Inspectores
        protected void onPreExecute() {
            //Toast.makeText(MainActivity.this, "Eliminando...",
                  //  Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Crear la URL de conexión al API
                URI baseUri = new URI("http://192.168.0.27/ApiRestBitacora/cuadernos.php");
                //URI baseUri = new URI("http://192.168.0.11/ApiRestBitacora/cuadernos.php");
                //URI baseUri = new URI("http://192.168.1.21/ApiRestBitacora/cuadernos.php");
                String[] parametros = {"idCuaderno",this.idCuaderno};
                URI uri = applyParameters(baseUri, parametros);
                // Create connection
                HttpURLConnection myConnection = (HttpURLConnection)
                        uri.toURL().openConnection();
                // Establecer método. Por defecto GET.
                myConnection.setRequestMethod("DELETE");
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    Log.println(Log.ASSERT, "Resultado", "Registro borrado");
                    myConnection.disconnect();
                } else {
                    // Error handling code goes here
                    Log.println(Log.ASSERT, "Error", "Error");
                }
            } catch (Exception e) {
                Log.println(Log.ASSERT, "Excepción", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(String mensaje) {
           //Toast.makeText(MainActivity.this, "Actualizando datos..."+ idCuaderno,
                  //  Toast.LENGTH_SHORT).show();
        }

        URI applyParameters(URI uri, String[] urlParameters) {
            StringBuilder query = new StringBuilder();
            boolean first = true;
            for (int i = 0; i < urlParameters.length; i += 2) {
                if (first) {
                    first = false;
                } else {
                    query.append("&");
                }
                try {
                    query.append(urlParameters[i]).append("=")
                            .append(URLEncoder.encode(urlParameters[i + 1], "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    /* As URLEncoder are always correct, this exception
                     * should never be thrown. */
                    throw new RuntimeException(ex);
                }
            }
            try {
                return new URI(uri.getScheme(), uri.getAuthority(),
                        uri.getPath(), query.toString(), null);
            } catch (Exception ex) {
                /* As baseUri and query are correct, this exception
                 * should never be thrown. */
                throw new RuntimeException(ex);
            }
        }
    }
}
