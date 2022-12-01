package lyc.compiler.files;

import java_cup.runtime.Symbol;
import lyc.compiler.symbolTable.SymbolTableData;
import lyc.compiler.symbolTable.SymbolTableGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AsmCodeGenerator implements FileGenerator {

    private Node ic;
    private int cont;

    private int MAX_TEXT_SIZE = 50;

    private Map<String,String> comparadores = Map.ofEntries(
        Map.entry("==","JNE"),
        Map.entry(">=","JNAE"),
        Map.entry(">","JNA"),
        Map.entry("<=","JA"),
        Map.entry("<","JAE"),
        Map.entry("!=","JE"));

    private Map<String,String> comparadoresNegados = Map.ofEntries(
            Map.entry("==","JE"),
            Map.entry(">=","JAE"),
            Map.entry(">","JA"),
            Map.entry("<=","JNA"),
            Map.entry("<","JNAE"),
            Map.entry("!=","JNE"));

    private Map<String,String> operadores = Map.ofEntries(
            Map.entry("+","FADD"),
            Map.entry("-","FSUB"),
            Map.entry("*","FMUL"),
            Map.entry("/","FDIV")
    );
    public AsmCodeGenerator(Node intermediateCode) {
        this.cont = 0;
        this.ic = intermediateCode;

    }
    @Override
    public void generate(FileWriter fileWriter) throws Exception {
        String asm = "include macros2.asm\ninclude number.asm\n .MODEL  LARGE\n.386\n.STACK 200h\n\n";
        asm += generarDATA();
        fileWriter.write(asm + generarCabecera() + generarPrograma(this.ic) + "\nmov ax, 4C00h\nint 21h\nEND START");
    }

    public String generarCabecera(){
        return ".CODE\nSTART:\nmov AX,@DATA\nmov DS,AX\nmov es,ax\n\n";
    }

    private String generarDATA(){
        Map<String, SymbolTableData> symbols = SymbolTableGenerator.getInstance().getSymbols();
        String asm_DATA = ".DATA\n";
        for (Map.Entry<String, SymbolTableData> entry : symbols.entrySet()) {
            SymbolTableData data = entry.getValue();
            if(!data.getType().equals("string")){
                asm_DATA += entry.getKey().replace(".","_") + "\t" + "dd ";
                if(data.getValue() == null)
                    asm_DATA += "? \n";
                else{
                    String value = "";
                    if(data.getType().equals("int"))
                        value = data.getValue() + ".0";
                    else
                        value = data.getValue();
                    asm_DATA += value + "\n";
                }
            }
            else{
                if(entry.getKey().startsWith("_")){
                    int largo = this.MAX_TEXT_SIZE - Integer.valueOf(data.getLength());
                    asm_DATA += entry.getKey().replaceAll("[^a-zA-z]*","") + "\t" + "db " + data.getValue() + ",'$'," + largo + " dup (?)\n";
                }
                else{
                    asm_DATA += entry.getKey().replaceAll("[^a-zA-z]*","") + "\t" + "db " + MAX_TEXT_SIZE + " dup (?),'$'\n";
                }
            }
        }

        return asm_DATA + "\n";
    }

    private String generarPrograma(Node nodo) throws Exception {
        String asm = "";

        if(nodo == null)
            return "";

        if(nodo.value.equals("prog")){
            asm += this.generarPrograma(nodo.left);
            asm += this.generarPrograma(nodo.right);
        }
        else if(nodo.value.equals("while"))
                asm = generarWhile(nodo);
        else if(nodo.value.equals("if"))
                asm = generarIf(nodo);
        else if(nodo.value.equals("="))
                asm = generarAsignacion(nodo);
        else if(nodo.value.equals("READ"))
                asm = generarPrintf(nodo);
        else if(nodo.value.equals("WRITE"))
                asm = generarScanf(nodo);

        return asm;
    }

    private String generarPrintf(Node node){
        String asm_printf = "";

        if(node.left.type.equals("float") || node.left.type.equals("int"))
            asm_printf = "DisplayFloat " + node.left.value + ",2\n";
        else if(node.left.type.equals("string"))
            asm_printf = "DisplayString "+ node.left.value.replaceAll("[^a-zA-z]*","") + "\n";

        return asm_printf + "newline 1\n";
    }
    private String generarScanf(Node node){
        String asm_scanf = "";

        if(node.left.type.equals("float") || node.left.type.equals("int"))
            asm_scanf = "GetFloat ";
        else if(node.left.type.equals("string"))
            asm_scanf = "GetString ";

        return asm_scanf + node.left.value + "\n";
    }

    private String generarWhile(Node nodo) throws Exception {
        String  asm_while = "";
        String etiquetaCondicion = this.generarEtiqueta();
        String etiquetaIncondicional = this.generarEtiqueta();

        asm_while += etiquetaIncondicional + ":\n";
        asm_while += this.generarCondiciones(nodo.left,etiquetaCondicion);
        asm_while += this.generarPrograma(nodo.right);
        asm_while += "JMP " + etiquetaIncondicional + "\n";
        asm_while += etiquetaCondicion + ":\n";

        return asm_while;
    }

    private String generarCondiciones(Node nodo,String etiqueta) throws Exception {
        String asm_cond = "";
        //String etiqueta = this.generarEtiqueta();
        if(nodo.value.equals("||")){
            String etiquetaAux = this.generarEtiqueta();
            asm_cond += this.generarCondicion(nodo.left,etiquetaAux,true);
            asm_cond += this.generarCondicion(nodo.right,etiqueta,false);
            asm_cond += etiquetaAux + ":" + "\n";
        }
        else if (nodo.value.equals("&&")){
            asm_cond += this.generarCondicion(nodo.left,etiqueta,false);
            asm_cond += this.generarCondicion(nodo.right,etiqueta,false);
        }
        else {
            asm_cond += this.generarCondicion(nodo,etiqueta,false);
        }

        return asm_cond;
    }
    private String generarCondicion(Node nodo,String etiqueta,boolean esOr) throws Exception { //condiciones multiples
        String condicion = "";
        String salto;

        if(esOr){
            salto = comparadoresNegados.get(nodo.value);
        }
        else {
            salto = comparadores.get(nodo.value);
        }
        /*if(nodo.value == "!"){
            nodo = nodo.left;
            salto = comparadoresNegados.get(nodo.value);
        }
        else {

        }*/
        condicion += this.generarExpresion(nodo.left);
        condicion += this.generarExpresion(nodo.right);
        condicion += "FXCH\nFCOM\nFSTSW AX\nSAHF\n";
        condicion += salto + " " + etiqueta + "\n";

        return condicion;
    }

    private String generarIf(Node nodo) throws Exception {
        String etiquetaIf = this.generarEtiqueta();

        String asm_if = this.generarCondiciones(nodo.left,etiquetaIf);

        if(nodo.right.value.equals("cuerpo")) {

            asm_if += this.generarPrograma(nodo.right.left);

            String etiquetaElse = this.generarEtiqueta();
            asm_if += "JMP " + etiquetaElse + "\n";
            asm_if += etiquetaIf + ":" + "\n";
            asm_if += this.generarPrograma(nodo.right.right);
            asm_if += etiquetaElse + ":" + "\n";
        }
        else{
            asm_if += this.generarPrograma(nodo.right);
            asm_if += etiquetaIf + ":" + "\n";
        }

        return asm_if;
    }

    private String generarAsignacion(Node nodo) throws Exception {
        String asm_asig = "";

        asm_asig += this.generarExpresion(nodo.right);//"FLD " + nodo.left.value + "\n";
        if(!nodo.left.type.equals(nodo.right.type))
            throw new Exception("\""+ nodo.left.value + "\"" + " es incompatible con el tipo de la expresion");

        asm_asig += "FSTP " + nodo.left.value + "\n";

        return asm_asig;
    }

    private String generarExpresion(Node nodo) throws Exception {
        String asm_exp = "";
        int alturaIzquierda = altura(nodo.left);
        int alturaDerecha = altura(nodo.right);

        if(nodo.esHoja()){
            return "FLD " + nodo.value.replace(".","_") + "\n";
        }

        if(alturaIzquierda >= alturaDerecha) {
            asm_exp += this.generarExpresion(nodo.left);
            asm_exp += this.generarExpresion(nodo.right);
        }
        else{
            asm_exp += this.generarExpresion(nodo.right);
            asm_exp += this.generarExpresion(nodo.left);
            asm_exp += "FXCH\n";
        }
        if(!nodo.left.type.equals(nodo.right.type))
            throw new Exception("\""+ nodo.left.value + "\" y " + "\"" + nodo.right.value + "\"" + " son tipos incompatibles");
        nodo.type = nodo.left.type;

        return asm_exp + operadores.get(nodo.value) + "\n";
    }

    private String generarEtiqueta() {
        return "etiq_" + cont++;
    }

    private int altura(Node nodo)
    {
        if (nodo == null) {
            return 0;
        }
        int izq = altura(nodo.left);
        int der = altura(nodo.right);
        if(izq > der){
            return 1 + izq;
        }else{
            return 1 + der;
        }
    }
}
