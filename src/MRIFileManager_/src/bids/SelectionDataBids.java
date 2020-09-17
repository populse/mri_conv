package bids;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import MRIFileManager.ExtractTxtfromFile;
import MRIFileManager.FileManagerFrame;
import MRIFileManager.GetStackTrace;
import MRIFileManager.OpenImageJ;
import abstractClass.PrefParam;
import abstractClass.SelectionData;
import ij.IJ;

public class SelectionDataBids extends SelectionData {

	private String dataSelected;

	public SelectionDataBids(FileManagerFrame wind) throws Exception {
		super();
		dataSelected = wind.getListPath().getSelectedItem().toString() + PrefParam.separator;
		dataSelected = dataSelected.substring(12);
	}

	@Override
	public void popMenuData(JPopupMenu pm) {
		JMenuItem seeDescription = new JMenuItem("see 'dataset_description.json' file");
		seeDescription.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showParamFile(dataSelected + "dataset_description.json", "dataset_description.json");
			}
		});
		pm.add(seeDescription);

		JMenuItem seeParticipants = new JMenuItem("see 'participants.tsv' file");
		seeParticipants.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showParamFile(dataSelected + "participants.tsv", "participants.tsv");
			}
		});
		pm.add(seeParticipants);

	}

	@Override
	public void popMenuDataExport(JPopupMenu popMenuExport) {
		// TODO Auto-generated method stub
	}

	private void showParamFile(String pathFile, String title) {

		String tm = "";

		tm = pathFile + " :\n";

		if (title.contentEquals("dataset_description.json")) {

			String tmp = new ExtractTxtfromFile(pathFile).getTxt();

			try {
				String prettyJson = toPrettyFormat(tmp);
				tmp = prettyJson;
				tm += tmp + "\n";
			} catch (Exception e) {
				tm = tm + title + " not found\n";
			}

		} else

			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(pathFile));
				StringWriter out = new StringWriter();
				int b;
				while ((b = in.read()) != -1)
					out.write(b);
				out.flush();
				out.close();
				in.close();
				tm += out.toString() + "\n";

			} catch (IOException e) {
				new GetStackTrace(e);
				tm = tm + title + " not found\n";
			}

		new OpenImageJ();
		IJ.log(tm);

	}

	public static String toPrettyFormat(String jsonString) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);

		return prettyJson;
	}
}