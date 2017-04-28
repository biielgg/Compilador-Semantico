/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.com.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 *
 * @author Cleison Douglas
 */
public class TabelaSimbolos {

    public HashMap<String, Token> TabelaSimbolos = new HashMap<>();

    //Hash Map de Lexema e Token
    public HashMap<String, Token> getTabela() throws FileNotFoundException, IOException {
        File filesimbolos = new File("./tabelasimbolos.txt");
        BufferedReader tabsimbolos = new BufferedReader(new FileReader(filesimbolos));

        String linha;

        while (tabsimbolos.ready()) {
            linha = tabsimbolos.readLine();
            if (linha.equals("")) {
                continue;
            }
            Token tk = new Token(linha.substring(0, linha.indexOf(',')), linha.substring(linha.indexOf(' ') + 1, linha.length()));
            TabelaSimbolos.put(tk.getLexema(), tk);
        }
        return TabelaSimbolos;
    }

    public void setTabela(String tipo, String lexema) throws IOException {
        File filesimbolos = new File("./tabelasimbolos.txt");
        FileWriter fileWriter = new FileWriter(filesimbolos, true);
        PrintWriter simbolos = new PrintWriter(fileWriter);
        simbolos.println(tipo + ", " + lexema);
        simbolos.close();
        TabelaSimbolos = getTabela();
    }
    
   
}
