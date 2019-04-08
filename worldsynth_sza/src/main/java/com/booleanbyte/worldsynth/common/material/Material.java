package com.booleanbyte.worldsynth.common.material;

import java.awt.Color;

public class Material implements Comparable<Material> {
	private String name;
	private String idName;
	private int id;
	private byte meta;
	private Color color;
	
	public Material(String name, String idName, int id, Color color) {
		this(name, idName, id, (byte) -1, color);
	}
	
	public Material(String name, String idName, int id, int meta, Color color) {
		this.name = name;
		this.idName = idName;
		this.id = id;
		this.meta = (byte) meta;
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
	
	public byte getMeta() {
		return meta;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getInternalId() {
		return MaterialRegistry.getInternalId(this);
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(Material comp) {
		return getName().compareTo(comp.getName());
	}
}
