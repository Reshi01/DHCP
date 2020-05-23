/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Danny
 */
public class ServidorDHCP {

    private InetAddress ip;
    private DatagramSocket socket;
    private ArrayList<Subred> subredes;
    private Map<Pair<Integer, byte[]>, Cliente> clientes;
    private Map<Pair<byte[], byte[]>, Arrendamiento> arrendamientos;
    private Map<DireccionIP, Arrendamiento> ofertas;

    public ServidorDHCP() {
        subredes = new ArrayList<>();
        clientes = new HashMap<Pair<Integer, byte[]>, Cliente>();
        arrendamientos = new HashMap<Pair<byte[], byte[]>, Arrendamiento>();
    }

    public void correrServidor() {
        byte[] bytesR = new byte[65535];
        DatagramPacket dRecibido;
        PaqueteDHCP pDHCP;
        try {
            ip = InetAddress.getLocalHost();
            socket = new DatagramSocket(67);
            while (true) {
                dRecibido = new DatagramPacket(bytesR, bytesR.length);
                socket.receive(dRecibido);
                pDHCP = new PaqueteDHCP(bytesR);

                //IMPRESIONES PARA DEPURAR
                System.out.println("Mensaje Recibido:");
                System.out.println("op:" + Byte.toUnsignedInt(pDHCP.getOp()));
                System.out.println("Requested IP Address: " + Byte.toUnsignedInt(pDHCP.getRequestedIpAddress()[0]) + "." + Byte.toUnsignedInt(pDHCP.getRequestedIpAddress()[1]) + "." + Byte.toUnsignedInt(pDHCP.getRequestedIpAddress()[2]) + "." + Byte.toUnsignedInt(pDHCP.getRequestedIpAddress()[3]));
                System.out.println("Parameter Request List:");
                for (Integer i : pDHCP.getParameterRequestList()) {
                    System.out.println(i);
                }
            }

        } catch (UnknownHostException ex) {
            Logger.getLogger(ServidorDHCP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(ServidorDHCP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServidorDHCP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Cliente obtenerCliente(PaqueteDHCP paquete) {
        byte[] dir = new byte[4];
        byte[] subR = new byte[4];
        byte[] mac = new byte[6]; //Dirección MAC del cliente. (Se asume que la dirección HW es MAC)
        for (int i = 0; i < 6; i++) {
            mac[i] = paquete.getChaddr()[i];
        }
        //Si giaddr es 0, el cliente está en la misma subred que el servidor
        if ((Byte.toUnsignedInt(paquete.getGiaddr()[0]) == 0) && (Byte.toUnsignedInt(paquete.getGiaddr()[1]) == 0) && (Byte.toUnsignedInt(paquete.getGiaddr()[2]) == 0) && (Byte.toUnsignedInt(paquete.getGiaddr()[3]) == 0)) {
            byte[] ipServidor = ip.getAddress();
            dir[0] = ipServidor[0];
            dir[1] = ipServidor[1];
            dir[2] = ipServidor[2];
            dir[3] = ipServidor[3];
        } //En caso contrario, está en la misma subred que la dirección en giaddr
        else {
            dir[0] = paquete.getGiaddr()[0];
            dir[1] = paquete.getGiaddr()[1];
            dir[2] = paquete.getGiaddr()[2];
            dir[3] = paquete.getGiaddr()[3];
        }
        //Recorrer todas las subredes
        for (Subred s : subredes) {
            //Se revisa si pertenece a esa subred
            if(perteneceSubred(dir, s)){
                int numSR = subredes.indexOf(s);
                Pair<Integer, byte[]> llave = new Pair<Integer, byte[]>(numSR, mac);
                Cliente cliente = clientes.get(llave);
                //Si el cliente no existe, se crea
                if(cliente == null){
                    Cliente nuevoCliente = new Cliente(mac, s);
                    clientes.put(llave, nuevoCliente);
                    return nuevoCliente;
                }
                else{
                    return cliente;
                }
            }
        }
        return null;
    }

    private boolean perteneceSubred(byte[] direccion, Subred subred) {
        byte[] subR = new byte[4];
        //Aplicar máscara de red
        subR[0] = (byte) (direccion[0] & subred.getMascaraRed()[0]);
        subR[1] = (byte) (direccion[1] & subred.getMascaraRed()[1]);
        subR[2] = (byte) (direccion[2] & subred.getMascaraRed()[2]);
        subR[3] = (byte) (direccion[3] & subred.getMascaraRed()[3]);

        //Revisar si la dirección de la subred coincide
        return (subR[0] == subred.getDireccionIp()[0]) && (subR[1] == subred.getDireccionIp()[1]) && (subR[2] == subred.getDireccionIp()[2]) && (subR[3] == subred.getDireccionIp()[3]);
    }

    private void manejarDiscover() {

    }

    private void manejarRequest() {

    }

    private void manejarRelease() {

    }

    private void enviarMensaje(PaqueteDHCP mensaje, byte[] ipDestino) {

    }

    public ArrayList<Subred> getSubredes() {
        return subredes;
    }

    public void setSubredes(ArrayList<Subred> subredes) {
        this.subredes = subredes;
    }

    public Map<Pair<Integer, byte[]>, Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(Map<Pair<Integer, byte[]>, Cliente> clientes) {
        this.clientes = clientes;
    }

    public Map<Pair<byte[], byte[]>, Arrendamiento> getArrendamientos() {
        return arrendamientos;
    }

    public void setArrendamientos(Map<Pair<byte[], byte[]>, Arrendamiento> arrendamientos) {
        this.arrendamientos = arrendamientos;
    }

    public Map<DireccionIP, Arrendamiento> getOfertas() {
        return ofertas;
    }

    public void setOfertas(Map<DireccionIP, Arrendamiento> ofertas) {
        this.ofertas = ofertas;
    }
}
