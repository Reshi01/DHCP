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
    private byte[] chddr;
    private byte[] sname;
    private byte[] file;
    private byte messageType;
    private ArrayList<Integer> parameterRequestList;
    private byte[] requestedIpAddress;
    private int ipAddressLeaseTime;
    private byte[] serverIdentiferier;
    private byte[] subnetMask;
    private ArrayList<byte[]> dns;
    private ArrayList<byte[]> router;

    public PaqueteDHCP() {
        xid=new byte[4];
        secs=new byte[2];
        flags=new byte[2];
        ciaddr=new byte[4];
        yiaddr=new byte[4];
        siaddr=new byte[4];
        giaddr=new byte[4];
        chddr=new byte[16];
        sname=new byte[64];
        file=new byte[128];
        parameterRequestList=new ArrayList<>();
        requestedIpAddress=new byte[4];
        serverIdentiferier=new byte[4];
        subnetMask=new byte[4];
        dns=new ArrayList<>();
        router=new ArrayList<>();
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

    public byte[] getChddr() {
        return chddr;
    }

    public void setChddr(byte[] chddr) {
        this.chddr = chddr;
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

    public int getIpAddressLeaseTime() {
        return ipAddressLeaseTime;
    }

    public void setIpAddressLeaseTime(int ipAddressLeaseTime) {
        this.ipAddressLeaseTime = ipAddressLeaseTime;
    }

    public byte[] getServerIdentiferier() {
        return serverIdentiferier;
    }

    public void setServerIdentiferier(byte[] serverIdentiferier) {
        this.serverIdentiferier = serverIdentiferier;
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
