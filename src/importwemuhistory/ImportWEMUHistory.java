package importwemuhistory;

import java.io.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.*;

public class ImportWEMUHistory {

    public static void main(String[] args) {
        File file = new File("/home/elkip/NetBeansProjects/ImportWEMUHistory/mozilla.pdf");
        
        
        ReadPDF(file);
    }
    
    private static void ReadPDF(File f) {

        try {
            //create a pdf stripper
            PDDocument pdDoc = PDDocument.load(f);
            PDFTextStripper stripper = new PDFTextStripper();
            System.out.println("PDF Loaded");
            
            //String builder to store extracted text
            StringBuilder sb = new StringBuilder();
            
            sb.append(stripper.getText(pdDoc));
            
            int offset = 0;
            //until the end of the document
            do{
                //read in line
                char[] Line = null;
                int i_end = sb.indexOf("\n",offset);
                Line = new char[i_end - offset];
                sb.getChars(offset, i_end, Line, 0);
                System.out.println(new String(Line));
                offset = i_end+1;

            } while(offset < sb.length());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
