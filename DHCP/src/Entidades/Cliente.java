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
public class Cliente {
    private byte[] mac;
    private Arrendamiento arrendmainetoActual;
    private Arrendamiento arrendamientoAnterior;

    public Cliente() {
        mac=new byte[6];
    }

    public byte[] getMac() {
        return mac;
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }

    public Arrendamiento getArrendmainetoActual() {
        return arrendmainetoActual;
    }

    public void setArrendmainetoActual(Arrendamiento arrendmainetoActual) {
        this.arrendmainetoActual = arrendmainetoActual;
    }

    public Arrendamiento getArrendamientoAnterior() {
        return arrendamientoAnterior;
    }

    public void setArrendamientoAnterior(Arrendamiento arrendamientoAnterior) {
        this.arrendamientoAnterior = arrendamientoAnterior;
    }
    
    
}
