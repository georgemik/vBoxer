package com.jmik.virtbox;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by georgino on 27.8.2017.
 */
public class Test {




	public static void main(String[] args) {
		System.out.println("start test");





//		System.out.print(("dir"));

		Test cmd = new Test();
//		List<String> cmdWithArgs = Arrays.asList("c:\\Windows\\System32\\PING.EXE", "8.8.8.8");
//		List<String> cmdWithArgs = Arrays.asList("whoami");
//		System.out.print(cmd.runSystemCommand(cmdWithArgs));

		StringBuffer output = null;
		try {
			output = cmd.runCommand(Arrays.asList("c:\\Program Files\\Oracle\\VirtualBox\\vboxmanage.exe", "list", "vmNamesAndIds"));
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(output);



//		StringBuffer text = new StringBuffer("line11\n" + "line2\n" + "line3\n");
//		System.out.print(text);
//		String matcher = ".*(line\\d).*";

		String matcher = "\"([^\"]+)\"\\s+\\{([^}]+)\\}";


//		String pattern = "(\\w)(\\s+)([\\.,])";
		Matcher m = Pattern.compile(matcher).matcher(output);
		while (m.find()){
			for (int i = 1; i <= m.groupCount(); i++){
				System.out.printf("i: %d; match: %s\n", i, m.group(i));
			}
		}





	}

	public StringBuffer runCommand (List cmd) throws RuntimeException{

		StringBuffer output = new StringBuffer();
		Process p;
		try{
//			p = Runtime.getRuntime().exec(cmd);
//			p.waitFor();
			p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
			boolean exitVal =  p.waitFor(1, TimeUnit.SECONDS);

			if (!exitVal){
				System.out.println("ERROR '" + cmd + "' exit value: " + exitVal);
				throw new RuntimeException();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;
			while ((line = reader.readLine())!= null){
				output.append(line + "\n");
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return output;
	}

}
