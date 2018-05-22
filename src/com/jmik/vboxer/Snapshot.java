package com.jmik.vboxer;

/**
 * Created by gerogemik on 22.5.2018.
 */
public interface Snapshot {

	String getName();

	String getUuid();

	boolean isNull();

	String getDescription();

	boolean isActive();

}
