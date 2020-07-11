package compiler.Parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringTokenizer;

public class LineParser {
	private String line;
    private List<String> parsedLine = new ArrayList<String>();
    private List<String> parsedLines = new ArrayList<String>();
    private String eol = ";$";
    private String opr = "^(\\d+\\s*[\\/\\+\\-\\*])$|^([a-zA-Z]+\\s*[\\/\\+\\-\\*])$";
    private String read = "^([a-zA-Z]+\\d*)$";
    private String write = "^([a-zA-Z]+\\d*)$|^(\\d+)$";

    public LineParser(List<String> lines){

        for(int i = 0 ; i < lines.size(); i++){
            if(checkEndOfLineCharacter(lines.get(i))){
                String s = removeEndOfLineCharacter(lines.get(i));
                parseLine(s);
            }
            else{
                parseLine(lines.get(i));
            }
        }
    }

    public LineParser(String line){
        //System.out.println(line);

        if(checkEndOfLineCharacter(line)){
            this.line = removeEndOfLineCharacter(line);
        }
        else{
            this.line = line;
        }
    }

    private String removeEndOfLineCharacter(String line){
        int length = line.length();
        return line.substring(0, length - 1);
    }

    private boolean checkEndOfLineCharacter(String line){
        Pattern pattern = Pattern.compile(this.eol, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }

    public void parseLine(String l){
        StringTokenizer st = new StringTokenizer(l);

        while (st.hasMoreTokens()){
            this.parsedLines.add(st.nextToken());
        }
    }

    public boolean parseLineForOP(){

        StringTokenizer st = new StringTokenizer(this.line);

        while (st.hasMoreTokens()){
            this.parsedLine.add(st.nextToken());
        }

        return checkOP(this.parsedLine);
    }

    public boolean parseLineForRW(){

        StringTokenizer st = new StringTokenizer(this.line);

        while (st.hasMoreTokens()){
            this.parsedLine.add(st.nextToken());
        }

        return checkRW(this.parsedLine);
    }

    private boolean checkRW(List<String> pl){
        for(int i = 0; i < pl.size(); i++){
            if(pl.get(i).equals("read")){
                if(i == (pl.size() - 1) || pl.size() > 2){
                    return false;
                }
                else{
                    return checkRegexRead(pl.get(i + 1));
                }
            }
            else{
                if(i == (pl.size() - 1) || pl.size() > 2){
                    return false;
                }
                else{
                    return checkRegexWrite(pl.get(i + 1));
                }
            }
        }

        return true;
    }

    private boolean checkOP(List<String> pl){

        for(int i = 0; i < pl.size(); i++){
            if(pl.get(i).charAt(0) == '+' || pl.get(i).charAt(0) == '-'
                    || pl.get(i).charAt(0) == '*' || pl.get(i).charAt(0) == '/'){
                if(i != 0){
                    String l = pl.get(i-1) + pl.get(i);
                    if(!checkRegexOP(l)){
                        return false;
                    }
                }
                else{
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkRegexOP(String line){
        Pattern pattern = Pattern.compile(this.opr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }

    private boolean checkRegexRead(String line){
        Pattern pattern = Pattern.compile(this.read, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }

    private boolean checkRegexWrite(String line){
        Pattern pattern = Pattern.compile(this.write, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }

    public List<String> getParsedLines(){
        return this.parsedLines;
    }

    public String getLine(){
        return this.line;
    }
}
