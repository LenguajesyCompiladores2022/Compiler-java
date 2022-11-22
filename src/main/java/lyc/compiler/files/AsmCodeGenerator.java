package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AsmCodeGenerator implements FileGenerator {

    private Node ic;
    private int cont;

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
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(this.recorrerPosOrden(this.ic));
    }

    private String recorrerPosOrden(Node nodo) {
        String asm = "";

        if(nodo == null)
            return "";

        if(nodo.value == "prog"){
            this.recorrerPosOrden(nodo.left);
            this.recorrerPosOrden(nodo.right);
        }
        else if(nodo.value == "while")
                generarWhile(nodo);
        else if(nodo.value == "if")
                asm = generarIf(nodo);
        else if(nodo.value == "=")
                asm = generarAsignacion(nodo);

        return asm;
    }

    private void generarWhile(Node nodo) {
        System.out.println("Insertar etiqueta while_" + cont++);
    }

    private String generarCondiciones(Node nodo,String etiqueta){
        String asm_cond = "";
        //String etiqueta = this.generarEtiqueta();
        if(nodo.value.compareTo("||") == 0){
            String etiquetaAux = this.generarEtiqueta();
            asm_cond += this.generarCondicion(nodo.left,etiquetaAux,true);
            asm_cond += this.generarCondicion(nodo.right,etiqueta,false);
            asm_cond += etiquetaAux + ":" + "\n";
        }
        else if (nodo.value.compareTo("&&") == 0){
            asm_cond += this.generarCondicion(nodo.left,etiqueta,false);
            asm_cond += this.generarCondicion(nodo.right,etiqueta,false);
        }
        else {
            asm_cond += this.generarCondicion(nodo,etiqueta,false);
        }

        return asm_cond;
    }
    private String generarCondicion(Node nodo,String etiqueta,boolean esOr) { //condiciones multiples
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

    private String generarIf(Node nodo) {
        String etiquetaIf = this.generarEtiqueta();

        String asm_if = this.generarCondiciones(nodo.left,etiquetaIf);

        if(nodo.right.value == "cuerpo") {

            asm_if += this.recorrerPosOrden(nodo.right.left);

            String etiquetaElse = this.generarEtiqueta();
            asm_if += "JMP " + etiquetaElse + "\n";
            asm_if += etiquetaIf + ":" + "\n";
            asm_if += this.recorrerPosOrden(nodo.right.right);
            asm_if += etiquetaElse + ":" + "\n";
        }
        else{
            asm_if += this.recorrerPosOrden(nodo.right);
            asm_if += etiquetaIf + ":" + "\n";
        }

        return asm_if;
    }

    private String generarAsignacion(Node nodo) {
        String asm_asig = "";

        asm_asig += this.generarExpresion(nodo.right);//"FLD " + nodo.left.value + "\n";
        asm_asig += "FSTP " + nodo.left.value + "\n";

        return asm_asig;
    }

    private String generarExpresion(Node nodo) {
        String asm_exp = "";
        int alturaIzquierda = altura(nodo.left);
        int alturaDerecha = altura(nodo.right);

        if(nodo.esHoja()){
            return "FLD " + nodo.value + "\n";
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
        return asm_exp + operadores.get(nodo.value) + "\n" + "FFREE 0\n";
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
