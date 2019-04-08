package com.booleanbyte.worldsynth.common.biome;

import java.awt.Color;

public class Biome implements Comparable<Biome> {
	private String name;
	private String idName;
	private int id;
	private Color color;
	
	public Biome(String name, String idName, int id, Color color) {
		this.name = name;
		this.idName = idName;
		this.id = id;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIdName() {
		return idName;
	}
	
	public int getId() {
		return id;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getInternalId() {
		return BiomeRegistry.getInternalId(this);
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Biome comp) {
		return getName().compareTo(comp.getName());
	}
}
