package es.studium.bitacoraapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

class ApuntesAdapter extends RecyclerView.Adapter<ApuntesAdapter.MyViewHolder>
{
    private List<Apunte> listaDeApuntes;
    public void setListaDeApuntes(List<Apunte> listaDeApuntes)
    {
        this.listaDeApuntes = listaDeApuntes;
    }
    public ApuntesAdapter(List<Apunte> apuntes)
    {
        this.listaDeApuntes = apuntes;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View filaApunte = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fila_apunte,
                viewGroup, false);
        return new MyViewHolder(filaApunte);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder  myViewHolder, int i) {
        // Obtener la frase de nuestra lista gracias al índice i
        Apunte apunte = listaDeApuntes.get(i);
        // Obtener los datos de la lista
        String textoApunte = apunte.getTexto("textoApunte");
        String fechaApunte = apunte.getFecha();

        // Y poner a los TextView los datos con setText
        myViewHolder.texto.setText(textoApunte);
        myViewHolder.fecha.setText(fechaApunte);

    }


    @Override
    public int getItemCount()
    {
        return listaDeApuntes.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView  fecha, texto;
        MyViewHolder(View itemView)
        {
            super(itemView);
            this.texto = itemView.findViewById(R.id.txtApunte);
            this.fecha = itemView.findViewById(R.id.txtFecha);

        }
    }
}
