package jnachos.kern;

public class JoinSysCallDetails {
private int mInvokingPid;
private int mWaitingOnPid;
public NachosProcess getObjJoinWaitingProcessPtr() {
	return objJoinWaitingProcessPtr;
}

public void setObjJoinWaitingProcessPtr(NachosProcess objJoinWaitingProcessPtr) {
	this.objJoinWaitingProcessPtr = objJoinWaitingProcessPtr;
}

private NachosProcess objJoinWaitingProcessPtr;

public JoinSysCallDetails(int mInvokingPid, int mWaitingOnPid, NachosProcess objJoinWaitingProcessPtr) {
	//super();
	this.setmInvokingPid(mInvokingPid);
	this.setmWaitingOnPid(mWaitingOnPid);
	this.objJoinWaitingProcessPtr = objJoinWaitingProcessPtr;
}

public int getmInvokingPid() {
	return mInvokingPid;
}

public void setmInvokingPid(int mInvokingPid) {
	this.mInvokingPid = mInvokingPid;
}

public int getmWaitingOnPid() {
	return mWaitingOnPid;
}

public void setmWaitingOnPid(int mWaitingOnPid) {
	this.mWaitingOnPid = mWaitingOnPid;
}

}
