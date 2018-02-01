package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

public class Timer {

    private long startTid;
    private long stoppTid;

    public static Timer lagOgStartTimer() {
        Timer timer = new Timer();
        timer.start();
        return timer;
    }

    public void start() {
        startTid = System.nanoTime();
    }

    public long stoppOgHentTid() {
        stoppTid = System.nanoTime();
        return (stoppTid - startTid) / 1000000;
    }

}
