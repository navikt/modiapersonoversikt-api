package no.nav.kjerneinfo.domain.person;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

public class Fodselsnummer implements Serializable {

    private static final long serialVersionUID = 7264814351886098818L;
    private String nummer;

    public Fodselsnummer() {
    }

    public Fodselsnummer(String fnr) {
        this.nummer = fnr;
    }

    public String getNummer() {
        return nummer;
    }

    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    @Override
    public String toString() {
        return "Fodselsnummer [fodselsnummer=" + nummer + "]";
    }

    /**
     * <p>
     * Checks if the Pid value could represent a D-nummer. A D-nummer is used as the birthnumber for foreigners living
     * in Norway. In a D-nummer, the number 4 has been added to the first cipher in the Pid. Otherwise it is similar to
     * a birthnumber for native Norwegians.
     * </p>
     * <p>
     * Note that this method may not work on weakly validated Pids (using special circumstances flag), as such Pids can
     * never be guaranteed.
     * </p>
     *
     * @return <code>true</code> if Pid is representing a D-nummer, otherwise <code>false</code>
     */
    public boolean isDnummer() {
        return isDnummer(nummer);
    }

    /**
     * Checks that a day may be a D-nummer.
     *
     * @param day Day part of the Pid
     * @return <code>true</code> if Day could be a D-number, otherwise <code>false</code>
     */
    private static boolean isDnrDay(int day) {
        // In a D-nummer 40 is added to the date part
        return (day > 40 && day <= 71);
    }

    /**
     * Calculates wether the Pid parameter is representing a D-nummer.
     *
     * @param pidValue The Pid to check
     * @return true if it is a D-nummer
     */
    private static boolean isDnummer(String pidValue) {
        return isDnrDay(getDay(pidValue));
    }

    /**
     * Gets the day part of a valid Pid.
     *
     * @param validPid - a valid fnr, dnr or bostnr
     * @return Day in birth date part of Pid
     */
    private static int getDay(String validPid) {
        return Integer.parseInt(validPid.substring(0, 2));
    }

    /**
     * Method that calculates and returns the birth date for
     * <code>this</code> pid.
     *
     * @return java.util.Date representing the birth date of person with this Pid
     */
    public LocalDate getFodselsdato() {
        // Adjust bnr or dnr (for fnr return value will be equal to pid)
        String adjustedFnr = makeDnrOrBostnrAdjustments(nummer);
        // Construct a date string with MMDDyyyy format

        String dateString = adjustedFnr.substring(0, 4) + get4DigitYearOfBirthWithAdjustedFnr(adjustedFnr, this.isDnummer());
        return LocalDate.parse(dateString, DateTimeFormat.forPattern("ddMMyyyy"));
    }

    public boolean isUnder1Year() {
        return getAlder() < 1;
    }

    public int getAlderIManeder() {
        return Months.monthsBetween(getFodselsdato(), new LocalDate()).getMonths();
    }

    /**
     * Calculates a person's age.
     *
     * @return age.
     */
    public int getAlder() {
        return Years.yearsBetween(getFodselsdato(), new LocalDate()).getYears();
    }

    /**
     * Returns a 4-digit birth date.
     *
     * @param dnrOrBnrAdjustedFnr a fnr, adjusted if it's a bnr or dnr
     * @param isDnummer boolean that says wether the dnrOrBnrAdjustedFnr is a Dnr
     * @return 4 digit birth date, 0 if invalid
     */
    private static int get4DigitYearOfBirthWithAdjustedFnr(String dnrOrBnrAdjustedFnr, boolean isDnummer) {
        int year = Integer.parseInt(dnrOrBnrAdjustedFnr.substring(4, 6));
        int individnr = Integer.parseInt(dnrOrBnrAdjustedFnr.substring(6, 9));
        // stilborn baby (dødfødt barn)
        if (!isDnummer && Integer.parseInt(dnrOrBnrAdjustedFnr.substring(6)) < 10) {
            return 0;
        } else {
			year = recalculateYear(year, individnr);

		}
        return year;
    }

	private static int recalculateYear(int year, int individnr) {
		int recalculatedYear = year;
		if (individnr < 500) {
			recalculatedYear += 1900;
		} else if ((individnr < 750) && (54 < year)) {
			recalculatedYear += 1800;
		} else if ((individnr < 1000) && (year < 40)) {
			recalculatedYear += 2000;
		} else if ((900 <= individnr) && (individnr < 1000) && (39 < year)) {
			recalculatedYear += 1900;
		} else {
			throw new IllegalArgumentException("Fødselsnummer og individnummer er konstruert feil");
		}
		return recalculatedYear;
	}

	/**
     * Gets the Month part of a valid Pid.
     *
     * @param validPid - a valid fnr, dnr or bostnr
     * @return Month in birth date part of Pid
     */
    private static int getMonth(String validPid) {
        return Integer.parseInt(validPid.substring(2, 4));
    }

    /**
     * Adjusts DNR and BostNr so that the first 6 numbers represents a valid date In the case wher DNR or BostNr is the
     * input, the return value will fail a modulus 11 check.
     *
     * @param value a personal identification number
     * @return the inparam if it wasn't a DNR or BostNr, otherwise the BostNr/DNR where the 6 first digits can be
     * converted to a valid date
     */
    private static String makeDnrOrBostnrAdjustments(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        // fnr format will be <DDMMAAXXXYY>
        int day = getDay(value);
        int month = getMonth(value);

        // DNR adjustment
        if (isDnrDay(day)) {
            day -= 40;
            StringBuilder fnr = new StringBuilder(value);

            if (day < 10) {
                fnr.replace(0, 2, "0" + day);
            } else {
                fnr.replace(0, 2, Integer.toString(day));
            }

            return fnr.toString();
        } else if (month > 20 && month <= 32) {
            // BOST adjustments
            month -= 20;
            StringBuilder fnr = new StringBuilder(value);

            if (month < 10) {
                fnr.replace(2, 4, "0" + month);
            } else {
                fnr.replace(2, 4, Integer.toString(month));
            }

            return fnr.toString();
        }

        // value was neither bostnr nor dnr
        return value;
    }

    /**
     * Gender based on FNR.
     *
     * @return true if female, false if male.
     */
    public boolean isFemale() {
        return Integer.parseInt(nummer.substring(8, 9)) % 2 == 0;
    }
}
