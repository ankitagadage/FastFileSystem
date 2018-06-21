/**
 * Copyright (c) 1992-1993 The Regents of the University of California.
 * All rights reserved.  See copyright.h for copyright notice and limitation 
 * of liability and disclaimer of warranty provisions.
 *  
 *  Created by Patrick McSweeney on 12/5/08.
 */
package jnachos.filesystem;

import jnachos.machine.*;
import jnachos.kern.*;

/**
 * 
 * @author pjmcswee
 *
 */
public class NachosOpenFile implements OpenFile {

	/** The header for the file. */
	private FileHeader mHdr;
	private int mSector;
	
	/** The position within the file. */
	private int mSeekPosition;

	/**
	 * Open a Nachos file for reading and writing. Bring the file header into
	 * memory while the file is open.
	 *
	 * @param sector
	 *            the location on disk of the file header for this file
	 */
	public NachosOpenFile(int sector) {
		
		mHdr = new FileHeader();
		
		mHdr.fetchFrom(sector);
		mSector = sector;
		mSeekPosition = 0;
	}

	/**
	 * Close a Nachos file, de-allocating any in-memory data structures.
	 */
	public void delete() {
		mHdr.delete();
	}

	/**
	 * Change the current location within the open file -- the point at which
	 * the next Read or Write will start from.
	 *
	 * @param position
	 *            the location within the file for the next Read/Write.
	 */
	public void seek(int position) {
		mSeekPosition = position;
	}

	/**
	 * Read/write a portion of a file, starting from mSeekPosition. Return the
	 * number of bytes actually written or read, and as a side effect, increment
	 * the current position within the file.
	 *
	 * Implemented using the more primitive ReadAt/WriteAt.
	 *
	 * @param into
	 *            the buffer to contain the data to be read from disk
	 * @param from
	 *            the buffer containing the data to be written to disk
	 * @param numBytes
	 *            the number of bytes to transfer
	 */
	public int read(byte[] into, int numBytes) {
		int result = readAt(into, numBytes, mSeekPosition);
		mSeekPosition += result;
		return result;
	}

	public int write(byte[] into, int numBytes) {
		int result = writeAt(into, numBytes, mSeekPosition);
		mSeekPosition += result;
		return result;
	}

	/**
	 * Read/write a portion of a file, starting at "position". Return the number
	 * of bytes actually written or read, but has no side effects (except that
	 * Write modifies the file, of course).
	 * 
	 * There is no guarantee the request starts or ends on an even disk sector
	 * boundary; however the disk only knows how to read/write a whole disk
	 * sector at a time. Thus:
	 * 
	 * For ReadAt: We read in all of the full or partial sectors that are part
	 * of the request, but we only copy the part we are interested in. For
	 * WriteAt: We must first read in any sectors that will be partially
	 * written, so that we don't overwrite the unmodified portion. We then copy
	 * in the data that will be modified, and write back all the full or partial
	 * sectors that are part of the request.
	 * 
	 * @param into
	 *            the buffer to contain the data to be read from disk
	 * @param from
	 *            the buffer containing the data to be written to disk
	 * @param numBytes
	 *            the number of bytes to transfer
	 * @param position
	 *            the offset within the file of the first byte to be
	 *            read/written
	 *            ----------------------------------------------------------------------
	 */
	public int readAt(byte[] into, int numBytes, int position) {
		int fileLength = mHdr.fileLength();
		int i, firstSector, lastSector, numSectors;
		byte[][] buf;

		if ((numBytes <= 0) || (position >= fileLength)) {
			return 0; // check request
		}

		if ((position + numBytes) > fileLength) {
			numBytes = fileLength - position;
		}

		Debug.print('f', "Reading " + numBytes + " bytes at " + position + ", from file of length " + fileLength);

		firstSector = (int) Math.floor((double) position / (double) Disk.SectorSize);
		lastSector = (int) Math.floor(((double) position + numBytes - 1) / (double) Disk.SectorSize);

		numSectors = 1 + lastSector - firstSector;

		// read in all the full and partial sectors that we need
		buf = new byte[numSectors][Disk.SectorSize];

		int skip = position - (firstSector * Disk.SectorSize);
		int offset = 0;
		int bytesRead = 0;
		for (i = firstSector; i <= lastSector; i++) {
			JNachos.mSynchDisk.readSector(mHdr.byteToSector(i * Disk.SectorSize), buf[(i - firstSector)]);

			// Copy the data into the buffer
			if (i == firstSector) {
				System.arraycopy(buf[(i - firstSector)], skip, into, offset,
						Math.min(into.length, Disk.SectorSize - skip));

				offset += Math.min(into.length, Disk.SectorSize - skip);
			} else if (i == lastSector) {
				// System.out.println(into.length +"\t" + numBytes + "\t" +( i *
				// Disk.SectorSize) + "\t" + (Disk.SectorSize - ((lastSector +
				// 1) * Disk.SectorSize) - (position + numBytes)));
				// System.out.println(Disk.SectorSize + "\t" + position + "\t" +
				// lastSector);
				System.arraycopy(buf[(i - firstSector)], 0, into, offset,
						Disk.SectorSize - (((lastSector + 1) * Disk.SectorSize) - (position + numBytes)));
			} else {
				System.arraycopy(buf[(i - firstSector)], 0, into, offset, Disk.SectorSize);
				offset += Disk.SectorSize;
			}
		}
		return numBytes;
	}
	
	
	public int readAtFragments(byte[] into, int numBytes, int position) {
		mHdr.fetchFrom(mSector);

		int fileLength = mHdr.fileLength();
		int i, firstFragment, lastFragment, numFragments;
		byte[][] buf;

		if ((numBytes <= 0) || (position >= fileLength)) {
			return 0; // check request
		}

		if ((position + numBytes) > fileLength) {
			numBytes = fileLength - position;
		}

		Debug.print('f', "Reading " + numBytes + " bytes at " + position + ", from file of length " + fileLength);

		firstFragment = (int) Math.floor((double) position / (double) Disk.FragmentSize);
		lastFragment = (int) Math.floor(((double) position + numBytes - 1) / (double) Disk.FragmentSize);

		numFragments = 1 + lastFragment - firstFragment;

		// read in all the full and partial sectors that we need
		buf = new byte[numFragments][Disk.FragmentSize];

		int skip = position - (firstFragment * Disk.FragmentSize);
		int offset = 0;
		int bytesRead = 0;
		for(int j = firstFragment; j <= lastFragment; ){
			 //checkIfAllFrags = true;
					Boolean checkIfAllFrags = true;
		if(mHdr.byteToFragment(j) % NachosFileSystem.FRAGMENTNUM == 0 &&  j+NachosFileSystem.FRAGMENTNUM-1 <=lastFragment){
			for(int k=0;k<NachosFileSystem.FRAGMENTNUM;k++)
			{
				if(mHdr.byteToFragment(j+k) == mHdr.byteToFragment(j)+k)
				{
					checkIfAllFrags = 	checkIfAllFrags && true;
				}
				else
				{
					checkIfAllFrags = 	checkIfAllFrags && false	;

				}
			
			}

			 if(checkIfAllFrags) 
			 {
				byte[] sectBuf = new byte[Disk.SectorSize];
				JNachos.mSynchDisk.readSector(mHdr.byteToFragment(j)/NachosFileSystem.FRAGMENTNUM, sectBuf);
				for(int k=0;k<NachosFileSystem.FRAGMENTNUM;k++)
				{
				//System.arraycopy(sectBuf, 0, buf[(j - firstFragment)], 0, Disk.FragmentSize);
				System.arraycopy(sectBuf,k* Disk.FragmentSize, buf[(j+k - firstFragment)], 0, Disk.FragmentSize);
				}

				//JNachos.mSynchDisk.readSector(mHdr.byteToFragment(j)/2, buf[(j - firstFragment)]);
				j= j+NachosFileSystem.FRAGMENTNUM;
			}
			else{

				//int sectNum = mHdr.byteToFragment(j)/2
				JNachos.mSynchDisk.readFragment(mHdr.byteToFragment(j), buf[(j - firstFragment)]);
				j=j+1;
			
			}
		}
			else{

				//int sectNum = mHdr.byteToFragment(j)/2
				JNachos.mSynchDisk.readFragment(mHdr.byteToFragment(j), buf[(j - firstFragment)]);
				j=j+1;
			
			}
		}
		
		
		for (i = firstFragment; i <= lastFragment; i=++i) {
			//JNachos.mSynchDisk.readSector(mHdr.byteToFragment(i)/2, buf[(i - firstFragment)]);

			// Copy the data into the buffer
			if (i == firstFragment) {
				System.arraycopy(buf[(i - firstFragment)], skip, into, offset,
						Math.min(into.length, Disk.FragmentSize - skip));

				offset += Math.min(into.length, Disk.FragmentSize - skip);
			} else if (i == lastFragment) {

				// System.out.println(into.length +"\t" + numBytes + "\t" +( i *
				// Disk.SectorSize) + "\t" + (Disk.SectorSize - ((lastSector +
				// 1) * Disk.SectorSize) - (position + numBytes)));
				// System.out.println(Disk.SectorSize + "\t" + position + "\t" +
				// lastSector);
				System.arraycopy(buf[(i - firstFragment)], 0, into, offset,
						Disk.FragmentSize - (((lastFragment + 1) * Disk.FragmentSize) - (position + numBytes)));
			} else {
				System.arraycopy(buf[(i - firstFragment)], 0, into, offset, Disk.FragmentSize);
				offset += Disk.FragmentSize;
			}
		}

		return numBytes;
	}

	

	public boolean writeToSector(int sectorNum, byte[] buffer) {
		int sectorStartIndex = sectorNum * Disk.SectorSize;
		int bufferStartIndex = 0;
		try {
			//System.arraycopy(buffer, bufferStartIndex, dest, sectorStartIndex, Disk.SectorSize);
		}catch(Exception e) {
			System.out.println("error writing to sector!");
			return false;
		}
		return false;
	}
	
	public int writeAt(byte[] from, int numBytes, int position) {
		int fileLength = mHdr.fileLength();
		int i, firstSector, lastSector, numSectors;
		boolean firstAligned, lastAligned;
		byte[][] buf;

		if ((numBytes <= 0) || (position >= fileLength)) {
			return 0; // check request
		}

		if ((position + numBytes) > fileLength) {
			numBytes = fileLength - position;
		}

		Debug.print('f', "Writing " + numBytes + " bytes at " + position + ", from file of length " + fileLength);

		// ceil
		firstSector = (int) Math.floor(((double) position) / Disk.SectorSize);
		lastSector = (int) Math.floor(((double) position + numBytes - 1) / Disk.SectorSize);

		numSectors = 1 + lastSector - firstSector;

		buf = new byte[numSectors][Disk.SectorSize];

		firstAligned = (position == (firstSector * Disk.SectorSize));
		lastAligned = ((position + numBytes) == ((lastSector + 1) * Disk.SectorSize));

		int firstSkip = position - (firstSector * Disk.SectorSize);
		int lastSkip = ((lastSector + 1) * Disk.SectorSize) - (position + numBytes);

		// read in first and last sector, if they are to be partially modified
		if (!firstAligned) {
			readAt(buf[0], Disk.SectorSize, firstSector * Disk.SectorSize);
		}
		if (!lastAligned && ((firstSector != lastSector) || firstAligned)) {
			readAt(buf[(lastSector - firstSector)], Disk.SectorSize, lastSector * Disk.SectorSize);
		}

		// copy in the bytes we want to change
		// bcopy(from, &buf[position - (firstSector * SectorSize)], numBytes);
		// System.arraycopy(from,0,buf,position - (firstSector *
		// Disk.SectorSize), numBytes);
		// ? Need quick array copy

		// System.out.println("FS: " + firstSkip);
		int offset = 0;
		// write modified sectors back
		for (i = firstSector; i <= lastSector; i++) {

			if (i == firstSector) {
				int bytes = (i + 1) * Disk.SectorSize - position;

				System.arraycopy(from, offset, buf[0], position - firstSector * Disk.SectorSize,
						Math.min(bytes, numBytes));
				offset += bytes;
			} else if (i == lastSector) {
				// System.out.println("LS: " + lastSkip + "loc :" + position);
				System.arraycopy(from, offset, buf[lastSector - firstSector], 0,
						position + numBytes - lastSector * Disk.SectorSize);
			} else {
				System.arraycopy(from, offset, buf[i - firstSector], 0, Disk.SectorSize);
				offset += Disk.SectorSize;
			}

			JNachos.mSynchDisk.writeSector(mHdr.byteToSector(i * Disk.SectorSize), buf[(i - firstSector)]);//
		}

		return numBytes;
	}

	public int writeAtFragment(byte[] from, int numBytes, int position) {
		boolean isTempBufferSet = false;
		byte[] tempBuf;
		int fileLength = mHdr.fileLength();
		//System.out.println("INFO: File length: " + fileLength);
		int i, firstFragment, lastFragment, numFragments;
		boolean firstAligned, lastAligned;
		byte[][] buf;

		if ((numBytes < 0) || (position > fileLength)) {
			return 0; // check request
			//newfileLength
		}

		if ((position + numBytes) > fileLength ) {
			//int spaceAvailable = fileLength - position;
			//int extraContent = numBytes - spaceAvailable; 
			//mHdr.allocateFragment(pFreeFragmentMap, pFileSize);
			int pos = mHdr.lastFragmentOffset;

			if( ( fileLength % Disk.SectorSize != 0 && fileLength % Disk.SectorSize < Disk.FragmentSize) && !(fileLength % Disk.FragmentSize + numBytes < Disk.FragmentSize))
			{
				byte[] readBuf = new byte[Disk.FragmentSize];

				readAtFragments(readBuf, Disk.FragmentSize,(int) Math.floor((double)fileLength / Disk.FragmentSize) * Disk.FragmentSize);

				int newNumBytes = (fileLength - pos) + numBytes;
				tempBuf = new  byte[pos+numBytes];
				isTempBufferSet = true;
				System.arraycopy(readBuf,0,tempBuf,0,fileLength % Disk.FragmentSize);	
				
				
				System.arraycopy(from, 0, tempBuf,pos, numBytes);
				numBytes = numBytes + pos ;
				position = (int) Math.floor((double)fileLength / Disk.FragmentSize) * Disk.FragmentSize;
				from = tempBuf;
				boolean isSuccess = JNachos.nachosFileSystem.expandFile(numBytes,(int) Math.floor(((double)fileLength) / Disk.FragmentSize),mHdr,mSector);
				if(!isSuccess){
					return 0;
				}
			}
			else{
				boolean isSuccess = JNachos.nachosFileSystem.expandFile(numBytes,-1,mHdr,mSector);
				if(!isSuccess){
					return 0;
				}
			}
			mHdr.fetchFrom(mSector);
			//numBytes = fileLength - position
		}

		Debug.print('f', "Writing " + numBytes + " bytes at " + position + ", from file of length " + fileLength);

		// ceil
		firstFragment = (int) Math.floor(((double) position) / Disk.FragmentSize);
		lastFragment = (int) Math.floor(((double) position + numBytes - 1) / Disk.FragmentSize);

		numFragments = 1 + lastFragment - firstFragment;

		buf = new byte[numFragments][Disk.FragmentSize];

		firstAligned = (position == (firstFragment * Disk.FragmentSize));
		lastAligned = ((position + numBytes) == ((lastFragment + 1) * Disk.FragmentSize));

		int firstSkip = position - (firstFragment * Disk.FragmentSize);
		int lastSkip = ((lastFragment + 1) * Disk.FragmentSize) - (position + numBytes);

		// read in first and last sector, if they are to be partially modified
		if (!firstAligned) {
			readAtFragments(buf[0], Disk.FragmentSize, firstFragment * Disk.FragmentSize);
		}
		if (!lastAligned && ((firstFragment != lastFragment) || firstAligned)) {
			readAtFragments(buf[(lastFragment - firstFragment)], Disk.FragmentSize, lastFragment * Disk.FragmentSize);
		}

		// copy in the bytes we want to change
		// bcopy(from, &buf[position - (firstSector * SectorSize)], numBytes);
		// System.arraycopy(from,0,buf,position - (firstSector *
		// Disk.SectorSize), numBytes);
		// ? Need quick array copy

		// System.out.println("FS: " + firstSkip);
		int offset = 0;
		// write modified sectors back
		for (i = firstFragment; i <= lastFragment; i++) {

			if (i == firstFragment) {
				int bytes = (i + 1) * Disk.FragmentSize - position;

				System.arraycopy(from, offset, buf[0], position - firstFragment * Disk.FragmentSize,
						Math.min(bytes, numBytes));
				offset += bytes;
			} else if (i == lastFragment) {
				// System.out.println("LS: " + lastSkip + "loc :" + position);
				System.arraycopy(from, offset, buf[lastFragment - firstFragment], 0,
						position + numBytes - lastFragment * Disk.FragmentSize);
			} else {
				System.arraycopy(from, offset, buf[i - firstFragment], 0, Disk.FragmentSize);
				offset += Disk.FragmentSize;
			}

			//JNachos.mSynchDisk.writeSector(mHdr.byteToSector(i * Disk.SectorSize), buf[(i - firstFragment)]);//
		}
		//for(int j = firstFragment; j <= lastFragment;j = j+NachosFileSystem.FRAGMENTNUM ){
			 //checkIfAllFrags = true;
			
		//}

		
		for(int j = firstFragment; j <= lastFragment ; ){
			Boolean checkIfAllFrags =true;

			if(j+NachosFileSystem.FRAGMENTNUM <= lastFragment && mHdr.byteToFragment(j) % NachosFileSystem.FRAGMENTNUM == 0){
				for(int k=0;k<NachosFileSystem.FRAGMENTNUM ;k++){
					if(mHdr.byteToFragment(j+k) == mHdr.byteToFragment(j)+k){
						checkIfAllFrags = 	checkIfAllFrags && true;
					}
					else{
						checkIfAllFrags = 	checkIfAllFrags && false	;
					}
				}
				if( checkIfAllFrags){
					byte[] sectBuf = new byte[Disk.SectorSize];
					//System.arraycopy(buf[j-firstFragment], 0,sectBuf, 0, Disk.FragmentSize);
					for(int k = 0;k < NachosFileSystem.FRAGMENTNUM; k++){
						System.arraycopy(buf[(j+k)-firstFragment], 0, sectBuf, k*Disk.FragmentSize, Disk.FragmentSize);
					}
					JNachos.mSynchDisk.writeSector(mHdr.byteToFragment(j)/NachosFileSystem.FRAGMENTNUM, sectBuf);
					j=j+NachosFileSystem.FRAGMENTNUM;
				}
				else{
					JNachos.mSynchDisk.writeFragment(mHdr.byteToFragment(j), buf[(j - firstFragment)]);
					j=j+1;
				}
			}else{
				JNachos.mSynchDisk.writeFragment(mHdr.byteToFragment(j), buf[(j - firstFragment)]);
				j=j+1;
			}
		}
		mHdr.lastFragmentOffset = mHdr.lastFragmentOffset + ( numBytes % Disk.FragmentSize );
		mHdr.writeBack(mSector);
		//JNachos.mSynchDisk.writeSector(mHdr.byteToSector(i * Disk.SectorSize), buf[(i - firstFragment)]);//
		return numBytes;
	}
	
	/**
	 * Closes the file
	 */
	public void closeFile() {

	}

	/**
	 * Return the number of bytes in the file.
	 */
	public int length() {
		return mHdr.fileLength();
	}

	/**
	 * Writes the content to the file sectors
	 * */
	public void customWrite(byte[] inputBytesArr) {
		
		byte[] buffer = new byte[Disk.SectorSize];
		int contentLength = inputBytesArr.length;
		int fileLength = mHdr.fileLength();
		int[] dataFraments = mHdr.mDataFragments;
		int currentSector;
		while(contentLength != 0) {
			
		}
	}
	
}
