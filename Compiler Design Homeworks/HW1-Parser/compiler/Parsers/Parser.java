/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.Parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author Ömer Ünaldı
 */
public class Parser {
     private List<String> s_lines;
    private List<String> s_parsedLines = new ArrayList<String>();
    private List<String> source = new ArrayList<String>();
    private List<String> syntax = new ArrayList<String>();
    private int tabCount = 0;
    private boolean isInRepeat = false;

    private String eol = ";$";

    public Parser(List<String> lines){

        this.s_lines = lines;

        prepareParseLines(this.s_lines);

        for(int i = 0; i < this.s_parsedLines.size(); i++){
            parseLineForSource(this.s_parsedLines.get(i), (i + 1));
            parseLineForSyntax(this.s_parsedLines.get(i), (i + 1));
        }

        this.source.add((this.s_parsedLines.size() + 1) + ": " + "EOF");

        System.out.println("------Source Code------\n");
        for(int i = 0; i < this.source.size(); i++){
            System.out.println(this.source.get(i));
        }

        System.out.println("\n------Syntax Tree------");
        for(int i = 0; i < this.syntax.size(); i++){
            System.out.println(this.syntax.get(i));
        }

    }

    private void prepareParseLines(List<String> l){

        for(int i = 0; i < this.s_lines.size(); i++){
            String line = this.s_lines.get(i).trim();
            if(checkEndOfLineCharacter(line)){
                s_parsedLines.add(removeEndOfLineCharacter(line));
            }
            else{
                s_parsedLines.add(line);
            }
        }


    }

    private void parseLineForSource(String line , int index){
        //System.out.println(line);
        List<String> parsedLine = new ArrayList<String>();

        parsedLine = trimBySpace(line);


        if(parsedLine.get(0).equals("if") || parsedLine.get(0).equals("end")
                || parsedLine.get(0).equals("repeat") || parsedLine.get(0).equals("else")){
            String l = index + ": " + line;
            this.source.add(l);
        }
        else{
            String l = index + ": " + line + ";";
            this.source.add(l);
        }

        for(int i = 0; i < parsedLine.size(); i++){
            if(parsedLine.get(i).equals("read") || parsedLine.get(i).equals("write")){
                this.source.add("\t" + index + ": reserved word: " + parsedLine.get(i));
            }
            else if(parsedLine.get(i).equals("repeat") || parsedLine.get(i).equals("if") || parsedLine.get(0).equals("else")
                    || parsedLine.get(i).equals("then") || parsedLine.get(i).equals("end") || parsedLine.get(i).equals("until")){
                this.source.add("\t" + index + ": reserved word: " + parsedLine.get(i));
            }
            else if(parsedLine.get(i).equals("<") || parsedLine.get(i).equals("=") || parsedLine.get(i).equals("+")
                    || parsedLine.get(i).equals("-") || parsedLine.get(i).equals("*") || parsedLine.get(i).equals("/")
                    || parsedLine.get(i).equals(":=")){

                this.source.add("\t" + index + ": " + parsedLine.get(i));
            }
            else {
                if(Character.isDigit(parsedLine.get(i).charAt(0))){
                    this.source.add("\t" + index + ": NUM, val= " + parsedLine.get(i));
                }
                else{
                    this.source.add("\t" + index + ": ID, name= " + parsedLine.get(i));
                }
            }
        }

        if(!parsedLine.get(0).equals("repeat") && !parsedLine.get(0).equals("if")
                && !parsedLine.get(0).equals("else")){
            this.source.add("\t" + index + ": ;");
        }
    }

    private void parseLineForSyntax(String line , int index){
        List<String> parsedLine = new ArrayList<String>();

        parsedLine = trimBySpace(line);

        for(int i = 0; i < parsedLine.size(); i++){
            if(parsedLine.get(i).equals("read")){
                if((i + 1) != parsedLine.size()){
                    this.syntax.add( giveTabForGivenInt(this.tabCount)+ parsedLine.get(i) + " " + parsedLine.get(i + 1));
                }
            }
            else if(parsedLine.get(i).equals("write")){
                this.syntax.add( giveTabForGivenInt(this.tabCount)+ parsedLine.get(i));
                this.tabCount++;
                AddIdOrConst(parsedLine.get(i + 1));
                this.tabCount--;
            }
            else if(parsedLine.get(i).equals(":=")){
                this.syntax.add(giveTabForGivenInt(this.tabCount)+ "Assign to: " + parsedLine.get(i - 1));

                this.tabCount++;

                int returnValue = isOperation(parsedLine);

                if(returnValue != -1){
                    this.syntax.add(giveTabForGivenInt(this.tabCount) + "Op: " + parsedLine.get(returnValue));
                    this.tabCount++;
                    AddIdOrConst(parsedLine.get(returnValue - 1));
                    AddIdOrConst(parsedLine.get(returnValue + 1));
                    this.tabCount--;
                }
                else {
                    AddIdOrConst(parsedLine.get(i + 1));
                }

                this.tabCount--;
            }
            else if(parsedLine.get(i).equals("if")){
                this.syntax.add( giveTabForGivenInt(this.tabCount) + parsedLine.get(i));

                this.tabCount++;

                int returnValue = findSmallerOrEqual(parsedLine);

                if(returnValue != -1){
                    this.syntax.add(giveTabForGivenInt(this.tabCount) + "Op: " + parsedLine.get(returnValue));
                    this.tabCount++;
                    AddIdOrConst(parsedLine.get(returnValue - 1));
                    AddIdOrConst(parsedLine.get(returnValue + 1));
                    this.tabCount--;
                }
                else {
                    AddIdOrConst(parsedLine.get(i + 1));
                }

                this.tabCount--;
            }
            else if(parsedLine.get(i).equals("repeat")){
                this.syntax.add( giveTabForGivenInt(this.tabCount) + parsedLine.get(i));
                this.isInRepeat = true;
            }
            else if(parsedLine.get(i).equals("until")){
                int returnValue = findSmallerOrEqual(parsedLine);

                if(returnValue != -1){
                    this.syntax.add(giveTabForGivenInt(this.tabCount) + "Op: " + parsedLine.get(returnValue));
                    this.tabCount++;
                    AddIdOrConst(parsedLine.get(returnValue - 1));
                    AddIdOrConst(parsedLine.get(returnValue + 1));
                    this.tabCount--;
                }
                else {
                    AddIdOrConst(parsedLine.get(i + 1));
                }

                this.isInRepeat = false;
            }
        }
    }

    private int isOperation(List<String> line){

        for(int i = 0; i< line.size(); i++){
            if(line.get(i).equals("+") || line.get(i).equals("-")
                    || line.get(i).equals("*") || line.get(i).equals("/")){

                return i;
            }
        }
        return -1;
    }

    private int findSmallerOrEqual(List<String> line){
        for(int i = 0; i< line.size(); i++){
            if(line.get(i).equals("<") || line.get(i).equals("=")){
                return i;
            }
        }
        return -1;
    }

    private void AddIdOrConst(String s){
        if(!Character.isDigit(s.charAt(0))){
            this.syntax.add(giveTabForGivenInt(this.tabCount) + "Id: " + s);
        }
        else{
            this.syntax.add(giveTabForGivenInt(this.tabCount) + "Const: " + s);
        }
    }

    private List<String> trimBySpace(String line){
        List<String> parsedLine = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(line);

        while (st.hasMoreTokens()){
            parsedLine.add(st.nextToken());
        }

        return parsedLine;
    }

    private boolean checkEndOfLineCharacter(String line){
        Pattern pattern = Pattern.compile(this.eol, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }

    private String removeEndOfLineCharacter(String line){
        int length = line.length();
        return line.substring(0, length - 1);
    }

    private String giveTabForGivenInt(int tNumber){
        String s = "";

        if(this.isInRepeat == true){
            tNumber++;
        }

        for(int i = 0; i < tNumber; i++){
            s += "\t";
        }

        return s;
    }

    List<String> getSource(){
        return this.source;
    }

    List<String> getSyntax(){
        return this.syntax;
    }
}
