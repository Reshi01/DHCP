/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.util.ArrayList;

/**
 *
 * Realizado por Daniel Hernández y Juan Carlos Suárez.
 */
public class PaqueteDHCP {
    private byte op; //Contenido de campo op
    private byte htype; //Contenido de campo htype
    private byte hlen; //Contenido de campo hlen
    private byte hops; //Contenido de campo hops
    private byte[] xid; //Contenido de campo xid
    private byte[] secs; //Contenido de campo secs
    private byte[] flags; //Contenido de campo flags
    private byte[] ciaddr; //Contenido de campo ciaddr
    private byte[] yiaddr; //Contenido de campo yiaddr
    private byte[] siaddr; //Contenido de campo siaddr
    private byte[] giaddr; //Contenido de campo giaddr
    private byte[] chaddr; //Contenido de campo chaddr
    private byte[] sname; //Contenido de campo sname
    private byte[] file; //Contenido de campo file
    private byte messageType; //Opción message type
    private ArrayList<Integer> parameterRequestList = null; //Arreglo con códigos incluidos en opción parameter request list
    private byte[] requestedIpAddress = null; //Opción requested IP address
    private byte[] ipAddressLeaseTime = null; //Opción IP address lease time
    private byte[] serverIdentifier = null; //Opción server identifier
    private byte[] subnetMask = null; //Opción subnetMask
    private ArrayList<byte[]> dns = null; //Arreglo con direcciones incluidas en opción dns
    private ArrayList<byte[]> router = null; //Opción con direcciones incluidas en opción router

    public PaqueteDHCP(){
        
    }
    
    //Constructor que llena los atributos de la clase a partir del arreglo de bytes del mensaje DHCP.
    public PaqueteDHCP(byte[] bytes) {
        if(bytes.length < 548){ //El tamaño mínimo del mensaje es de 548 bytes
            System.out.println("Ocurrió un problema leyendo los bytes.");
            return;
        }
        op = bytes[0];
        htype = bytes[1];
        hlen = bytes[2];
        hops = bytes[3];
        xid = new byte [4];
        for(int i = 0; i < 4; i++){
            xid[i] = bytes[i + 4];
        }
        secs=new byte[2];
        secs[0] = bytes[8];
        secs[1] = bytes[9];
        flags=new byte[2];
        flags[0] = bytes[10];
        flags[0] = bytes[11];
        ciaddr=new byte[4];
        for(int i = 0; i < 4; i++){
            ciaddr[i] = bytes[i + 12];
        }
        yiaddr=new byte[4];
        for(int i = 0; i < 4; i++){
            yiaddr[i] = bytes[i + 16];
        }
        siaddr=new byte[4];
        for(int i = 0; i < 4; i++){
            siaddr[i] = bytes[i + 20];
        }
        giaddr=new byte[4];
        for(int i = 0; i < 4; i++){
            giaddr[i] = bytes[i + 24];
        }
        chaddr=new byte[16];
        for(int i = 0; i < 16; i++){
            chaddr[i] = bytes[i + 28];
        }
        sname=new byte[64];
        for(int i = 0; i < 64; i++){
            sname[i] = bytes[i + 44];
        }
        file=new byte[128];
        for(int i = 0; i < 128; i++){
            file[i] = bytes[i + 108];
        }
        //opciones
        int i = 240; //Se saltan también los primeros 4 bytes del campo de opciones, que tienen la "magic cookie"
        int codigo;
        int tam;
        while(true){
            //Para cada opción:
            codigo = Byte.toUnsignedInt(bytes[i]);
            i++;
            if(codigo == 255){ //Opción End
                break;
            }
            tam = Byte.toUnsignedInt(bytes[i]);
            i++;
            if(codigo == 53){ //Opción DHCP Message Type
                messageType = bytes[i];
                i++;
            }
            else if(codigo == 55){ //Opción Parameter Request List
                parameterRequestList = new ArrayList<>();
                for(int j = 0; j < tam; j++){
                    parameterRequestList.add(Byte.toUnsignedInt(bytes[i]));
                    i++;
                }
            }
            else if(codigo == 50){ //Opción Requested IP Address
                requestedIpAddress = new byte[tam];
                for(int j = 0; j < tam; j++){
                    requestedIpAddress[j] = bytes[i];
                    i++;
                }
            }
            else if(codigo == 51){ //Opción IP Address Lease Time
                ipAddressLeaseTime = new byte[tam];
                for(int j = 0; j < tam; j++){
                    ipAddressLeaseTime[j] = bytes[i];
                    i++;
                }
            }
            else if(codigo == 54){ //Opción Server Identifier
                serverIdentifier = new byte[tam];
                for(int j = 0; j < tam; j++){
                    serverIdentifier[j] = bytes[i];
                    i++;
                }
            }
            else if(codigo == 1){ //Opción Subnet Mask
                subnetMask = new byte[tam];
                for(int j = 0; j < tam; j++){
                    subnetMask[j] = bytes[i];
                    i++;
                }
            }
            else if(codigo == 6){ //Opción Domain Name Service
                dns = new ArrayList<>();
                int cant = tam/4;
                for(int j = 0; j < cant; j++){
                    byte[] dir = new byte[4];
                    for(int k = 0; k < 4; k++){
                        dir[k] = bytes[i];
                        i++;
                    }
                    dns.add(dir);
                }
            }
            else if(codigo == 3){ //Opción Router
                router = new ArrayList<>();
                int cant = tam/4;
                for(int j = 0; j < cant; j++){
                    byte[] dir = new byte[4];
                    for(int k = 0; k < 4; k++){
                        dir[k] = bytes[i];
                        i++;
                    }
                    dns.add(dir);
                }
            }
            else{
                i += tam;
            }
            //Falta considerar la opción Option Overload
        }
    }
    
    //Método que retorna el arreglo de bytes del paquete DHCP construido a partir de los atributos del objeto.
    public byte[] construirPaquete(){
        //Primero se calcula el tamaño de las opciones
        int tamOp = 5; //Magic Cookie + end
        int tamTotal;
        tamOp += 3; //Message Type (siempre debe estar)
        if(parameterRequestList != null){
            tamOp += 2 + parameterRequestList.size(); //Parameter Request List
        }
        if(requestedIpAddress != null){
            tamOp += 6; //Requested IP Adress
        }
        if(ipAddressLeaseTime != null){
            tamOp += 6; //IP Address Lease Time
        }
        if(serverIdentifier != null){
            tamOp += 6; //Server Identifier
        }
        if(subnetMask != null){
            tamOp += 6;
        }
        if(dns != null){
            tamOp += 2 + 4*(dns.size()); //DNS
        }
        if(router != null){
            tamOp += 2 + 4*(router.size());
        }
        if(tamOp < 312){
            tamTotal = 548; //Tamaño del mensaje si el tamaño de las opciones es el mínimo
        }
        else{
            tamTotal = 236 + tamOp; //Tamaño del mensaje si las opciones superan el mínimo
        }
        
        byte[] paquete = new byte[tamTotal]; //Arreglo de bytes en el que se guarda el mensaje
        paquete[0] = op;
        paquete[1] = htype;
        paquete[2] = hlen;
        paquete[3] = hops;
        for(int i = 0; i < 4; i++){
            paquete[4 + i] = xid[i];
        }
        paquete[8] = secs[0];
        paquete[9] = secs[1];
        paquete[10] = flags[0];
        paquete[11] = flags[1];
        for(int i = 0; i < 4; i++){
            paquete[12 + i] = ciaddr[i];
        }
        for(int i = 0; i < 4; i++){
            paquete[16 + i] = yiaddr[i];
        }
        for(int i = 0; i < 4; i++){
            paquete[20 + i] = siaddr[i];
        }
        for(int i = 0; i < 4; i++){
            paquete[24 + i] = giaddr[i];
        }
        for(int i = 0; i < 16; i++){
            paquete[28 + i] = chaddr[i];
        }
        for(int i = 0; i < 64; i++){
            paquete[44 + i] = sname[i];
        }
        for(int i = 0; i < 128; i++){
            paquete[108 + i] = file[i];
        }
        //Inicio de campo de opciones
        //Magic Cookie
        paquete[236] = (byte)(99 & 0xff);
        paquete[237] = (byte)(130 & 0xff);
        paquete[238] = (byte)(83 & 0xff);
        paquete[239] = (byte)(99 & 0xff);
        int i = 240;
        //Message Type
        paquete[i] = (byte)(53 & 0xff);
        i++;
        paquete[i] = (byte)(1 & 0xff);
        i++;
        paquete[i] = messageType;
        i++;
        //Parameter Request List
        if(parameterRequestList != null){
            paquete[i] = (byte)(55 & 0xff);
            i++;
            paquete[i] = (byte)(parameterRequestList.size() & 0xff);
            i++;
            for(Integer code : parameterRequestList){
                paquete[i] = (byte)(code & 0xff);
                i++;
            }
        }
        //Requested IP Address
        if(requestedIpAddress != null){
            paquete[i] = (byte)(50 & 0xff);
            i++;
            paquete[i] = (byte)(4 & 0xff);
            i++;
            for(int j = 0; j < 4; j++){
                paquete[i] = requestedIpAddress[j];
                i++;
            }
        }
        //IP Address Lease time
        if(ipAddressLeaseTime != null){
            paquete[i] = (byte)(51 & 0xff);
            i++;
            paquete[i] = (byte)(4 & 0xff);
            i++;
            for(int j = 0; j < 4; j++){
                paquete[i] = ipAddressLeaseTime[j];
                i++;
            }
        }
        //Server Identifier
        if(serverIdentifier != null){
            paquete[i] = (byte)(54 & 0xff);
            i++;
            paquete[i] = (byte)(4 & 0xff);
            i++;
            for(int j = 0; j < 4; j++){
                paquete[i] = serverIdentifier[j];
                i++;
            }
        }
        //Subnet Mask
        if(subnetMask != null){
            paquete[i] = (byte)(1 & 0xff);
            i++;
            paquete[i] = (byte)(4 & 0xff);
            i++;
            for(int j = 0; j < 4; j++){
                paquete[i] = subnetMask[j];
                i++;
            }
        }
        //DNS
        if(dns != null){
            paquete[i] = (byte)(6 & 0xff);
            i++;
            paquete[i] = (byte)((dns.size()*4) & 0xff);
            i++;
            for(int j = 0; j < dns.size(); j++){
                for(int k = 0; k < 4; k++){
                    paquete[i] = dns.get(j)[k];
                    i++;
                }
            }
        }
        //Router
        if(router != null){
            paquete[i] = (byte)(3 & 0xff);
            i++;
            paquete[i] = (byte)((router.size()*4) & 0xff);
            i++;
            for(int j = 0; j < router.size(); j++){
                for(int k = 0; k < 4; k++){
                    paquete[i] = router.get(j)[k];
                    i++;
                }
            }
        }
        while(i != (tamTotal - 1)){//Hasta que se llegue a la posición en la que debe ir la opción End
            paquete[i] = (byte)(0); //Opción Pad
            i++;
        }
        paquete[i] = (byte)(255 & 0xff); //Opción End
        return paquete;
    }

    public byte getOp() {
        return op;
    }

    public void setOp(byte op) {
        this.op = op;
    }

    public byte getHtype() {
        return htype;
    }

    public void setHtype(byte htype) {
        this.htype = htype;
    }

    public byte getHlen() {
        return hlen;
    }

    public void setHlen(byte hlen) {
        this.hlen = hlen;
    }

    public byte getHops() {
        return hops;
    }

    public void setHops(byte hops) {
        this.hops = hops;
    }

    public byte[] getXid() {
        return xid;
    }

    public void setXid(byte[] xid) {
        this.xid = xid;
    }

    public byte[] getSecs() {
        return secs;
    }

    public void setSecs(byte[] secs) {
        this.secs = secs;
    }

    public byte[] getFlags() {
        return flags;
    }

    public void setFlags(byte[] flags) {
        this.flags = flags;
    }

    public byte[] getCiaddr() {
        return ciaddr;
    }

    public void setCiaddr(byte[] ciaddr) {
        this.ciaddr = ciaddr;
    }

    public byte[] getYiaddr() {
        return yiaddr;
    }

    public void setYiaddr(byte[] yiaddr) {
        this.yiaddr = yiaddr;
    }

    public byte[] getSiaddr() {
        return siaddr;
    }

    public void setSiaddr(byte[] siaddr) {
        this.siaddr = siaddr;
    }

    public byte[] getGiaddr() {
        return giaddr;
    }

    public void setGiaddr(byte[] giaddr) {
        this.giaddr = giaddr;
    }

    public byte[] getChaddr() {
        return chaddr;
    }

    public void setChaddr(byte[] chddr) {
        this.chaddr = chddr;
    }

    public byte[] getSname() {
        return sname;
    }

    public void setSname(byte[] sname) {
        this.sname = sname;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public ArrayList<Integer> getParameterRequestList() {
        return parameterRequestList;
    }

    public void setParameterRequestList(ArrayList<Integer> parameterRequestList) {
        this.parameterRequestList = parameterRequestList;
    }

    public byte[] getRequestedIpAddress() {
        return requestedIpAddress;
    }

    public void setRequestedIpAddress(byte[] requestedIpAddress) {
        this.requestedIpAddress = requestedIpAddress;
    }

    public byte[] getIpAddressLeaseTime() {
        return ipAddressLeaseTime;
    }

    public void setIpAddressLeaseTime(byte[] ipAddressLeaseTime) {
        this.ipAddressLeaseTime = ipAddressLeaseTime;
    }

    public byte[] getServerIdentifier() {
        return serverIdentifier;
    }

    public void setServerIdentifier(byte[] serverIdentiferier) {
        this.serverIdentifier = serverIdentiferier;
    }

    public byte[] getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(byte[] subnetMask) {
        this.subnetMask = subnetMask;
    }

    public ArrayList<byte[]> getDns() {
        return dns;
    }

    public void setDns(ArrayList<byte[]> dns) {
        this.dns = dns;
    }

    public ArrayList<byte[]> getRouter() {
        return router;
    }

    public void setRouter(ArrayList<byte[]> router) {
        this.router = router;
    }
}
