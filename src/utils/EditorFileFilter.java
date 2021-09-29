package utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class EditorFileFilter extends FileFilter {
	protected String ext;

	protected String desc;

	public EditorFileFilter(String extension, String description) {
		ext = extension.toLowerCase();
		desc = description;
	}

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().toLowerCase().endsWith(ext);
	}

	@Override
	public String getDescription() {
		return desc;
	}

	public String getExtension() {
		return ext;
	}

	public void setExtension(String extension) {
		this.ext = extension;
	}
}
