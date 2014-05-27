package org.irri.fieldlab.model;

public class FileListModel implements Comparable<FileListModel> {
	private String name;
	private String data;
	private String path;

	public FileListModel(String n, String d, String p) {
		name = n;
		data = d;
		path = p;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int compareTo(FileListModel o) {
		if (this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
		else
			throw new IllegalArgumentException();
	}
}
