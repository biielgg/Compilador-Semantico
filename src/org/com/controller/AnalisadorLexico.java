/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.com.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Observações Os lexemas são identificados no final Os erros são identificados
 * no inicio
 *
 * @author SAMSUNG
 */
public class AnalisadorLexico {

    /**
     *
     */
    public static String lexema, desconhecido, txterro;
    int estado;
    public static char c;
    public static int i = 1, j = 0, ant = 0, inicio = 0;
    public static BufferedReader arquivo;
    public static BufferedReader logs;
    public static BufferedReader logsTokens;
    public static InputStreamReader teste;

    TabelaSimbolos ts = new TabelaSimbolos();

    public Token proxToken() throws FileNotFoundException, IOException {
        txterro = "";
        lexema = "";
        desconhecido = "";
        estado = 1;

        HashMap mapSimbolos = ts.getTabela();
        ArrayList<Token> valores = new ArrayList();
        valores.addAll(mapSimbolos.values());

        while (true) {
            switch (estado) {
                case 1:
                    if (c == '\uffff') {
                        return new Token("", "");
                    } else if (c == '\r') {
                        break;
                    } else if (Character.isLetter(c)) {
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 2;
                    } else if (Character.isDigit(c)) {
                        inicio = j;
                        j++;
                        arquivo.mark(1);
                        lexema += c;
                        estado = 3;
                        //} else if (c == ' ' || c == '\t' || c == '\n') {
                    } else if (c == '\n') {
                        i++;
                        j = 0;
                        break;
                    } else if (c == '\t') {
                        j = 4 + j;
                        break;
                    } else if (c == ' ') {
                        j++;
                        break;
                    } else if (c == '=') {   // OP (==)   //***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 5;
                    } else if (c == '!') {    // OP (!=) //***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 6;
                    } else if (c == '>') {    // OP (>=)//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 7;
                    } else if (c == '<') {    // OP (<=)//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 8;
                    } else if (c == '/') {    // OP (/* kasfd*/)//***-----****
                        inicio = j;
                        j++;
                        ant = j;
                        lexema += c;
                        estado = 9;
                    } else if (c == '*') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 12;
                    } else if (c == '+') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 13;
                    } else if (c == '-') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 14;
                    } else if (c == '.') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 15;
                    } else if (c == ';') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 16;
                    } else if (c == ',') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 17;
                    } else if (c == ':') { //***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 18;
                    } else if (c == '(') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 19;
                    } else if (c == ')') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 20;
                    } else if (c == '[') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 21;
                    } else if (c == ']') {//***-----****
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 22;
                    } else if (c == '{') {//***-----********************************
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 23;
                    } else if (c == '}') {//***-----******************************
                        inicio = j;
                        j++;
                        lexema += c;
                        estado = 24;
                    } else if (c == '"') {
                        inicio = j;
                        j++;
                        ant = j;
                        lexema += c;
                        estado = 25;
                    } else {
                        j++;
                        desconhecido += c;
                        estado = 27;
                    }
                    break;
                case 2:
                    if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
                        j++;
                        lexema += c;
                    } else {
                        for (Token valor : valores) {
                            if (lexema.equals(valor.getLexema())) {
                                return new Token(lexema, lexema);
                            }
                        }
                        return new Token("ID", lexema);
                    }
                    break;
                case 3:
                    if (Character.isDigit(c)) {
                        j++;
                        arquivo.mark(1);
                        lexema += c;
                    } else if (c == '.') {
                        j++;
                        lexema += c;
                        estado = 4;

                    } else {
                        return new Token("ConstInteger", lexema);
                    }
                    break;
                case 4:
                    if (Character.isDigit(c)) {
                        j++;
                        lexema += c;
                        estado = 77;
                    } else {
                        arquivo.reset();
                        lexema = lexema.substring(0, lexema.length() - 1);
                        c = (char) arquivo.read();
                        j--;
                        return new Token("ConstInteger", lexema);
                    }

                    break;
                case 77:
                    if (Character.isDigit(c)) {
                        j++;
                        lexema += c;
                    } else {
                        return new Token("ConstDouble", lexema);
                    }
                    break;
                case 5:
                    if (c == '=') {
                        j++;
                        estado = 30;
                        break;
                    } else {
                        return new Token("=", lexema);
                    }
                case 6:
                    if (c == '=') {
                        j++;
                        lexema += c;
                        estado = 31;
                        break;
                    } else {
                        return new Token("!", lexema);
                    }
                case 7:
                    if (c == '=') {
                        j++;
                        lexema += c;
                        estado = 32;
                        break;
                    } else {
                        return new Token(">", lexema);
                    }
                case 8:
                    if (c == '=') {
                        j++;
                        lexema += c;
                        estado = 33;
                        break;
                    } else {
                        return new Token("<", lexema);

                    }
                case 9:
                    if (c == '*') { //comentário por blocos
                        j++;
                        lexema = "";
                        estado = 10;
                    } else if (c == '/') {
                        while (c != '\r' && c != '\uffff') {
                            c = (char) arquivo.read();
                        }
                        estado = 1;
                        lexema = "";
                    } else {
                        estado = 26;
                    }
                    break;
                case 10:
                    if (c != '*') {

                        if (c != '\uffff') {
                            if (c == '\n') {
                                i++;
                                j = 0;
                            } else {
                                j++;
                            }
                        } else {
                            estado = 29;
                        }
                    } else {
                        j++;
                        estado = 11;
                    }
                    break;
                case 11:
                    if (c != '/') {
                        if (c != '\uffff') {
                            estado = 10;
                        } else {
                            estado = 29;
                        }
                    } else {
                        j++;
                        lexema = "";
                        estado = 1;
                    }
                    break;
                case 12:
                    return new Token("*", lexema);//***-----****
                case 13:
                    return new Token("+", lexema);//***-----****
                case 14:
                    return new Token("-", lexema);//***-----****
                case 15:
                    return new Token(".", lexema);//***-----****
                case 16:
                    return new Token(";", lexema);//***-----****
                case 17:
                    return new Token(",", lexema);//***-----****
                case 18:
                    return new Token(":", lexema);//***-----****
                case 19:
                    return new Token("(", lexema);//***-----****
                case 20:
                    return new Token(")", lexema);//***-----****
                case 21:
                    return new Token("[", lexema);//***-----****
                case 22:
                    return new Token("]", lexema);//***-----****
                case 23:
                    return new Token("{", lexema);//*******************************
                case 24:
                    return new Token("}", lexema);//*******************************
                case 25:
                    if (c != '"') {
                        if (c != '\r' && c != '\uffff' && c != '\n' && c != '\t') {
                            j++;
                            lexema += c;
                        } else {
                            estado = 28;
                        }
                    } else {
                        j++;
                        lexema += c;
                        c = (char) arquivo.read();
                        return new Token("ConstString", lexema);
                    }
                    break;
                case 26:
                    return new Token("/", lexema);//***-----****
                case 30:
                    return new Token("==", lexema);//***-----****
                case 31:
                    return new Token("!=", lexema);//***-----****
                case 32:
                    return new Token(">=", lexema);//***-----****
                case 33:
                    return new Token("<=", lexema);//***-----****
                default:
                    if (estado == 27) {
                        txterro = "Caracter Desconhecido " + desconhecido + " na linha: " + i + " na coluna: " + j;
                        return new Token("", "");
                    } else if (estado == 28) {
                        txterro = "String não fechada a partir da" + " linha: " + i + " e coluna: " + ant;
                        lexema = "";
                        return new Token("", "");
                    } else if (estado == 29) {
                        txterro = "Comentário não fechado a partir da" + " linha: " + i + " e coluna: " + ant;
                        return new Token("", "");
                    } else {
                        return new Token("", "");
                    }
            }
            if (estado != 26) {
                c = (char) arquivo.read();
            }
        }
    }

}
