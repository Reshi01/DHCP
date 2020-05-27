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
 * Realizado por Daniel Hernández y Juan Carlos Suárez.
 */
public class Arrendamiento {
    private boolean vigente; //Booleano que indica si el arrendamiento está vigente.
    private int tiempoArrendamiento; //Tiempo de arrendamiento.
    private DireccionIP direccionIp; //Dirección IP arrendada..
    private Cliente cliente; //Cliente al que se le prestó la dirección IP.
    private LocalDateTime horaInicio; //Hora de inicio del arrendamiento.
    private LocalDateTime horaRevocacion; //Hora de revocación del arrendamiento/hora de vencimiento de oferta
    //Parámetros de configuración
    private byte[] mascara; //Máscara de red del arrendamiento
    private ArrayList<byte[]> gateway; //Lista de gateways del arrendamiento
    private ArrayList<byte[]> dns; //Lista de dns del arrendamiento
    private PaqueteDHCP solicitud; //Paquete DHCP con la solicitud inicial

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

    public void setHoraRevocacion(LocalDateTime horaRevocacion) {
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

    public PaqueteDHCP getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(PaqueteDHCP solicitud) {
        this.solicitud = solicitud;
    }
    
    
    
}
