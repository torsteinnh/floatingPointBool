package com.booleanbyte.worldsynth.common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.booleanbyte.worldsynth.common.Synth;

public class ProjectWriter {
	
	private static String synthToDocumentFormat(Synth synth) {
		Element e = synth.toElement();
		return e.toDocumentformat();
	}
	
	public static void writeSynthToFile(Synth synth, File file) {
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			byte[] bytebuffer = synthToDocumentFormat(synth).getBytes();
			fos.write(bytebuffer);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
