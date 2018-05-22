package com.jmik.virtbox;

/**
 * Created by gerogemik on 22.5.2018.
 */
public class NullSnapshot implements Snapshot {

	private static NullSnapshot instance = new NullSnapshot();

	private NullSnapshot() {}

	public static NullSnapshot getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "No snapshot";
	}

	@Override
	public String getUuid() {
		return "No snapshot";
	}

	@Override
	public String getDescription() {
		return "No snapshot";
	}

	@Override
	public boolean isNull() {
		return true;
	}

	@Override
	public boolean isActive() {
		return false;
	}

}
