package es.studium.bitacoraapp;
public class Cuaderno {
    private String texto;

    private long id; // El ID de la BD

    public Cuaderno(String texto) {
        this.texto = texto;
    }

    // Constructor para cuando instanciamos desde la BD
    public Cuaderno(String texto, long id) {
        this.texto = texto;
        this.id = id;
    }

    public Cuaderno() {

    }

    public long getId() {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public String getTexto(String nombreCuaderno) {
        return texto;
    }
    public void setTexto(String texto) {this.texto = texto;}

    @Override
    public String toString()
    {
        return "Cuaderno{ texto =" + texto + ""+id+'}';
    }
}
