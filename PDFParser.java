/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package importwemuhistory;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author elkip
 */
public class PDFParser {
    //String builder to store extracted text

    private StringBuilder sb;
    private int offset;

    public PDFParser() {
        sb = new StringBuilder();
    }

    public PDFParser(File f) {
        sb = new StringBuilder();
        try {
            //create a pdf stripper
            PDDocument pdDoc = PDDocument.load(f);
            PDFTextStripper stripper = new PDFTextStripper();
            System.out.println("PDF Loaded");
            sb.append(stripper.getText(pdDoc));
            pdDoc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char[] nextLine() {
        char[] Line = null;
        int i_end = sb.indexOf("\n", offset);
        
        if (i_end == -1) {
            Line = new char[]{'0','\n'};
            offset = sb.length() + 1;
        }
            
        else {
            Line = new char[i_end - offset];
            sb.getChars(offset, i_end, Line, 0);
            offset = i_end + 1;
           
        }
          return Line;
    }

    public boolean done() {
        return (offset < sb.length());
    }
}
