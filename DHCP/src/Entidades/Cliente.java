/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

/**
 *
 * Realizado por Daniel Hernández y Juan Carlos Suárez.
 */
public class Cliente {
    private byte[] mac; //Dirección MAC del cliente
    private Subred subred; //Subred del cliente
    private Arrendamiento arrendamientoActual; //Arrendamiento actual del cliente
    private Arrendamiento arrendamientoAnterior; //Arrendamiento anterior del cliente

    public Cliente() {
        mac=new byte[6];
    }
    
    public Cliente(byte[] dMac, Subred subR){
        mac = dMac;
        subred = subR;
    }

    public byte[] getMac() {
        return mac;
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }

    public Subred getSubred() {
        return subred;
    }

    public void setSubred(Subred subred) {
        this.subred = subred;
    }
    
    public Arrendamiento getArrendamientoActual() {
        return arrendamientoActual;
    }

    public void setArrendamientoActual(Arrendamiento arrendmainetoActual) {
        this.arrendamientoActual = arrendmainetoActual;
    }

    public Arrendamiento getArrendamientoAnterior() {
        return arrendamientoAnterior;
    }

    public void setArrendamientoAnterior(Arrendamiento arrendamientoAnterior) {
        this.arrendamientoAnterior = arrendamientoAnterior;
    }
    
    
}
