package be.enyed.locker.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.A;

import be.enyed.locker.api.Lock;
import be.enyed.locker.api.Locked;

public class LockIndicatorComponent extends A {
	
	private static final long serialVersionUID = 8889991170860338290L;
	
	private static final Logger logger = LoggerFactory.getLogger(LockIndicatorComponent.class);

	private String iconSclass = "z-icon-lock";
	private String sclass = "lock-indicator";
	private String style = 
			"    .lock-indicator {" + 
	        "		cursor: auto;" +
	        "       text-decoration:none;"+
	        "    }";

	private LockHolderComponent lockholderComp;
	
	public LockIndicatorComponent(){
		setSclass(sclass);
		setIconSclass(iconSclass);
		setStyle(style);
	}	

	public void onLock(Event event) {
		Locked locked = (Locked) event.getData();
		logger.debug("onLock " + locked);
		setStyle("color:red");
		setTooltiptext("locked by " + locked.owner());
		setIconSclass("z-icon-lock");
		BindUtils.postGlobalCommand(null, null, Lock.ON_LOCK, null);
	}

	public void onUnLock(Event event) {
		logger.debug("onUnLock " + event);
		setStyle("color:grey");
		setTooltiptext("");
		setIconSclass("z-icon-unlock");
		BindUtils.postGlobalCommand(null, null, Lock.ON_UNLOCK, null);
	}

	public void onUnLockOwned(Event event) {
		Locked locked = (Locked) event.getData();
		logger.debug("onUnLockOwned " + locked);
		setStyle("color:green");
		setTooltiptext(locked.owner() +", you own the lock.");
		setIconSclass("z-icon-lock");
		BindUtils.postGlobalCommand(null, null, Lock.ON_LOCK_OWNED, null);
	}

	public void setLockholderComp(LockHolderComponent lockHolderComponent) {
		this.lockholderComp = lockHolderComponent;
	}
	
	public LockHolderComponent getLockholderComp() {
		return lockholderComp;
	}

}
