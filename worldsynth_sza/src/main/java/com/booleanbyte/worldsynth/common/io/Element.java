package com.booleanbyte.worldsynth.common.io;

import java.util.ArrayList;

public class Element {
	
	public ArrayList<Element> elements = new ArrayList<Element>();
	
	public String tag;
	public String content;
	
	public Element(String tag, String content) {
		this.tag = tag;
		this.content = content;
	}
	
	public Element(String tag, Element element) {
		this.tag = tag;
		this.elements.add(element);
	}
	
	public Element(String tag, ArrayList<Element> elements) {
		this.tag = tag;
		if(elements == null) return;
		this.elements.addAll(elements);
	}
	
	public Element(String tag, String content, ArrayList<Element> elements) {
		this.tag = tag;
		this.content = content;
		if(elements == null) return;
		this.elements.addAll(elements);
	}
	
	public Element(String stringFormat) {
		//Format the string to not contain tabs and linebeaks of tabulations
		stringFormat = stringFormat.replace("\t", "");
		stringFormat = stringFormat.replace("\n", "");
		stringFormat = stringFormat.replace("\r", "");
		
		StringBuilder stringB = new StringBuilder(stringFormat);
		decodeFromString(stringB);
	}
	
	private Element(StringBuilder documentFormat) {
		decodeFromString(documentFormat);
	}
	
	public String toDocumentformat() {
		String documentFormat = "<" + tag + ">\n";
		
		if(content != null) {
			if(!content.equals("")) {
				documentFormat += "\t" + content + "\n";
			}
		}
		
		if(elements.size() > 0) {
			for(Element e: elements) {
				if(e == null) continue;
				String childString = e.toDocumentformat();
				childString = tabulateString(childString);
				documentFormat += childString;
				documentFormat += "\n";
			}
		}
		documentFormat += "</" + tag + ">";
		return documentFormat;
	}
	
	private void decodeFromString(StringBuilder documentformat) {
		
		//Extreact the element tag
		int startBracket = documentformat.indexOf("<");
		int stopBracket = documentformat.indexOf(">");
		tag = documentformat.substring(startBracket+1, stopBracket);
		documentformat.delete(startBracket, stopBracket+1);
		
		while(true) {
			if(documentformat.charAt(0) == '<' && documentformat.charAt(1) != '/') {
				//This is the start of a new element, send documentformat to start the decoding of this internal element
				elements.add(new Element(documentformat));
			}
			else if(documentformat.charAt(0) == '<' && documentformat.charAt(1) == '/') {
				//This is the end of the current element, mark endpoint for use in removing the element from documentformat
				stopBracket = documentformat.indexOf(">");
				break;
			}
			else {
				//This is the content of this element, insert the content into content
				content = documentformat.substring(0, documentformat.indexOf("<"));
				documentformat.delete(0, documentformat.indexOf("<"));
			}
		}
		
		//Remove the extracted element from the documentformat
		documentformat.delete(0, stopBracket+1);
	}
	
	private String tabulateString(String s) {
		String[] line = s.split("\\n");
		s = "";
		for(String l: line) {
			s += "\t";
			s += l + "\n";
		}
		s = s.substring(0, s.length() - 1);
		
		return s;
	}
	
	@Override
	public String toString() {
		return toDocumentformat();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Element)) {
			return false;
		}
		if(toString().equals(obj.toString())) {
			return true;
		}
		return false;
	}
}
