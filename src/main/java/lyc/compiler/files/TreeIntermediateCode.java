package lyc.compiler.files;

import lyc.compiler.constants.Pointers;

import java.util.HashMap;
import java.util.Stack;

public class TreeIntermediateCode {
	private HashMap<String,Node> pointers;
	private Stack<String> pila;
	private Stack<Node> pilaNodo;
	private Stack<Node> pilaCondiciones;
	public TreeIntermediateCode() {
		this.pointers = new HashMap<String,Node>();
		this.pila = new Stack<String>();
		this.pilaNodo = new Stack<Node>();
		this.pilaCondiciones = new Stack<Node>();
	}

	public void crearHoja(String pointer,String value) {
		this.pointers.put(pointer,new Node(value));
	}

	public String recorrer(){

		Node raiz = this.pointers.get(Pointers.Pptr);

		return this.recorrerR(raiz);
	}

	private String recorrerR(Node nodo) {

		if(nodo.left == null && nodo.right == null)
			return nodo.value + " ";

		String resParcial = "";

		resParcial += nodo.left != null ? recorrerR(nodo.left) : "";
		resParcial += nodo.value + " ";
		return resParcial + (nodo.right != null ? recorrerR(nodo.right) : "");
	}

	public void crearNodo(String ptrResultado,String operador, String hijoIzq, String hijoDer){
		Node derecho = pointers.get(hijoDer);
		Node izquierdo = pointers.get(hijoIzq);

		this.pointers.put(ptrResultado, new Node(operador, izquierdo, derecho));
	}


	public void asignar(String ptrIzq,String ptrDer) {
		Node der = this.pointers.get(ptrDer);
		this.pointers.put(ptrIzq,der);
	}

	public void asignar(String ptrIzq,Node nodo) {
		this.pointers.put(ptrIzq,nodo);
	}

	public void asignarNULL(String ptr){
		this.pointers.put(ptr,null);
	}

	public void apilar(String value) {
		this.pila.push(value);
	}

	public String desapilar() {
		return this.pila.pop();
	}

	public void apilarCondicion(String pointer) {
		Node node = this.pointers.get(pointer);
		this.pilaCondiciones.push(node);
	}
	public void apilarNodo(String pointer) {
		Node node = this.pointers.get(pointer);
		this.pilaNodo.push(node);
	}

	public void desapilarNodo(String pointer) {
		Node node = this.pilaNodo.pop();
		this.pointers.put(pointer,node);
	}

	public void desapilarCondicion(String pointer) {
		Node node = this.pilaCondiciones.pop();
		this.pointers.put(pointer,node);
	}

	public Node obtenerNodo(String pointer) {
		return this.pointers.get(pointer);
	}

	public void preSentenciaControl() {
		this.apilarNodo(Pointers.Pptr);
		this.asignarNULL(Pointers.Pptr);
	}

	public void postSentenciaControl(String padre) {

		this.desapilarCondicion(Pointers.CONDptr);
		if(padre == "if"){
			this.crearNodo(Pointers.SELptr,padre,Pointers.CONDptr,Pointers.Pptr);
		}
		else {
			this.crearNodo(Pointers.Iptr,padre,Pointers.CONDptr,Pointers.Pptr);
		}
		this.desapilarNodo(Pointers.Pptr);
	}
}
