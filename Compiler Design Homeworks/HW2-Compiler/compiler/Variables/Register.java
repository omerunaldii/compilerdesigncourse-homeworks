package compiler.Variables;

public class Register {
	
	private Variable var;
	private int index;
	
	public Register(Variable var, int index) {
		this.var = var;
		this.index = index;
	}
	
	public Variable getVariable() {
		return this.var;
	}
	
	public int getRegisterIndex() {
		return this.index;
	}
}
