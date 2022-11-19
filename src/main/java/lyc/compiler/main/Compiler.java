package lyc.compiler.main;

import java_cup.runtime.Symbol;
import lyc.compiler.Parser;
import lyc.compiler.factories.FileFactory;
import lyc.compiler.factories.ParserFactory;
import lyc.compiler.files.AsmCodeGenerator;
import lyc.compiler.files.FileOutputWriter;
import lyc.compiler.files.IntermediateCodeGenerator;
import lyc.compiler.files.TreeIntermediateCode;
import lyc.compiler.symbolTable.SymbolTableGenerator;

import java.io.IOException;
import java.io.Reader;

public final class Compiler {

    private Compiler(){}

    public static void main(String[] args) {
        /*if (args.length != 1) {
            System.out.println("Filename must be provided as argument.");
            System.exit(0);
        }*/


        try (Reader reader = FileFactory.create("E:\\PC\\Usuario\\Escritorio\\Compiler-java\\src\\main\\resources\\input\\test.txt")) {
            Parser parser = ParserFactory.create(reader);
            parser.parse();
            FileOutputWriter.writeOutput("symbol-table.txt", SymbolTableGenerator.getInstance());
            FileOutputWriter.writeOutput("intermediate-code.txt", IntermediateCodeGenerator.getInstance());
            FileOutputWriter.writeOutput("final.asm", new AsmCodeGenerator(IntermediateCodeGenerator.getInstance().getIntermediateCode()));
        } catch (IOException e) {
            System.err.println("There was an error trying to read input file " + e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Compilation error: " + e.getMessage());
            System.exit(0);
        }

        System.out.println("Compilation Successful");

    }

}
