/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Ömer Ünaldı
 */
public class ReadFile {
    List<String> lines = new ArrayList<String>();

    public ReadFile(String fPath) {

        try {
            Scanner scanner = new Scanner(new File(fPath));
            while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String empty = new String();
				if(!line.equals(empty)){
					this.lines.add(line);
				}
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLines(){
        return this.lines;
    }
}
