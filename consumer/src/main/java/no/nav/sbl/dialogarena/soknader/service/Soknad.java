package no.nav.sbl.dialogarena.soknader.service;

/*
Midlertidig klasse mens vi venter på domene objekter
 */
public class Soknad {
    private String heading;
    private String date;

    public Soknad(String heading, String date){
        this.heading = heading;
        this.date = date;
    }

    public String getHeading() {
        return heading;
    }

    public String getDate() {
        return date;
    }
}
