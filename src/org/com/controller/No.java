/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.com.controller;

import java.util.ArrayList;

/**
 *
 * @author Gabriel
 */
public class No {
    
    private Token pai;
    private final ArrayList<No> listaFilhos;
    public Token tipo;
    public String code = "";
    public String tipoAssembly;
    private static int espaco = 0;
    
     public No(Token s) {
        this.pai = s;
        this.listaFilhos = new ArrayList<>();
    }
    
    public Token getPai() {
        return pai;
    }
    
    public void setPai(Token s) {
        this.pai = s;
    } 
     
    public void addNoFilho(No filho){
        this.listaFilhos.add(filho);
    }
    
    public ArrayList<No> getFilhos(){
        return listaFilhos;
    }      
    
    public Token getTipo(){
        return this.tipo;
    }
    
    public void setCode(String s){
        code += s;
    }
    
    public String getCode(){
        return code;
    }
    
    public void setTipoAssembly(String s){
        tipoAssembly = s;
    }
    
    public String getTipoAssembly(){
        return tipoAssembly;
    }
    
    public void imprimeConteudo() {
        if (this.pai != null) {
            for (int i = 0; i < espaco; i++) {
                System.out.print(".   ");
            }
            System.out.print(this.pai.getLexema() + " - Tipo: " + this.tipoAssembly + "\n");
            espaco++;
        }
        for (No filho : listaFilhos) {
            if (filho != null){
                filho.imprimeConteudo();
            }
        }
        if (this.pai != null) {
            espaco--;
        }
    }
}
