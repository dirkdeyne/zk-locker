
package be.enyed.locker.component;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.Div;
import org.zkoss.zul.Style;
import org.zkoss.zul.Timer;

import be.enyed.locker.api.Lock;
import be.enyed.locker.api.LockHolder;
import be.enyed.locker.api.Locked;

public class LockHolderComponent extends Div implements AfterCompose, BeforeCompose {
	
	private static final long serialVersionUID = 2280817416801938663L;
	
	private static final Logger logger = LoggerFactory.getLogger(LockHolderComponent.class);

	private Set<Component> lockComponents = new HashSet<>();
	private int duration = 120;
	private boolean extended = true;
	private Duration lockDuration = Duration.ofSeconds(duration);
	
	private Object owner = UUID.randomUUID();
	private Object lockOn;
	private LockHolder lockholder = LockHolder.get();
	private boolean warningMassageShown = false;
	private int tick = 0;
	private Locked current;

	@Override
	public void beforeCompose() {
		addStyle();
	}

	@Override
	public void afterCompose() {
		addEventListener(Events.ON_CREATE, (event) -> registerLockComponents());
		addEventListener(LockTriggerComponent.ON_LOCK_TRIGGER, (event) -> lockTriggered(event));
		lockComponents.add(this);
	}

	private void init(Component comp) {
		lockComponents.add(comp);
		Locked init = lockholder.status(lockOn, owner);
		logger.debug("init: " + init);
		Events.postEvent(init.status(), comp, init);
	}

	private void addStyle() {
		Style style = new Style();
		style.setContent(Lock.LOCK_SCANNER_STYLE);
		getChildren().add(0, style);
	}
	
	private void lockTriggered(Event evnt) {
		Locked locked = lockholder.lock(lockOn, owner, lockDuration);
		postLockedEvent(locked);
	}

	public void registerLockComponents() {
		getChildren()
			.stream()
			.peek( comp -> logger.debug(comp.getClass().getSimpleName()  +  " => " + comp.getId()))
			.forEach(comp -> register(comp));
	}

	private void register(Component comp) {
		if(comp instanceof LockTriggerComponent) {
			LockTriggerComponent ltc = (LockTriggerComponent) comp;
			ltc.addEventListener(Lock.ON_LOCK, (event) -> ltc.onLock(event));
			ltc.addEventListener(Lock.ON_UNLOCK, (event) -> ltc.onUnlock(event));
			ltc.setLockholderComp(this);
			init(ltc);
		} else if(comp instanceof LockIndicatorComponent) {
			LockIndicatorComponent lic = (LockIndicatorComponent) comp;
			lic.addEventListener(Lock.ON_LOCK, (event) -> lic.onLock(event));
			lic.addEventListener(Lock.ON_UNLOCK, (event) -> lic.onUnLock(event));
			lic.addEventListener(Lock.ON_LOCK_OWNED, (event) -> lic.onUnLockOwned(event));
			lic.setLockholderComp(this);
			init(lic);
		} else if (!comp.getChildren().isEmpty()){
			comp.getChildren().forEach(child -> register(child));
		}
	}

	private void reInit() {
		Locked lockStatus = lockholder.status(lockOn, owner);
		if(lockStatus.status().equals(Lock.ON_LOCK_OWNED) || lockStatus.status().equals(Lock.ON_UNLOCK)){
			unlock();
		} else {
			lock();
		}
	}
	
	private void unlock() {
		postLockedEvent(lockholder.unLock(lockOn, owner));
	}

	private void postLockedEvent(Locked locked) {
		logger.debug("locked: " + locked);
		if(!locked.equals(current)){
			current = locked;
			lockComponents.forEach((comp) -> Events.postEvent(current.status(), comp, locked));
		} 
	}

	private void lock() {
		postLockedEvent(lockholder.lock(lockOn, owner, lockDuration));	
	}
	
	public void setTick(int tick) {
		this.tick = tick;
		if(tick !=0) {
			Component timer = timer();
			getChildren().add(0,timer);
			timer.addEventListener(Events.ON_TIMER, (event) -> onTimer());
		}
	}
	
	private void onTimer() {
		Locked locked = lockholder.status(lockOn, owner);
		postLockedEvent(locked);
	}

	private Component timer() {
		Timer timer = new Timer(tick);
		timer.setRepeats(true);
		timer.start();
		return timer;
	}

	public int getTick() {
		return tick;
	}
	
	public void setOwner(Object owner) {
		this.owner = owner;
		reInit();
	}
	
	public void setLockOn(Object lockOn) {
		this.lockOn = lockOn;
		reInit();
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
		lockDuration = Duration.ofSeconds(duration);
		reInit();
	}
	
	public Object getLockOn() {
		return lockOn;
	}

	public Object getOwner() {
		return owner;
	}

	public int getDuration() {
		return duration;
	}
	
	public void setExtended(boolean extended) {
		this.extended = extended;
	}
	
	public boolean isExtended() {
		return extended;
	}

	public boolean isWarningMassageShown() {
		return warningMassageShown;
	}

	public void setWarningMassageShown(boolean warningMassageShown) {
		this.warningMassageShown = warningMassageShown;
	}
}
