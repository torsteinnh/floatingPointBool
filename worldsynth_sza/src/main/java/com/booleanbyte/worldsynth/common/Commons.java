package com.booleanbyte.worldsynth.common;

import java.io.File;

public class Commons {
	public static File getExecutionDirectory() {
		String path = Commons.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if(path.endsWith("bin/")) {
			path = path.substring(0, path.lastIndexOf("/bin/"));
		}
		else {
			path = path.substring(0, path.lastIndexOf("/"));
		}
		return new File(path);
	}
}
