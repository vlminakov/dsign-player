package main;

import java.io.File;

public class Resource {

	public static final byte	VIDEO			= 0;
	public static final byte	MUSIC			= 1;
	public static final byte	BITMAP			= 2;
	public static final byte	INCOMPATABLE	= 3;

	public static final String	LOGO_ID				= "0xe56f4827e08d17a7L";//com.mp4player.id_logo

	public static final String[]	VIDEO_TYPES	= {
		"mp4", "flv"
	};

	public static final String[]	MUSIC_TYPES	= {
		"mp3", "wav"
	};

	public static final String[]	BITMAP_TYPES	= {
		"bmp", "jpg", "gif", "png"
	};

	private String				path;
	private String				name;
	private long				count;
	private byte				type;
	private String				id;

	public Resource(String path, long count) {
		setPath(path);
		setType(getFileType(path));
		if (getType() == BITMAP){
			setCount(count);
		} else {
			setCount(1);
		}
	}

	public Resource(Resource resource) {
		setPath(resource.getPath());
		setCount(resource.getCount());
		setType(resource.getType());
		setId(resource.getId());
	}

	public static Resource getLogoResources(long timeToStart) {
		Resource resource = new Resource(Path.LOGO, timeToStart - System.currentTimeMillis());
		resource.setId(LOGO_ID);
		resource.setName("logo");
		return resource;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = Math.max(0, count);
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	//checking file type by extension
	private byte getFileType(String path) {
		String pathFind = new String(path);
		if (pathFind.length() > 4) {
			pathFind = pathFind.toLowerCase();
			for (int i = 0; i < VIDEO_TYPES.length; i++)
				if (pathFind.endsWith(VIDEO_TYPES[i]))
					return VIDEO;
			for (int i = 0; i < BITMAP_TYPES.length; i++)
				if (pathFind.endsWith(BITMAP_TYPES[i]))
					return BITMAP;
			for (int i = 0; i < MUSIC_TYPES.length; i++)
				if (pathFind.endsWith(MUSIC_TYPES[i]))
					return MUSIC;
		}
		return INCOMPATABLE;
	}

	public boolean checkFileExist() {
		return new File(getPath()).exists();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
