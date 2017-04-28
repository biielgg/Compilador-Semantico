/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.com.controller;

/**
 *
 * @author SAMSUNG
 */
public class Token {

    private String tipo;
    private String lexema;
    private boolean tipoFunc = false;
    private int endereco;

    public Token(String tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }

    public Token(String tipo, String lexema, int endereco) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.endereco = endereco;
    }

    public Token(String tipo, String lexema, boolean tipoFunc) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.tipoFunc = tipoFunc;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public boolean isTipoFunc() {
        return tipoFunc;
    }

    public int getEndereco() {
        return endereco;
    }

    public void setEndereco(int endereco) {
        this.endereco = endereco;
    }

    public void setTipoFunc(boolean tipoFunc) {
        this.tipoFunc = tipoFunc;
    }
    
}
