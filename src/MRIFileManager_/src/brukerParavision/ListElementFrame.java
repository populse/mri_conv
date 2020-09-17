package brukerParavision;

import java.io.IOException;

import MRIFileManager.ExtractTxtfromFile;

public class ListElementFrame {

	private String txtVisupars;

	private String[][] visuparsParams = { 	{ "##$VisuCoreDataUnits=", "VisuCoreDataUnits= ", "2" },
											{ "##$VisuFGElemComment=", "VisuFGElemComment= ", "2" },
											};

	public ListElementFrame(String file) {
		txtVisupars = new ExtractTxtfromFile(file).getTxt();

	}

	public String[][] listElement() throws IOException {
		String[][] listElem = new String[2][2];
		String text;

		listElem[0][0] = visuparsParams[0][1];
		listElem[1][0] = visuparsParams[1][1];

		if (txtVisupars.contains(visuparsParams[0][0]) && txtVisupars.contains(visuparsParams[1][0]))
			for (int i = 0; i < 2; i++) {
				text = txtVisupars;
				text = text.substring(text.indexOf(visuparsParams[i][0]) + 3);
				text = text.substring(text.indexOf("<"), text.indexOf("##"));
				if (text.contains("$$"))
					text = text.substring(0, text.indexOf("$$"));
				text=text.replace("\n", "");
				listElem[i][1] = text;
			}

		return listElem;
	}
}
