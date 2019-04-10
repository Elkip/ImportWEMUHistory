package importwemuhistory;

import java.io.*;
import java.util.LinkedList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.Timestamp;


public class ImportWEMUHistory {

    public static void main(String[] args) {
        File file = new File("/home/elkip/Documents/wemu_import.pdf");
        //System.out.println(FormatDate("3:43pm 01/31/2019"));
        Formatpdf(file);
    } 
    
    public static String FormatDate(String strDate)  {
        Timestamp timeStampDate = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("hh:mmaa MM/dd/yyyy"); //format of dates in pdf
            Date parsedDate = dateFormat.parse(strDate);
            timeStampDate = new Timestamp(parsedDate.getTime());
        }
        catch (ParseException e) {
        }
        //drop milliseconds from the TS
        String ts = timeStampDate.toString();
        ts= ts.substring(0, ts.length()-2);
        return ts;
    }
    
    private static void Formatpdf(File f) {
        PDFParser parse = new PDFParser(f);

        LinkedList<record> insert = new LinkedList<>();
        record i = new record();

        //until the end of the text in document
        while(parse.done()){
            //read in line will be formatted or discarded
            String output = new String(parse.nextLine()); 
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
                //read in title
                i.track = new String(parse.nextLine());

                if (i.artist == null || i.album == null) {
                    i.reset();
                } 
                else {
                    insert.add(i);
                }

                i = new record();
            }
            else if (output.contains("http://wemu.org/programs/")) {
                //read in the dates and store them in the records in linked list
                record nodeNow;
                String oldDate;
                String fDate;
                String date = output.substring(0, 18);

                for (int j = 0; j < insert.size(); j++) {
                    nodeNow = insert.get(j);

                    oldDate = date;
                    fDate = FormatDate(date);
                    nodeNow.timestamp = fDate;

                    while (date.equals(oldDate)) {
                        //read in next line, next time stamp
                        if (parse.done())
                            break;
                        output = new String(parse.nextLine());
                        if (output.length()>=18)
                            date = output.substring(0, 18);
                    }
                }
            }
            //TIME TO WRITE IMPORT?
            if (!insert.isEmpty() && insert.peekFirst().isFull()) {
                while (!insert.isEmpty()) {
                    record r = insert.pop();
                    r.addQuotes();
                    System.out.println("INSERT INTO Play_Log(Play_Timestamp, Album_ID, Track_Name, Host, Set_Artist, Set_Label) VALUES\n"
                            + "	(" + r.timestamp + ", SELECT Album_ID FROM Album WHERE Title = " + r.album + ", " + r.track + ", " + r.host + ", " + r.artist + ", " + r.label + ");");
                }
            }
        }
        System.out.println("Done");
    }
}
