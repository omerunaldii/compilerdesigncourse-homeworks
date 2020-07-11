package compiler;

import compiler.Error.ErrorDetection;
import compiler.GenerateCode.CodeGenerator;
import compiler.Parsers.Parser;
import compiler.Reader.ReadFile;
import compiler.Semantic.SemanticAnalysis;
import compiler.Variables.Marker;
import compiler.Variables.Variable;

import java.util.ArrayList;
import java.util.List;

public class main {

	public static void main(String[] args) {

		System.out.println("Compiler start!");

        for(int i = 0; i <args.length; i++)
        {
            List<String> lines;
		 	List<String> EOLlines;
         	List<Variable> vars = new ArrayList<Variable>();

            String f = args[i];

            // Read file parameter is given file directory
            ReadFile rf = new ReadFile(f);
            lines = rf.getLines();
			EOLlines = rf.getLinesWithEOL();
			
            // create ErrorDetection class
            ErrorDetection ed = new ErrorDetection(lines);

            // Check there is a error or not.
            if(!ed.getIsErrorFound()){
                // Perform lexical analysis.
                Parser p = new Parser(lines, EOLlines);
                
                List<Marker> markers = new ArrayList<Marker>();
             	markers = p.getMarkers();
                
                SemanticAnalysis sa = new SemanticAnalysis(lines);
             	vars = sa.getVariableList();
             	new CodeGenerator(EOLlines, vars, markers, 1);
            }
            else{
                System.out.println( ed.getNumberOfError() + " error found.");
            }
        }

    }

}
