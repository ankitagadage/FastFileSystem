package jnachos.kern;
import java.util.*;

import jnachos.machine.Machine;



public class SwapSpace {
	int mId;
	String mszFileName;
	HashMap<Integer, Boolean> isSwapMap = new HashMap<Integer, Boolean>();
	
	public SwapSpace(int iId,int pageNumber) {

		mszFileName = new String("Swap_"+iId);
		mId = iId;

		JNachos.mFileSystem.create(mszFileName, pageNumber * Machine.PageSize);
	}
	int SwapIn(int page,int frame)
	{
		  assert(page >= 0 && page < isSwapMap.size());
		  assert(frame >= 0 && frame < Machine.NumPhysPages);
		 // JNachos.mFileSystem.open(mszFileName).readAt(Machine.mMainMemory, Machine.PageSize, position);


		
		
		return 0;
	}
	int SwapOut(int iFrameNum)
	{
		  assert(iFrameNum >= 0 && iFrameNum < Machine.NumPhysPages);
		  AddrSpace adresptr = JNachos.getCurrentProcess().getSpace();
		  int iPage = adresptr.getPageNumFromPageTable(iFrameNum);
		  if (iPage == -1){
			    return 1;
			  }
		  
		  if (adresptr.getTableEntry(iPage).dirty || !isSwapMap.get(iPage)){ 
				byte[] bytes = new byte[Machine.PageSize];
				System.arraycopy(Machine.mMainMemory, iFrameNum * Machine.PageSize,bytes ,0 ,
						Machine.PageSize);
				JNachos.mFileSystem.open(mszFileName).writeAt(bytes, Machine.PageSize, iPage*Machine.PageSize);
		  }
		  adresptr.getTableEntry(iPage).valid = false;
		  adresptr.getTableEntry(iPage).dirty = false;
		  adresptr.getTableEntry(iPage).physicalPage = -1;
		  isSwapMap.put(iPage, true);
		  //coremap

		  return 0;

	}
}

