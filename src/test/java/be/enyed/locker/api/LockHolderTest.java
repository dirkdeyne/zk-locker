package be.enyed.locker.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;

public class LockHolderTest {
	
	private Object world = "Hello World!";
	private Object dirk ="Dirk";
	
	private Object chillworld = "Hello Chillworld!";
	private Object filip="Filip";
	
	private LockHolder lockholder = LockHolder.get();
	
	@Before
	public void unlockAll(){
		// just to make sure nothing is locked before we start a test
		lockholder.unLock(world, dirk);
		lockholder.unLock(world, filip);
		
		lockholder.unLock(chillworld, dirk);
		lockholder.unLock(chillworld, filip);
	}
	
	@Test
	public void world_should_be_lockable() throws Exception {
		lockholder.lock(world, dirk, Duration.ofSeconds(2));
		assertThat("locked object should not be empty", false, is(lockholder.getLockedObjects().isEmpty()));
	}
	
	@Test
	public void status_check() {
		Locked lockTheWorld = lockholder.lock(world, dirk, Duration.ofSeconds(2));
		assertThat("status lockTheWorld for dirk should be LOCK_OWNED", lockTheWorld.status(), equalTo(Lock.ON_LOCK_OWNED));
		assertThat("status lockTheWorld for filip should be LOCK", lockholder.lock(world, filip, Duration.ofSeconds(2)).status(), equalTo(Lock.ON_LOCK));
		
		Locked lockChillWorld = lockholder.lock(chillworld, filip, Duration.ofSeconds(2));
		assertThat("status lockChillWorld for filip should be LOCK_OWNED", lockChillWorld.status(), equalTo(Lock.ON_LOCK_OWNED));
		assertThat("status lockChillWorld for dirk should be LOCK", lockholder.lock(world, filip, Duration.ofSeconds(2)).status(), equalTo(Lock.ON_LOCK));
	}
	
	@Test
	public void unlock() {
		lockholder.lock(world, dirk, Duration.ofSeconds(2));
		assertThat("the world should be locked :)", true, is(lockholder.hasLock(world)));
	
		lockholder.unLock(world, filip);
		assertThat("the world should still be locked :)", true, is(lockholder.hasLock(world)));
	
		lockholder.unLock(world, dirk);
		assertThat("dirk should be able to unlock the world :)", false, is(lockholder.hasLock(world)));
	}
	
	public void pauseTestFor(long seconds) throws Exception {
		Thread.sleep(seconds * 1000);
	}

}
