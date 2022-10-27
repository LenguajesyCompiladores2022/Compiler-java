package lyc.compiler.files;

import lyc.compiler.constants.Pointers;

import java.util.HashMap;
import java.util.Stack;

public class TreeIntermediateCode {
	private HashMap<String,Node> pointers;
	private Stack<String> pila;
	private Stack<Node> pilaNodo;
	public TreeIntermediateCode() {
		this.pointers = new HashMap<String,Node>();
		this.pila = new Stack<String>();
		this.pilaNodo = new Stack<Node>();
	}

	public void crearHoja(String pointer,String value) {
		System.out.println("aaa: " + value);
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

	public void apilar(String value) {
		this.pila.push(value);
	}

	public String desapilar() {
		return this.pila.pop();
	}

	public void apilarNodo(String pointer) {
		Node node = this.pointers.get(pointer);
		this.pilaNodo.push(node);
	}

	public void desapilarNodo(String pointer) {
		Node node = this.pilaNodo.pop();
		this.pointers.put(pointer,node);
	}

	public Node obtenerNodo(String pointer) {
		return this.pointers.get(pointer);
	}
}