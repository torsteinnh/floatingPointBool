package com.booleanbyte.worldsynth.module;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleRegisterLoader {
	
	String pathToJar;
	
	ArrayList<AbstractModuleRegister> moduleRegisters = new ArrayList<AbstractModuleRegister>();
	
	public ModuleRegisterLoader(File jar) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		pathToJar = jar.getAbsolutePath();
		JarFile jarFile = new JarFile(pathToJar);
		Enumeration<JarEntry> allJarEntries = jarFile.entries();

		URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
		ClassLoader cl = URLClassLoader.newInstance(urls, AbstractModuleRegister.class.getClassLoader());

		while (allJarEntries.hasMoreElements()) {
		    JarEntry jarEntry = allJarEntries.nextElement();
		    if(jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")){
		        continue;
		    }
		    String className = jarEntry.getName().substring(0,jarEntry.getName().length()-6);
		    className = className.replace('/', '.');
		    Class<?> c = cl.loadClass(className);
		    
		    if(AbstractModuleRegister.class.isAssignableFrom(c)) {
		    	Class<AbstractModuleRegister> moduleRegisterClass = (Class<AbstractModuleRegister>) c;
		    	Constructor<AbstractModuleRegister> classConstructor = moduleRegisterClass.getConstructor();
				moduleRegisters.add(classConstructor.newInstance());
		    }
		}
		
		jarFile.close();
	}
	
	public AbstractModuleRegister[] getModuleRegisters() {
		AbstractModuleRegister[] registers = new AbstractModuleRegister[moduleRegisters.size()];
		registers = moduleRegisters.toArray(registers);
		return registers;
	}
}
