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
		intermediateCode.crearHoja("AUX1ptr","@result");
		intermediateCode.crearHoja("AUX2ptr","true");
		intermediateCode.crearNodo(Pointers.Pptr,"=","AUX1ptr","AUX2ptr");
		intermediateCode.crearHoja("AUX2ptr","false");
		intermediateCode.crearNodo("FALSEptr","=","AUX1ptr","AUX2ptr");

		while(desp < tamArray) {
			numArray = 0;
			while(numArray < this.cantArrays-1){
				intermediateCode.asignar("AUX3Ptr",this.elementos.get(numArray*tamArray+desp));
				intermediateCode.asignar("AUX4Ptr",this.elementos.get((numArray+1)*tamArray+desp));
				intermediateCode.crearNodo("COMPptr","!=","AUX3Ptr","AUX4Ptr");
				intermediateCode.crearNodo("CUERPOptr","cuerpo","FALSEptr",Pointers.Pptr);
				intermediateCode.crearNodo(Pointers.Pptr,"if","COMPptr","CUERPOptr");
				numArray++;
			}
			desp++;
		}

		intermediateCode.asignar(Pointers.AEptr,Pointers.Pptr);

		System.out.println("funciona?");
	}
}
