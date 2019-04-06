package importwemuhistory;

import java.io.*;
import java.util.LinkedList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.*;

public class ImportWEMUHistory {

    public static void main(String[] args) {
        File file = new File("/home/elkip/NetBeansProjects/ImportWEMUHistory/mozilla.pdf");
        StringBuilder pdf = ReadPDF(file);
        //System.out.println(pdf);
        Formatpdf(pdf);
    }

    private static StringBuilder ReadPDF(File f) {
        //String builder to store extracted text
        StringBuilder sb = new StringBuilder();

        try {
            //create a pdf stripper
            PDDocument pdDoc = PDDocument.load(f);
            PDFTextStripper stripper = new PDFTextStripper();
            System.out.println("PDF Loaded");
            sb.append(stripper.getText(pdDoc));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }

    public static void Formatpdf(StringBuilder sb) {
        int offset = 0; //how far into text of document (sb)
        DateFormat dateFormat = new SimpleDateFormat("hh:mmaa mm/dd/yyyy"); //format of dates in pdf
        String formattedDate;
        LinkedList<record> insert = new LinkedList<>();
        record i = new record();
        char[] Line;
        
        //until the end of the text in document
        do {
            //read in line

            int i_end = sb.indexOf("\n", offset);
            Line = new char[i_end - offset];
            sb.getChars(offset, i_end, Line, 0);
            offset = i_end + 1;

            String output = new String(Line); //value of attribute

            //filter the string accordingly
            if (output.startsWith("ARTIST:")) {
                output = output.substring(8);
                i.artist = output;
            } 
            else if (output.startsWith("ALBUM:")) {
                output = output.substring(7);
                i.album = output;
            } 
            else if (output.startsWith("LABEL:")) {
                output = output.substring(7);
                i.label = output;
                if (i.label.equals("N/A")) {
                    i.label = "NULL";
                }
            } 
            else if (output.startsWith("HOSTS:")) {
                output = output.substring(7);
                i.host = output;

                //read in next line for title
                i_end = sb.indexOf("\n", offset);
                Line = new char[i_end - offset];
                sb.getChars(offset, i_end, Line, 0);
                offset = i_end + 1;
                
                i.track = new String(Line);

                
                if (i.artist == null || i.album == null)
                    i.reset();
                else
                    insert.add(i);
                
                i = new record();
            } 
            else if (output.contains("http://wemu.org/programs/")) {
                //read in the dates and store them in the records in linked list
                record nodeNow;
                String oldDate = null;
                String date = output.substring(0, 18);
                
                for (int j = 0; j < insert.size(); j++) {
                    nodeNow = insert.get(j);
                    //System.out.println(j + " " + output);

                    
                    //formattedDate = dateFormat.format(date).toString();
                    nodeNow.timestamp = date;
                    //nodeNow.print();
                    
                    oldDate = date;
                    while(date.equals(oldDate)){
                        //read in next line, next time stamp
                        i_end = sb.indexOf("\n", offset);
                        Line = new char[i_end - offset];
                        sb.getChars(offset, i_end, Line, 0);
                        offset = i_end + 1;
                        output = new String(Line);
                        date = output.substring(0, 18);
                    }
                }

            } 
            else {
                continue;
            }
            //TIME TO IMPORT
            
            if (!insert.isEmpty() && insert.peekFirst().isFull()) {
                while (!insert.isEmpty()) {
                    record r = insert.pop();
                    System.out.println("INSERT INTO Play_Log(Play_Timestamp, Album_ID, Track_Name, Host, Set_Artist, Set_Label) VALUES\n"
                            + "	(" + r.timestamp + ", SELECT Album_ID FROM Album WHERE Title = \"" + r.album + "\", " + r.track + ", " + r.host + ", " + r.artist + ", " + r.label + ");");
                }
            } 

           

        } while (offset < sb.length());
    }

    //parameters of every record in Play_Log
    public static class record {

        String host = null, track = null, album = null, artist = null, label = null, timestamp = null;

        public void reset() {
            this.host = null;
            this.album = null;
            this.artist = null;
            this.track = null;
            this.label = null;
            this.timestamp = null;
        }

        public boolean isFull() {
            return (this.host != null && this.track != null && this.album != null && this.artist != null
                    && this.label != null && this.timestamp != null);
        }
        
        public void print() {
            System.out.println("host: " + this.host + " album: " + this.album + " artist: " + this.artist + " track: " + this.track + " label: " + this.label + " timestamp: " + this.timestamp);
        }
    }

}
