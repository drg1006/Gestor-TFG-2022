package ubu.digit.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que se encarga de eliminar los caracteres unicode BOM (Byte Order Mark) de los ficheros.
 */
public class BOMRemoveUTF {
	
	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER =  LoggerFactory.getLogger(BOMRemoveUTF.class.getName());
	
	public static void main(String[] args) throws IOException {
		new BOMRemoveUTF().bomRemoveUTFDirectory(".\\rsc");
	}
	
	/***
	 * Elimina el caracter unicode BOM (Byte Order Mark) de los archivos del directorio.
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
			LOGGER.error(e.getMessage());
			
		}
	}

	/**
	 * Elimina el caracter unicode BOM (Byte Order Mark) del comienzo del fichero.
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
	
	/**
	 * Elimina el caracter unicode character BOM (Byte Order Mark) del comienzo del fichero.
	 * @param buffer
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public  void bomRemoveUTF(byte[] buffer, String fileStrPath) throws FileNotFoundException, IOException{
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
	

	/** 
	 * Comprueba si el buffer es un caracter unicode BOM (Byte Order Mark)  
	 * @see https://en.wikipedia.org/wiki/Byte_order_mark
	 * @param buffer
	 * @return 
	 * @throws IOException
	 */
	private  boolean hasBom(byte[] buffer){
		String string = new String();
		for (byte b : buffer)
			string += String.format("%02X", b);
		return string.equals("EFBBBF") ? true : false;
	}
}
