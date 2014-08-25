package dsnet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import main.Options;
import main.Path;
import main.Resource;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpCore implements Path {
	private FTPClient client;
	
	public FtpCore(String host) throws Exception{
		this.client = new FTPClient();
		this.client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		int reply;
		this.client.connect(host);
		reply = this.client.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)){
			this.client.disconnect();
			throw new Exception("Can not connect to ftp server");
		}
		this.client.login(Options.getInstance().getEmail(), Options.getInstance().getPassword());
		this.client.setFileType(FTP.BINARY_FILE_TYPE);
		this.client.enterLocalPassiveMode();
	}
	
	public boolean downloadFile(String fileName, int fileType) throws Exception{
		String sourceDirectory = "";
		switch (fileType) {
		case Resource.VIDEO:
			sourceDirectory = "video";
			break;
		case Resource.MUSIC:
			sourceDirectory = "audio";
			break;
		case Resource.BITMAP:
			sourceDirectory = "image";
			break;

		default:
			break;
		}
		if (this.client == null){
			throw new Exception("Ftp client is not initialized");
		}
		OutputStream fos = new FileOutputStream("/home/player/media/" + fileName);
		this.client.retrieveFile(sourceDirectory + File.separator + fileName, fos);
		File file = new File("/home/player/media/" + fileName);
		
		return file.exists();
	}
	
	public void disconnect() throws IOException{
		if (this.client.isConnected()){
			this.client.logout();
			this.client.disconnect();
		}
	}
}