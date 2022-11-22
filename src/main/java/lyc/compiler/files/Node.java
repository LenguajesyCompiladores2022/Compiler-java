package lyc.compiler.files;

public class Node {
	String value;
	Node left;
	Node right;

	public Node(String value){
		this.value = value;
		right = null;
		left = null;
	}
	public Node(String value, Node left, Node right){
		this.value = value;
		this.right = right;
		this.left = left;
	}

	public boolean esHoja(){
		return this.left == null && this.right == null;
	}
}
