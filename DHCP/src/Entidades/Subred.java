/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.util.ArrayList;

/**
 *
 * @author Danny
 */
public class Subred {
    private byte[] direccionIp;
    private byte[] mascaraRed;
    private int tiempoArrendamiento;
    private ArrayList<byte[]> gateway;
    private ArrayList<byte[]> dns;
    private ArrayList<DireccionIP> direcciones;

    public Subred() {
        direccionIp=new byte[4];
        mascaraRed=new byte[4];
        gateway=new ArrayList<>();
        dns = new ArrayList<>();
        direcciones=new ArrayList();
    }

    public byte[] getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(byte[] direccionIp) {
        this.direccionIp = direccionIp;
    }

    public byte[] getMascaraRed() {
        return mascaraRed;
    }

    public void setMascaraRed(byte[] mascaraRed) {
        this.mascaraRed = mascaraRed;
    }

    public ArrayList<byte[]> getGateway() {
        return gateway;
    }

    public void setGateway(ArrayList<byte[]> gateway) {
        this.gateway = gateway;
    }

    public ArrayList<byte[]> getDns() {
        return dns;
    }

    public void setDns(ArrayList<byte[]> dns) {
        this.dns = dns;
    }

    public ArrayList<DireccionIP> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(ArrayList<DireccionIP> direcciones) {
        this.direcciones = direcciones;
    }

    public int getTiempoArrendamiento() {
        return tiempoArrendamiento;
    }

    public void setTiempoArrendamiento(int tiempoArrendamiento) {
        this.tiempoArrendamiento = tiempoArrendamiento;
    }
    
    
}
