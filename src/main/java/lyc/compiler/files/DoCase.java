package lyc.compiler.files;

import java.util.Stack;

public class DoCase {
	private Stack<Node> condiciones;
	private Stack<Node>	programas;
	private Stack<Integer> contProgramas;
	private TreeIntermediateCode intermediateCode;

	public DoCase(TreeIntermediateCode intermediateCode){
		this.intermediateCode = intermediateCode;
		this.condiciones = new Stack<Node>();
		this.programas = new Stack<Node>();
		//this.contProgramas = new Stack<Integer>();
	}

	public void crearArbol() {
		while(!this.condiciones.isEmpty()) {
			int cant = this.contProgramas.pop();
			Node derecho = this.programas.pop();
			this.intermediateCode.asignar("DERptr",derecho);
			cant--;
			while(cant-- > 0) {
				Node izquierdo = this.programas.pop();
				this.intermediateCode.asignar("IZQptr",izquierdo);
				this.intermediateCode.crearNodo("DERptr","prog","IZQptr","DERptr");
			}
			this.intermediateCode.crearNodo("CUERPOptr","cuerpo","DERptr","Pptr");
			Node condicion = this.condiciones.pop();
			this.intermediateCode.asignar("CONDptr",condicion);
			this.intermediateCode.crearNodo("Pptr","if","CONDptr", "CUERPOptr");
		}
		
		this.intermediateCode.asignar("DOCASEptr","Pptr");
	}

	public void defaultCase() {
		int cant = this.contProgramas.pop();
		Node derecho = this.programas.pop();
		this.intermediateCode.asignar("DEFAULTptr",derecho);
		cant--;
		while(cant-- > 0) {
			Node izquierdo = this.programas.pop();
			this.intermediateCode.asignar("DEFAULT_AUXptr", izquierdo);
			this.intermediateCode.crearNodo("DEFAULTptr", "prog", "DEFAULT_AUXptr", "DEFAULTptr");
		}
		this.intermediateCode.asignar("Pptr","DEFAULTptr");
	}

	public void apilarPrograma(){
		Node programa = this.intermediateCode.obtenerNodo("Pptr");
		this.programas.push(programa);
	}

	public void apilarCondicion() {
		Node condicion = this.intermediateCode.obtenerNodo("CONDptr");
		this.condiciones.push(condicion);
	}

	public void apilarCont(int cont) {
		if(this.contProgramas == null) {
			this.contProgramas = new Stack<Integer>();
			return;
		}
		this.contProgramas.push(cont);
	}
}
