package com.jmik.virtbox;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VirtualMachine {

	private HostHelper host = new HostHelper();
	private String name;
	private String id;
	private ArrayList<Snapshot> snapshotz;
	private String state;
	private StringBuffer vmInfo;

	public VirtualMachine(String vmName, String id){
		name = vmName;
		this.id = id;
		snapshotz = setAllSnapshots();
		vmInfo = getVmInfo();
		setState();
	}



	public String getName(){
		return name;
	}
	public String getId(){
		return id;
	}

	private ArrayList<Snapshot> setAllSnapshots(){
		ArrayList<Snapshot> snaps = new ArrayList<>();
		StringBuffer snapshotListStdout = new StringBuffer();

		try{
			snapshotListStdout = host.runSystemCommand(Arrays.asList(host.getVboxManagePath(), "snapshot", id, "list"));
		}catch (IllegalFormatException e){
			System.out.println("ERROR: System command '" + host.getVboxManagePath() + " snapshot " + id + " list' cannot be executed");
			e.printStackTrace();
			System.exit(1);
		}
		if(snapshotListStdout.toString().startsWith("This machine does not have any snapshots")){
			return null;
		}

		String[] stdoutLines = snapshotListStdout.toString().split("\\n");
		String nameAndUuidMatcher = ".*?Name:\\s+(.*) \\(UUID: ([\\w-]+)\\)";

		for(int i=0; i < stdoutLines.length ; i++){
			String currentLine = stdoutLines[i];
			String name;
			String uuid;
			String description = "";
			Snapshot snapshot = new Snapshot();

			// If current line matches regular expression save name and uuid
			if(currentLine.matches(".*?Name:.*?\\(UUID: [\\w-]+\\).*")){
				Matcher m = Pattern.compile(nameAndUuidMatcher).matcher(currentLine);
				if(m.find()){
					// initialize snapshot object
					name = m.group(1);
					uuid = m.group(2);
					snapshot.init(name, uuid);
				}
			}
			// if next line does not start with Name: but Description: pattern save whole description
			if (i != (stdoutLines.length) - 1 && stdoutLines[i+1].matches(".*?Description:.*")){
				i+=2; // move index to line after 'Description:' one
				int y = i;
				// until line does not start with 'Name:' pattern save content to String but check array boundary
				while(y != stdoutLines.length && !stdoutLines[y].matches(".*?Name:.*")){
					description += stdoutLines[y] + "\n";
					y++;
				}
				snapshot.setDescription(description);
				i = y; // sync main Array index with the previous loop
			}

			if(snapshot.getName() != null){
				snaps.add(snapshot);
			}
		}

		return snaps;
	}

	public ArrayList<String> getAllSnapshotNames(){

		ArrayList<String> snapshotNames= new ArrayList<>();
		if(snapshotz == null){
			snapshotNames.add("");
			return snapshotNames;
		}
		snapshotz.forEach((s) -> snapshotNames.add(s.getName()));
		return snapshotNames;
	}

	public ArrayList<String> getAllSnapshotUuids(){

		ArrayList<String> snapshotUuids= new ArrayList<>();
		if(snapshotz == null){
			snapshotUuids.add("");
			return snapshotUuids;
		}
		snapshotz.forEach((s) -> snapshotUuids.add(s.getUuid()));
		return snapshotUuids;
	}

	public Snapshot getSnapshotByUuid(String uuid){
		for(Snapshot snapshot: snapshotz){
			if (snapshot.getUuid().equals(uuid)){
				return snapshot;
			}
		}
		return null;
	}

	private StringBuffer getVmInfo(){
		StringBuffer infoStdout = new StringBuffer();

		try{
			infoStdout = host.runSystemCommand(Arrays.asList(host.getVboxManagePath(), "showvminfo", id, "--machinereadable"));
		}catch (IllegalFormatException e){
			System.out.println("ERROR: System command '" + host.getVboxManagePath() + " showvminfo " + id + " --machinereadable' cannot be executed");
			e.printStackTrace();
			System.exit(1);
		}
		return infoStdout;
	}

	private void setState(){
		if(vmInfo == null){
			return;
		}
//		TODO investigate why this property is not true from vboxmanage output????
		String stateRegex = "VMState=\"(\\w+)\"";
		Matcher match = Pattern.compile(stateRegex).matcher(vmInfo);
		if(match.find()){
			state = match.group(1);
		}
	}

	public String getVmState(){
		if (state == null || state.isEmpty()){
			return "";
		}
		return state;
	}

	public boolean isVmStarted(){
		switch (state){
			case ("poweroff"): { 	return false; 	}
			case ("poweron") : {	return true;	}
			default: return false;
		}
	}


}


















//	private Map<String, Snapshot> snapshots = new HashMap<String, Snapshot>(25);
//	public VirtualMachine(String vmName){
//		name = vmName;
//		System.out.println(vmName + "Created");
//
////		parties.put("snap1", new Snapshot("s1")); // all anonymously added
////		parties.put("snap2", new Snapshot("s12"));
////		parties.put("snap2", new Snapshot("s13"));
////		parties.put("snap5", new Snapshot(this, "s13"));
////		parties.put("snap5", new Snapshot(this, "s14"));
////		parties.put("snap5", new Snapshot(this, "s16"));
//
//	}