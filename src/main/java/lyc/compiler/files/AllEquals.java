package lyc.compiler.files;

import lyc.compiler.constants.Pointers;
import java.util.*;

public class AllEquals {
	private ArrayList<Node> elementos;
	private int cantArrays;

	public AllEquals() {
		this.elementos = new ArrayList<Node>();
		this.cantArrays = 0;
	}

	public void insertar(Node nodo) {
		this.elementos.add(nodo);
	}

	public void incrementarCantArrays() {
		this.cantArrays++;
	}

	public void crearArbol(TreeIntermediateCode intermediateCode) {
		int numArray = 0;
		int desp = 0;
		int tamArray = this.elementos.size() / this.cantArrays;
		intermediateCode.crearHoja("AUX1ptr","@result", "float");
		intermediateCode.crearHoja("AUX2ptr","_1_0","float");
		intermediateCode.crearNodo(Pointers.AEptr,"=","AUX1ptr","AUX2ptr");
		intermediateCode.crearHoja("AUX2ptr","_0_0","float");
		intermediateCode.crearNodo("FALSEptr","=","AUX1ptr","AUX2ptr");

		while(desp < tamArray) {
			numArray = 0;
			while(numArray < this.cantArrays-1){
				intermediateCode.asignar("AUX3Ptr",this.elementos.get(numArray*tamArray+desp));
				intermediateCode.asignar("AUX4Ptr",this.elementos.get((numArray+1)*tamArray+desp));
				intermediateCode.crearNodo("COMPptr","!=","AUX3Ptr","AUX4Ptr");
				intermediateCode.crearNodo("CUERPOptr","cuerpo","FALSEptr",Pointers.AEptr);
				intermediateCode.crearNodo(Pointers.AEptr,"if","COMPptr","CUERPOptr");
				numArray++;
			}
			desp++;
		}
		this.elementos.clear();
		this.cantArrays = 0;
		//intermediateCode.asignar(Pointers.AEptr,Pointers.Pptr);
	}
}
