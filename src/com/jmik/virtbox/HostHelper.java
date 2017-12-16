package com.jmik.virtbox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by georgemik on 25.10.2017.
 */
public class HostHelper {

	private static String vboxMng;
	private static String WINDOWS_VBOXMANAGE_PATH = "c:\\Program Files\\Oracle\\VirtualBox\\vboxmanage.exe";
	private static String LINUX_VBOXMANAGE_PATH = "linuxPathToBeAdded";


	public String getVboxManagePath() {
		if (getOsPlatform().startsWith("Windows")) {
			vboxMng = WINDOWS_VBOXMANAGE_PATH;
		}
		if (getOsPlatform().toLowerCase().startsWith("linux")) {
			vboxMng = LINUX_VBOXMANAGE_PATH;
		}
		return vboxMng;
	}

	public String getOsPlatform() {
		return System.getProperty("os.name");
	}

	public StringBuffer runSystemCommand(List cmd) throws IllegalStateException {
		StringBuffer output = new StringBuffer();
		Process p;
		int exitVal = 42;
		try {
			p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
			boolean waitFor = p.waitFor(1, TimeUnit.SECONDS);

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			exitVal = p.exitValue();

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (exitVal > 0) {
			return null;
		}

		if (output.toString().contains("Syntax error: Unknown ") || output.toString().contains("Syntax error: unknown ") || output.toString().contains("Syntax error: Invalid ")) {
			throw new IllegalStateException("ERROR running command '" + cmd + "'");
		}

		return output;

	}

}
