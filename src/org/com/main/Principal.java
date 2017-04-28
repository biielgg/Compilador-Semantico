/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.com.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.com.controller.AnalisadorLexico;
import static org.com.controller.AnalisadorLexico.txterro;
import org.com.controller.AnalisadorSintaticoSemantico;
import org.com.controller.TabelaSimbolos;
import org.com.controller.Token;
import org.com.view.Tela;

/**
 *
 * @author SAMSUNG
 */
public class Principal {

    public static int kk = 0;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        int i = 0;
        File fileErros = new File("./logs.txt");
        FileWriter fileWriter = new FileWriter(fileErros);
        PrintWriter armaz = new PrintWriter(fileWriter);

        File fileTokens = new File("./logsTokens.txt");
//        FileWriter fileWriterTokens = new FileWriter(fileTokens);
//        PrintWriter armazTokens = new PrintWriter(fileWriter);

        TabelaSimbolos ts = new TabelaSimbolos();
        HashMap mapSimbolos = ts.getTabela();
        ArrayList<String> valores = new ArrayList();
        valores.addAll(mapSimbolos.values());

        Tela tela = new Tela();

        try {

            AnalisadorLexico.arquivo = new BufferedReader(new FileReader("arquivo.txt"));
            AnalisadorLexico analisador = new AnalisadorLexico();

            String outputString = "";
            String tmp = "";
            String contArquivo = "";
            String erros = "";

            while (AnalisadorLexico.arquivo.ready()) {
                // Armazena tudo o que está no arquivo na string contArquivo
                contArquivo = getConteudoArquivo(AnalisadorLexico.arquivo);
            }

            Tela.Txt_Input.setText(contArquivo);
            Tela.Txt_Input.setEditable(false);
            AnalisadorLexico.arquivo.close();

            AnalisadorLexico.arquivo = new BufferedReader(new FileReader("arquivo.txt"));

            AnalisadorLexico.c = (char) AnalisadorLexico.arquivo.read();

            while (AnalisadorLexico.c != '\uffff') {
                String tipo = analisador.proxToken().getTipo();
                tmp = "<" + tipo + " , " + AnalisadorLexico.lexema + "> ";

                if (!tmp.equals(("< , > "))) {
                    outputString += tmp + "Localização - " + "Linha: " + AnalisadorLexico.i + " Coluna: " + (AnalisadorLexico.inicio + 1) + "\n";
                    AnalisadorSintaticoSemantico.listaTokens.add(new Token(tipo, AnalisadorLexico.lexema));
                }
                if (!txterro.equals("")) {
                    armaz.println(txterro);
                }
            }

            Tela.Txt_Output.setText(outputString);
            Tela.Txt_Output.setEditable(false);
            AnalisadorLexico.arquivo.close();
            armaz.close();

            AnalisadorLexico.logs = new BufferedReader(new FileReader("logs.txt"));
            while (AnalisadorLexico.logs.ready()) {
                // Armazena tudo o que está no arquivo na string contArquivo
                erros = getConteudoArquivo(AnalisadorLexico.logs);
            }

            AnalisadorLexico.logs.close();

            //tokens
            AnalisadorLexico.logsTokens = new BufferedReader(new FileReader("logsTokens.txt"));
            while (AnalisadorLexico.logsTokens.ready()) {
                // Armazena tudo o que está no arquivo na string contArquivo
                erros = getConteudoArquivo(AnalisadorLexico.logsTokens);
            }
            Tela.Txt_Erro.setText(erros);
            Tela.Txt_Erro.setEditable(false);
            AnalisadorLexico.logsTokens.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(kk);
        if (kk >= 1) {
            AnalisadorLexico.i = 1;
            AnalisadorLexico.j = 0;
            tela.setVisible(true);
            kk++;
        } else {
            AnalisadorLexico.i = 1;
            AnalisadorLexico.j = 0;
            tela.setVisible(true);
            kk++;
        }
        AnalisadorSintaticoSemantico.main(args); //Executa o Sintatico + Semantico
    }

    private static String getConteudoArquivo(BufferedReader arquivo)
        throws IOException {
            String conteudo = "";
            while (arquivo.ready()) {
                conteudo += arquivo.readLine() + "\n";
            }
        return conteudo;
    }

    public static void main() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
