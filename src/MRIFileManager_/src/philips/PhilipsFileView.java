package philips;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import MRIFileManager.GetStackTrace;
import abstractClass.PrefParam;

public class PhilipsFileView extends FileView {
	
@Override
public Icon getIcon(File file) {
		
		if (file.isDirectory()) {
				if (searchParRec(".REC", file)){
					return PrefParam.iconPhilips;
				} 
		}
		return null;
	}
	
	@Override
	public Boolean isTraversable(File file) {
		if (file.isDirectory()) {
			if (searchParRec(".REC", file)){
				return false;
			} 
		}
		return true;
	}
	
	public boolean searchParRec (String extToFind, File searchIn) {
        String[] listOfFiles = searchIn.list();
        boolean find = false;
        int i = 0;
        try {
        while (i < listOfFiles.length && !find) {
        	if (listOfFiles[i].toUpperCase().endsWith(extToFind)) find=true;
           	i++;
        }
        }
        catch (Exception e) {
			new GetStackTrace(e);
//			FileManagerFrame.getBugText().setText(FileManagerFrame.getBugText().getText()+"\n----------------\n"+GetStackTrace.getMessage());	
        }
        return find;
	}
}