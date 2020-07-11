package compiler.Error;

import compiler.Parsers.LineParser;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorDetection {
	private int ErrorCount = 0;
    private boolean isErrorFound = false;
    private int commentLineCount = 0;

    private String eol = ";$";
    private String rw = "^(read\\s*[a-zA-Z]+\\s*;)$|^(write\\s*[a-zA-Z]+\\s*;)$|^(write\\s*\\d+\\s*;)$";
    private String asg = "^([a-zA-Z]+\\s*:=\\s*\\d+;)$";
    private String cl = "^[*]";
    private String until = "^(until\\s+[a-zA-Z]+\\s*[<|=]\\s*[a-zA-Z]+\\s*;)$|^(until\\s+[a-zA-Z]+\\s*[<|=]\\s*[0-9]+\\s*;)$|(if\\s+[a-zA-Z]+\\s*[<|=]\\s*[a-zA-Z]+\\s+then)$|^(if\\s+[a-zA-Z]+\\s*[<|=]\\s*[0-9]+\\s+then)$";
    private String repeat = "^(repeat)$|^(end)$|^(else)$";

    private String err_asg = "^(\\d+)|^(\\s*:=)";

    public ErrorDetection(List<String> lines) {

        for(int i = 0; i< lines.size(); i++){
            String line = lines.get(i).trim();
            Pattern pattern = Pattern.compile(this.cl, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);

            if(!matcher.find()){
                if(line.contains(":=")){
                    checkASG(line, (i + 1));
                }
                else if(line.contains("if") || line.contains("until")){
                    checkCondition(line, (i + 1));
                }
                else if(line.contains("else") || line.contains("repeat") || line.contains("end")){
                    checkElseRepeatEnd(line, (i + 1));
                }
                else {
                    checkRW(line, (i + 1));
                }
            }
            else{
                this.commentLineCount++;
            }
        }
    }

    private boolean checkEOL(String line){
        Pattern pattern = Pattern.compile(this.eol, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }

    private void checkRW(String line, int index){
        Pattern pattern = Pattern.compile(this.rw, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        if(!matcher.find()){
            LineParser lp = new LineParser(line);

            // This code looking for [a-zA-Z] after read/write
            if(!lp.parseLineForRW()){
                this.ErrorCount++;
                ErrorCodes errCode = ErrorCodes.ERR_AFTR_RW;
                writeErrorCode(errCode, index);
            }

            // This code looking for end of line character ';'
            if(!checkEOL(line)){
                this.ErrorCount++;
                ErrorCodes errCode = ErrorCodes.ERR_EOL;
                writeErrorCode(errCode, index);
            }
        }
    }

    private void checkASG(String line, int index){
        Pattern pattern = Pattern.compile(this.asg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        if(!matcher.find()){

            // This code looking for left side of assignment character ':='
            if(!checkLeftSideOfASG(line)){
                this.ErrorCount++;
                ErrorCodes errCode = ErrorCodes.ERR_ASG_LEFT;
                writeErrorCode(errCode, index);
            }

            // This code looking for end of line character ';'
            if(!checkEOL(line)){
                this.ErrorCount++;
                ErrorCodes errCode = ErrorCodes.ERR_EOL;
                writeErrorCode(errCode, index);
            }

            LineParser lp = new LineParser(line);

            if(!lp.parseLineForOP()){
                this.ErrorCount++;
                ErrorCodes errCode = ErrorCodes.ERR_ASG_RIGHT;
                writeErrorCode(errCode, index);
            }

            //System.out.println("index at " + index + " : " + lp.parseLine());
        }
    }

    private void checkCondition(String line, int index){

    	Pattern pattern = Pattern.compile(this.until, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        if(!matcher.find()){

            this.ErrorCount++;
            ErrorCodes errCode = ErrorCodes.ERR_CONDITION;
            writeErrorCode(errCode, index);
        }
    }

    private void checkElseRepeatEnd(String line, int index){
        Pattern pattern = Pattern.compile(this.repeat, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        if(!matcher.find()){
            System.out.println("error found at line " + index);
        }
    }

    private boolean checkLeftSideOfASG(String line){
        Pattern pattern = Pattern.compile(this.err_asg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        return !matcher.find();
    }

    private void writeErrorCode(ErrorCodes errorCode , int line){
        this.isErrorFound = true;
        switch (errorCode) {
            case ERR_EOL://ERR_EOL
                System.out.println("Missing end of line character ';' at line " + line);
                break;
            case ERR_ASG_LEFT: //ERR_ASG_LEFT
                System.out.println("Check left side of ':=' at line " + line);
                break;
            case ERR_ASG_RIGHT: //ERR_ASG_RIGHT
                System.out.println("Check right side of ':=' at line " + line);
                break;
            case ERR_AFTR_RW: //ERR_AFTR_RW
                System.out.println("Check after read/write at line " + line);
                break;
            case ERR_CONDITION: //ERR_CONDITION
                System.out.println("Type error at line " + line + ": if/until is not boolean");
                break;
            default:
                System.out.println("Unknown error code: "+ errorCode);
                break;
        }
    }

    public boolean getIsErrorFound(){
        return this.isErrorFound;
    }

    public int getNumberOfError(){
        return this.ErrorCount;
    }

    public int getNumberOfCommentLine(){
        return this.commentLineCount;
    }
}
