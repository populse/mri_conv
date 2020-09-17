package MRIFileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;

import abstractClass.PrefParam;

public class PrefParamLoad extends PrefParam {

	public PrefParamLoad() {

		// lectBruker = new JFileChooser().getCurrentDirectory().toString();
		lectBruker = UtilsSystem.pathOfJar();
		lectDicom = lectBruker;
		lectParRec = lectBruker;
		lectNifTI = lectBruker;
		lectBids = lectBruker;
		outExport = lectBruker;
		namingRepNiftiExport = separator;
		namingFileNiftiExport = "PatientName-StudyName-CreationDate-SeqNumber-Protocol-SequenceName-AcquisitionTime";
		namingOptionsNiftiExport = "00000";
		previewActived = false;
		SeqDetail = 1023;
//		deidentify = false;

		new UtilsSystem();
		FilestmpRep = new File(UtilsSystem.pathOfJar() + separator + "FilestmpRep.txt");

		String txt = "";

		if (FilestmpRep.exists()) {
			txt = new ExtractTxtfromFile(FilestmpRep.toString()).getTxt();

			try {
				BufferedReader bg = new BufferedReader(new StringReader(txt));
				String tmp = "";
				while ((tmp = bg.readLine()) != null) {
					if (tmp.contains("[Bruker]"))
						lectBruker = tmp.substring(9);
					if (tmp.contains("[Dicom]"))
						lectDicom = tmp.substring(8);
					if (tmp.contains("[ParRec]"))
						lectParRec = tmp.substring(9);
					if (tmp.contains("[NifTI]"))
						lectNifTI = tmp.substring(8);
					if (tmp.contains("[Bids]"))
						lectBids = tmp.substring(7);
					if (tmp.contains("[Export]"))
						outExport = tmp.substring(9);
					if (tmp.contains("[NamingFileNifTI]"))
						namingFileNiftiExport = tmp.substring(18);
					if (tmp.contains("[NamingRepNifTI]"))
						namingRepNiftiExport = tmp.substring(17);
					if (tmp.contains("[NamingOptionsNifTI]")) {
						namingOptionsNiftiExport = tmp.substring(21);
						for (int i = namingOptionsNiftiExport.length(); i < 5; i++) {
							namingOptionsNiftiExport += "0";
						}
					}
					if (tmp.contains("[LookAndFeel]") && OptionLookAndFeel)
						LookFeelCurrent = tmp.substring(14);
					if (tmp.contains("[previewActived]"))
						previewActived = (tmp.substring(17).contentEquals("No")) ? false : true;
					if (tmp.contains("[SeqDetail]"))
						SeqDetail = Integer.parseInt(tmp.substring(12));
				}

			} catch (Exception e) {
				new GetStackTrace(e);
//				FileManagerFrame.getBugText().setText(GetStackTrace.getMessage());
			}

			if (!new File(lectBruker).exists())
				lectBruker = "";
			if (!new File(lectDicom).exists())
				lectDicom = "";
			if (!new File(lectParRec).exists())
				lectParRec = "";
			if (!new File(lectNifTI).exists())
				lectNifTI = "";
			if (!new File(lectBids).exists())
				lectBids = "";
			if (!new File(outExport).exists())
				outExport = "";
		}

		else {
			txt = "[Bruker] " + lectBruker + "\n" + "[Dicom] " + lectDicom + "\n" + "[ParRec] " + lectParRec + "\n"
					+ "[NifTI] " + lectNifTI + "\n" + "[Bids] " + lectBids + "\n" + "[Export] " + outExport + "\n" + "[LookAndFeel] "
					+ LookFeelCurrent + "\n" + "[NamingRepNifTI] " + separator + "\n" + "[NamingFileNifTI] "
					+ "PatientName-StudyName-CreationDate-SeqNumber-Protocol-SequenceName-AcquisitionTime" + "\n"
					+ "[NamingOptionsNifTI] " + "00000" + "\n" + "[previewActived] " + "No" + "\n" + "[SeqDetail] "
					+ "1023" + "\n";

			namingRepNiftiExport = separator;
			namingFileNiftiExport = "PatientName-StudyName-CreationDate-SeqNumber-Protocol-SequenceName-AcquisitionTime";
		}
	}
}