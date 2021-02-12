package ubu.digit.pesistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

public class BOMRemoveUTF {
	
	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = Logger.getLogger(BOMRemoveUTF.class);
	
	public static void main(String[] args) throws IOException {
		//new BOMRemoveUTF().bomRemoveUTF(".\\rsc\\Tribunal2.csv");
		new BOMRemoveUTF().bomRemoveUTFDirectory(".\\rsc");
	}
	
	
	/***
	 * Remove unicode character BOM (Byte Order Mark) from of files of a
	 * directory
	 * @param directoryStrPath
	 */
	public void bomRemoveUTFDirectory(String directoryStrPath) {

		File directory = new File(directoryStrPath);
		
		try {
			if (directory.isDirectory()) {
				for (File fileEntry : directory.listFiles()) {
					bomRemoveUTF(directoryStrPath + "/" +  fileEntry.getName());
				}
			}
		} catch (IOException e) {
			LOGGER.error(e);
			
		}

	}

	/**Remove unicode character BOM (Byte Order Mark) at the beginning of file 
	 * @see https://en.wikipedia.org/wiki/Byte_order_mark
	 * @param fileStrPath 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public  void bomRemoveUTF(String fileStrPath) throws FileNotFoundException, IOException {
		RandomAccessFile file = new RandomAccessFile(fileStrPath,"rw");
		byte[] buffer = new byte[3];
		file.read(buffer);
		if (hasBom(buffer)){
			int inputSize = (int)file.length();
			byte[] bufferWithoutBom = new byte[inputSize-3];
			file.read(bufferWithoutBom,0,inputSize-3);
			file.seek(0);
			file.write(bufferWithoutBom, 0, inputSize-3);
			file.setLength(inputSize-3);

		}
		file.close();
	}
	

	/** Check if buffer is unicode character BOM (Byte Order Mark)  
	 * @see https://en.wikipedia.org/wiki/Byte_order_mark
	 * @param buffer
	 * @return 
	 * @throws IOException
	 */
	private  boolean hasBom(byte[] buffer) throws IOException {
		String string = new String();
		for (byte b : buffer)
			string += String.format("%02X", b);
		return string.equals("EFBBBF") ? true : false;
	}
}
