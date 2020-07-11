/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.Semantic;

import compiler.Parsers.LineParser;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Ömer Ünaldı
 */
public class SemanticAnalysis {
    private List<String> lines;
    private List<String> parsedLines;
    private List<String> variables = new ArrayList<String>();
    private int location  = 0;
    private List<String> parsedLine = new ArrayList<String>();

    public SemanticAnalysis(List<String> lines){
        this.lines = lines;

        LineParser lp = new LineParser(this.lines);
        this.parsedLines = lp.getParsedLines();

        System.out.println("\n------Symbol Table------\n");
        System.out.println("Variable Name\t\tLocation\t\tLine Numbers");
        System.out.println("-------------\t\t--------\t\t------------");
        prepare();
        printSymbolTable();
    }

    private void prepare(){
        for(int i = 0; i < this.parsedLines.size(); i++){

            if(this.parsedLines.get(i).equals("read")){
                if(this.parsedLines.get(i + 1) != null){
                    String s = this.parsedLines.get(i + 1);
                    if(!isAlreadyExist(s)){
                        this.variables.add(s);
                    }
                }

            }
            else if(this.parsedLines.get(i).equals(":=")){
                if(this.parsedLines.get(i - 1) != null){
                    String s = this.parsedLines.get(i - 1);
                    if(!isAlreadyExist(s)){
                        this.variables.add(s);
                    }
                }
            }

        }
    }

    private boolean isAlreadyExist(String var){

        if(this.variables.size() == 0)
            return false;

        for (int i = 0; i< this.variables.size(); i++){
            if(this.variables.get(i).equals(var)){
                return true;
            }
        }

        return false;
    }

    private void printSymbolTable(){

        String var;
        String line;
        int index;

        for(int i = 0; i < this.variables.size(); i++){
            var = this.variables.get(i);
            System.out.format("%-20s", var);
            System.out.format("%-17d", location);
            this.location++;
            for(int j = 0; j < this.lines.size(); j++){
                line = lines.get(j).trim();

                LineParser lp = new LineParser(line);
                line = lp.getLine();
                parseLine(line);

                for(int k = 0; k < this.parsedLine.size(); k++){
                    if(this.parsedLine.get(k).equals(var)){
                        System.out.format("%-5d",(j + 1));
                        index = (j + 1);
                    }
                }
                this.parsedLine.clear();
            }
            System.out.println();
        }
    }

    private void parseLine(String line){
        StringTokenizer st = new StringTokenizer(line);

        while (st.hasMoreTokens()){
            this.parsedLine.add(st.nextToken());
        }
    }

    public List<String> getVariables(){
        return this.variables;
    }
}
