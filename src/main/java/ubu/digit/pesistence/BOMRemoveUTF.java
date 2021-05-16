package ubu.digit.pesistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BOMRemoveUTF {
	
	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER =  LoggerFactory.getLogger(BOMRemoveUTF.class.getName());
	
	public static void main(String[] args) throws IOException {
		new BOMRemoveUTF().bomRemoveUTFDirectory(".\\rsc");
	}
	
	/***
	 * Remove unicode character BOM (Byte Order Mark) from of files of a
	 * directory
	 * @param directoryStrPath
	 */
	public void bomRemoveUTFDirectory(String directoryStrPath) {
		LOGGER.info("bomRemoveUTFDirectory : " + directoryStrPath);
		File directory = new File(directoryStrPath);

		
		try {
			if (directory.isDirectory()) {
				for (File fileEntry : directory.listFiles()) {
					bomRemoveUTF(directoryStrPath + "/" +  fileEntry.getName());
					LOGGER.info("Ruta directory  " + directoryStrPath + "/" +  fileEntry.getName());
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			
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
		LOGGER.info("Ruta file " + fileStrPath);
		byte[] buffer = new byte[3];
		file.read(buffer);
		if (hasBom(buffer)){
			LOGGER.info("BUFFER BOM  " + buffer);
			int inputSize = (int)file.length();
			byte[] bufferWithoutBom = new byte[inputSize-3];
			file.read(bufferWithoutBom,0,inputSize-3);
			file.seek(0);
			file.write(bufferWithoutBom, 0, inputSize-3);
			file.setLength(inputSize-3);
		}
		file.close();
	}
	
	/**
	 * Remove unicode character BOM (Byte Order Mark) at the beginning of file 
	 * @param buffer
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public  void bomRemoveUTF(byte[] buffer, String fileStrPath) throws FileNotFoundException, IOException {
		RandomAccessFile file = new RandomAccessFile(fileStrPath,"rw");
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
