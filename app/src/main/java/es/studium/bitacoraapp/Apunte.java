package es.studium.bitacoraapp;
public class Apunte {
    private String texto;
    private String fecha;
    private long id; // El ID de la BD
    private long idFK;

    public Apunte(String fecha, String texto, long idFK ) {
        this.fecha = fecha;
        this.texto = texto;
        this.idFK = idFK;
    }

    // Constructor para cuando instanciamos desde la BD
    public Apunte(String fecha, String texto, long id, long idFK) {
        this.fecha = fecha;
        this.texto = texto;
        this.id = id;
        this.idFK = idFK;
    }

    public long getId() {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public String getTexto(String textoApunte)
    {
        return texto;
    }
    public void setTexto(String texto)
    {
        this.texto = texto;
    }
    public String getFecha()
    {
        return fecha;
    }
    public void setFecha(String fecha)
    {
        this.fecha = fecha;
    }


    @Override
    public String toString()
    {
        return "Apunte{" +
                "fecha='" + fecha + '\'' +
                ",texto=" + idFK +
                '}';
    }
}
