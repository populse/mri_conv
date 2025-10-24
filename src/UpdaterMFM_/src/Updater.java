import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;


public class Updater {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java -jar Updater.jar temp_path");
            return;
        }
        new Updater(args[0]);
    }

    private Updater(String tempPath) throws Exception{
        
    	Path MFMcurrent = Paths.get(this.getClass().getClassLoader().getResource("").toURI());
    	boolean scs;
    	
        System.out.println("Updater:" + tempPath);

        // wait MRIFIleManager is closed 
//        boolean locked;
//        do {
//            locked = isFileLocked(exePath);
//            if (locked) {
//                System.out.println("Attente de la fermeture de MonLogiciel...");
//                Thread.sleep(2000);
//            }
//        } while (locked);
        
    	String LibrSrc = tempPath + File.separator + "mri_conv-devpt" + File.separator + "MRIFileManager" + File.separator + "MRIManager_lib";
    	String SoftSrc = tempPath + File.separator + "mri_conv-devpt" + File.separator + "MRIFileManager" + File.separator + "MRIManager.jar";
    	String dest = MFMcurrent + File.separator + "MRIManager_lib";
    	String destSoft = MFMcurrent + File.separator + "MRIManager.jar";
  		scs = CopyFiles(LibrSrc, dest);
  		if (scs == true) {
  			CopyFiles(SoftSrc, destSoft);
  			JOptionPane.showMessageDialog(null, "Done, you can restart MRI Files Manager");
  		}
  		else
  			JOptionPane.showMessageDialog(null, "Error");

//        System.exit(0);

        // Restart MRIFIleManager
//        new ProcessBuilder("java", "-jar", exePath).start();
    }

    // Vérifie si un fichier est encore verrouillé
    private static boolean isFileLocked(String path) {
        File file = new File(path);
        try {
            FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE);
            channel.close();
            return false; // Non verrouillé
        } catch (IOException e) {
            return true;  // Toujours utilisé
        }
    }
    
	public boolean CopyFiles(String src, String dest) {
		
		boolean success = false;

		Path source = Paths.get(src);
		Path destination = Paths.get(dest);

		List<String> extensionsAuthorized = Arrays.asList(".jar");

        try {
            // remove destination repertory if exists
        	if (Files.exists(source) && Files.exists(destination)) {
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
	            success = true;
        	}
        	else {
        		System.out.println("error ! Source or Destination not found");
        	}
        } catch (IOException e) {
            System.err.println("Error : " + e.getMessage());
            success = false;
        }
        return success;
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
