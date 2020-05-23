/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

/**
 *
 * @author Danny
 */
public class DireccionIP {
    private byte[] direccion;
    private boolean disponible;

    public DireccionIP() {
        direccion=new byte[4];
        disponible=true;
    }

    public byte[] getDireccion() {
        return direccion;
    }

    public void setDireccion(byte[] direccion) {
        this.direccion = direccion;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
}
