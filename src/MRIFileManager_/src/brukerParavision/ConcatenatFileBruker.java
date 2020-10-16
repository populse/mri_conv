package brukerParavision;

import java.io.File;

import MRIFileManager.ExtractTxtfromFile;
import abstractClass.PrefParam;

public class ConcatenatFileBruker extends PrefParam {

	private String chemAcqp, chemMethod, chemVisupars, chemReco, chemSubject;
	private StringBuffer txtCont;
	private String missFile;
	private ExtractTxtfromFile extractFile;

	public ConcatenatFileBruker(String chemSeq) {
		
		chemAcqp = chemSeq.substring(0, chemSeq.indexOf("pdata") - 1) + separator + "acqp";
		chemMethod = chemSeq.substring(0, chemSeq.indexOf("pdata") - 1) + separator + "method";
		chemVisupars = chemSeq.substring(0, chemSeq.indexOf("2dseq") - 1) + separator + "visu_pars";
		chemReco = chemSeq.substring(0, chemSeq.indexOf("2dseq") - 1) + separator + "reco";
		chemSubject = chemSeq;
		for (int i = 0; i < 4; i++)
			chemSubject = chemSubject.substring(0, chemSubject.lastIndexOf(separator));
		chemSubject += separator + "subject";
		if (!new File(chemReco).exists()) {
			chemReco = chemReco.substring(0, chemReco.lastIndexOf(PrefParam.separator));
			chemReco = chemReco.substring(0, chemReco.lastIndexOf(PrefParam.separator) + 1) + "1" + separator + "reco";
		}
		missFile = "";
		extractFile = new ExtractTxtfromFile(chemSubject);
		txtCont = new StringBuffer(extractFile.getTxt()).append("\n");
		if (! extractFile.full()) missFile = " subject ";
		extractFile = new ExtractTxtfromFile(chemAcqp);
		txtCont = txtCont.append(extractFile.getTxt()).append("\n");
		if (! extractFile.full()) missFile += " acqp ";
		extractFile = new ExtractTxtfromFile(chemVisupars);
		txtCont = txtCont.append(extractFile.getTxt()).append("\n");
		if (! extractFile.full()) missFile += " visu_pars ";
		extractFile = new ExtractTxtfromFile(chemReco);
		txtCont = txtCont.append(extractFile.getTxt()).append("\n");
		if (! extractFile.full()) missFile += " reco ";
		extractFile = new ExtractTxtfromFile(chemMethod);
		txtCont = txtCont.append(extractFile.getTxt());
		if (! extractFile.full()) missFile += " method ";
		
		if (!missFile.isEmpty())
			missFile = "error : missing"+missFile+"file(s)";
		
//		txtCont = new StringBuffer(new ExtractTxtfromFile(chemSubject).getTxt()).append("\n")
//				.append(new ExtractTxtfromFile(chemAcqp).getTxt()).append("\n")
//				.append(new ExtractTxtfromFile(chemVisupars).getTxt()).append("\n")
//				.append(new ExtractTxtfromFile(chemReco).getTxt()).append("\n")
//				.append(new ExtractTxtfromFile(chemMethod).getTxt());
	}

	public String getTxtCont() {
		return txtCont.toString();
	}
	
	public String missingFiles() {
		return this.missFile;
	}
}