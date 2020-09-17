package MRIFileManager;

import java.io.FileWriter;

import abstractClass.PrefParam;

public class PrefParamModif extends PrefParam {

	public PrefParamModif() {

		String txt = "";

//		if (FilestmpRep.exists()) {
//			txt = new ExtractTxtfromFile(FilestmpRep.toString()).getTxt();
//
//			try {
//				BufferedReader bg = new BufferedReader(new StringReader(txt));
//				String tmp = "";
//				while ((tmp = bg.readLine()) != null) {
//					if (tmp.contains(key))
//						txt = txt.replace(tmp, key + " " + newVal);
//				}
//			} catch (Exception e) {
//				new GetStackTrace(e);
////				FileManagerFrame.getBugText().setText(GetStackTrace.getMessage());
//			}
//		}
//
//		else {
			
			txt = "[Bruker] " + lectBruker + "\n" + "[Dicom] " + lectDicom + "\n" + "[ParRec] " + lectParRec + "\n"
					+ "[NifTI] " + lectNifTI + "\n" + "[Bids] "+ lectBids + "\n" + "[Export] " + outExport + "\n" + "[LookAndFeel] "
					+ LookFeelCurrent + "\n" + "[NamingRepNifTI] " + namingRepNiftiExport + "\n" + "[NamingFileNifTI] "
					+ namingFileNiftiExport + "\n"
					+ "[NamingOptionsNifTI] " + namingOptionsNiftiExport+ "\n" 
					+ "[previewActived] " + ((previewActived)?"Yes":"No")+ "\n"
					+ "[SeqDetail] " + "1023" + "\n";
//		}
		try {
			FileWriter printRep = new FileWriter(FilestmpRep);
			printRep.write(txt);
			printRep.close();
		} catch (Exception e1) {
			new GetStackTrace(e1);
		}
	}
}