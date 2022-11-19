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
    public AsmCodeGenerator(Node intermediateCode) {
        this.cont = 0;
        this.ic = intermediateCode;

    }
    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(this.recorrerPosOrden(this.ic));
    }

    public String recorrerPosOrden(Node nodo) {
        String asm = "";

        if(nodo == null)
            return "";

        if(nodo.value == "prog")
            this.recorrerPosOrden(nodo.left);
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

    private String generarCondicion(Node nodo,String etiqueta) {
        String condicion = "";
        String salto;

        if(nodo.value == "!"){
            nodo = nodo.left;
            salto = comparadoresNegados.get(nodo.value);
        }
        else {
            salto = comparadores.get(nodo.value);
        }
        condicion += "FLD " + nodo.left.value + "\n";
        condicion += "FLD " + nodo.right.value+ "\n";
        condicion += "FXCH\nFCOM\nFSTSW AX\nSAHF\n";
        condicion += salto + " " + etiqueta + "\n";

        return condicion;
    }

    private String generarIf(Node nodo) {
        String etiquetaIf = this.generarEtiqueta();

        String asm_if = this.generarCondicion(nodo.left,etiquetaIf);

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

    public String generarAsignacion(Node nodo) {
        String asm_asig = "";

        asm_asig += "FLD " + nodo.left.value + "\n";
        asm_asig += "FSTP " + nodo.right.value + "\n";

        return asm_asig;
    }

    public String generarEtiqueta() {
        return "etiq_" + cont++;
    }
}
