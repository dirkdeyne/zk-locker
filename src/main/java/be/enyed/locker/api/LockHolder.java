package be.enyed.locker.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockHolder {
	
	private static final Logger logger = LoggerFactory.getLogger(LockHolder.class);

	private final class UnLockedImpl implements Locked {
		@Override
		public String status() {
			return Lock.ON_UNLOCK;
		}
		
		@Override
		public String toString() {
			return status();
		}

		@Override
		public Object owner() {
			return "";
		}
		
		@Override
		public boolean equals(Object obj) {
			if(null == obj) {
				return false;
			}
			return toString().equals(obj.toString());
		}
		
		@Override
		public int hashCode() {
			return toString().hashCode();
		}
		
	}
	
	private final class LockedImpl implements Locked {
		
		private final Object owner;
		private final String status;
		
		private LockedImpl(LockPair pair, Object owner) {
			if(pair.owner.equals(owner)) {
				status = Lock.ON_LOCK_OWNED;
				this.owner = owner;
			} else {
				this.owner = pair.owner;
				status = Lock.ON_LOCK;
			}
		}

		@Override
		public String status() {
			return status;
		}
		
		@Override
		public String toString() {
			return status() + ", " + owner();
		}

		@Override
		public Object owner() {
			return owner;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(null == obj) {
				return false;
			}
			return toString().equals(obj.toString());
		}
		
		@Override
		public int hashCode() {
			return toString().hashCode();
		}
	}

	private static class LockPair {
		
		private Object lockOn;
		private Object owner;
		
		public LockPair(Object lockOn, Object owner) {
			this.lockOn = lockOn;
			this.owner = owner;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((lockOn == null) ? 0 : lockOn.hashCode());
			result = prime * result + ((owner == null) ? 0 : owner.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			LockPair other = (LockPair) obj;
			if (lockOn == null) {
				if (other.lockOn != null) {
					return false;
				}
			} else if (!lockOn.equals(other.lockOn)) {
				return false;
			} 
			if (owner == null) {
				if (other.owner != null) {
					return false;
				}
			} else if (!owner.equals(other.owner)) {
				return false;
			}
			return true;
		}
		
	}
	
	private static boolean running = true;
	private static final LockHolder INSTANCE = new LockHolder();
	private static ConcurrentHashMap<LockPair, LocalDateTime> lockedObjects  = new ConcurrentHashMap<>();

	private LockHolder() {
		new Thread(() ->  {
			try {
				while(running) {
					Thread.sleep(1000);
					autoUnlock();
				}
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}).start();
	}
	
	private void autoUnlock() {
		lockedObjects.entrySet().stream()
			.filter(entry -> entry.getValue().isBefore(LocalDateTime.now()))
			.forEach(entry -> autoRemove(entry.getKey()));
	}
	
	private void autoRemove(LockPair key) {
		lockedObjects.remove(key);
		logger.debug("auto unlocked : " + key.lockOn);
	}
	
	public Locked status(Object lockon, Object owner) {
		Locked locked = new UnLockedImpl();
		if(hasLock(lockon)) {
			locked = lock(lockon, owner);
		}
		return locked;
	}
	
	public static LockHolder get() {
		return INSTANCE;
	}
	
	public synchronized Locked lock(Object lockOn, Object owner){
		return lock(lockOn, owner, Duration.ofSeconds(0), false);
	}
	
	public synchronized Locked lock(Object lockOn, Object owner, Duration unlockAfter){
		return lock(lockOn, owner, unlockAfter, true);
	}	
	
	public synchronized Locked lock(Object lockOn, Object owner, Duration unlockAfter, boolean extendDuration) {
		logger.debug(String.format("put lock on %s, owned by %s, for %s of %s", lockOn, owner, extendDuration?"extended duration":"duration", unlockAfter));
		Locked locked = null;
		Optional<LockPair> find = INSTANCE.find(lockOn);
		if(find.isPresent()) {
			LockPair pair = find.get();
			locked = INSTANCE.new LockedImpl(pair, owner);
			if(extendDuration && pair.owner.equals(owner)) {
				lockedObjects.put(pair, LocalDateTime.now().plus(unlockAfter));
			}
		} else {
			locked = INSTANCE.setLock(lockOn, owner, unlockAfter);
		}
		logger.debug(String.format("lock on %s, owned by %s, resulted in '%s'.", lockOn, owner,locked));
		return locked;
	}
	
	public synchronized Locked unLock(Object lockOn, Object owner) {
		logger.debug(String.format("unlock %s, requested by %s",lockOn, owner));
		return INSTANCE.clearLock(lockOn, owner);
	}
	
	public Set<Object> getLockedObjects() {
		return lockedObjects.keySet().stream()
					.map(lo -> lo.lockOn)
					.collect(Collectors.toSet());
	}
	
	public boolean hasLock(Object lockon) {
		return getLockedObjects().contains(lockon);
	}
	
	private Locked setLock(Object lockOn, Object owner, Duration unlockAfter) {
		LockPair pair = new LockPair(lockOn, owner);
		lockedObjects.put(pair, LocalDateTime.now().plus(unlockAfter));
		return new LockedImpl(pair, owner);
	}
	
	private Locked clearLock(Object lockOn, Object owner) {
		Locked locked= null;
		Optional<LockPair> pair = find(lockOn);
		if(!pair.isPresent()) {
			locked = new UnLockedImpl();
		} else {
			LockPair lockPair = find(lockOn).get();
			if(lockPair.owner.equals(owner)) {
				locked = new UnLockedImpl();
				lockedObjects.remove(lockPair);
			} else {
				locked = new LockedImpl(lockPair, owner);
			}
		}
		logger.debug(String.format("unlock on %s by %s, resulted in '%s'.", lockOn, owner,locked));
		return locked;
	}
	
	private Optional<LockPair> find(Object lockOn) {
		return lockedObjects.keySet().stream()
					.filter(locked -> locked.lockOn.equals(lockOn))
					.findFirst();
	}
	
}
