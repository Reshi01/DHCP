/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;

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
