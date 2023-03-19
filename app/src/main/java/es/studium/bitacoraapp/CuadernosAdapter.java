package es.studium.bitacoraapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class CuadernosAdapter extends RecyclerView.Adapter<CuadernosAdapter.MyViewHolder>
{
    private ArrayList<Cuaderno> listaDeCuadernos;

    public void setListaDeCuadernos(ArrayList<Cuaderno> listaDeCuadernos)
    {
        this.listaDeCuadernos = listaDeCuadernos;
    }
    public CuadernosAdapter(ArrayList<Cuaderno> cuadernos)
    {
        this.listaDeCuadernos = cuadernos;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
            View filaCuaderno = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fila_cuaderno,
                viewGroup, false);
        return new MyViewHolder(filaCuaderno);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder  myViewHolder, int i) {
        // Obtener la frase de nuestra lista gracias al índice i
        Cuaderno cuaderno = listaDeCuadernos.get(i);
        // Obtener los datos de la lista
        String textoCuaderno = cuaderno.getTexto("nombreCuaderno");
        //long idCuaderno = cuaderno.getId(getItemId(i));
        // Y poner a los TextView los datos con setText
        myViewHolder.texto.setText(textoCuaderno);
       // myViewHolder.itemView.getContext(idCuaderno);
        //myViewHolder.getItemId();
    }

    @Override
    public int getItemCount()
    {
        return listaDeCuadernos.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView texto;
        MyViewHolder(View itemView)
        {
            super(itemView);
            this.texto = itemView.findViewById(R.id.txtCuaderno);
        }
    }
}
