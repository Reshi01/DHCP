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
public class PaqueteDHCP {
    private byte op;
    private byte htype;
    private byte hlen;
    private byte hops;
    private byte[] xid;
    private byte[] secs;
    private byte[] flags;
    private byte[] ciaddr;
    private byte[] yiaddr;
    private byte[] siaddr;
    private byte[] giaddr;
    private byte[] chaddr;
    private byte[] sname;
    private byte[] file;
    private byte messageType;
    private ArrayList<Integer> parameterRequestList = null;
    private byte[] requestedIpAddress = null;
    private byte[] ipAddressLeaseTime = null;
    private byte[] serverIdentifier = null;
    private byte[] subnetMask = null;
    private ArrayList<byte[]> dns = null;
    private ArrayList<byte[]> router = null;

    public PaqueteDHCP(){
        
    }
    
    public PaqueteDHCP(byte[] bytes) {
        if(bytes.length < 300){ //El tamaño mínimo del mensaje es de 300 bytes
            System.out.println("Ocurrió un problema leyendo los bytes.");
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
            if(codigo == 55){ //Opción Parameter Request List
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
                    requestedIpAddress[j] = bytes[i];
                    i++;
                }
            }
            else if(codigo == 54){ //Opción Server Identifier
                serverIdentifier = new byte[tam];
                for(int j = 0; j < tam; j++){
                    requestedIpAddress[j] = bytes[i];
                    i++;
                }
            }
            else if(codigo == 1){ //Opción Subnet Mask
                subnetMask = new byte[tam];
                for(int j = 0; j < tam; j++){
                    requestedIpAddress[j] = bytes[i];
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
    
    public byte[] construirPaquete(){
        return null;
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

    public byte[] getServerIdentiferier() {
        return serverIdentifier;
    }

    public void setServerIdentiferier(byte[] serverIdentiferier) {
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
