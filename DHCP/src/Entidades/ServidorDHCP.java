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
                System.out.println("Message Type: " + Byte.toUnsignedInt(pDHCP.getMessageType()));
                System.out.println("Requested IP Address: " + Byte.toUnsignedInt(pDHCP.getRequestedIpAddress()[0]) + "." + Byte.toUnsignedInt(pDHCP.getRequestedIpAddress()[1]) + "." + Byte.toUnsignedInt(pDHCP.getRequestedIpAddress()[2]) + "." + Byte.toUnsignedInt(pDHCP.getRequestedIpAddress()[3]));
                System.out.println("Parameter Request List:");
                for (Integer i : pDHCP.getParameterRequestList()) {
                    System.out.println(i);
                }
                //FIN IMPRESIONES
                
                //Se revisa que sea BOOTREQUEST
                if(Byte.toUnsignedInt(pDHCP.getOp()) == 1){
                    //Se obtiene el cliente que envió el mensaje
                    Cliente cliente = obtenerCliente(pDHCP);
                    //Se continua si el cliente está en una subred manejada por el servidor
                    if(cliente != null){
                        int tipoM = Byte.toUnsignedInt(pDHCP.getMessageType());
                        if(tipoM == 1){ //Si el mensaje recibido es DHCPDISCOVER
                            //PaqueteDHCP respuesta = crearPaqueteDHCPOffer(cliente, pDHCP);
                        }
                        else if(tipoM == 3){ //Si el mensaje recibido es DHCPREQUEST
                            
                        }
                        else if(tipoM == 7){ //Si el mensaje recibido es DHCPRELEASE
                            
                        }
                    }
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
    
    public void crearPaqueteDHCPOffer(Cliente cliente, PaqueteDHCP paquete){
        boolean direccionAsignada=false;
        PaqueteDHCP paqueteOffer=new PaqueteDHCP();
        if(cliente.getArrendamientoActual()!=null && cliente.getArrendamientoActual().getDireccionIp().isDisponible() && this.ofertas.get(cliente.getArrendamientoActual().getDireccionIp())==null){
            direccionAsignada=true;
            paqueteOffer.setSiaddr(cliente.getArrendamientoActual().getDireccionIp().getDireccion());
        }else if(cliente.getArrendamientoAnterior()!=null){
            direccionAsignada=true;
        }else if(paquete.getRequestedIpAddress()!=null && perteneceSubred(paquete.getRequestedIpAddress(),cliente.getSubred())){
            direccionAsignada=true;
        }else{
            
        }
        if(!direccionAsignada){
            System.out.println("No se encontro una direccion disponible: "+(cliente.getMac()[0]&0xFF)+"."+(cliente.getMac()[1]&0xFF)+"."+(cliente.getMac()[2]&0xFF)+"."+(cliente.getMac()[3]&0xFF)+"."+(cliente.getMac()[4]&0xFF)+"."+(cliente.getMac()[5]&0xFF));
            System.out.println("En la subred: "+(cliente.getSubred().getDireccionIp()[0]&0xFF)+"."+(cliente.getSubred().getDireccionIp()[1]&0xFF)+"."+(cliente.getSubred().getDireccionIp()[2]&0xFF)+"."+(cliente.getSubred().getDireccionIp()[3]&0xFF));
            return;
        }
        paqueteOffer.setOp((byte)2);
        paqueteOffer.setHtype(paquete.getHtype());
        paqueteOffer.setHlen(paquete.getHlen());
        paqueteOffer.setHops((byte)0);
        paqueteOffer.setXid(paquete.getXid());
        byte[] aux=new byte[2];
        aux[0]=0;
        aux[1]=0;
        paqueteOffer.setSecs(aux);
        byte[] aux2=new byte[4];
        aux[0]=0;
        aux[1]=0;
        aux[2]=0;
        aux[3]=0;
        paqueteOffer.setCiaddr(aux2);
        
    }
    
    public boolean cofigurar() throws FileNotFoundException, IOException {
        JFileChooser selector=new JFileChooser();
        JOptionPane.showMessageDialog(null, "Indique archivo de configuracion", "Indique archivo de configuracion", JOptionPane.INFORMATION_MESSAGE);
        selector.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int resultado=selector.showOpenDialog(null);
        if(resultado==JFileChooser.ERROR_OPTION)
            return false;
        File archivo=selector.getSelectedFile();
        if(archivo==null || archivo.getName().equals("")){
            JOptionPane.showMessageDialog(null, "Nombre de archivo inválido", "Nombre de archivo inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String[] datos;
        String aux;
        FileReader file=new FileReader(archivo);
        BufferedReader lector = new BufferedReader(file);
        while((aux = lector.readLine()) != null) {
            System.out.println(aux);
            datos=aux.split(" ");
            agregarSubred(datos);
            datos=null;
        }
        lector.close();
        for (Subred subred : this.subredes) {
            completarDireccionesIp(subred);
        }
        return true;
    }
// a partir del arreglo de datos crea una subred y la anade al arreglo de subredes.
    private void agregarSubred(String[] datos) {
        Subred subred=new Subred();
        String[] direccion;
        byte[] octetos=new byte[4];
        Integer aux;
        //Direccion ip de subred
        direccion=datos[0].split("-");
        for (int i = 0; i < 4; i++) {
            aux=Integer.parseInt(direccion[i]);
            octetos[i]=aux.byteValue();
        }
        subred.setDireccionIp(octetos);
        //mascara
        byte[] octetos1=new byte[4];
        direccion=datos[1].split("-");
        for (int i = 0; i < 4; i++) {
            aux=Integer.parseInt(direccion[i]);
            octetos1[i]=aux.byteValue();
        }
        subred.setMascaraRed(octetos1);
        //primera direccion
        byte[] octetos2=new byte[4];
        direccion=datos[2].split("-");
        for (int i = 0; i < 4; i++) {
            aux=Integer.parseInt(direccion[i]);
            octetos2[i]=aux.byteValue();
        }
        DireccionIP direccionIp1=new DireccionIP();
        direccionIp1.setDireccion(octetos2);
        direccionIp1.setTiempoArrendamiento(Integer.parseInt(datos[4]));
        direccionIp1.setDisponible(true);
        subred.getDirecciones().add(0, direccionIp1);
        //ultima direccion
        direccion=datos[3].split("-");
        byte[] octetos3=new byte[4];
        for (int i = 0; i < 4; i++) {
            aux=Integer.parseInt(direccion[i]);
            octetos3[i]=aux.byteValue();
        }
        DireccionIP direccionIp2=new DireccionIP();
        direccionIp2.setDireccion(octetos3);
        direccionIp2.setTiempoArrendamiento(Integer.parseInt(datos[4]));
        direccionIp2.setDisponible(true);
        subred.getDirecciones().add(1,direccionIp2);
        //gateways
        int ngateways=Integer.parseInt(datos[5]);
        for (int i =6; i < 6+ngateways; i++) {
            byte[] gateway=new byte[4];
            direccion=datos[i].split("-");
            for (int j = 0; j < 4; j++) {
                aux=Integer.parseInt(direccion[j]);
                gateway[j]=aux.byteValue();
            }
            subred.getGateway().add(gateway);
        }
        //dns
        int ndns=Integer.parseInt(datos[6+ngateways]);
        for (int i = 7+ngateways; i < 7+ngateways+ndns; i++) {
            byte[] dns=new byte[4];
            direccion=datos[i].split("-");
            for (int j = 0; j < 4; j++) {
                aux=Integer.parseInt(direccion[j]);
                dns[j]=aux.byteValue();
            }
            subred.getDns().add(dns);
        }
        this.subredes.add(subred);
        
    }
//Para una subred genera las direcciones posibles entre la posicion 0 de direcciones y posicion  1 del arreglo de direcciones. (Primera y ultima direccion)
    private void completarDireccionesIp(Subred subred) {
        byte[] ultima=subred.getDirecciones().get(1).getDireccion();
        byte[] aux=new byte[4];
        boolean fin=false;
        for (int i = 0; i < 4; i++) {
            aux[i]=subred.getDirecciones().get(0).getDireccion()[i];
        }
        int ndir=1;
        do{
            DireccionIP ip=new DireccionIP();
            if((aux[3]&0xFF)==255){
                aux[3]=0;
                if((aux[2]&0xFF)==255){
                    aux[2]=0;
                    if((aux[1]&0xFF)==255){
                        aux[1]=0;
                        if((aux[0]&0xFF)==255){
                            aux[0]=0;
                        }else{
                            aux[0]+=1;
                        }
                    }else{
                        aux[1]+=1;
                    }
                }else{
                    aux[2]+=1;
                }
            }else{
                aux[3]+=1;
            }
            
            fin=false;
            for (int i = 0; i < 4; i++) {
                fin=true;
                if((aux[i]& 0xFF)!=(ultima[i]& 0xFF)){
                    fin=false;
                    break;
                }
            }
            if(fin)
                break;
            else{
                byte[] direccion=new byte[4];
                for (int i = 0; i < 4; i++) {
                    direccion[i]=aux[i];
                }
                ip.setDisponible(true);
                ip.setTiempoArrendamiento(subred.getDirecciones().get(0).getTiempoArrendamiento());
                ip.setDireccion(direccion);
                subred.getDirecciones().add(ndir, ip);
                ndir++;
            }
        }while(!fin);
    }  
}
