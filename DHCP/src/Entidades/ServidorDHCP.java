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
import java.nio.ByteBuffer;
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
        ofertas=new HashMap<DireccionIP,Arrendamiento>();
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
                if (Byte.toUnsignedInt(pDHCP.getOp()) == 1) {
                    //Se obtiene el cliente que envió el mensaje
                    Cliente cliente = obtenerCliente(pDHCP);
                    //Se continua si el cliente está en una subred manejada por el servidor
                    if (cliente != null) {
                        int tipoM = Byte.toUnsignedInt(pDHCP.getMessageType());
                        if (tipoM == 1) { //Si el mensaje recibido es DHCPDISCOVER
                            PaqueteDHCP rDHCP = crearPaqueteDHCPOffer(cliente, pDHCP);
                            byte[] respuesta = rDHCP.construirPaquete();
                            DatagramPacket pRespuesta;
                            byte[] broadcast=new  byte[4];
                            if ((Byte.toUnsignedInt(pDHCP.getGiaddr()[0]) == 0) && (Byte.toUnsignedInt(pDHCP.getGiaddr()[1]) == 0) && (Byte.toUnsignedInt(pDHCP.getGiaddr()[2]) == 0) && (Byte.toUnsignedInt(pDHCP.getGiaddr()[3]) == 0)) {
                                if ((Byte.toUnsignedInt(pDHCP.getCiaddr()[0]) == 0) && (Byte.toUnsignedInt(pDHCP.getCiaddr()[1]) == 0) && (Byte.toUnsignedInt(pDHCP.getCiaddr()[2]) == 0) && (Byte.toUnsignedInt(pDHCP.getCiaddr()[3]) == 0)) {
                                    //Si giaddr y ciaddr son cero, se envía el mensaje a la dirección broadcast
                                    System.out.println("Band 1");
                                    broadcast[0] = (byte) '\uffff';
                                    broadcast[1] = (byte) '\uffff';
                                    broadcast[2] = (byte) '\uffff';
                                    broadcast[3] = (byte) '\uffff';
                                    pRespuesta = new DatagramPacket(respuesta, respuesta.length, InetAddress.getByAddress(broadcast), 68);
                                } else { //Si giaddr es cero y ciaddr no, se envía el mensaje a la dirección en ciaddr
                                    System.out.println("Band 2");
                                    pRespuesta = new DatagramPacket(respuesta, respuesta.length, InetAddress.getByAddress(pDHCP.getCiaddr()), 68);
                                }
                            } else { //Si giaddr no es cero, se envía el mensaje al puerto de servidor DHCP de la dirección en ese campo
                                System.out.println("Band 3");
                                pRespuesta = new DatagramPacket(respuesta, respuesta.length, InetAddress.getByAddress(pDHCP.getGiaddr()), 67);
                            }
                            System.out.println("Direccion: "+broadcast[0]+broadcast[1]+broadcast[2]+broadcast[3]);
                            socket.send(pRespuesta); //Se envía el paquete

                        } else if (tipoM == 3) { //Si el mensaje recibido es DHCPREQUEST

                        } else if (tipoM == 7) { //Si el mensaje recibido es DHCPRELEASE

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
            if (perteneceSubred(dir, s)) {
                int numSR = subredes.indexOf(s);
                Pair<Integer, byte[]> llave = new Pair<Integer, byte[]>(numSR, mac);
                Cliente cliente = clientes.get(llave);
                //Si el cliente no existe, se crea
                if (cliente == null) {
                    Cliente nuevoCliente = new Cliente(mac, s);
                    clientes.put(llave, nuevoCliente);
                    return nuevoCliente;
                } else {
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

    public PaqueteDHCP crearPaqueteDHCPOffer(Cliente cliente, PaqueteDHCP paqueteDiscover) {
        //direccionAsignada verifica que si se pudo asignar una direccion
        //igual se usa para buescar una DireccionIP que tenga la direccion requerida por el cliente
        //nuevo indica si el arrendamiento es nuevo o es un actual
        boolean direccionAsignada = false, igual = true, nuevo = true;
        PaqueteDHCP paqueteOffer = new PaqueteDHCP();
        Arrendamiento nuevoArrendamiento = new Arrendamiento();
        //Se selecciona una direccion ip para prestar
        //Si el cliente tiene una dirección actualmente prestada, escoger esa. 
        if (cliente.getArrendamientoActual() != null && cliente.getArrendamientoActual().getDireccionIp().isDisponible() && this.ofertas.get(cliente.getArrendamientoActual().getDireccionIp()) == null) {
            direccionAsignada = true;
            nuevo = false;
            paqueteOffer.setYiaddr(cliente.getArrendamientoActual().getDireccionIp().getDireccion());
            paqueteOffer.setIpAddressLeaseTime(intToByteArray(cliente.getArrendamientoActual().getTiempoArrendamiento()));
            nuevoArrendamiento.setTiempoArrendamiento(cliente.getArrendamientoActual().getTiempoArrendamiento());
        //En caso contrario, escoger la dirección anterior del cliente, si está disponible
        } else if (cliente.getArrendamientoAnterior() != null && cliente.getArrendamientoAnterior().getDireccionIp().isDisponible() && this.ofertas.get(cliente.getArrendamientoAnterior().getDireccionIp()) == null) {
            direccionAsignada = true;
            paqueteOffer.setYiaddr(cliente.getArrendamientoAnterior().getDireccionIp().getDireccion());
            nuevoArrendamiento.setDireccionIp(cliente.getArrendamientoAnterior().getDireccionIp());
        //En caso contrario se escoge la dirección solicitada, si está libre. 
        } else if (paqueteDiscover.getRequestedIpAddress() != null && perteneceSubred(paqueteDiscover.getRequestedIpAddress(), cliente.getSubred())) {
            byte[] aux = paqueteDiscover.getRequestedIpAddress();
            for (DireccionIP direcciones : cliente.getSubred().getDirecciones()) {
                igual = true;
                for (int i = 0; i < 4; i++) {
                    if (aux[i] != direcciones.getDireccion()[i]) {
                        igual = false;
                    }
                }
                if (igual && direcciones.isDisponible() && !this.ofertas.containsKey(direcciones)) {
                    direccionAsignada = true;
                    paqueteOffer.setYiaddr(paqueteDiscover.getRequestedIpAddress());
                    nuevoArrendamiento.setDireccionIp(direcciones);
                    break;
                }
            }
        } 
        //En caso contrario, escoger una dirección de la subred apropiada que esté disponible. 
        if(!direccionAsignada){
            for (DireccionIP direcciones : cliente.getSubred().getDirecciones()) {
                if (direcciones.isDisponible()) {
                    direccionAsignada = true;
                    paqueteOffer.setYiaddr(direcciones.getDireccion());
                    nuevoArrendamiento.setDireccionIp(direcciones);
                }
            }
        }
        //En caso contrario, imprimir mensaje de error y termina
        if (!direccionAsignada) {
            System.out.println("No se encontro una direccion disponible: " + (cliente.getMac()[0] & 0xFF) + "." + (cliente.getMac()[1] & 0xFF) + "." + (cliente.getMac()[2] & 0xFF) + "." + (cliente.getMac()[3] & 0xFF) + "." + (cliente.getMac()[4] & 0xFF) + "." + (cliente.getMac()[5] & 0xFF));
            System.out.println("En la subred: " + (cliente.getSubred().getDireccionIp()[0] & 0xFF) + "." + (cliente.getSubred().getDireccionIp()[1] & 0xFF) + "." + (cliente.getSubred().getDireccionIp()[2] & 0xFF) + "." + (cliente.getSubred().getDireccionIp()[3] & 0xFF));
            return null;
        }
        //Se configuran los parametros del mensaje
        paqueteOffer.setOp((byte) 2);
        paqueteOffer.setHtype(paqueteDiscover.getHtype());
        paqueteOffer.setHlen(paqueteDiscover.getHlen());
        paqueteOffer.setHops((byte) 0);
        paqueteOffer.setXid(paqueteDiscover.getXid());
        byte[] aux = new byte[2];
        aux[0] = 0;
        aux[1] = 0;
        paqueteOffer.setSecs(aux);
        byte[] aux2 = new byte[4];
        aux2[0] = 0;
        aux2[1] = 0;
        aux2[2] = 0;
        aux2[3] = 0;
        paqueteOffer.setCiaddr(aux2);
        paqueteOffer.setSiaddr(this.ip.getAddress());
        paqueteOffer.setFlags(paqueteDiscover.getFlags());
        paqueteOffer.setGiaddr(paqueteDiscover.getGiaddr());
        paqueteOffer.setChaddr(paqueteDiscover.getChaddr());
        byte[] aux3 = new byte[64];
        paqueteOffer.setSname(aux3);
        paqueteOffer.setFile(paqueteDiscover.getFile());
        //Sellecion del tiempo de arrendamiento
        //Si el cliente pide un tiempo especifico se le permite tenerlo
        if (paqueteDiscover.getIpAddressLeaseTime() != null) {
            paqueteOffer.setIpAddressLeaseTime(paqueteDiscover.getIpAddressLeaseTime());
            nuevoArrendamiento.setTiempoArrendamiento(byteArrayToInt(paqueteDiscover.getIpAddressLeaseTime()));
        //Si el cliente no pide una direccion, y no tiene arrendamiento actual se le asigna el tiempo por defecto. El arrendamiento actual se revisa en el primer condicional del metodo.
        } else if (paqueteOffer.getIpAddressLeaseTime() == null) {
            paqueteOffer.setIpAddressLeaseTime(intToByteArray(cliente.getSubred().getTiempoArrendamiento()));
            nuevoArrendamiento.setTiempoArrendamiento(cliente.getSubred().getTiempoArrendamiento());
        }
        //Se configuran las opciones del mensaje
        paqueteOffer.setMessageType((byte) 2);
        paqueteOffer.setServerIdentiferier(this.ip.getAddress());
        paqueteOffer.setSubnetMask(cliente.getSubred().getMascaraRed());
        paqueteOffer.setDns(cliente.getSubred().getDns());
        paqueteOffer.setRouter(cliente.getSubred().getGateway());
        //Si el arrendamiento es nuevo se configuran los parametros del arrendamiento y se coloca en lista de ofertas
        if (nuevo) {
            nuevoArrendamiento.setVigente(false);
            nuevoArrendamiento.setCliente(cliente);
            nuevoArrendamiento.setMascara(cliente.getSubred().getMascaraRed());
            nuevoArrendamiento.setDns(cliente.getSubred().getDns());
            nuevoArrendamiento.setGateway(cliente.getSubred().getGateway());
            this.ofertas.put(nuevoArrendamiento.getDireccionIp(), nuevoArrendamiento);
        }
        return paqueteOffer;
    }

    public boolean cofigurar() throws FileNotFoundException, IOException {
        JFileChooser selector = new JFileChooser();
        JOptionPane.showMessageDialog(null, "Indique archivo de configuracion", "Indique archivo de configuracion", JOptionPane.INFORMATION_MESSAGE);
        //se pide que se indique la direccion del archivo de configuracion
        selector.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int resultado = selector.showOpenDialog(null);
        if (resultado == JFileChooser.ERROR_OPTION) {
            return false;
        }
        File archivo = selector.getSelectedFile();
        //se revisa que la direccion dada sea valida
        if (archivo == null || archivo.getName().equals("")) {
            JOptionPane.showMessageDialog(null, "Nombre de archivo inválido", "Nombre de archivo inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String[] datos;
        String aux;
        //se habre el lector del archivo
        FileReader file = new FileReader(archivo);
        BufferedReader lector = new BufferedReader(file);
        //se leen e imprimen las lineas del texto
        while ((aux = lector.readLine()) != null) {
            System.out.println(aux);
            datos = aux.split(" ");
            agregarSubred(datos);
            datos = null;
        }
        lector.close();
        //por cada subred se generan las redes posibles
        for (Subred subred : this.subredes) {
            completarDireccionesIp(subred);
        }
        return true;
    }
    
    // a partir del arreglo de datos crea una subred y la anade al arreglo de subredes.
    //Para cada direccion se divide en los octetos(Strings), los cuales se convierten a integer y luego a byte
    private void agregarSubred(String[] datos) {
        Subred subred = new Subred();
        String[] direccion;
        byte[] octetos = new byte[4];
        Integer aux;
        //Direccion ip de subred
        direccion = datos[0].split("-");
        for (int i = 0; i < 4; i++) {
            aux = Integer.parseInt(direccion[i]);
            octetos[i] = aux.byteValue();
        }
        subred.setDireccionIp(octetos);
        //mascara
        byte[] octetos1 = new byte[4];
        direccion = datos[1].split("-");
        for (int i = 0; i < 4; i++) {
            aux = Integer.parseInt(direccion[i]);
            octetos1[i] = aux.byteValue();
        }
        subred.setMascaraRed(octetos1);
        //primera direccion
        byte[] octetos2 = new byte[4];
        direccion = datos[2].split("-");
        for (int i = 0; i < 4; i++) {
            aux = Integer.parseInt(direccion[i]);
            octetos2[i] = aux.byteValue();
        }
        DireccionIP direccionIp1 = new DireccionIP();
        direccionIp1.setDireccion(octetos2);
        subred.setTiempoArrendamiento(Integer.parseInt(datos[4]));
        direccionIp1.setDisponible(true);
        subred.getDirecciones().add(0, direccionIp1);
        //ultima direccion
        direccion = datos[3].split("-");
        byte[] octetos3 = new byte[4];
        for (int i = 0; i < 4; i++) {
            aux = Integer.parseInt(direccion[i]);
            octetos3[i] = aux.byteValue();
        }
        DireccionIP direccionIp2 = new DireccionIP();
        direccionIp2.setDireccion(octetos3);
        direccionIp2.setDisponible(true);
        subred.getDirecciones().add(1, direccionIp2);
        //gateways
        int ngateways = Integer.parseInt(datos[5]);
        for (int i = 6; i < 6 + ngateways; i++) {
            byte[] gateway = new byte[4];
            direccion = datos[i].split("-");
            for (int j = 0; j < 4; j++) {
                aux = Integer.parseInt(direccion[j]);
                gateway[j] = aux.byteValue();
            }
            subred.getGateway().add(gateway);
        }
        //dns
        int ndns = Integer.parseInt(datos[6 + ngateways]);
        for (int i = 7 + ngateways; i < 7 + ngateways + ndns; i++) {
            byte[] dns = new byte[4];
            direccion = datos[i].split("-");
            for (int j = 0; j < 4; j++) {
                aux = Integer.parseInt(direccion[j]);
                dns[j] = aux.byteValue();
            }
            subred.getDns().add(dns);
        }
        this.subredes.add(subred);

    }
//Para una subred genera las direcciones posibles entre la posicion 0 de direcciones y posicion  1 del arreglo de direcciones. (Primera y ultima direccion)
//Se revisa si la suma es posible sin salirse del rango (<=255)
    private void completarDireccionesIp(Subred subred) {
        byte[] ultima = subred.getDirecciones().get(1).getDireccion();
        byte[] aux = new byte[4];
        boolean fin = false;
        for (int i = 0; i < 4; i++) {
            aux[i] = subred.getDirecciones().get(0).getDireccion()[i];
        }
        int ndir = 1;
        do {
            DireccionIP ip = new DireccionIP();
            if ((aux[3] & 0xFF) == 255) {
                aux[3] = 0;
                if ((aux[2] & 0xFF) == 255) {
                    aux[2] = 0;
                    if ((aux[1] & 0xFF) == 255) {
                        aux[1] = 0;
                        if ((aux[0] & 0xFF) == 255) {
                            aux[0] = 0;
                        } else {
                            aux[0] += 1;
                        }
                    } else {
                        aux[1] += 1;
                    }
                } else {
                    aux[2] += 1;
                }
            } else {
                aux[3] += 1;
            }

            fin = false;
            for (int i = 0; i < 4; i++) {
                fin = true;
                if ((aux[i] & 0xFF) != (ultima[i] & 0xFF)) {
                    fin = false;
                    break;
                }
            }
            if (fin) {
                break;
            } else {
                byte[] direccion = new byte[4];
                for (int i = 0; i < 4; i++) {
                    direccion[i] = aux[i];
                }
                ip.setDisponible(true);
                ip.setDireccion(direccion);
                subred.getDirecciones().add(ndir, ip);
                ndir++;
            }
        } while (!fin);
    }

    private byte[] intToByteArray(int data) {
        return new byte[]{
            (byte) ((data >> 24) & 0xff),
            (byte) ((data >> 16) & 0xff),
            (byte) ((data >> 8) & 0xff),
            (byte) ((data >> 0) & 0xff)};
    }

    private int byteArrayToInt(byte[] data) {
       return ByteBuffer.wrap(data).getInt();
    }
}
