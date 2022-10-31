package lyc.compiler.symbolTable;

import lyc.compiler.files.FileGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SymbolTableGenerator implements FileGenerator {
    private static SymbolTableGenerator symbolTable;

    private Map<String,SymbolTableData> symbols;
    private SymbolTableGenerator() {
        this.symbols = new HashMap<String,SymbolTableData>();
    }
    public static SymbolTableGenerator getInstance() {
        if(symbolTable == null) {
            symbolTable = new SymbolTableGenerator();
        }
        return symbolTable;
    }
    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        String file = String.format("%-20s|%-20s|%-20s|%-20s\n","NOMBRE","TIPODATO","VALOR","LONGITUD");
        for (Map.Entry<String, SymbolTableData> entry : this.symbols.entrySet()) {
             file += String.format("%-20s", entry.getKey()) + "|" + entry.getValue().toString() + "\n";
        }
        fileWriter.write(file);
    }

    public void addToken(String token) {
        if(!this.symbols.containsKey(token)) {
            this.symbols.put(token,new SymbolTableData());
        }
    }

    public void addDataType(String id,String dataType) throws Exception {
        SymbolTableData data = this.symbols.get(id);

        if(data.getType() != null)
            throw new Exception("variable " + "\"" + id + "\"" + " ya declarada");

        data.setType(dataType);
    }
}
