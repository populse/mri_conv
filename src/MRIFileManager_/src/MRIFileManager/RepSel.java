package MRIFileManager;

import java.io.File;

import javax.swing.JFileChooser;

import abstractClass.PrefParam;

public class RepSel extends PrefParam {
	
	public RepSel() {
		
		new UtilsSystem();
		FilestmpRep = new File(UtilsSystem.pathOfJar()+separator+"FilestmpRep.txt");
		
		if (FilestmpRep.exists()){
			String StringRep = new ExtractTxtfromFile(FilestmpRep.toString()).getTxt();
			
			try {
				lectBruker	= StringRep.substring(StringRep.indexOf("[Bruker]")+9,StringRep.indexOf("\n"));
				StringRep=StringRep.substring(StringRep.indexOf("\n")+1);
				if (lectBruker.isEmpty())
					lectBruker	= new JFileChooser().getCurrentDirectory().toString();
				lectDicom		= StringRep.substring(StringRep.indexOf("[Dicom]")+8,StringRep.indexOf("\n"));
				StringRep=StringRep.substring(StringRep.indexOf("\n")+1);
				if (lectDicom.isEmpty())
					lectDicom	= new JFileChooser().getCurrentDirectory().toString();
				lectParRec	= StringRep.substring(StringRep.indexOf("[ParRec]")+9,StringRep.indexOf("\n"));
				StringRep=StringRep.substring(StringRep.indexOf("\n")+1);
				if (lectParRec.isEmpty())
					lectParRec	= new JFileChooser().getCurrentDirectory().toString();
				lectNifTI 	= StringRep.substring(StringRep.indexOf("[NifTI]")+8,StringRep.indexOf("\n"));
				StringRep=StringRep.substring(StringRep.indexOf("\n")+1);
				if (lectNifTI.isEmpty())
					lectNifTI	= new JFileChooser().getCurrentDirectory().toString();
			}
			catch (Exception e2) {
				new GetStackTrace(e2);
				FilestmpRep.delete();
				lectBruker	= new JFileChooser().getCurrentDirectory().toString();
				lectDicom 	= new JFileChooser().getCurrentDirectory().toString();
				lectParRec	= new JFileChooser().getCurrentDirectory().toString();
				lectNifTI	= new JFileChooser().getCurrentDirectory().toString();
//				lectNifTI	= StringRep.substring(StringRep.indexOf("[NifTI]")+10);
			}
		}
		else {
			lectBruker 	= new JFileChooser().getCurrentDirectory().toString();
			lectDicom 	= lectBruker;
			lectParRec  = lectBruker;
			lectNifTI   = lectBruker;
		}
	}
}