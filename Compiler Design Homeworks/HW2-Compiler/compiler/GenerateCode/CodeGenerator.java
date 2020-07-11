package compiler.GenerateCode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import compiler.Variables.Marker;
import compiler.Variables.Register;
import compiler.Variables.Variable;

public class CodeGenerator {
	
	private String fileName;
	
	private List<String> lines = new ArrayList<String>();
	private List<String> words = new ArrayList<String>();
	private int[] registers = new int[]{0,0,0,0,0,0,1};
	
	private List<String> machineCode = new ArrayList<String>();
	
	private List<Variable> vars = new ArrayList<Variable>();
	private List<Register> regs = new ArrayList<Register>();
	private static final int PCIndex = 6;
	
	private int currentIndex = 0;
	
	public CodeGenerator(List<String> lines, List<Variable> vars, List<Marker> markers ,int fileIndex) {
		this.lines = lines;
		this.vars = vars;
		
		this.fileName = fileIndex + "-output.txt";

		prepareToGenerateCode();
		
		generateMachineCode();
		
		try {
			writeMachineCodeToFile(this.machineCode);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("\nRegisters");
//		for(int i = 0; i < this.registers.length; i++) {
//			System.out.print(this.registers[i] + " ");
//		}
//		
//		System.out.println("\nRegisters \n");
//		
//		for(int i = 0; i < this.regs.size(); i++ ) {
//			System.out.println("Variable : " + this.regs.get(i).getVariable().getVariableName()
//					+ " Register Index : " + this.regs.get(i).getRegisterIndex());
//		}
//		
//		System.out.println("\nRegisters");
//		for(int i = 0; i < this.registers.length; i++) {
//			System.out.print(this.registers[i] + " ");
//		}

	}
	
	private void prepareToGenerateCode() {
		for(int i = 0; i< this.lines.size(); i++) {
			
			Scanner sc = new Scanner(this.lines.get(i));
			
			while (sc.hasNextLine()) {
	            Scanner s2 = new Scanner(sc.nextLine());
		        while (s2.hasNext()) {
		        	this.words.add(s2.next());
		        }
			}
		}
	}
	
	private void generateMachineCode() {
		for(int i = 0; i < this.words.size(); i++) {
			this.currentIndex = i;
			if(this.words.get(i).equals("read")) {
				
				String varName = this.words.get(i + 1);
				
				int regIndex = addVariableToRegister(varName);
				
				if(regIndex != -1) {
					this.machineCode.add("IN " + regIndex + ",0,0");
					increasePC();
				}
			}
			else if(this.words.get(i).equals("write")) {
				int regIndex = findVariableFromRegisterList(this.words.get(i + 1));
				
				if(regIndex != -1) {
					this.machineCode.add("OUT " + regIndex + ",0,0");
					increasePC();
				}
			}
			else if(this.words.get(i).equals(":=")) {
				
				String varName = this.words.get(i - 1);
				
				addVariableToRegister(varName);
				
				increasePC();

			}
			else if(this.words.get(i).equals("+") || this.words.get(i).equals("-") 
					|| this.words.get(i).equals("*") || this.words.get(i).equals("/")) {
				
				int index = i;
				
				String varName = this.words.get(i - 1);
				
				int rIndex1 = addVariableToRegister(varName);
								
				varName = this.words.get(i + 1);
				
				int rIndex2 = addVariableToRegister(varName);
								
				String op = findOperation(this.words.get(i));
				
				//System.out.println("Operation : " + op);
				
				// Go back until find :=
				while(!this.words.get(index).equals(":=")) {
					index--;
				}
				
				int regIndex = findVariableFromRegisterList(this.words.get(index - 1));
				
				if(rIndex1 != -1 || rIndex2 != -1) {
					String macCode = op + " " + regIndex + "," + rIndex1 + "," + rIndex2;
					this.machineCode.add(macCode);
				}

			}
		}
		this.machineCode.add("HALT 0,0,0");
	
	}
	
	private int addVariableToRegister(String variable) {
		int emptyIndex = -1;
		
		int regIndex = findVariableFromRegisterList(variable);

		if(regIndex == -1) {

			emptyIndex = findEmptyRegister();
			
			if(emptyIndex != -1) {

				Variable var = new Variable(variable);
				Register reg = new Register(var, emptyIndex);
				
				this.regs.add(reg);
				
				this.registers[emptyIndex] = 1;
								
				return emptyIndex;
			}
			else {
				dischargeUselessRegiser();
				addVariableToRegister(variable);
			}
		}
		
		return regIndex;
	}
	
	private void addToMachineCodeList() {
		
	}
	
	private void dischargeUselessRegiser() {
		
		boolean variableIsAliveFuture = false;
				
		for(int i = 0; i < this.regs.size(); i++) {
			for(int j = this.currentIndex; j < this.words.size(); j++) {
				if(this.regs.get(i).getVariable().getVariableName().equals(this.words.get(j))) {
					variableIsAliveFuture = true;
				}
			}
			
			if(variableIsAliveFuture == false ) {
				int index = this.regs.get(i).getRegisterIndex();
				this.registers[index] = 0;
				this.regs.remove(i);
			}
			else {
				variableIsAliveFuture = false;
			}
		}
		
//		System.out.println("\nAfter Discharge Registers");
//		for(int i = 0; i < this.registers.length; i++) {
//			System.out.print(this.registers[i] + " ");
//		}
	}
	
	private int findVariableFromRegisterList(String varName) {
		for(int i = 0; i < this.regs.size(); i++) {
			if(this.regs.get(i).getVariable().getVariableName().equals(varName)) {
				return this.regs.get(i).getRegisterIndex();
			}
		}
		
		return -1;
	}
	
	private void writeMachineCodeToFile(List<String> macCode) throws IOException {
				
		FileWriter fw = new FileWriter(this.fileName);

	    for (int i = 0; i < macCode.size(); i++) {
	    	fw.write(i + ": " +macCode.get(i) + "\n");
	    }
	    fw.close();
	}
	
	private int findEmptyRegister() {
		for(int i = 0; i < (this.registers.length - 1); i++) {
			if(this.registers[i] == 0) {
				return i;
			}
		}
		
		return -1;
	}
	
	private String findOperation(String op) {
		switch(op) {
		case "+":
			return "ADD";
		case "-":
			return "SUB";
		case "*":
			return "MUL";
		case "/":
			return "DIV";
		default:
			return null;
		}
	}
	
	private void increasePC() {
		this.registers[this.PCIndex] ++;
	}
	
	private int getPC() {
		return this.registers[this.PCIndex];
	}
}
