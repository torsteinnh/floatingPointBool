package com.booleanbyte.worldsynth.common.biome;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.booleanbyte.worldsynth.common.Commons;

public class BiomeRegistry {
	
	public static ArrayList<Biome> REGISTER = new ArrayList<Biome>();
	
	public BiomeRegistry() {
		registerBiomes();
	}
	
	private void registerBiomes() {
		//Load materials from files
		File biomesDirectory = new File(Commons.getExecutionDirectory(), "biomes");
		if(!biomesDirectory.exists()) {
			biomesDirectory.mkdir();
		}
		loadeBiomesLib(biomesDirectory);
	}
	
	private void loadeBiomesLib(File biomessDirectory) {
		
		if(!biomessDirectory.isDirectory()) {
			System.err.println("Lib directory \"" + biomessDirectory.getAbsolutePath() + "\" does not exist");
			return;
		}
		
		for(File sub: biomessDirectory.listFiles()) {
			if(sub.isDirectory()) {
				loadeBiomesLib(sub);
			}
			else if(sub.getName().endsWith(".csv")) {
				try {
					loadBiomessFromFile(sub);
				} catch (IOException e) {
					System.err.println("Problem occoured while trying to read biome file: " + sub.getName());
					e.printStackTrace();
				}
			}
		}
	}
	
	private void loadBiomessFromFile(File biomesFile) throws IOException {
		FileInputStream fis = new FileInputStream(biomesFile);
		byte[] bytebuffer = new byte[(int) biomesFile.length()];
		fis.read(bytebuffer);
		fis.close();
		String stringFormat = new String(bytebuffer);
		
		stringFormat = stringFormat.replace("\t", "");
		stringFormat = stringFormat.replace("\r", "");
		
		String[] materialEntries = stringFormat.split("\n");
		
		for(String materialEnty: materialEntries) {
			String[] separatedValues = materialEnty.split(";");
			
			int entryValues = 4;
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
			
			String biomeName = separatedValues[0];
			String biomeIdName = separatedValues[1];
			int biomeIdNumber;
			Color biomeColor;
			
			try {
				biomeIdNumber = Byte.parseByte(separatedValues[2]);
			} catch (NumberFormatException e) {
				biomeIdNumber = -1;
			}
			
			try {
				biomeColor = new Color(Integer.parseInt(separatedValues[3], 16));
			} catch (NumberFormatException e) {
				continue;
			}
			
			
			Biome newMaterial = new Biome(biomeName, biomeIdName, biomeIdNumber, biomeColor);
			
			REGISTER.add(newMaterial);
		}
	}
	
	public static Biome getDefaultBiome() {
		return REGISTER.get(0);
	}
	
	public static Biome getBiome(int id) {
		for(Biome b: REGISTER) {
			if(id == b.getId()) {
				return b;
			}
		}
		return new Biome("Unknown biome " + id, "unknown", id, Color.MAGENTA);
	}
	
	public static Biome getBiome(String idName) {
		if(idName != null) {
			for(Biome b: REGISTER) {
				if(idName.equals(b.getIdName())) {
					return b;
				}
			}
		}
		
		return new Biome("Unknown biome", idName, -1, Color.MAGENTA);
	}
	
	public static Biome getBiomeByInternalId(int internalId) {
		if(internalId < 0 || internalId >= REGISTER.size()) {
			return new Biome("Unknown biome i" + internalId, "unknown", internalId, Color.MAGENTA);
		}
		return REGISTER.get(internalId);
	}
	
	public static int getInternalId(Biome material) {
		return REGISTER.indexOf(material);
	}
	
	public static Biome[] getBiomesAlphabetically() {
		//Sort biomes alphabetically by name
		Biome[] biomeSort = new Biome[REGISTER.size()];
		REGISTER.toArray(biomeSort);
		
		Arrays.sort(biomeSort);
		
		return biomeSort;
	}
}
