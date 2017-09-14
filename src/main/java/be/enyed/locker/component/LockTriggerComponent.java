package be.enyed.locker.component;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;

import be.enyed.locker.api.Lock;
import be.enyed.locker.api.Locked;


public class LockTriggerComponent extends Div implements AfterCompose, EventListener<Event> {

	private static final long serialVersionUID = 2481658873345676610L;
	private static final Logger log = LoggerFactory.getLogger(LockTriggerComponent.class);

	public static final String ON_LOCK_TRIGGER = "onLockTrigger";
	
	private LockHolderComponent lockholderComp;
	
	@Override
	public void afterCompose() {
		addEventListener(Events.ON_CREATE, (event) -> registerListenners(this));
		//addEventListener(Events.ON_VISIBILITY_CHANGE, (event) -> registerListenners(this));
	}
	
	public void registerListenners(Component comp){
		log.debug("registerListenners: " + comp.getClass().getSimpleName());
		if(comp instanceof Checkbox) {
			comp.addEventListener(Events.ON_CHECK, this);
		} else if(comp instanceof Textbox) {
			comp.addEventListener(Events.ON_CHANGE, this);
 		} else if(comp instanceof Intbox) {
			comp.addEventListener(Events.ON_CHANGE, this);
 		} else if(comp instanceof Doublebox) {
			comp.addEventListener(Events.ON_CHANGE, this);
 		} else if(comp instanceof Longbox) {
			comp.addEventListener(Events.ON_CHANGE, this);
 		} else if(comp instanceof Datebox) {
			comp.addEventListener(Events.ON_SELECTION, this);
		} else if(comp instanceof Button) {
			comp.addEventListener(Events.ON_CLICK, this);
		} else if(comp instanceof Listbox) {
			comp.addEventListener(Events.ON_SELECT, this);
		}
		else if(!comp.getChildren().isEmpty()){
			comp.getChildren().forEach(c -> registerListenners(c));
		}
	}
	
	public void setLockholderComp(LockHolderComponent lockholderComp) {
		this.lockholderComp = lockholderComp;
	}
	
	public LockHolderComponent getLockholderComp() {
		return lockholderComp;
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		log.debug("Lock triggered on "+lockholderComp);
		if(null != lockholderComp) {
			Events.sendEvent(ON_LOCK_TRIGGER, lockholderComp, null);
		} else {
			log.error("Where is my lockholderComp");
		}
	}
	
	public void onLock(Event event) {
		Locked locked = (Locked) event.getData();
		log.debug("LOCKED " + lockholderComp + " ==> " + locked.status() + ", " + locked.owner());
		
		if(Lock.ON_LOCK.equals(locked.status())) {
			if(! lockholderComp.isWarningMassageShown()) {
				Clients.showNotification("LOCKED by " + locked.owner(),"warning", null ,"middle_center",15000,true);
				lockholderComp.setWarningMassageShown(true);
			}
			Clients.showBusy(this,"locked");
		}
	}
	
	public void onUnlock(Event event) {
		log.debug("UNLOCKED "  + lockholderComp.getOwner());
		Clients.clearBusy(this);
	}
	
	@Override
	public String toString() {
		return getId() + "::" + (null == lockholderComp ? "NO PARENT" :  lockholderComp.getOwner());
	}


}
