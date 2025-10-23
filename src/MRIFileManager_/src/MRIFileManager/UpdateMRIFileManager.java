package MRIFileManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import abstractClass.PrefParam;


public class UpdateMRIFileManager extends PrefParam {

	public UpdateMRIFileManager(FileManagerFrame wind) throws Exception {

//	    String MFMcurrent = this.getClass().getClassLoader().getResource("").getPath();
	    Path MFMcurrent = Paths.get(this.getClass().getClassLoader().getResource("").toURI());
	    System.out.println("MRIFileManager Directory = " + MFMcurrent);

	    String urlstr = "https://github.com/populse/mri_conv/archive/refs/heads/devpt.zip";
		String urlReadme = "https://raw.githubusercontent.com/populse/mri_conv/refs/heads/devpt/README.md";
		String destDir = System.getProperty("java.io.tmpdir");
	    System.out.println("Temporary Directory = " + destDir);

		String fileZip = destDir + PrefParam.separator + "devpt.zip";
        byte[] buffer = new byte[1024];
        Boolean unzipState = false;

		URL urlRM = new URL(urlReadme);
        Scanner sc = null;
        String versionMFM = null;
        try {
            URLConnection connection = urlRM.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedInputStream bisRM = new BufferedInputStream(connection.getInputStream());
            sc = new Scanner(bisRM);
            versionMFM = sc.nextLine();
            System.out.println("Version MFM : " + versionMFM);

        } catch (IOException e) {
            System.err.println("Error : " + e.getMessage());
            return;
        } finally {
            if (sc != null) sc.close();
            System.out.println("finally");
        }
		
//		BufferedInputStream bisRM = new BufferedInputStream(urlRM.openStream());
		versionMFM = versionMFM.substring(versionMFM.indexOf("(") + 1, versionMFM.lastIndexOf(")"));

        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");

		int answ = JOptionPane.showConfirmDialog(
                wind,
                "Upgrade to " + versionMFM + " ?\nYour current version is " + versionSoft,
                "MRIFileManager updater",
                JOptionPane.YES_NO_OPTION
        );

        if (answ == JOptionPane.YES_OPTION) {

        	URL url = new URL(urlstr);
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			FileOutputStream fis = new FileOutputStream(fileZip);

	        ProgressDialog dialog = new ProgressDialog(wind, "Download in progress...", true, true);

	        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	            @Override
	            protected Void doInBackground() throws Exception {
	            	int count=0;
	                while((count = bis.read(buffer,0,1024)) != -1){
	                    fis.write(buffer, 0, count);
	                }
	                fis.close();
	                bis.close();
	                return null;
	            }

	            @Override
	            protected void done() {
	                dialog.close();
	                if (isCancelled()) {
	                    JOptionPane.showMessageDialog(wind, "Processing canceled.");
	                } else {
	                }
	            }
	        };

	        dialog.bindToWorker(worker);
	        worker.execute();
	        dialog.setVisible(true);

	        try {
	        	unzip(fileZip, destDir);
	        	unzipState = true;
	        	
			} catch (Exception e) {
				// TODO: handle exception
			}
        }

        if (unzipState == true) {
        	
        	String TempSrc = destDir;
        	String OS = System.getProperty("os.name");
        	ProcessBuilder processBuilder;
        	try {
        		if (!OS.toLowerCase().contains("windows"))
        				processBuilder = new ProcessBuilder("sh", "-c", "java -jar Updater.jar " + TempSrc + "&");
        		else
        			    processBuilder = new ProcessBuilder("cmd", "/c", "start", "java", "-jar", "Updater.jar", TempSrc); 
	    	 // Optional: redirect standard output and errors to the current console
	            processBuilder.inheritIO();
	    	    System.out.println("Launch Updater.jar");
	    	    processBuilder.start();
//	    	    Thread.sleep(500); // 0,5 seconde
	            System.exit(0);

//	    	    Process process = processBuilder.start();
//	    	    System.exit(0);
//	    	    int exitCode = process.waitFor();
//	    	    System.out.println("Process finished with code : " + exitCode);
	
	    	} catch (IOException e) {
	    	    e.printStackTrace();
	    	}
        }
	}

	public static void unzip(String zipFile, String destFolder) throws Exception {
	        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
	            ZipEntry entry;
	            byte[] buffer = new byte[1024];
	            while ((entry = zis.getNextEntry()) != null) {
	                File newFile = new File(destFolder + separator + entry.getName());
	                if (entry.isDirectory()) {
	                    newFile.mkdirs();
	                } else {
	                    new File(newFile.getParent()).mkdirs();
	                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
	                        int length;
	                        while ((length = zis.read(buffer)) > 0) {
	                            fos.write(buffer, 0, length);
	                        }
	                    }
	                }
	            }
	        }
	    }
}
