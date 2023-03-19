package es.studium.bitacoraapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MainActivityApuntes extends AppCompatActivity {

    ArrayList<Apunte> listaDeApuntes;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAgregarApunte, fabVolver;
    ConsultaRemota acceso;
    BajaRemota baja;
    JSONArray result;
    JSONObject jsonobject;
    int idApunte;
    String textoApunte = "";
    String fechaApunte = "";
    int posicion;
    String idCuaderno;
    Button btnModificar;
    int idFK;
    private ApuntesAdapter apuntesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_apuntes);
        acceso = new ConsultaRemota();
        acceso.execute();
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
        long idCuaderno = extras.getLong("idCuaderno");
        String textoCuaderno = extras.getString("textoCuaderno");
        //Toast.makeText(MainActivityApuntes.this, ""+idCuaderno,
              //  Toast.LENGTH_SHORT).show();
            idFK = (int) idCuaderno;

        //acceso = new ConsultaRemota();
       // acceso.execute();
        // Instanciar vistas
        recyclerView = findViewById(R.id.recyclerViewApuntes);
        btnModificar = findViewById(R.id.btnEditarApunte);
        fabAgregarApunte = findViewById(R.id.floatingActionButton);
        fabVolver = findViewById(R.id.floatingActionButton2);
        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        // Listener de los clicks en la lista, o sea el RecyclerView
        recyclerView.addOnItemTouchListener(new
                RecyclerTouchListener(getApplicationContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                // Pasar a la actividad MainActivityApuntes.java
                //Apunte apunteSeleccionado = listaDeApuntes.get(position);
               // Intent intent = new Intent(MainActivityApuntes.this,
                      //  MainActivityApuntes.class);
               // intent.putExtra("idApunte", apunteSeleccionado.getId());

               // intent.putExtra("textoApunte", apunteSeleccionado.getTexto("textoApunte"));
               // startActivity(intent);
            }
            @Override
            public void onLongClick(View view, int position) {
                final Apunte apunteParaEliminar = listaDeApuntes.get(position);
                AlertDialog dialog = new AlertDialog
                        .Builder(MainActivityApuntes.this)
                        .setPositiveButton("Sí, eliminar",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {

                                        Toast.makeText(MainActivityApuntes.this, " "+apunteParaEliminar.getId(),
                                                Toast.LENGTH_SHORT).show();
                                        baja = new BajaRemota(apunteParaEliminar.getId());
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
                                apunteParaEliminar.getTexto(textoApunte) + "?")
                        .create();
                dialog.show();
            }
        }));


        // Listener del FAB
        fabAgregarApunte.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                idFK = (int) idCuaderno;
                // Simplemente cambiamos de actividad
                Intent intent = new Intent(MainActivityApuntes.this,
                        AgregarApunteActivity.class);
                intent.putExtra("idCuaderno", idFK);
                //Toast.makeText(MainActivityApuntes.this, ""+idFK,Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });
        fabVolver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                // Simplemente cambiamos de actividad
                Intent intent = new Intent(MainActivityApuntes.this,
                        MainActivity.class);
                startActivity(intent);

            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Apunte apunteSeleccionado = listaDeApuntes.get(posicion);
              //  Intent intent = new Intent(MainActivityApuntes.this,
                       // EditarApunteActivity.class);
               // intent.putExtra("idApunte", apunteSeleccionado.getId());
               // intent.putExtra("textoApunte", apunteSeleccionado.getTexto("textoApunte"));
                //startActivity(intent);
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
                URL url = new URL("http://192.168.0.27/ApiRestBitacora/apuntes.php?idCuaderno=1");
                //URL url = new URL("http://192.168.0.11/ApiRestBitacora/apuntes.php?idCuaderno=1");
               // URL url = new URL("http://192.168.1.21/ApiRestBitacora/apuntes.php?idCuaderno=1");

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

    private class BajaRemota extends AsyncTask<Void, Void, String> {
        // Atributos
        String idApunte;

        // Constructor
        public BajaRemota(long id) {
            this.idApunte = String.valueOf(id);
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
                //URI baseUri = new URI("http://192.168.0.11/ApiRestBitacora/apuntes.php");
                //URI baseUri = new URI("http://192.168.1.21/ApiRestBitacora/apuntes.php?idCuaderno=1");
                String[] parametros = {"idApunte", String.valueOf(this.idApunte)};
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
            // Toast.makeText(MainActivity.this, "Actualizando datos...",
            // Toast.LENGTH_SHORT).show();
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