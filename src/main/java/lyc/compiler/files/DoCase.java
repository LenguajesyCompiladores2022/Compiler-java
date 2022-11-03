package lyc.compiler.files;

import lyc.compiler.constants.Pointers;

import java.util.Stack;

public class DoCase {
	private Stack<Integer> contCasos;
	private TreeIntermediateCode intermediateCode;

	public DoCase(TreeIntermediateCode intermediateCode){
		this.intermediateCode = intermediateCode;
		this.contCasos = new Stack<Integer>();
	}

	public void crearArbol(TreeIntermediateCode intermediateCode) {
		int cont = this.contCasos.pop();
		intermediateCode.asignar("FALSEptr","DEFAULTptr");
		while(cont-- >= 0) {
			intermediateCode.desapilarCondicion(Pointers.CONDptr);
			intermediateCode.desapilarNodo(Pointers.Pptr);
			intermediateCode.crearNodo("CUERPOptr","cuerpo",Pointers.Pptr,"FALSEptr");
			intermediateCode.crearNodo("FALSEptr","if",Pointers.CONDptr,"CUERPOptr");
		}
		this.intermediateCode.asignar("DOCASEptr","FALSEptr");
	}
	public void apilarCont(int cont) {
		this.contCasos.push(cont);
	}

	public void incrementarContador() {
		int contadorActual = this.contCasos.pop();
		contadorActual++;
		this.contCasos.push(contadorActual);
	}

	public void decrementarContador() {
		int contadorActual = this.contCasos.pop();
		contadorActual--;
		this.contCasos.push(contadorActual);
	}
}
