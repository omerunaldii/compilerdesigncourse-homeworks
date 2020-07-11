package compiler.Variables;

public class Marker {
	
	private String word;
	private int line;
	
	public Marker(String word, int line) {
		this.word = word;
		this.line = line;
	}
	
	public String getReservedWord() {
		return this.word;
	}
	
	public int getLine() {
		return this.line;
	}
}
