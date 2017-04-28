/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.com.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Heriklys
 */
public class AnalisadorSintaticoSemantico {

    private int rotulo = 0;
    public static List<Token> listaTokens = new LinkedList<>();
    public static List<Token> listaNoTokens = new LinkedList<>();
    public static HashMap<String, HashMap<String, Token>> listaTabelas = new HashMap<>(); // Lista de tabelas de Simbolos (por blocos)
    public static boolean result = true;
    public static HashMap<String, Token> tabelaSimbolos = new HashMap<>();
    static TabelaSimbolos ts = new TabelaSimbolos();
    static HashMap<String, No> ListaDeclara = new HashMap<>();
//    static HashMap<String, Token> zica = new HashMap<>();
////        
////        listaTabelas.add(new HashMap<String, Token>());
////        listaTabelas.get(0).values();
////        Token token = new Token(listaNoTokens.get(0).getTipo(), listaNoTokens.get(0).getLexema());
////        zica.put(listaNoTokens.get(0).getLexema(), token);
    public static HashMap<String, String[]> tabelaTipos = new HashMap<>();

    static boolean erroSemantico = false;
    static int contError;
    static String e1 = "token";
    static String identa = "\t";
    static String nomeClasse;
    static String variavelGlobal = "";
    static int contEndereço = 0;

    public static void criarTipos() {
        tabelaTipos.put("integer", new String[]{"I", "i"});
        tabelaTipos.put("String", new String[]{"Ljava/lang/String;", "a"});
        tabelaTipos.put("double", new String[]{"D", "d"});
        tabelaTipos.put("bool", new String[]{"I", "i"});
        tabelaTipos.put("ConstInteger", new String[]{"I", "i"});
        tabelaTipos.put("ConstDouble", new String[]{"D", "d"});
        tabelaTipos.put("ConstString", new String[]{"Ljava/lang/String;", "a"});
        tabelaTipos.put("void", new String[]{"V", ""});

    }

    public String novoRotulo() {
        return "L" + this.rotulo++;
    }

//    public static No Atualiza(No pai, No funcao1, No funcao2) {
//        pai.setPai(funcao2.getPai()); //aqui ele nao recebe nada e tem q ser montado pra funcionar            
//        pai.code += funcao1.code + funcao2.code;
//        pai.tipoAssembly = funcao1.tipoAssembly;
//        pai.addNoFilho(funcao1);
//        return pai;
//    }
    public static No Atualiza(No pai, No funcao1, No funcao2) {
        //funcao2.setPai(funcao2.getPai()); //aqui ele nao recebe nada e tem q ser montado pra funcionar            
        funcao2.code = funcao1.code + funcao2.code;
        funcao2.tipoAssembly = funcao1.tipoAssembly;//*****************
        funcao2.addNoFilho(funcao1);
        Collections.reverse(funcao2.getFilhos());
        return funcao2;
    }

    public static No Atualiza(No funcao1, No funcao2) {
        //funcao2.setPai(funcao2.getPai()); //aqui ele nao recebe nada e tem q ser montado pra funcionar            
        funcao2.code = funcao1.code + funcao2.code;
        if (!funcao1.code.equals("")) {
            funcao2.tipoAssembly = funcao1.tipoAssembly;//*****************
        }
        funcao2.addNoFilho(funcao1);
        Collections.reverse(funcao2.getFilhos());
        return funcao2;
    }

    public static No AddFilho(No pai, No filho) {
        pai.code += filho.code;
        pai.tipoAssembly = filho.tipoAssembly;
        pai.addNoFilho(filho);
        return pai;
    }

    public static No AddECompara(No pai, No funcao1, No funcao2) {
        if (funcao2.tipoAssembly != funcao1.tipoAssembly) {
            ErroSemantico("Tipos incompativeis");
        } else {
            pai.tipoAssembly = funcao1.tipoAssembly;
            pai.code += funcao2.code + funcao1.code;
        }
        pai.addNoFilho(funcao2);
        funcao2.addNoFilho(funcao1);
        return pai;
    }

    public static void main(String[] args) throws IOException {
        tabelaSimbolos.putAll(ts.getTabela());
        String cod = "";
        criarTipos();
        while (!listaTokens.isEmpty() && contError < 5) {
            cod = Programa().code;
        }
        File assembly = new File("./programa.txt");
        FileWriter fileWriter = new FileWriter(assembly);
        try (PrintWriter codAssembly = new PrintWriter(fileWriter)) {
            codAssembly.print(cod);
            System.out.println("Arquivo contém: " + contError + " erros.");
        }
    }

    //Programa → Classe EOF
    static No Programa() {
        //Classe()
        No programa = Classe();
        programa.imprimeConteudo();
        return programa;
    }

    //Classe → "class" ID ":" ListaFuncao Main "end" "."
    static No Classe() {
        No Noclasse = new No(null);
        if (casaToken("class")) {
            No filho = new No(listaNoTokens.get(0));
            //espera-se um ID
            nomeClasse = listaTokens.get(0).getLexema();
            if (casaToken("ID")) {
                Noclasse.setPai(listaNoTokens.get(0));
                Noclasse.addNoFilho(filho);
                Noclasse.code += identa + "\r\n";
                if (casaToken(":")) {
                    //ListaFuncao()
                    No NoLF = ListaFuncao();
                    //Noclasse = AddFilho(Noclasse, NoLF);
                    Noclasse.addNoFilho(NoLF);
                    //Main()
                    No NoMain = Main();
                    //Noclasse = AddFilho(Noclasse, NoMain);
                    Noclasse.addNoFilho(NoMain);
                    if (casaToken("end")) {
                        if (!casaToken(".")) {
                            Error(e1, ".");
                        } else {
                            //Noclasse = CriaCod(Noclasse);
                            String prog = ".class public " + nomeClasse + ":\r\n"
                                    + ".super java/lang/Object\r\n"
                                    + identa + NoLF.code + "\r\n"
                                    + identa + ".method public static main([Ljava/lang/String;)V\r\n"
                                    + identa + ".limit stack 100\r\n"
                                    + identa + ".limit locals 100\r\n"
                                    + NoMain.code
                                    + identa + ".end method";
                            Noclasse.code = prog;
                            return Noclasse;
                        }
                    } else {
                        Error(e1, "end");
                    }
                } else {
                    Error(e1, ":");
                }
            } else {
                Error(e1, "ID");
            }
        } else {
            Error(e1, "class");
        }
        return Noclasse;
    }

    //ListaFuncao → Funcao ListaFuncao | λ
    static No ListaFuncao() {
        //Funcao()
        //No listafuncao = Funcao();
        No listafuncao = new No(null);
        if (isToken("def")) {
            No Nofuncao = Funcao();
            listafuncao = ListaFuncao();
//            if (listafuncao != null) {
//                //Se tiver mais de uma funcao
//                No x = ListaFuncao();
//                listafuncao.addNoFilho(x);
//            }
            if (listafuncao.getPai() == null) {
                //listafuncao = Nofuncao;
                listafuncao = Atualiza(listafuncao, Nofuncao);
                //listafuncao.addNoFilho(Nofuncao);
            }

        }
        //listafuncao = CriaCod(listafuncao);
        //Collections.reverse(listafuncao.getFilhos());
        return listafuncao;
    }

    //Funcao → "def" TipoMacro ID "(" ListaArg ")" ":" DeclaraVariasID ListaCmd Retorno "end" ";" 
    static No Funcao() {
        No funcao = null;
        if (casaToken("def")) {
            //No funcaofilho = new No(listaNoTokens.get(0));
            String tipo = TipoMacro();
            //espera-se um ID
            if (!tipo.equals("Tipo Primitivo")) {
                if (isToken("ID")) {
                    contEndereço = 0;
                    if (!listaTabelas.keySet().contains(listaTokens.get(0).getLexema())) {
                        listaTabelas.put(listaTokens.get(0).getLexema(), new HashMap<String, Token>());
                        try {
                            listaTabelas.get(listaTokens.get(0).getLexema()).putAll(ts.getTabela());
                        } catch (IOException ex) {
                            Logger.getLogger(AnalisadorSintaticoSemantico.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        listaTokens.get(0).setTipoFunc(true);
                        tabelaSimbolos.put(listaTokens.get(0).getLexema(), listaTokens.get(0));
                        variavelGlobal = listaTokens.get(0).getLexema();

                        casaToken("ID");
                        funcao = new No(listaNoTokens.get(0));
                        funcao.tipoAssembly = tipo;
                        //funcao.addNoFilho(funcaofilho);
                        if (casaToken("(")) {
                            //ListaArg()
                            //Otimo momento pra pegar os parametros da função e usar no assembly
                            No x = ListaArg();
                            funcao.addNoFilho(x);
                            if (casaToken(")")) {
                                if (casaToken(":")) {
                                    //DeclaraVariasID() 
                                    funcao.code = ".method public static func\r\n" //inserir os parametros aqui
                                            + identa + ".limit stack 10\r\n"
                                            + identa + ".limit locals 10\r\n";
                                    No x1 = DeclaraVariasID();
                                    funcao.addNoFilho(x1); //Declaração não entra no assembly
                                    //ListaCmd()
                                    No x2 = ListaCmd();
                                    funcao.addNoFilho(x2);
                                    if (x2.getPai() != null) {
                                        funcao.code += x2.code + "\r\n";
                                    }
                                    //Retorno()
                                    No x3 = Retorno();
                                    funcao.addNoFilho(x3);
                                    //Se o retorno == null e a função != de void tem que da erro
                                    //Se o tipo do retorno != do tipo da função tem que da erro
                                    if ((x3.getPai() == null && (!funcao.tipoAssembly.contains("void")))
                                            || (!x3.tipoAssembly.toLowerCase().contains(funcao.tipoAssembly.toLowerCase()))) {
                                        ErroSemantico("Retorno incompatível");
                                        erroSemantico = true;
                                    }
                                    funcao.code += x3.code + "\r\n";
                                    if (casaToken("end")) {
                                        if (casaToken(";")) {
                                            funcao.code += identa + ".end method";
//                                    funcao = CriaCod(funcao);
                                            //   return funcao;
                                        } else {
                                            Error(e1, ";");
                                        }
                                    } else {
                                        Error(e1, "end");
                                    }
                                } else {
                                    Error(e1, ":");
                                }
                            } else {
                                Error(e1, ")");
                            }
                        } else {
                            Error(e1, "(");
                        }
                    } else {
                        ErroSemantico("Função: " + listaTokens.get(0).getLexema() + "já declarada!");
                        erroSemantico = true;
                    }
                } else {
                    Error(e1, "ID");
                }
            } else {
                ErroSemantico("Tipo não permitido: " + listaNoTokens.get(0).getLexema());
                erroSemantico = true;
            }
        }
        ListaDeclara.clear();
        contEndereço = 0;
        return funcao;
    }

    //TipoMacro → TipoPrimitivo TP
    static String TipoMacro() {
        String tipo = TipoPrimitivo();
        String tp = TP();
        return (tp + tipo);
    }

    //TipoPrimitivo → "bool" | "integer" | "String" | "double" | "void"
    static String TipoPrimitivo() {
        //No NoTipP = new No(null);
        if (casaToken("String")) {
            //NoTipP.setPai((listaNoTokens.get(0)));
            return "String";
        } else if (casaToken("double")) {
            //.setPai((listaNoTokens.get(0)));
            return "double";
        } else if (casaToken("void")) {
            //NoTipP.setPai((listaNoTokens.get(0)));
            return "void";
        } else if (casaToken("bool")) {
            // NoTipP.setPai((listaNoTokens.get(0)));
            return "bool";
        } else if (casaToken("integer")) {
            //NoTipP.setPai((listaNoTokens.get(0)));
            return "integer";
        } else {
            Error(e1, "Tipo Primitivo");
        }
        return "Tipo Primitivo";
    }

    //TP → "[" "]" | λ
    static String TP() {
        if (casaToken("[")) {
            if (!casaToken("]")) {
                Error(e1, "]");
            }
            return "new array ";
        }
        return "";
    }

    //ListaArg → Arg LA
    static No ListaArg() {
        //Arg();
        No x = Arg();
        No listaarg = LA();
        if (listaarg == null) {
            listaarg = x;
        } else {
            listaarg.addNoFilho(x);
        }
//        listaarg = CriaCod(listaarg);
        return listaarg;
    }

    //Arg → TipoMacro ID
    static No Arg() {
        No arg = null;
        String tipo = TipoMacro();
        //espera-se um ID  -- DECLARAÇÃO DE ID EM PARAMETROS DE FUNÇÕES
        if (isToken("ID")) {
            for (String metodo : listaTabelas.keySet()) {
                if (metodo.equals(variavelGlobal)) {
                    if (!listaTabelas.get(metodo).containsKey(listaTokens.get(0).getLexema())) {
                        listaTokens.get(0).setEndereco(contEndereço);
                        listaTabelas.get(metodo).put(listaTokens.get(0).getLexema(), listaTokens.get(0));
                        contEndereço++;
                        casaToken("ID");
                        arg = new No(listaNoTokens.get(0));
                        arg.tipoAssembly = tipo;
                        ListaDeclara.put(listaNoTokens.get(0).getLexema(), arg);
//            arg = CriaCod(arg);
//                        return arg;
                    } else {
                        ErroSemantico("Variavel " + listaTokens.get(0).getLexema() + " declarada em duplicidade!");
                    }
                }
            }

        } else {
            Error(e1, "ID");
        }
//        arg = CriaCod(arg);
        return arg;
    }

    //LA → "," ListaArg | λ
    static No LA() {
        No la = null;
        if (casaToken(",")) {
            //ListaArg();
            la = ListaArg();
        }
//        la = CriaCod(la);
        return la;
    }

    //DeclaraVariasID → DeclaraID DeclaraVariasID | λ
    static No DeclaraVariasID() {
        No declaravariasid = new No(null);

        if (isToken("bool") || isToken("integer") || isToken("String") || isToken("double") || isToken("void")) {
            No NoDeclaraID = DeclaraID();
            declaravariasid = DeclaraVariasID();

            if (declaravariasid.getPai() == null) {
                //listafuncao = Nofuncao;
                declaravariasid = Atualiza(declaravariasid, NoDeclaraID);
                //listafuncao.addNoFilho(Nofuncao);
            }
        }
        return declaravariasid;
    }

    //DeclaraID → TipoMacro ID ";"
    static No DeclaraID() {
        No declaraid = null;
        if (isToken("bool") || isToken("integer") || isToken("String") || isToken("double")) {
            String tipo = TipoMacro();
            if (isToken("ID")) {
                for (String metodo : listaTabelas.keySet()) {
                    if (metodo.equals(variavelGlobal)) {
                        if (!listaTabelas.get(metodo).containsKey(listaTokens.get(0).getLexema())) {
                            listaTokens.get(0).setEndereco(contEndereço);
                            listaTabelas.get(metodo).put(listaTokens.get(0).getLexema(), listaTokens.get(0));
                            contEndereço++;
                            casaToken("ID");
                            //DeclaraID();
                            declaraid = new No(listaNoTokens.get(0));
                            declaraid.tipoAssembly = tipo;
                            ListaDeclara.put(listaNoTokens.get(0).getLexema(), declaraid);
                            if (!casaToken(";")) {
                                Error(e1, ";");
                            }
                        } else {
                            ErroSemantico("Variavel " + listaTokens.get(0).getLexema() + " declarada em duplicidade!");
                            erroSemantico = true;
                        }
                    }
                }
            }
        } else {
            ErroSemantico("Tipo não permitido: " + listaNoTokens.get(0).getLexema());
            erroSemantico = true;
        }
//        declaraid = CriaCod(declaraid);
        return declaraid;
    }

    //ListaCmd → Cmd ListaCmd | λ
    static No ListaCmd() {
        //Cmd()
        No listacmd = new No(null);
        if (isToken("if") || isToken("while") || isToken("ID") || isToken("write") || isToken("writeln")) {
            No Nocmd = Cmd();
            listacmd = ListaCmd();
            if (listacmd.getPai() == null) {
                //listafuncao = Nofuncao;
                listacmd = Atualiza(Nocmd,listacmd);
                //listafuncao.addNoFilho(Nofuncao);
            }

        }
        return listacmd;
    }

    //Cmd → CmdIF | CmdWhile | CmdAtribui | CmdFuncao | CmdWrite | CmdWriteln
    static No Cmd() {
        No cmd = null;
        if (isToken("if")) {
            cmd = CmdIF();
            //CmdIF();
        } else if (isToken("while")) {
            cmd = CmdWhile();
            //CmdWhile();
        } else if (isToken("ID")) {
            for (String metodo : listaTabelas.keySet()) {
                if (metodo.equals(variavelGlobal)) {
                    if (listaTabelas.get(metodo).containsKey(listaTokens.get(0).getLexema())) {
                        casaToken("ID");
                        if (isToken("=")) {
                            cmd = CMA(listaNoTokens.get(0));//pra levar o ID junto
                            //CMA();
                        } else if (isToken("(")) {
                            cmd = CmdFuncao(listaNoTokens.get(0));//pra levar o ID junto
                            //CmdFuncao();
                        } else {
                            //Token inesperado, não veio '=' nem '('
                            Error(e1, "'='/'('");
                        }
                    } else {
                        ErroSemantico("Variavel " + listaTokens.get(0).getLexema() + " não declarada.");
                        break;
                    }
                }
            }
        } else if (isToken("write")) {
            cmd = CmdWrite();
            //CmdWrite();
        } else if (isToken("writeln")) {
            cmd = CmdWriteln();
            //CmdWriteln();
        }
//        cmd = CriaCod(cmd);
        return cmd;
    }

    //CmdIF → "if" "(" Expressao ")" ":" ListaCmd CIF 
    static No CmdIF() {
        No cmdif = null;
        if (casaToken("if")) {
            cmdif = new No(listaNoTokens.get(0));
            if (casaToken("(")) {
                //cmdif = Expressao();
                No x = Expressao();
                cmdif.addNoFilho(x);
                //Expressao();
                if (casaToken(")")) {
                    if (casaToken(":")) {
                        No x1 = ListaCmd();
                        cmdif.addNoFilho(x1);
                        //ListaCmd();
                        No x2 = CIF();
                        cmdif.addNoFilho(x2);
                        //CIF();
                    } else {
                        Error(e1, ":");
                    }
                } else {
                    Error(e1, ")");
                }
            } else {
                Error(e1, "(");
            }
        } else {
            Error(e1, "if");
        }
//        cmdif = CriaCod(cmdif);
        return cmdif;
    }

    //CIF → "end" ";" | "else" ListaCmd "end" ";"
    static No CIF() {
        No cif = null;
        if (casaToken("end")) {
            if (!casaToken(";")) {
                Error(e1, ";"); //ignorado da arvore por ser delimitador
            }
        } else if (casaToken("else")) {
            cif = ListaCmd();
            //ListaCmd();
            if (casaToken("end")) {
                if (!casaToken(";")) {
                    Error(e1, ";");
                }
            } else {
                Error(e1, "end");
            }
        } else {
            //Token inesperado, não veio end nem else;
            Error(e1, "");
        }
//        cif = CriaCod(cif);
        return cif;
    }

    //CmdWhile → "while" "(" Expressao ")" ":" ListaCmd "end" ";"
    static No CmdWhile() {
        No cmdwhile = null;
        if (casaToken("while")) {
            cmdwhile = new No(listaNoTokens.get(0));
            if (casaToken("(")) {
                //Expressao()
                No x = Expressao();
                cmdwhile.addNoFilho(x);
                if (casaToken(")")) {
                    if (casaToken(":")) {
                        //ListaCmd()
                        No x1 = ListaCmd();
                        cmdwhile.addNoFilho(x1);
                        if (casaToken("end")) {
                            if (casaToken(";")) {
                            } else {
                                Error(e1, ";");
                            }
                        } else {
                            Error(e1, "end");
                        }
                    } else {
                        Error(e1, ":");
                    }
                } else {
                    Error(e1, ")");
                }
            } else {
                Error(e1, "(");
            }
        } else {
            Error(e1, "while");
        }
//        cmdwhile = CriaCod(cmdwhile);
        return cmdwhile;
    }

    //CMA → "=" Expressao ";" | "[" Expressao "]" "=" Expressao ";"
    static No CMA(Token a) {
        No id = ListaDeclara.get(a.getLexema());
        No cma = new No(null);
        if (casaToken("=")) {
            cma.setPai(listaNoTokens.get(0));
            No x = Expressao();
            cma.addNoFilho(id);//*************************************************************************
            cma.addNoFilho(x);
//            if (x.tipoAssembly.contains(tabelaSimbolos.get(a.getLexema()))){
//                ErroSemantico("Atribuição de tipos incompativeis");
//            }
            //Expressao();
            if (!casaToken(";")) {
                Error(e1, ";");
            }
            if (x.tipoAssembly.toLowerCase().contains(id.tipoAssembly.toLowerCase())) {
                cma.code = x.code
                        + identa + identa +tabelaTipos.get(id.tipoAssembly)[1] + "store " + id.getPai().getEndereco() + "\r\n";
            } else {
                ErroSemantico("Tipos de atribuição incompatíveis.");
            }
        } else if (casaToken("[")) {
            cma = new No(listaNoTokens.get(0));
            No x = Expressao();
            cma.addNoFilho(x);
            if (casaToken("]")) {
                if (casaToken("=")) {
                    cma = new No(listaNoTokens.get(0));
                    No x1 = Expressao();
                    cma.addNoFilho(x1);
                    cma.addNoFilho(id);
                    if (!casaToken(";")) {
                        Error(e1, ";");
                    }
                } else {
                    Error(e1, "=");
                }
            } else {
                Error(e1, "]");
            }
        } else {
            Error(e1, "");
        }
        //Collections.reverse(cma.getFilhos());
//      cma = CriaCod(cma);
        return cma;
    }

    //CmdFuncao → ID "(" VariasExpressao ")" ";" 
    static No CmdFuncao(Token a) {
        No cmdfuncao = null;
        //espera-se um ID
        if (isToken("ID")) {
            if (verificaTabela(listaTokens.get(0).getLexema())) {
                casaToken("ID");
                cmdfuncao = new No(listaNoTokens.get(0));
                if (casaToken("(")) {
                    No x = VariasExpressao();
                    cmdfuncao.addNoFilho(x);
                    //VariasExpressao();
                    if (casaToken(")")) {
                        if (!casaToken(";")) {
                            Error(e1, ";");
                        }
                    } else {
                        Error(e1, ")");
                    }
                } else {
                    Error(e1, "(");
                }
            } else {
                // Error(e3, listaTokens.get(0).getLexema());
            }
        } else {
            Error(e1, "ID");
        }
//        cmdfuncao = CriaCod(cmdfuncao);
        return cmdfuncao;
    }

    //CmdWrite → "write" "(" Expressao ")" ";"
    static No CmdWrite() {
        No cmdwrite = null;
        if (casaToken("write")) {
            cmdwrite = new No(listaNoTokens.get(0));
            if (casaToken("(")) {
                No x = Expressao();
                cmdwrite.addNoFilho(x);
                //Expressao();
                if (casaToken(")")) {
                    if (!casaToken(";")) {
                        Error(e1, ";");
                    }
                    cmdwrite.code = identa + "getstatic java/lang/System/out Ljava/io/PrintStream;\r\n"
                            + "    " + x.code
                            + identa + "invokevirtual java/io/PrintStream/println(" + x.getTipo().getLexema() + ")V\r\n";
                    //tem que pegar o tipo da expressão que está vindo
                } else {
                    Error(e1, ")");
                }
            } else {
                Error(e1, "(");
            }
        } else {
            Error(e1, "write");
        }
//        cmdwrite = CriaCod(cmdwrite);
        return cmdwrite;
    }

    //CmdWriteln → "writeln" "(" Expressao ")" ";"
    static No CmdWriteln() {
        No cmdwriteln = null;
        if (casaToken("writeln")) {
            cmdwriteln = new No(listaNoTokens.get(0));
            if (casaToken("(")) {
                No x = Expressao();
                cmdwriteln.addNoFilho(x);
                //Expressao();
                if (casaToken(")")) {
                    if (!casaToken(";")) {
                        Error(e1, ";");
                    }
                } else {
                    Error(e1, ")");
                }
            } else {
                Error(e1, "(");
            }
        } else {
            Error(e1, "writeln");
        }
//        cmdwriteln = CriaCod(cmdwriteln);
        return cmdwriteln;
    }

    //VariasExpressao → ExpressaoDentro | λ
    static No VariasExpressao() {
        No variasexpressao = null;
        if (isToken("or") || isToken("and") || isToken("==") || isToken("!=") || isToken("<") || isToken("<=")
                || isToken(">") || isToken(">=") || isToken("-") || isToken("+") || isToken("*") || isToken("/")
                || isToken("!") || isToken("(") || isToken(")") || isToken("ID") || isToken("ConstInteger")
                || isToken("ConstDouble") || isToken("ConstString") || isToken("true") || isToken("false")
                || isToken("vector")) {
            variasexpressao = ExpressaoDentro();
            //ExpressaoDentro();
        }
//        variasexpressao = CriaCod(variasexpressao);
        return variasexpressao;
    }

    //ExpressaoDentro → Expressao ED 
    static No ExpressaoDentro() {
        //Expressao();
        No expressaod = Expressao();
        No ed = ED();
        if (ed != null) {
            No x = expressaod;
            expressaod = ed;
            ed.addNoFilho(x);
        }
//        expressaod = CriaCod(expressaod);
        return expressaod;
    }

    //ED → λ | "," ExpressaoDentro
    static No ED() {
        No ed = null;
        if (casaToken(",")) {
            //ExpressaoDentro();
            ed = ExpressaoDentro();
        }
//        ed = CriaCod(ed);
        return ed;
    }

    //Expressao → Expressao1 Exp 
    static No Expressao() {
        No expressao = new No(null);
        No exp1 = Expressao1();
        No exp = Exp();
        if (exp.getPai() == null) {
            //Expressao1()
            expressao = exp1;
        } else {
            //Exp()
            expressao = Atualiza(expressao, exp1, exp);
        }
//        expressao = CriaCod(expressao);
        return expressao;
    }

    //Exp → "or" Expressao1 Exp | λ
    static No Exp() {
        No exp = new No(null);
        if (casaToken("or")) {
            exp.setPai(listaNoTokens.get(0));
            No ex1 = Expressao1();
            No ex = Exp();
            if (ex.getPai() == null) {
                //Expressao1()
                exp = AddFilho(exp, ex1);
            } else {
                //Exp()
                exp = AddECompara(exp, ex1, ex);
            }
        }
//        exp = CriaCod(exp);
        return exp;
    }

    //Expressao1 → Expressao2 Exp1 
    static No Expressao1() {
        //Expressao2()
        No expressao1 = new No(null);
        No exp2 = Expressao2();
        No exp1 = Exp1();
        if (exp1.getPai() == null) {
            expressao1 = exp2;
        } else {
            //Exp1()
            expressao1 = Atualiza(expressao1, exp2, exp1);
        }
//        expressao1 = CriaCod(expressao1);
        return expressao1;
    }

    //Exp1 → "and" Expressao2 Exp1 | λ
    static No Exp1() {
        No exp1 = new No(null);
        if (casaToken("and")) {
            exp1 = new No(listaNoTokens.get(0));
            No ex2 = Expressao2();
            No ex1 = Exp1();
            if (ex2.getPai() == null) {
                //Expressao2()
                exp1 = AddFilho(exp1, ex1);
            } else {
                //Exp1()
                exp1 = AddECompara(exp1, ex2, ex1);
            }
        }
//        exp1 = CriaCod(exp1);
        return exp1;
    }

    //Expressao2 → Expressao3 Exp2
    static No Expressao2() {
        No expressao2 = new No(null);
        No exp3 = Expressao3();
        No exp2 = Exp2();
        if (exp2.getPai() == null) {
            //Expressao3()
            expressao2 = exp3;
        } else {
            //Exp2()
            expressao2 = Atualiza(expressao2, exp3, exp2);
        }
//        expressao2 = CriaCod(expressao2);
        return expressao2;
    }

    //Exp2 → "==" Expressao3 Exp2 | "!=" Expressao3 Exp2 | λ
    static No Exp2() {
        No exp2 = new No(null);
        if (casaToken("==") || casaToken("!=")) {
            exp2 = new No(listaNoTokens.get(0));
            No ex3 = Expressao3();
            No ex2 = Exp2();
            if (ex2.getPai() == null) {
                //Expressao3()
                exp2 = AddFilho(exp2, ex3);
            } else {
                //Exp2();
                exp2 = AddECompara(exp2, ex3, ex2);
            }
        }
//        exp2 = CriaCod(exp2);
        return exp2;
    }

    //Expressao3 → Expressao4 Exp3
    static No Expressao3() {
        No expressao3 = new No(null);
        No exp4 = Expressao4();
        No exp3 = Exp3();
        if (exp3.getPai() == null) {
            //Expressao4()
            expressao3 = exp4;
        } else {
            //Exp3()
            expressao3 = Atualiza(expressao3, exp4, exp3);
        }
//        expressao3 = CriaCod(expressao3);
        return expressao3;
    }

    //Exp3 → "<" Expressao4 Exp3 | "<=" Expressao4 Exp3 | ">" Expressao4 Exp3 | ">=" Expressao4 Exp3 | λ
    static No Exp3() {
        No exp3 = new No(null);
        if (casaToken("<") || casaToken("<=") || casaToken(">") || casaToken(">=")) {
            String opera = "";
            switch (listaNoTokens.get(0).getLexema()) { //Usar tabela de simbolos
                case "<":
                    opera = "menor\r\n";
                    break;
                case "=<":
                    opera = "menorigual\r\n";
                    break;
                case ">":
                    opera = "maior\r\n";
                    break;
                case ">=":
                    opera = "maiorigual\r\n";
                    break;
            }
            exp3.setPai(listaNoTokens.get(0));
            No ex4 = Expressao4();
            No ex3 = Exp3();
            if (ex3.getPai() == null) {
                //Expressao4()
                exp3 = AddFilho(exp3, ex4);
            } else {
                //Exp3()
                exp3 = AddECompara(exp3, ex4, ex3);
            }
            exp3.code += identa + identa + tabelaTipos.get(listaNoTokens.get(0).getTipo())[1] + opera;
        }
//        exp3 = CriaCod(exp3);
        return exp3;
    }

    //Expressao4 → Expressao5 Exp4 
    static No Expressao4() {
        No expressao4 = new No(null);
        No ex5 = Expressao5();
        No ex4 = Exp4();
        if (ex4.getPai() == null) {
            //Expressao5()
            expressao4 = ex5;
        } else {
            //Exp4()
            expressao4 = Atualiza(expressao4, ex5, ex4);
        }
//        expressao4 = CriaCod(expressao4);
        return expressao4;
    }

    //Exp4 → "-" Expressao5 Exp4 | "+" Expressao5 Exp4 | λ
    static No Exp4() {
        No exp4 = new No(null);
        if (casaToken("-") || casaToken("+")) {
            String opera = "";
            if ("-".equals(listaNoTokens.get(0).getLexema())) {
                opera = "sub\r\n";
            } else if ("+".equals(listaNoTokens.get(0).getLexema())) {
                opera = "add\r\n";
            }
            exp4.setPai(listaNoTokens.get(0));
            No ex5 = Expressao5();
            No ex4 = Exp4();
            if (ex4.getPai() == null) {
                //Expressao5()
                exp4 = AddFilho(exp4, ex5);
            } else {
                //Exp4()
                exp4 = AddECompara(exp4, ex5, ex4);
            }
            exp4.code += identa + identa + tabelaTipos.get(listaNoTokens.get(0).getTipo())[1] + opera;
        }
//        exp4 = CriaCod(exp4);
        return exp4;
    }

//Expressao5 → Expressao6 Exp5 
    static No Expressao5() {
        No expressao5 = new No(null);
        No exp6 = Expressao6();
        No exp5 = Exp5();
        if (exp5.getPai() == null) {
            //Expressao6()
            expressao5 = exp6; //Aqui ele recebe a carga completa do exp6
        } else {
            //Exp5
            expressao5 = Atualiza(expressao5, exp6, exp5);
        }
//        expressao5 = CriaCod(expressao5);
        return expressao5;
    }

    //Exp5 → "/" Expressao6 Exp5 | "*" Expressao6 Exp5 | λ
    static No Exp5() {
        No exp5 = new No(null);
        if (casaToken("/") || casaToken("*")) {
            String opera = "";
            if ("*".equals(listaNoTokens.get(0).getLexema())) {
                opera = "mul\r\n";
            } else if ("/".equals(listaNoTokens.get(0).getLexema())) {
                opera = "div\r\n";
            }
            exp5.setPai(listaNoTokens.get(0));
            No ex6 = Expressao6();
            No ex5 = Exp5();
            if (ex5.getPai() == null) {
                //Expressao6()
                exp5 = AddFilho(exp5, ex6);
            } else {
                //Exp5()
                exp5 = AddECompara(exp5, ex6, ex5);
            }
            exp5.code += identa + identa + tabelaTipos.get(listaNoTokens.get(0).getTipo())[1] + opera;
        }
//        exp5 = CriaCod(exp5);
        return exp5;
    }

    //Expressao6 → OpUnario Expressao7 | Expressao7
    static No Expressao6() {
        No expressao6;
        if (isToken("!") || isToken("-")) {
            //OpUnario()
            expressao6 = OpUnario();
            //Expressao7()
            No x = Expressao7();
            expressao6.addNoFilho(x);
            expressao6.tipoAssembly = x.tipoAssembly;
            expressao6.code += identa + identa + "ldc 0\r\n" + expressao6.code + "Tsub\r\n";
        } else {
            expressao6 = Expressao7();
        }
//        expressao6 = CriaCod(expressao6);
        return expressao6;
    }

    //Expressao7 → "(" Expressao8 ")" | Expressao8
    static No Expressao7() {
        No expressao7;
        if (casaToken("(")) {
            expressao7 = Expressao();
            //Expressao();
            if (!casaToken(")")) {
                Error(e1, ")");
            }
        } else {
            expressao7 = Expressao8();
        }
//        expressao7 = CriaCod(expressao7);
        return expressao7;
    }

    //Expressao8 → ID E | ConstInteger | ConstDouble | ConstString | "true" | "false" | "vector" TipoPrimitivo "[" Expressao "]" 
    static No Expressao8() {
        No expressao8 = new No(null);
        if (casaToken("ID")) {
            //MATUTAR NUMERO / POSIÇÕES DAS VARIAVES NO ASSEMBLY, EX iload 0, iload1, etc.

            for (String k : tabelaSimbolos.keySet()) {
                if (k.equals(listaNoTokens.get(0).getLexema())) {
                    expressao8.setPai(listaNoTokens.get(0));
                    expressao8.code += identa + identa + tabelaTipos.get(listaNoTokens.get(0).getTipo())[1] + "load " + tabelaSimbolos.get(k).getEndereco() + "\r\n";
                    expressao8.tipoAssembly = listaNoTokens.get(0).getTipo();
                    //E()
                    No x = E();
                    expressao8.addNoFilho(x);
                }
            }

        } else if (casaToken("ConstInteger") || casaToken("ConstDouble") || casaToken("ConstString") || casaToken("true") || casaToken("false")) {
            expressao8.setPai(listaNoTokens.get(0));
            expressao8.code += identa + identa + "ldc " + listaNoTokens.get(0).getLexema() + "\r\n";
            expressao8.tipoAssembly = listaNoTokens.get(0).getTipo();

        } else if (casaToken("vector")) {
            expressao8.setPai(listaNoTokens.get(0));
            //expressao8.addNoFilho(TipoPrimitivo());
            if (casaToken("[")) {
                No x = Expressao();
                expressao8 = AddFilho(expressao8, x);
                if (!casaToken("]")) {
                    Error(e1, "]");
                }
                expressao8.code += "new array " + x.tipoAssembly + "\r\n";
            } else {
                Error(e1, "[");
            }
        } else {
            Error(e1, "espaço");
        }
        //expressao8 = CriaCod(expressao8);
        return expressao8;
    }

    //OpUnario → "-" | "!"
    static No OpUnario() {
        No opunario = null;
        if (casaToken("-")) {
            opunario = new No(listaNoTokens.get(0));
        } else if (!casaToken("!")) {
            opunario = new No(listaNoTokens.get(0));
            Error(e1, "");
        }
//        opunario = CriaCod(opunario);
        return opunario;
    }

    //E → λ | "[" Expressao"]" | "(" VariasExpressao ")"
    static No E() {
        No e = null;
        if (casaToken("[")) {
            e = Expressao();
            //Expressao()
            if (!casaToken("]")) {
                Error(e1, "]");
            }
        } else if (casaToken("(")) {
            //VariasExpressao()
            e = VariasExpressao();
            if (!casaToken(")")) {
                Error(e1, ")");
            }
        }
//        e = CriaCod(e);
        return e;
    }

    //Retorno → "return" Expressao ";" | λ
    static No Retorno() {
        No retorno = new No(null);
        if (casaToken("return")) {
            retorno.setPai(listaNoTokens.get(0));
            //Expressao()
            No x = Expressao();
            retorno = AddFilho(retorno, x);
            if (!casaToken(";")) {
                Error(e1, ";");
            }
            retorno.code += identa + identa + tabelaTipos.get(retorno.tipoAssembly)[1] + "return";
        }
//        retorno = CriaCod(retorno);
        return retorno;
    }

    //Main → "defstatic" "void" "main" "(" "String" "[" "]" ID ")" ":" DeclaraVariasID ListaCmd "end" ";" 
    static No Main() {
        No main = new No(null);
        if (casaToken("defstatic")) {
            No def = new No(listaNoTokens.get(0));
            if (casaToken("void")) {
                //No voi = new No(listaNoTokens.get(0));
                //voi.addNoFilho(def);
                if (casaToken("main")) {
                    main.setPai(listaNoTokens.get(0));
                    main.addNoFilho(def);
                    variavelGlobal = "main";
                    listaTabelas.put(listaNoTokens.get(0).getLexema(), new HashMap<String, Token>());
                    if (casaToken("(")) {
                        if (casaToken("String")) {
                            if (casaToken("[")) {
                                if (casaToken("]")) {
                                    //espera-se um ID
                                    if (isToken("ID")) {
                                        if (!tabelaSimbolos.keySet().contains(listaTokens.get(0).getLexema())) {
                                            listaTokens.get(0).setEndereco(contEndereço);
                                            listaTabelas.get("main").put(listaTokens.get(0).getLexema(), listaTokens.get(0));
                                            contEndereço++;
                                            casaToken("ID");
                                            //main.addNoFilho(new No(listaNoTokens.get(0)));
                                            if (casaToken(")")) {
                                                if (casaToken(":")) {
                                                    //DeclaraVariasID();
                                                    No x = DeclaraVariasID();
                                                    main = AddFilho(main, x);
                                                    //ListaCmd();
                                                    No x1 = ListaCmd();
                                                    main = AddFilho(main, x1);
                                                    if (casaToken("end")) {
                                                        if (!casaToken(";")) {
                                                            Error(e1, ";");
                                                        }
                                                    } else {
                                                        Error(e1, "end");
                                                    }
                                                } else {
                                                    Error(e1, ":");
                                                }
                                            } else {
                                                Error(e1, ")");
                                            }
                                        } else {
                                            ErroSemantico("Variavel " + listaTokens.get(0).getLexema() + "declarada em duplicidade!");
                                        }
                                    } else {
                                        Error(e1, "ID");
                                    }
                                } else {
                                    Error(e1, "}");
                                }
                            } else {
                                Error(e1, "{");
                            }
                        } else {
                            Error(e1, "String");
                        }
                    } else {
                        Error(e1, "(");
                    }
                } else {
                    Error(e1, "main");
                }
            } else {
                Error(e1, "void");
            }
        } else {
            Error(e1, "defstatic");
        }
//        main = CriaCod(main);
        main.code += identa+ identa+"return main\r\n";
        return main;
    }

    public static String NomeTipo(String tipo) {

        return "";
    }

    static boolean casaToken(String token) {
        if (!listaTokens.isEmpty()) {
            if (listaTokens.get(0).getTipo().equals(token)) {
                listaNoTokens.clear();
                listaNoTokens.add(listaTokens.get(0));
                listaTokens.remove(0);
                return true;
            }
            return false;
        }
        return false;
    }

    static boolean isToken(String token) {
        if (!listaTokens.isEmpty()) {
            return listaTokens.get(0).getTipo().equals(token);
        }
        return false;
    }

    static void Error(String erro, String tokenEsperado) {
        result = false;
        contError++;
        switch (erro) {
            case "token":
                while (result == false && !listaTokens.isEmpty()) {
                    //System.err.println("entrou no while");
                    if (isToken(";") || isToken(".") || isToken(")")) {
                        result = true;
                        // System.err.println("result recebeu true");
                    } else {
                        listaTokens.remove(0);
                        //System.err.println("removeu um item");
                    }
                }
                System.err.println("Token " + "'" + tokenEsperado + "'" + " esperado!");
                break;

            default:
                break;
        }
    }

    static void ErroSemantico(String erro) {
        System.err.println("Erro Semântico: " + erro);
        System.exit(0);
    }

    public static boolean verificaTabela(String id) {
        return tabelaSimbolos.keySet().contains(id);
    }
}
