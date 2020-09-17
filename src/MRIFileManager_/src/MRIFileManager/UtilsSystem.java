package MRIFileManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;

import abstractClass.PrefParam;
 
public class UtilsSystem extends PrefParam {
 
  /**
   * Return the absolute path of the jar (Java 1.6 min)
   *
   * @return The path of the jar
   */
  public static String pathOfJar()
  {
    CodeSource codeSource;
    String jarDir = "";
    File jarFile;
 
    try
    {
      codeSource = UtilsSystem.class.getProtectionDomain().getCodeSource();
      jarFile = new File(URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8"));
      jarDir = jarFile.getParentFile().getPath() + separator;
    }
    catch (URISyntaxException e)
    {
    	new GetStackTrace(e);
//		FileManagerFrame.getBugText().setText(FileManagerFrame.getBugText().getText()+"\n----------------\n"+GetStackTrace.getMessage());
    }
    catch (UnsupportedEncodingException e)
    {
    	new GetStackTrace(e);
//		FileManagerFrame.getBugText().setText(FileManagerFrame.getBugText().getText()+"\n----------------\n"+GetStackTrace.getMessage());
    }
 
    return jarDir;
  }
 
}