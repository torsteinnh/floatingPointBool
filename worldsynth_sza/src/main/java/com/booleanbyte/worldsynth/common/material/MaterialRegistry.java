package com.booleanbyte.worldsynth.common.material;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.booleanbyte.worldsynth.common.Commons;

public class MaterialRegistry {
	
	public static ArrayList<Material> REGISTER = new ArrayList<Material>();
	
	public MaterialRegistry() {
		registerMaterials();
	}
	
	private void registerMaterials() {
		REGISTER.add(new Material("Air", "minecraft:air", 0, new Color(0, 0, 0)));
		
		//Load materials from files
		File materialsDirectory = new File(Commons.getExecutionDirectory(), "materials");
		if(!materialsDirectory.exists()) {
			materialsDirectory.mkdir();
		}
		loadeMaterialsLib(materialsDirectory);
	}
	
	private void loadeMaterialsLib(File materialsDirectory) {
		
		if(!materialsDirectory.isDirectory()) {
			System.err.println("Lib directory \"" + materialsDirectory.getAbsolutePath() + "\" does not exist");
			return;
		}
		
		for(File sub: materialsDirectory.listFiles()) {
			if(sub.isDirectory()) {
				loadeMaterialsLib(sub);
			}
			else if(sub.getName().endsWith(".csv")) {
				try {
					loadMaterialsFromFile(sub);
				} catch (IOException e) {
					System.err.println("Problem occoured while trying to read material file: " + sub.getName());
					e.printStackTrace();
				}
			}
		}
	}
	
	private void loadMaterialsFromFile(File materialsFile) throws IOException {
		FileInputStream fis = new FileInputStream(materialsFile);
		byte[] bytebuffer = new byte[(int) materialsFile.length()];
		fis.read(bytebuffer);
		fis.close();
		String stringFormat = new String(bytebuffer);
		
		stringFormat = stringFormat.replace("\t", "");
		stringFormat = stringFormat.replace("\r", "");
		
		String[] materialEntries = stringFormat.split("\n");
		
		for(String materialEnty: materialEntries) {
			String[] separatedValues = materialEnty.split(";");
			
			int entryValues = 7;
			if(separatedValues.length < entryValues) {
				continue;
			}
			
			boolean validEntry = true;
			for(int i = 0; i < entryValues; i++) {
//				if(i != 3 && separatedValues[i].equals("")) {
//					validEntry = false;
//				}
			}
			if(!validEntry) {
				continue;
			}
			
			String materialName = separatedValues[0];
			String materialIdName = separatedValues[1];
			int materialIdNumber;
			byte materialMetaNumber;
			int red, green, blue;
			
			try {
				materialIdNumber = Integer.parseInt(separatedValues[2]);
			} catch (NumberFormatException e) {
				materialIdNumber = -1;
			}
			
			try {
				materialMetaNumber = Byte.parseByte(separatedValues[3]);
			} catch (NumberFormatException e) {
				materialMetaNumber = 0;
			}
			
			try {
				red = Integer.parseInt(separatedValues[4]);
				green = Integer.parseInt(separatedValues[5]);
				blue = Integer.parseInt(separatedValues[6]);
			} catch (NumberFormatException e) {
				continue;
			}
			
			
			Material newMaterial = new Material(materialName, materialIdName, materialIdNumber, materialMetaNumber, new Color(red, green, blue));
			
			REGISTER.add(newMaterial);
		}
	}
	
	public static Material getDefaultMaterial() {
		return REGISTER.get((REGISTER.size() > 1) ? 1 : 0);
	}
	
	public static Material getMaterial(int id, byte meta) {
		for(Material b: REGISTER) {
			if(id == b.getId()) {
				if(meta == b.getMeta() || b.getMeta() == -1) {
					return b;
				}
			}
		}
		return new Material("Unknown " + id + ":" + meta, "unknown", id, meta, Color.MAGENTA);
	}
	
	public static Material getMaterial(String idName) {
		if(idName != null) {
			for(Material b: REGISTER) {
				if(idName.equals(b.getIdName())) {
					return b;
				}
			}
		}
		
		return new Material("Unknown", idName, -1, Color.MAGENTA);
	}
	
	public static Material getMaterialByInternalId(int internalId) {
		if(internalId < 0 || internalId >= REGISTER.size()) {
			return new Material("Unknown i" + internalId, "unknown", internalId, Color.MAGENTA);
		}
		return REGISTER.get(internalId);
	}
	
	public static int getInternalId(Material material) {
		return REGISTER.indexOf(material);
	}
	
	public static Material[] getMaterialsAlphabetically() {
		//Sort materials alphabetically by name
		Material[] materialSort = new Material[REGISTER.size()];
		REGISTER.toArray(materialSort);
		
		Arrays.sort(materialSort);
		
		return materialSort;
	}
}
