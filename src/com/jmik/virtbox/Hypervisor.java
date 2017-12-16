package com.jmik.virtbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by georgemik on 25.10.2017.
 */


public class Hypervisor {
	private HostHelper host;
	private String VBOXMANAGE;
	private ArrayList<VirtualMachine> virtalMachines = new ArrayList<>();
	HashMap<String, String> vmNamesAndIds = new HashMap<>();

	public Hypervisor() {
		host = new HostHelper();
		VBOXMANAGE = host.getVboxManagePath();
		vmNamesAndIds = this.getVmNamesAndUuids();
		System.out.println(Arrays.asList(vmNamesAndIds));
		initializeVms();

		// ================ testing code ==================
//		startVm("ubuntu", true);
		//	isVmOn("kali");
		//	poweroffVm("kali");
		//	acpiPoweroffVm("kali");
		isVmOn("ubuntu");
		revertSnapshot("ubuntu", "test1", true);

	}


	private void initializeVms() {
		vmNamesAndIds.forEach((k, v) -> virtalMachines.add(new VirtualMachine(v, k)));
/*		virtalMachines.forEach( (v) -> System.out.println(
				"VM name:"  +v.getName()
				+ "; id: " + v.getId()
				+ "snap: " + v.getAllSnapshotNames().forEach( (k) -> );
				+
		));*/
		for (VirtualMachine vm : virtalMachines) {
			System.out.println("-------------------------------------------------------------------------");
			System.out.println("VM name: " + vm.getName() + "; id: " + vm.getId() + "; state: " + vm.getVmState() + vm.isVmStarted());

			for (String uuid : vm.getAllSnapshotUuids()) {
				if (uuid.equals("")) {
					break;
				}
				Snapshot snap = vm.getSnapshotByUuid(uuid);
				System.out.println("	Snapshot Name: " + snap.getName());
				System.out.println("	Snapshot UUID: " + snap.getUuid());
				System.out.println("	Snapshot active: " + snap.isActive());
				System.out.println("	Snapshot Description:\n" + snap.getDescription());
			}
		}
		System.out.println("-------------------------------------------------------------------------");

	}


	private HashMap<String, String> getVmNamesAndUuids() {
		HashMap<String, String> virtMachinesList = new HashMap<>();
		StringBuffer stdout = null;
		try {
			stdout = host.runSystemCommand(Arrays.asList(VBOXMANAGE, "list", "vms"));
		} catch (Exception e) {
			System.out.println("ERROR: System command '" + VBOXMANAGE + " list vms' cannot be executed");
			e.printStackTrace();
			System.exit(1);
		}
		//  matcher to match output of the machines list (format: '"ubuntu" {0d37fa6d-f03f-47df-afc3-60a9098fd9c3}')
		String matcher = "\"([^\"]+)\"\\s+\\{([^}]+)\\}";

		Matcher m = Pattern.compile(matcher).matcher(stdout);
		while (m.find()) {
			virtMachinesList.put(m.group(2), m.group(1));
		}
		return virtMachinesList;
	}

	private VirtualMachine getVmByName(String vmName) {
		return virtalMachines.stream().filter(p -> p.getName().equals(vmName)).findFirst().get();
	}


	// ================ public methods ======================================================
	public void startVm(String vmName, boolean headless) {
		getVmByName(vmName).startVm(headless);
	}

	public void poweroffVm(String vmName) {
		getVmByName(vmName).poweroffVm();
	}

	public void acpiPoweroffVm(String vmName) {
		getVmByName(vmName).acpiPoweroffVm();
	}

	public void revertSnapshot(String vmName, String snapshotName, boolean powerOffFirst) {
		if (isVmOn(vmName)) {
			if (powerOffFirst) {
				poweroffVm(vmName);
			} else {
				System.out.println("WARN: Machine " + vmName + " is running, cannot revert snapshot");
				return;
			}
		}
		getVmByName(vmName).revertSnapshot(snapshotName);
	}




	// ================== helper temp methods ====================================================
	public boolean isVmOn(String vmName) {
		boolean on = getVmByName(vmName).isVmStarted();
		System.out.println("Is vm " + vmName + "started: " + on);
		return on;
	}


}
