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



	public String getVboxManagePath(){
		if(System.getProperty("os.name").startsWith("Windows")){
			vboxMng = WINDOWS_VBOXMANAGE_PATH;
		}
		if(System.getProperty("os.name").toLowerCase().startsWith("linux")){
			vboxMng = LINUX_VBOXMANAGE_PATH;
		}
		return vboxMng;
	}

	public String getOsPlatform(){
		return System.getProperty("os.name");
	}

	public StringBuffer runSystemCommand(List cmd) throws IllegalArgumentException{
		StringBuffer output = new StringBuffer();
		Process p;
		try{
			p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
			boolean exitVal =  p.waitFor(1, TimeUnit.SECONDS);

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;
			while ((line = reader.readLine())!= null){
				output.append(line + "\n");
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		if (output.toString().contains("Syntax error: Unknown ")){
			throw new IllegalArgumentException("ERROR running command '" + cmd + "'");
		}

		return output;

	}

}
