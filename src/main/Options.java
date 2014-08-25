package main;

public class Options {
	private static Options instance = null;
	
	private String email;
	private String password;
	private String upid;
	
	private Options(){
		upid = "f891052085d1cfc3c81b4ecccd77f68b";
		email = "";
		password = "";
	}
	
	public static Options getInstance(){
		if (instance == null){
			instance = new Options();
		}
		
		return instance;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUpid() {
		return upid;
	}
}
