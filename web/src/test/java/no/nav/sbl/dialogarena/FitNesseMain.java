package no.nav.sbl.dialogarena;

import static org.kohsuke.args4j.ExampleMode.ALL;
import no.nav.modig.test.fitnesse.run.FitnesseTestRunner;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public final class FitNesseMain {

    @SuppressWarnings({ "PMD.SystemPrintln" })
    public static void main(String ... args) throws Exception {
        FitnesseTestRunner fitnesse = new FitnesseTestRunner();
        CmdLineParser parser = new CmdLineParser(fitnesse);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException cmdLineException) {
            System.out.println(cmdLineException.getMessage() + "\n");
            System.out.println("Usage:");
            parser.printUsage(System.out);
            System.out.println("\nExample:\n" + FitnesseTestRunner.class.getName() + parser.printExample(ALL) + "\n");
            return;
        }
        fitnesse.start();
    }

    private FitNesseMain() { }

}