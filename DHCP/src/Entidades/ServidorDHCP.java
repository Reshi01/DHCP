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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Danny
 */
public class ServidorDHCP {
    private ArrayList<Subred> subredes;
    private Map<Pair<Integer,byte[]>,Cliente> clientes;
    private Map<Pair<byte[],byte[]>,Arrendamiento> arrendamientos;
    private Map<DireccionIP,Arrendamiento> ofertas;

    public ServidorDHCP() {
        subredes=new ArrayList<>();
        clientes=new HashMap<Pair<Integer,byte[]>,Cliente>();
        arrendamientos=new HashMap<Pair<byte[],byte[]>,Arrendamiento>();        
    }
    
    public void correrServidor(){
        
    }
    
    private void manejarDiscover(){
        
    }
    
    private void manejarRequest(){
        
    }
    
    private void manejarRelease(){
        
    }
    
    private void enviarMensaje(PaqueteDHCP mensaje, byte[] ipDestino){
        
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
            datos=aux.split(aux);
            agregarSubred(datos);
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
        direccion=datos[0].split(".");
        for (int i = 0; i < 4; i++) {
            aux=Integer.parseInt(direccion[i]);
            octetos[i]=aux.byteValue();
        }
        subred.setDireccionIp(octetos);
        //mascara
        direccion=datos[1].split(".");
        for (int i = 0; i < 4; i++) {
            aux=Integer.parseInt(direccion[i]);
            octetos[i]=aux.byteValue();
        }
        subred.setMascaraRed(octetos);
        //primera direccion
        direccion=datos[2].split(".");
        for (int i = 0; i < 4; i++) {
            aux=Integer.parseInt(direccion[i]);
            octetos[i]=aux.byteValue();
        }
        DireccionIP direccionIp1=new DireccionIP();
        direccionIp1.setDireccion(octetos);
        direccionIp1.setTiempoArrendamiento(Integer.parseInt(datos[4]));
        direccionIp1.setDisponible(true);
        subred.getDirecciones().add(direccionIp1);
        //ultima direccion
        direccion=datos[3].split(".");
        for (int i = 0; i < 4; i++) {
            aux=Integer.parseInt(direccion[i]);
            octetos[i]=aux.byteValue();
        }
        DireccionIP direccionIp2=new DireccionIP();
        direccionIp2.setDireccion(octetos);
        direccionIp2.setTiempoArrendamiento(Integer.parseInt(datos[4]));
        direccionIp2.setDisponible(true);
        subred.getDirecciones().add(direccionIp2);
        //gateways
        int ngateways=Integer.parseInt(datos[5]);
        for (int i =6; i < 6+ngateways; i++) {
            byte[] gateway=new byte[4];
            direccion=datos[i].split(".");
            for (int j = 0; j < 4; j++) {
                aux=Integer.parseInt(direccion[j]);
                gateway[j]=aux.byteValue();
            }
            subred.getGateway().add(gateway);
        }
        //dns
        int ndns=Integer.parseInt(datos[5+ngateways]);
        for (int i = 6+ngateways; i < 6+ngateways+ndns; i++) {
            byte[] dns=new byte[4];
            direccion=datos[i].split(".");
            for (int j = 0; j < 4; j++) {
                aux=Integer.parseInt(direccion[j]);
                dns[j]=aux.byteValue();
            }
            subred.getGateway().add(dns);
        }
    }
//Para una subred genera las direcciones posibles entre la posicion 0 de direcciones y posicion  1 del arreglo de direcciones. (Primera y ultima direccion)
    private void completarDireccionesIp(Subred subred) {
        byte[] direccion=new byte[4];
        boolean fin=false;
        direccion=subred.getDirecciones().get(0).getDireccion();
        while(!fin){
            DireccionIP ip=new DireccionIP();
            direccion[0]+=1;
            if((direccion[0]& 0xFF)==0){
                direccion[1]+=1;
            }
            if((direccion[1]& 0xFF)==0){
                direccion[2]+=1;
            }
            if((direccion[2]& 0xFF)==0){
                direccion[3]+=1;
            }
            fin=false;
            for (int i = 0; i < 4; i++) {
                if(direccion[i]!=subred.getDirecciones().get(subred.getDirecciones().size()).getDireccion()[i])
                    fin=true;
            }
            if(fin)
                break;
            else{
                
            }
        }
    }
}
