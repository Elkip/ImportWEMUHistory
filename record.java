/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package importwemuhistory;

/**
 *
 * @author elkip
 */
//parameters of every record in Play_Log
public class record {
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
                    && this.timestamp != null);
        }
        
        public void addQuotes(){
            this.album = ("'"+album.trim()+"'");
            this.artist = ("'"+artist.trim()+"'");
            this.host = ("'"+host.trim()+"'");
            if (this.label == null)
                        this.label = "NULL";
            if (!this.label.equals("NULL"))
                this.label = ("'"+label.trim()+"'");
            this.timestamp = ("'"+timestamp.trim()+"'");
            this.track = ("'"+track.trim()+"'");
        }
        
        public void print() {
            System.out.println("host: " + this.host + " album: " + this.album + " artist: " + this.artist + " track: " + this.track + " label: " + this.label + " timestamp: " + this.timestamp);
        }
}
