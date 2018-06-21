package jnachos.filesystem;

import jnachos.machine.Disk;

/**
 * @author imcoolswap
 * */

public class Fragment {
	// number of used bytes in this fragment
	int usedBytes;
	
	// number of free bytes in this fragment
	int freeBytes;
	
	// if the current fragment is in use
	boolean isInUse;
	
	public Fragment() {
		usedBytes = 0;
		freeBytes = Disk.SectorSize / NachosFileSystem.FRAGMENTNUM;
		isInUse = false;
	}
	
}
