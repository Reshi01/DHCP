/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhcp;

import Entidades.ServidorDHCP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Danny
 */
public class DHCP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ServidorDHCP servidor=new ServidorDHCP();
        if(!servidor.cofigurar()){
            System.out.println("Error al configurar servidor");
            return;
        }
        System.out.println("");
        System.out.println("Iniciando Ejecucion del Servidor");
                System.out.println("");
        servidor.correrServidor();
    }
    
}
