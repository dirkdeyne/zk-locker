package be.enyed.locker.api;

public interface Locked {
	String status();
	Object owner();
}
