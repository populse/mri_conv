package brukerParavision;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;

public class ChangeSubject implements ParamMRI2 {

	private String cheminSubject;
	private String fieldToChange;
	private String newName;

	public ChangeSubject(String cheminSubject, String newName, String fieldToChange) throws IOException {
		this.cheminSubject = cheminSubject;
		this.fieldToChange = fieldToChange;
		this.newName = newName;
		writenewSubject();
	}

	public void writenewSubject() throws IOException {
		String ligne;
		File file = new File(cheminSubject);
		File fileorigin = new File(cheminSubject.substring(0, cheminSubject.indexOf("subject")) + "subject_origin");
		File tmp = new File(cheminSubject.substring(0, cheminSubject.indexOf("subject")) + "subject_tmp");

		if (!fileorigin.exists()) {
			FileChannel in = null;
			FileChannel out = null;
			try {
				in = extracted2().getChannel();
				out = extracted1().getChannel();

				in.transferTo(0, in.size(), out);

			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
//				FileManagerFrame.getBugText().setText(GetStackTrace.getMessage());

			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e) {
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
					}
				}
			}
		}

		BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		while ((ligne = br.readLine()) != null) {
			if (ligne.contains(dictionaryMRISystem.get(fieldToChange).get("keyName"))) {
				if (ligne.contains("(")) {
					bw.write(ligne + "\n");
					bw.write("<" + newName + ">\n");
					bw.flush();
					br.readLine();
				} else {
					ligne = ligne.substring(0, ligne.indexOf("=") + 1);
					bw.write(ligne + newName + "\n");
				}
			} else {
				bw.write(ligne + "\n");
				bw.flush();
			}
		}
		bw.close();
		br.close();

		file.delete();
		tmp.renameTo(new File(cheminSubject));
	}

	private FileOutputStream extracted1() throws FileNotFoundException {
		return new FileOutputStream(cheminSubject.substring(0, cheminSubject.indexOf("subject")) + "subject_origin");
	}

	private FileInputStream extracted2() throws FileNotFoundException {
		return new FileInputStream(cheminSubject);
	}
}