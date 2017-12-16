package com.jmik.virtbox;

/**
 * Created by georgemik on 27.8.2017.
 */

public class Snapshot {
	private String name;
	private String uuid;
	private String description;
	private boolean isActive;


	public Snapshot() {
	}

	public Snapshot(String snapshotName, String uuid, String description) {
		init(snapshotName, uuid);
		setDescription(description);
	}

	public Snapshot(String snapshotName, String uuid) {
		init(snapshotName, uuid);
	}


//	public Snapshot(VirtualMachine vm, String snapshotName){
//		name = snapshotName;
//		System.out.println("Snapshot " +snapshotName +" instance created");
//		System.out.println("parent VM name " +vm.getName() +".");
//		return;
//	}

	public void init(String name, String uuid) {
		setName(name);
		setUuid(uuid);
	}

	public void init(String name, String uuid, boolean isActive) {
		setName(name);
		setUuid(uuid);
		setActiveState(isActive);
	}

	public String getName() {
		return name;
	}

	public String getUuid() {
		return uuid;
	}

	public String getDescription() {
		return description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setDescription(String desc) {
		description = desc;
	}

	private void setName(String name) {
		this.name = name;
	}

	private void setUuid(String uuid) {
		this.uuid = uuid;
	}

	private void setActiveState(boolean isActive) {
		this.isActive = isActive;
	}


}
