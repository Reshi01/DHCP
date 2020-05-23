/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Danny
 */
public class Arrendamiento {
    private boolean vigente;
    private int tiempoArrendamiento;
    private DireccionIP direccionIp;
    private Cliente cliente;
    private LocalDateTime horaInicio;
    private LocalDateTime horaRevocacion;
    private byte[] mascara;
    //parametrod de configuracion
    private ArrayList<byte[]> gateway;
    private ArrayList<byte[]> dns;

    public Arrendamiento() {
        vigente=false;
        mascara=new byte[4];
        gateway=new ArrayList<>();
        dns=new ArrayList<>();
    }

    public boolean isVigente() {
        return vigente;
    }

    public void setVigente(boolean vigente) {
        this.vigente = vigente;
    }

    public DireccionIP getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(DireccionIP direccionIp) {
        this.direccionIp = direccionIp;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraRevocacion() {
        return horaRevocacion;
    }

    public void setHoraRenovacion(LocalDateTime horaRevocacion) {
        this.horaRevocacion = horaRevocacion;
    }

    public byte[] getMascara() {
        return mascara;
    }

    public void setMascara(byte[] mascara) {
        this.mascara = mascara;
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

    public int getTiempoArrendamiento() {
        return tiempoArrendamiento;
    }

    public void setTiempoArrendamiento(int tiempoArrendamiento) {
        this.tiempoArrendamiento = tiempoArrendamiento;
    }
    
    
    
}
