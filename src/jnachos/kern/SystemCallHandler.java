/**
 * Copyright (c) 1992-1993 The Regents of the University of California.
 * All rights reserved.  See copyright.h for copyright notice and limitation 
 * of liability and disclaimer of warranty provisions.
 *  
 *  Created by Patrick McSweeney on 12/5/08.
 */
package jnachos.kern;

import java.util.Arrays;

import jnachos.machine.*;

/** The class handles System calls made from user programs. */
public class SystemCallHandler {
	/** The System call index for halting. */
	public static final int SC_Halt = 0;

	/** The System call index for exiting a program. */
	public static final int SC_Exit = 1;

	/** The System call index for executing program. */
	public static final int SC_Exec = 2;

	/** The System call index for joining with a process. */
	public static final int SC_Join = 3;

	/** The System call index for creating a file. */
	public static final int SC_Create = 4;

	/** The System call index for opening a file. */
	public static final int SC_Open = 5;

	/** The System call index for reading a file. */
	public static final int SC_Read = 6;

	/** The System call index for writting a file. */
	public static final int SC_Write = 7;

	/** The System call index for closing a file. */
	public static final int SC_Close = 8;

	/** The System call index for forking a forking a new process. */
	public static final int SC_Fork = 9;

	/** The System call index for yielding a program. */
	public static final int SC_Yield = 10;

	/**
	 * Entry point into the Nachos kernel. Called when a user program is
	 * executing, and either does a syscall, or generates an addressing or
	 * arithmetic exception.
	 * 
	 * For system calls, the following is the calling convention:
	 * 
	 * system call code -- r2 arg1 -- r4 arg2 -- r5 arg3 -- r6 arg4 -- r7
	 * 
	 * The result of the system call, if any, must be put back into r2.
	 * 
	 * And don't forget to increment the pc before returning. (Or else you'll
	 * loop making the same system call forever!
	 * 
	 * @pWhich is the kind of exception. The list of possible exceptions are in
	 *         Machine.java
	 **/
	public static void handleSystemCall(int pWhichSysCall) {

		System.out.println("SysCall:" + pWhichSysCall + "  PrcocessId::" +JNachos.getCurrentProcess().getmProcessId());

		switch (pWhichSysCall) {
		// If halt is received shut down
		case SC_Halt:
			Debug.print('a', "Shutdown, initiated by user program.");
			int arg = Machine.readRegister(4);
			Interrupt.halt();
			
			break;

		case SC_Exit:
			// Read in any arguments from the 4th register
			System.out.println("In exit funtion*********");
			boolean oldInterruptExit = Interrupt.setLevel(false);
			arg = Machine.readRegister(4);
			System.out.println("In exit funtion*********"+arg);

			if(JNachos.getmJoinData().size()>0)
			{
				for(int i =0;i<JNachos.getmJoinData().size();i++)
				{
					if(JNachos.getCurrentProcess().getmProcessId()   == JNachos.getmJoinData().get(i).getmWaitingOnPid())
					{
						if(JNachos.getmJoinData().get(i).getObjJoinWaitingProcessPtr() != null)
						{	
							JNachos.getmJoinData().get(i).getObjJoinWaitingProcessPtr().saveUserState(2, arg);
						}
						else
						{
							System.out.println("Is NULL");
						}
						Scheduler.readyToRun((NachosProcess)JNachos.getmJoinData().get(i).getObjJoinWaitingProcessPtr());
						JNachos.getmJoinData().remove(i);
					
					}
				}
			}

			// Finish the invoking process
			Interrupt.setLevel(oldInterruptExit);
			//System.out.println("Final list::"+JNachos.fifoListList.toString());


			for(int i = 0;i < JNachos.getCurrentProcess().getSpace().getmNumPages();i++)
			{
				//System.out.println("Virtual address::"+JNachos.getCurrentProcess().getSpace().getmPageTable()[i].physicalPage +"JNachos.getCurrentProcess().getSpace().getPageNumFromVirtualMemory(i)::"+JNachos.getCurrentProcess().getSpace().getPageNumFromVirtualMemory(i));

				for(int j=0;j<JNachos.fifoListList.size();j++)
				{
					if(JNachos.getCurrentProcess().getSpace().getPageNumFromVirtualMemory(i)==JNachos.fifoListList.get(j))
					{
						
						//System.out.println("JNachos.fifoListList.get(j):"+JNachos.fifoListList.get(j));
						JNachos.fifoListList.remove(j);
						Arrays.fill(Machine.mMainMemory, JNachos.getCurrentProcess().getSpace().getmPageTable()[i].physicalPage * Machine.PageSize,
								(JNachos.getCurrentProcess().getSpace().getmPageTable()[i].physicalPage + 1) * Machine.PageSize, (byte) 0);
						AddrSpace.mFreeMap.clear(JNachos.getCurrentProcess().getSpace().getmPageTable()[i].physicalPage);
						byte b[] = new byte[Machine.PageSize];
						Arrays.fill(b,0,Machine.PageSize,(byte)0);
						JNachos.getmSwapFilePtr().writeAt(b, Machine.PageSize, JNachos.getCurrentProcess().getSpace().getmPageTable()[i].SwapSpacePagelocation*Machine.PageSize);

					}
				}
				
				
			}
			System.out.println("Final list::"+JNachos.fifoListList.toString());

			
		JNachos.getCurrentProcess().finish();
			break;
		case SC_Exec:
		
			boolean oldInterrupt = Interrupt.setLevel(false);
			int iExecutablePathPtr = Machine.readRegister(4);
			String str = new String();
			int val =1;
			while((char)val!='\0')
			{
				val = Machine.readMem(iExecutablePathPtr, 1);
				if((char)val!='\0')
					str += (char)val;
				iExecutablePathPtr++;
			}
			System.out.println("In Exec system call1");
			System.out.println("Exdec name::"+str);
			JNachos.getCurrentProcess().Exec(str);
			Interrupt.setLevel(oldInterrupt);
			
			break;
		case SC_Join:
			System.out.println("In join system Call");
			boolean oldInterruptJoin = Interrupt.setLevel(false);
			int iProcessId = Machine.readRegister(4);
			Machine.writeRegister(Machine.PCReg, Machine.readRegister(Machine.NextPCReg));
			Machine.writeRegister(Machine.NextPCReg, Machine.readRegister(Machine.PCReg)+4);
		
			if( JNachos.getCurrentProcess().getmProcessId() != iProcessId && iProcessId !=0) 
			{
				if( true == JNachos.getProcessIdList().contains(iProcessId) )
				{
					JoinSysCallDetails ptrJoinDetails = new JoinSysCallDetails(JNachos.getCurrentProcess().getmProcessId(), iProcessId, JNachos.getCurrentProcess());
					JNachos.getmJoinData().add(ptrJoinDetails);
					
					JNachos.getCurrentProcess().sleep();
					Interrupt.setLevel(oldInterruptJoin);
					
					
				}
			}
			break;
		case SC_Fork:
			System.out.println("In fork System Call");
			boolean oldInterruptFork = Interrupt.setLevel(false);
			NachosProcess objNachosChildProcess = new NachosProcess("Child");
			objNachosChildProcess.setSpace(new AddrSpace(JNachos.getCurrentProcess().getSpace()));
			JNachos.objNachosProcess.put(objNachosChildProcess.getmProcessId(), objNachosChildProcess);

			
			Machine.writeRegister(Machine.PCReg, Machine.readRegister(Machine.NextPCReg));
			Machine.writeRegister(Machine.NextPCReg, Machine.readRegister(Machine.PCReg)+4);
		
			
			objNachosChildProcess.saveUserState();
			objNachosChildProcess.getUserRegisters()[2] = 0;
			
			
			Machine.writeRegister(2,objNachosChildProcess.getmProcessId());
			Interrupt.setLevel(oldInterruptFork);
			objNachosChildProcess.fork(new StartFork() ,objNachosChildProcess);
			
			
		break;
		
		default:
			Interrupt.halt();
			break;
		}
	}
}
