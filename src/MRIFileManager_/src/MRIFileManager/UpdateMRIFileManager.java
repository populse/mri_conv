package MRIFileManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import abstractClass.PrefParam;


public class UpdateMRIFileManager extends PrefParam {

	private Scanner sc;

	public UpdateMRIFileManager(FileManagerFrame wind) throws Exception {

	    String MFMcurrent = this.getClass().getClassLoader().getResource("").getPath();
	    System.out.println("Working Directory = " + MFMcurrent);

		String urlstr = "https://github.com/populse/mri_conv/archive/refs/heads/devpt.zip";
		String urlReadme = "https://raw.githubusercontent.com/populse/mri_conv/refs/heads/devpt/README.md";
		String destDir = System.getProperty("java.io.tmpdir");
		String fileZip = destDir + PrefParam.separator + "devpt.zip";
        byte[] buffer = new byte[1024];
        Boolean unzipState = false;

		URL urlRM = new URL(urlReadme);
		BufferedInputStream bisRM = new BufferedInputStream(urlRM.openStream());
		sc = new Scanner(bisRM);
		String versionMFM =  sc.nextLine();
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
        	String LibrSrc = destDir + separator + "mri_conv-devpt" + separator + "MRIFileManager" + separator + "MRIManager_lib";
        	String SoftSrc = destDir + separator + "mri_conv-devpt" + separator + "MRIFileManager" + separator + "MRIManager.jar";
        	String dest = MFMcurrent + separator + "MRIManager_lib";
        	String destSoft = MFMcurrent + separator + "MRIManager.jar";
        	CopyFiles(LibrSrc, dest);
        	CopyFiles(SoftSrc, destSoft);
        	JOptionPane.showMessageDialog(wind, "Please close and restart MRI Files Manager");
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

	public void CopyFiles(String src, String dest) {

		Path source = Paths.get(src);
		Path destination = Paths.get(dest);

		List<String> extensionsAuthorized = Arrays.asList(".jar");

        try {
            // remove destination repertory if exists
        	if (Files.exists(destination)) {
                removeRepertory(destination);

	            // copy repertory with filter
	            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
	                @Override
	                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	                    Path destinationPath = destination.resolve(source.relativize(dir));
	                    Files.createDirectories(destinationPath);
	                    return FileVisitResult.CONTINUE;
	                }
	
	                @Override
	                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                    String fileName = file.getFileName().toString().toLowerCase();
	                    boolean extensionValide = extensionsAuthorized.stream()
	                            .anyMatch(fileName::endsWith);
	
	                    if (extensionValide) {
	                        Path destinationPath = destination.resolve(source.relativize(file));
	                        Files.copy(file, destinationPath, StandardCopyOption.REPLACE_EXISTING);
	                        System.out.println("File copied : " + file);
	                    } else {
	                        System.out.println("File ignored (extension no authorized) : " + file);
	                    }
	
	                    return FileVisitResult.CONTINUE;
	                }
	            });
	
	            System.out.println("✅ copy finished !");
        	}
        	else {
        		System.out.println("error ! Destination not found");
        	}
        } catch (IOException e) {
            System.err.println("Error : " + e.getMessage());
        }
	}

    public static void removeRepertory(Path chemin) throws IOException {
        Files.walkFileTree(chemin, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path fichier, BasicFileAttributes attrs) throws IOException {
                Files.delete(fichier);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dossier, IOException exc) throws IOException {
                Files.delete(dossier);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
