package com.jmik.virtbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by georgino on 25.10.2017.
 */


public class Hypervisor {
	private HostHelper host;
	private String VBOXMANAGE;// = host.getVboxManagePath();
	private ArrayList<VirtualMachine> virtalMachines = new ArrayList<>();
	HashMap<String, String> vmNamesAndIds = new HashMap<>();

	public Hypervisor(){
		host = new HostHelper();
		VBOXMANAGE = host.getVboxManagePath();
		vmNamesAndIds = this.getVirtualMachinesHash();
		System.out.println(Arrays.asList(vmNamesAndIds));
		initializeVms();
	}


	private void initializeVms() {
		vmNamesAndIds.forEach((k, v) -> virtalMachines.add(new VirtualMachine(v, k)));
/*		virtalMachines.forEach( (v) -> System.out.println(
				"VM name:"  +v.getName()
				+ "; id: " + v.getId()
				+ "snap: " + v.getAllSnapshotNames().forEach( (k) -> );
				+
		));*/

		for (VirtualMachine vm : virtalMachines){
			System.out.println( "-------------------------------------------------------------------------");
			System.out.println( "VM name: "  + vm.getName() + "; id: " + vm.getId());

//				System.out.println("snap ids: " + vm.getAllSnapshotUuids());
			for (String uuid: vm.getAllSnapshotUuids()) {
				if (uuid.equals("")) {
					break;
				}
				Snapshot snap = vm.getSnapshotByUuid(uuid);
				System.out.println("	Snapshot Name: " + snap.getName());
				System.out.println("	Snapshot UUID: " + snap.getUuid());
				System.out.println("	Snapshot Description:\n" + snap.getDescription());
			}
		}
		System.out.println( "-------------------------------------------------------------------------");

	}


	public HashMap<String, String> getVirtualMachinesHash() {
		HashMap<String, String> virtMachinesList = new HashMap<>();
		StringBuffer stdout = null;
		try {
			stdout = host.runSystemCommand(Arrays.asList(VBOXMANAGE, "list", "vms"));
		}catch(IllegalArgumentException e){
			System.out.println("ERROR: System command '" + VBOXMANAGE + " list vms' cannot be executed");
			e.printStackTrace();
			System.exit(1);
		}
		//  matcher to match output of the machines list (format: '"ubuntu" {0d37fa6d-f03f-47df-afc3-60a9098fd9c3}')
		String matcher = "\"([^\"]+)\"\\s+\\{([^}]+)\\}";

		Matcher m = Pattern.compile(matcher).matcher(stdout);
		while (m.find()){
			   virtMachinesList.put(m.group(2), m.group(1));
		}
		return virtMachinesList;
	}




}
