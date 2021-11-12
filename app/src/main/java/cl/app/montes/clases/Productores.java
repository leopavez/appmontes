package cl.app.montes.clases;

public class Productores {

    int id = 0;
    String rut;
    String razon_social;

    public Productores() {

    }

    public Productores(int id, String rut, String razon_social) {
        this.id = id;
        this.rut = rut;
        this.razon_social = razon_social;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }
}
