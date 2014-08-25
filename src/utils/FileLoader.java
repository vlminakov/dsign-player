package utils;

import java.io.File;
import java.util.Vector;

import dsnet.FtpCore;
import dsnet.HttpConstants;
import main.Path;
import main.Resource;

public class FileLoader implements Path {

	public static void startLoading(final Vector<Resource> resources) {
		Thread loader = 
				new Thread(new Runnable() {
			@Override
			public void run() {
				//start downloading of files from path
				try {
					FtpCore ftpEngine = new FtpCore(HttpConstants.FTP_HOST_NAME);
					//synchronized (resources) {
						for (Resource resource : resources) {
							if (resource.getName().equals("logo"))
								continue;
							System.out.println("trying to download file - "
									+ resource.getName());
							File file = new File("/home/player/media/"
									+ resource.getName());
							if (file.exists()) {
								System.out
										.println("file - "
												+ resource.getName()
												+ " exists and will not be downloaded...");
								continue;
							}
							if (ftpEngine.downloadFile(resource.getName(),
									(int) resource.getType())) {
								System.out.println("file - "
										+ resource.getName() + " downloaded");
							} else {
								System.out.println("can not download file - "
										+ resource.getName());
							}
						}
					//}
					ftpEngine.disconnect();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Fileloader");
					e.printStackTrace();
				}
				// end download
			}
		});
		loader.start();
	}
}
