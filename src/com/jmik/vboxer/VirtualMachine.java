package com.jmik.vboxer;

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

	public VirtualMachine(String vmName, String id) {
		name = vmName;
		this.id = id;
		snapshotz = setAllSnapshots();
		loadState();
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public ArrayList<String> getAllSnapshotNames() {

		ArrayList<String> snapshotNames = new ArrayList<>();
		if (snapshotz == null) {
			snapshotNames.add("");
			return snapshotNames;
		}
		snapshotz.forEach((s) -> snapshotNames.add(s.getName()));
		return snapshotNames;
	}

	public ArrayList<String> getAllSnapshotUuids() {

		ArrayList<String> snapshotUuids = new ArrayList<>();
		if (snapshotz == null) {
			snapshotUuids.add("");
			return snapshotUuids;
		}
		snapshotz.forEach((s) -> snapshotUuids.add(s.getUuid()));
		return snapshotUuids;
	}

	public Snapshot getSnapshotByUuid(String uuid) {
		for (Snapshot snapshot : snapshotz) {
			if (snapshot.getUuid().equals(uuid)) {
				return snapshot;
			}
		}
		return NullSnapshot.getInstance();
	}

	public String getVmState() {
		loadState();
		if (state == null || state.isEmpty()) {
			return "";
		}
		return state;
	}

	public boolean isVmStarted() {
		switch (this.getVmState()) {
			case ("poweroff"): {
				return false;
			}
			case ("running"): {
				return true;
			}
			default:
				return false;
		}
	}

	public void revertSnapshot(String snapshotName) {
		Snapshot snapshotToRevert = getSnapshotIfExists(snapshotName);
		if (snapshotToRevert.isNull()) {
			System.out.println("WARN: Snapshot '" + snapshotName + "' doesn't exist");
			return;
		}
		host.runSystemCommand(Arrays.asList(host.getVboxManagePath(), "snapshot", id, "restore", snapshotToRevert.getUuid()));
	}

	public void startVm(boolean headless) {
		String type = headless ? "headless" : "separate";
		if (isVmStarted()) {
			return;
		}
		host.runSystemCommand(Arrays.asList(host.getVboxManagePath(), "startvm", id, "--type", type));
	}

	public void poweroffVm() {
		host.runSystemCommand(Arrays.asList(host.getVboxManagePath(), "controlvm", id, "poweroff"));
	}

	public void acpiPoweroffVm() {
		host.runSystemCommand(Arrays.asList(host.getVboxManagePath(), "controlvm", id, "acpipowerbutton"));
	}

	public String getActiveSnapshotName() {

		if(!isAnySnapshotTaken()) {
			return NullSnapshot.getInstance().getName();
		}

		for (Snapshot s : snapshotz) {

			if (!s.isNull() && s.isActive()) {
				return s.getName();
			}
		}
		return NullSnapshot.getInstance().getName();
	}


	// =========================== private methods ===========================================================

	private boolean isAnySnapshotTaken() {
		return (snapshotz != null && snapshotz.size() == 1 && snapshotz.get(0).isNull());
	}

	private ArrayList<Snapshot> setAllSnapshots() {
		ArrayList<Snapshot> snaps = new ArrayList<>();
		StringBuffer snapshotListStdout = new StringBuffer();

		snapshotListStdout = host.runSystemCommand(Arrays.asList(host.getVboxManagePath(), "snapshot", id, "list"));
		if (snapshotListStdout == null) {
			snaps.add(NullSnapshot.getInstance());
			return snaps;
		}

		String[] stdoutLines = snapshotListStdout.toString().split("\\n");
		String nameAndUuidMatcher = ".*?Name:\\s+(.*) \\(UUID: ([\\w-]+)\\)(.*)";

		for (int i = 0; i < stdoutLines.length; i++) {
			String currentLine = stdoutLines[i];
			String name = null;
			String uuid = null;
			boolean active = false;
			String description = "";

			// If current line matches regular expression save name and uuid
			if (currentLine.matches(".*?Name:.*?\\(UUID: [\\w-]+\\).*")) {
				Matcher m = Pattern.compile(nameAndUuidMatcher).matcher(currentLine);
				if (m.find()) {
					// initialize snapshot object
					name = m.group(1);
					uuid = m.group(2);
					if (!m.group(3).isEmpty() && m.group(3).matches("\\s+\\*")) {
						active = true;
					}
				}
			}
			// if next line does not start with Name: but Description: pattern save whole description
			if (i != (stdoutLines.length) - 1 && stdoutLines[i + 1].matches(".*?Description:.*")) {
				i += 2; // move index to line after 'Description:' one
				int y = i;
				// until line does not start with 'Name:' pattern save content to String but check array boundary
				while (y != stdoutLines.length && !stdoutLines[y].matches(".*?Name:.*")) {
					description += stdoutLines[y] + "\n";
					y++;
				}
				i = y - 1; // sync main Array index with the previous loop
			}

			if (name != null && uuid != null) {
				Snapshot snapshot = new SnapshotImpl(name, uuid, active, description);
				snaps.add(snapshot);
			} else {
				snaps.add(NullSnapshot.getInstance());
			}
		}

		return snaps;
	}

	private StringBuffer getVmInfo() {
		StringBuffer infoStdout = new StringBuffer();

		infoStdout = host.runSystemCommand(Arrays.asList(host.getVboxManagePath(), "showvminfo", id, "--machinereadable"));

		if (infoStdout == null) {
			System.out.println("ERROR: System command '" + host.getVboxManagePath() + " showvminfo " + id + " --machinereadable' cannot be executed");
			System.exit(1);
		}
		return infoStdout;
	}

	private void setState() {
		if (vmInfo == null) {
			return;
		}
		//		TODO investigate why this property is not true from vboxmanage output????
		String stateRegex = "VMState=\"(\\w+)\"";
		Matcher match = Pattern.compile(stateRegex).matcher(vmInfo);
		if (match.find()) {
			state = match.group(1);
		}
	}

	private void loadState() {
		vmInfo = getVmInfo();
		setState();
	}

	private Snapshot getSnapshotIfExists(String snapshotName) {
		return snapshotz.stream().filter(s -> s.getName().equals(snapshotName)).findFirst().orElse(null);
	}


}

