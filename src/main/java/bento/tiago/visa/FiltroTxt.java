package bento.tiago.visa;

import java.io.File;
import java.io.FileFilter;

public class FiltroTxt implements FileFilter {
	public boolean accept(File f) {
		return f.getAbsolutePath().toLowerCase().endsWith(".txt");
	}
}
