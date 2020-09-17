package dcm;

import java.util.HashMap;

import MRIFileManager.Dateformatmodif;

public class DicomDictionaryAdjustement {

	public DicomDictionaryAdjustement() {
	}

	public HashMap<String, String> valuesAdjustement(HashMap<String, String> listValues) {

		String tmp;

		if (listValues.get("Rescale Intercept").isEmpty())
			listValues.put("Rescale Intercept", "0");
		if (listValues.get("Rescale Slope").isEmpty())
			listValues.put("Rescale Slope", "1");

//		if (listValues.get("Slice Orientation").split(" +").length == 1 && listValues.get("Image Position Patient").split(" +").length != 1) {
//			String pos0,pos1;
//			String[] listOfPosition = listValues.get("Image Position Patient").split(" +");
//			int leng = listOfPosition.length;
//			pos0=listOfPosition[0];
//			pos1=listOfPosition[leng-1];
//
//			switch (listValues.get("Slice Orientation")) {
//			case "axial":
//				pos0=pos0.split("\\\\")[2];
//				pos1=pos1.split("\\\\")[2];
//				tmp=String.valueOf(Math.abs((Float.parseFloat(pos1)-Float.parseFloat(pos0))/(leng-1)));
//				break;
//			case "coronal":
//				pos0=pos0.split("\\\\")[1];
//				pos1=pos1.split("\\\\")[1];
//				tmp=String.valueOf(Math.abs((Float.parseFloat(pos1)-Float.parseFloat(pos0))/(leng-1)));
//				break;
//			case "sagittal":
//				pos0=pos0.split("\\\\")[0];
//				pos1=pos1.split("\\\\")[0];
//				tmp=String.valueOf(Math.abs((Float.parseFloat(pos1)-Float.parseFloat(pos0))/(leng-1)));
//				break;
//			default:
//				tmp = listValues.get("Slice Separation");
//			}
//			try {
//				listValues.put("Slice Separation", tmp);
//			} catch (Exception e){};
//		}

		if (listValues.get("Slice Separation").isEmpty()) {
			String[] listPos = listValues.get("Slice Position").split(" +");
			try {
				tmp = String.valueOf(Math.abs(Float.parseFloat(listPos[1]) - Float.parseFloat(listPos[0])));
				listValues.put("Slice Separation", tmp);
			} catch (Exception e) {
			}
		}

		try {
			if (Float.parseFloat(listValues.get("Slice Separation")) < 0.0)
				listValues.put("Slice Separation",
						String.valueOf(Math.abs(Float.parseFloat(listValues.get("Slice Separation")))));
		} catch (Exception e) {
		}

		try {
			listValues.put("Spatial Resolution", listValues.get("Spatial Resolution").split("\\\\")[0] + " "
					+ listValues.get("Spatial Resolution").split("\\\\")[1]);
		} catch (Exception e) {
		}

		try {
			listValues.put("Scan Resolution", listValues.get("Rows") + " " + listValues.get("Columns"));
		} catch (Exception e) {
		}

		try {
			listValues.put("Number Of Diffusion",
					String.valueOf(listValues.get("Number Of Diffusion").split(" +").length));
		} catch (Exception e) {
		}

		/***************************************************************
		 * reorganization Diffusion Direction and B-values effective
		 *************************************************************/

//		String bvectmp = listValues.get("Direction Diffusion");
//		String bvaltmp = listValues.get("B-values effective");;
//		String[] listbvec = bvectmp.split(" +");
//		String[] listbval = bvaltmp.split(" +");
//		String lvtmpb = "", lvtmpc = "";
//		int cnt = 0;
//		for (int i=0; i<listbvec.length;i++) {
//			lvtmpb += listbvec[i]+ " ";
//			if (cnt == 0)
//				lvtmpc += hh + " ";
//	 
//		}
//		listValues.put("Direction Diffusion", lvtmpb.trim());
//		listValues.put("B-values effective", lvtmpc.trim());

		/********* to put Diffusion Ao Images number at 0 *************/
		listValues.put("Diffusion Ao Images number", "0");

		try {
			if (listValues.get("Image Position Patient").split(" +").length > 1)
				listValues.put("Number Of Slice",
						String.valueOf(listValues.get("Image Position Patient").split(" +").length));
			else
				listValues.put("Number Of Slice", listValues.get("Images In Acquisition"));
		} catch (Exception e) {
		}
		

		try {
			if (!listValues.get("Echo Time").contentEquals("(simplified view mode)"))
				listValues.put("Number Of Echo", String.valueOf(listValues.get("Echo Time").split(" +").length));
			else
				listValues.put("Number Of Echo", "");

		} catch (Exception e) {
		}


		try {
			listValues.put("Number Of Repetition",
					String.valueOf(listValues.get("Number Of Repetition").split(" +").length));
		} catch (Exception e) {
		}

		try {
			tmp = listValues.get("Study Date") + " " + listValues.get("Study Time");
			tmp = new Dateformatmodif(tmp, "yyyyMMdd ss", "yyyy-MM-dd HH:mm:ss").getNewFormatDate();
			listValues.put("Creation Date", tmp);
		} catch (Exception e) {
		}

		try {
			tmp = listValues.get("Read Direction");
			String read_direction = "";
			if (tmp.split(" +")[0].contentEquals("0")) { // case column read
															// direction
				for (String gg : listValues.get("Slice Orientation").split(" +"))
					switch (gg) {
					case "axial":
						read_direction += "L_R ";
						break;
					case "coronal":
						read_direction += "L_R ";
						break;
					case "sagittal":
						read_direction += "A_P ";
						break;
					case "oblique":
						read_direction += "nc ";
						break;
					}
				listValues.put("Read Direction", read_direction.trim());
			} else { // case row read direction
				for (String gg : listValues.get("Slice Orientation").split(" +"))
					switch (gg) {
					case "axial":
						read_direction += "A_P ";
						break;
					case "coronal":
						read_direction += "H_F ";
						break;
					case "sagittal":
						read_direction += "H_F ";
						break;
					case "oblique":
						read_direction += "nc ";
						break;
					}
				listValues.put("Read Direction", read_direction.trim());
			}
		} catch (Exception e) {
		}

		return listValues;
	}

}
