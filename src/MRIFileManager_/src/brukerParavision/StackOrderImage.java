package brukerParavision;

import java.io.IOException;

import MRIFileManager.ExtractTxtfromFile;

public class StackOrderImage {

	private String pathVisupars;

	private String[][] visuparsParams = { 	{ "##$VisuCoreFrameCount=", "VisuCoreFrameCount= " },
											{ "##$VisuFGOrderDescDim=", "VisuFGOrderDescDim= " }, 
											{ "##$VisuFGOrderDesc=", "VisuFGOrderDesc= " },
											{ "##$VisuGroupDepVals=", "VisuGroupDepVals= " } };

	public StackOrderImage(String pathVisupars) {
		this.pathVisupars = pathVisupars;
	}

	public String[][] listStack() throws IOException {
		String[][] listOrder = new String[4][2];
		String txtParam = new ExtractTxtfromFile(pathVisupars).getTxt();
		listOrder[0][0] = visuparsParams[0][1];
		listOrder[0][1] = new SearchParamBruker2(visuparsParams[0][0], txtParam).result();
		listOrder[1][0] = visuparsParams[1][1];
		listOrder[1][1] = new SearchParamBruker2(visuparsParams[1][0], txtParam).result();
		listOrder[2][0] = visuparsParams[2][1];
		listOrder[2][1] = new SearchParamBruker2(visuparsParams[2][0], txtParam).result();
		listOrder[3][0] = visuparsParams[3][1];
		listOrder[3][1] = new SearchParamBruker2(visuparsParams[3][0], txtParam).result();
		return listOrder;
	}
}