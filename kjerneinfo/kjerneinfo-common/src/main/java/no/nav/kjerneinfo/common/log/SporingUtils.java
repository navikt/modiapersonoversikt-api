package no.nav.kjerneinfo.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class SporingUtils {
	private static Logger logger = LoggerFactory.getLogger(SporingUtils.class);

	public static BufferedReader configFileAsBufferedReader(InputStream inputStream, String filepath)  {
		BufferedReader br = null;
		try {
			InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
			br = new BufferedReader(isr);
		} catch (UnsupportedEncodingException e) {
			logger.warn("Feil i oppsett av sporingslogg" + filepath, e);
		}
		return br;
	}
}
