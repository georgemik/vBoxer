package com.jmik.virtbox;

/**
 * Created by georgemik on 27.8.2017.
 */

public class SnapshotImpl implements Snapshot {
	private String name;
	private String uuid;
	private String description;
	private boolean isActive;


	public SnapshotImpl(String snapshotName, String uuid, boolean isActive, String description) {
		init(snapshotName, uuid, isActive);
		setDescription(description);
	}

	public void init(String name, String uuid) {
		setName(name);
		setUuid(uuid);
	}

	public void init(String name, String uuid, boolean isActive) {
		setName(name);
		setUuid(uuid);
		setActiveState(isActive);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public boolean isNull() {
		return false;
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


	// private methods
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
