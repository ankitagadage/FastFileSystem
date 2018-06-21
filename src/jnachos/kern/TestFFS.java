package jnachos.kern;

import jnachos.filesystem.NachosFileSystem;
import jnachos.filesystem.NachosOpenFile;
import jnachos.machine.JavaSys;

public class TestFFS implements VoidFunctionPtr{

	public static String fileName = "sampleFile";
	public static final int StartingPos = 0;
	public static int fileSize = 0;
	
	public static final String string1 = "pHuwdcTyF3wZdBGdoLe8";
	public static final String string2 = "Hw7phhNHdwNKEohgVaKu080DAmKl1vBnBhWCvxuBUseOGVLvO8x9GwbZWXdDC69qmvQgzHXSe9QiHCTiQnASXF3CceVTTNWOyepVXNUcFnVm";
	public static final String string3 = "dqV678ju9KwBSwBqlNydjVHTn8SxqPjlSNBzeWqQsnfoIPz4A6M25vHQ9vfyFJVgMkk2sT6MalKhruf7jNHxdJW0XnUwXBR11Z8ZRGB9KV1dyETGKb0EQhUJpNsiWRSO";
	public static final String string4 = "AQZWwls7o81OJG2g391XrH4yVoQ7JIVKyaVarkeTOTWbc5AYWt9Wr6CfP3irU1S6yjYalY93vLUe29i8OpVbxopU06Q2yakfb1Wh7SSs15BBWAmyp8SckV0QjdF4jzI1ck2GDkwjzt5KPRoxCLC0zTuYYNBz5PT9cT4UvfvgpFA67R50G21PECECbfdwxMhvRq1yEWsk9QcOAEGElCpEYci4jdL6I8N1R18EDNX9s5ADsb4wNu5tQB532DHHV6iQ";
	public static final String string5 = "Za3eQKjpExIafUQ2RIv3CDUdoJQD3kplpDgS6UO20zmCqrQ1QBTqO5STZuDg8zMsZHFdawrUONBRq1BadxVvjUbD5nCd2NmSZetsce730bG1zp40No7Vz1h7gaa29c4Nz4RBJ7A0AWQNKvVxljcT";
	public static final String string6 = "IcQySBSZ2CLskwFCVh9kuSSS2KfpqjuHmiDrTuYDDXI8kkBRuPfjx0e3AlGNwtt1dhlWLdLfmLGxJMegTUurH9X18v1bf5T1ykxaJgsoxu9B";
	
	public static final int writeSize1 = string1.length();//20;
	public static final int writeSize2 = string2.length();//108;
	public static final int writeSize3 = string3.length();//128;
	public static final int writeSize4 = string4.length();//256;
	public static final int writeSize5 = string5.length();//148;
	public static final int writeSize6 = string6.length();//108;
	
	
	public static int readSize = writeSize1 + writeSize2 + writeSize3 + writeSize4 + writeSize5 + writeSize6;
	
	@Override
	public void call(Object pArg) {
		
		// TODO Auto-generated method stub
		System.out.println("INFO: Initializing NachosFileSystem...");
		NachosFileSystem objNachosFileSystem = new NachosFileSystem(true);
		JNachos.nachosFileSystem = objNachosFileSystem;
		System.out.println("INFO: Creating file "+fileName+" with size " +fileSize+ "...");
		objNachosFileSystem.create(fileName, fileSize);
		System.out.println("INFO: File created successfully...");
		NachosOpenFile sampleFile = objNachosFileSystem.open(fileName);
		if(sampleFile == null){
			System.out.println("ERROR: File creation failed.");
		}
		
		byte[] bytes_1 = new byte[writeSize1];
		byte[] bytes_2 = new byte[writeSize2];
		byte[] bytes_3 = new byte[writeSize3];
		byte[] bytes_4 = new byte[writeSize4];
		byte[] bytes_5 = new byte[writeSize5];
		byte[] bytes_6 = new byte[writeSize6];
		byte[] bytes_7 = new byte[readSize];
		
		bytes_1 = string1.getBytes();
		bytes_2 = string2.getBytes();
		bytes_3 = string3.getBytes();
		bytes_4 = string4.getBytes();
		bytes_5 = string5.getBytes();
		bytes_6 = string6.getBytes();
		
//		for (int i = 0; i < writeSize1; ++i) {
//			bytes_1[i] = (byte)i;
//		}
//		
//		for (int i = 0; i < writeSize2; ++i) {
//			bytes_2[i] = (byte)(i + writeSize1);
//		}
//		
//		for (int i = 0; i < writeSize3; ++i) {
//			bytes_3[i] = (byte)(i + writeSize1 + writeSize2);
//		}
//		
//		for (int i = 0; i < writeSize4; ++i) {
//			bytes_4[i] = (byte)(i + writeSize1 + writeSize2 + writeSize3);
//		}
//		
//		for (int i = 0; i < writeSize5; ++i) {
//			bytes_5[i] = (byte)(i +  writeSize1 + writeSize2 + writeSize3 + writeSize4);
//		}
//		
//		for (int i = 0; i < writeSize6; ++i) {
//			bytes_6[i] = (byte)(i + writeSize1 + writeSize2 + writeSize3 + writeSize4 + writeSize5);
//		}
		
		System.out.println("INFO: Writing to file: " + fileName + " chunk of size: " + writeSize1);
		System.out.println("INFO: Writing content: \n" + string1);
		System.out.println("INFO: File Size: " + writeSize1);
		sampleFile.writeAtFragment(bytes_1, writeSize1, StartingPos);
		
		System.out.println("INFO: Writing to file: " + fileName + " chunk of size: " + writeSize2);
		System.out.println("INFO: Writing content: \n" + string2);
		System.out.println("INFO: File Size: " + (writeSize1 + writeSize2));
		sampleFile.writeAtFragment(bytes_2, writeSize2, writeSize1);
		
		System.out.println("INFO: Writing to file: " + fileName + " chunk of size: " + writeSize3);
		System.out.println("INFO: Writing content: \n" + string3);
		System.out.println("INFO: File Size: " + (writeSize1 + writeSize2 + writeSize3));
		sampleFile.writeAtFragment(bytes_3, writeSize3, writeSize1 + writeSize2);
		
		System.out.println("INFO: Writing to file: " + fileName + " chunk of size: " + writeSize4);
		System.out.println("INFO: Writing content: \n" + string4);
		System.out.println("INFO: File Size: " + (writeSize1 + writeSize2 + writeSize3 + writeSize4));
		sampleFile.writeAtFragment(bytes_4, writeSize4, writeSize3 + writeSize2 + writeSize1);
		
		System.out.println("INFO: Writing to file: " + fileName + " chunk of size: " + writeSize5);
		System.out.println("INFO: Writing content: \n" + string5);
		System.out.println("INFO: File Size: " + (writeSize1 + writeSize2 + writeSize3 + writeSize4 + writeSize5));
		sampleFile.writeAtFragment(bytes_5, writeSize5, writeSize4 + writeSize3 + writeSize2 + writeSize1);
		
		System.out.println("INFO: Writing to file: " + fileName + " chunk of size: " + writeSize6);
		System.out.println("INFO: Writing content: \n" + string6);
		System.out.println("INFO: File Size: " + (writeSize1 + writeSize2 + writeSize3 + writeSize4 + writeSize5 + writeSize6));
		sampleFile.writeAtFragment(bytes_6, writeSize6, writeSize5 + writeSize4  + writeSize3 + writeSize2 + writeSize1);
		
		System.out.println("INFO: Reading from file: " + fileName + " chunk of size: " + readSize);
		System.out.println("INFO: Data read from all the allocated blocks and fragments.");
		sampleFile.readAtFragments(bytes_7, readSize, StartingPos);
		
		System.out.println("INFO: File Content:\n");
		for (int i = 0; i < readSize; ++i) {
			System.out.print((char)bytes_7[i]);
		}
		
	}
}

/*int[] arrayInt= new int[readSize];
for(int i=0;i<readSize;++i)
{
	arrayInt[i] = (bytes_7[i]);
}*/


/*
byte[] bytes = new byte[20];
byte[] bytes1 = new byte[360];
byte[] bytes2 = new byte[340];


for(int i=0;i<20;i++){
	bytes[i] = (byte)i;
}

for(int i=0;i<340;i++){
	bytes2[i] = (byte)(i+20);
}

sampleFile.writeAtFragment(bytes, 20, 0); 
sampleFile.writeAtFragment(bytes2, 340, 20);


sampleFile.readAtFragments(bytes1, 360, 0);
*/
