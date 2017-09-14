package be.enyed.locker.api;

public abstract class Lock {
	
	public final static String ON_LOCK = "onLock";
	public final static String ON_LOCK_OWNED = "onLockOwned";
	public final static String ON_UNLOCK = "onUnlock";
	
	public final static String LOCK_SCANNER_STYLE =
			".z-loading, .z-apply-loading, .z-apply-loading-indicator {" +
			"	display:none;" + 
	        " }" +
	        ".z-apply-mask{" +
	        "	background: #fcfcfc;" +
	        "   z-index: 10;"+
	        " }" +
	    	".z-notification{"+
	      	"  z-index: 100; " +
	      	" } ";
	
}
