package compiler.Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFile {
	
	private String eol = ";$";
	
	List<String> lines = new ArrayList<String>();
	List<String> withoutSClines = new ArrayList<String>();

    public ReadFile(String fPath) {

        try {
            Scanner scanner = new Scanner(new File(fPath));
            while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String empty = new String();
				if(!line.equals(empty)){
					this.lines.add(line);
					
					if(checkEndOfLineCharacter(line)){
						this.withoutSClines.add(removeEndOfLineCharacter(line));
		            }
		            else{
		            	this.withoutSClines.add(line);
		            }
				}
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public List<String> getLines(){
        return this.lines;
    }
    
    public List<String> getLinesWithEOL(){
    	return this.withoutSClines;
    }
}
