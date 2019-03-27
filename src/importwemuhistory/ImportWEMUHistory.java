
package importwemuhistory;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;



public class ImportWEMUHistory {

    public static void main(String[] args) throws IOException {

        //Load Document
        File file = new File("/home/elkip/NetBeansProjects/ImportWEMUHistory/mozilla.pdf");
        PDDocument document = PDDocument.load(file);
        
        System.out.println("PDF Loaded");
        
        document.close();
    }
    
}
