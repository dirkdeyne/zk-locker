package be.enyed.locker.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;

public class LockHolderListenerIntegrationTest {
	
	private Object world = "Hello World!";
	private Object dirk ="Dirk";
	private Object filip="Filip";
	
	private LockHolder lockholder = LockHolder.get();
	
	
	@Before
	public void unlockAll(){
		// just to make sure nothing is locked before we start a test
		lockholder.unLock(world, dirk);
		lockholder.unLock(world, filip);
	}
	
	
	@Test
	public void world_should_be_unlocked_automatically() throws Exception {
		//lockholder.register(this);
		lockholder.lock(world, dirk, Duration.ofSeconds(2));
		assertThat("the world should be locked :)", true, is(lockholder.hasLock(world)));
		
		pauseTestFor(4);
		
		
		assertThat("the world should no longer be locked :)", false, is(lockholder.hasLock(world)));
		assertThat("status for filip should be LOCK when he locks the world", lockholder.lock(world, filip, Duration.ofSeconds(2)).status(), equalTo(Lock.ON_LOCK_OWNED));
	}
	
	public void pauseTestFor(long seconds) throws Exception {
		Thread.sleep(seconds * 1000);
	}

}
