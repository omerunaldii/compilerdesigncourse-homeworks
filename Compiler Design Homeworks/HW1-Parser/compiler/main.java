/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import compiler.Error.ErrorDetection;
import compiler.Parsers.Parser;
import compiler.Reader.ReadFile;
import compiler.Semantic.SemanticAnalysis;
import java.util.List;
import java.io.IOException;

/**
 *
 * @author Ömer Ünaldı
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException  {

		System.out.println("Compiler start!");

        for(int i = 0; i <args.length; i++)
        {
            List<String> lines;
            String f = args[i];

            // Read file parameter is given file directory
            ReadFile rf = new ReadFile(f);
            lines = rf.getLines();

            // creare ErrorDetection class
            ErrorDetection ed = new ErrorDetection(lines);

            // Check there is a error or not.
            if(!ed.getIsErrorFound()){
                // Perform lexical analysis.
                Parser sp = new Parser(lines);
                SemanticAnalysis sa = new SemanticAnalysis(lines);
            }
            else{
                System.out.println( ed.getNumberOfError() + " error found.");
            }
        }

    }
    
}
