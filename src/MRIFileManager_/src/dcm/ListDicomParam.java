package dcm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;

public class ListDicomParam implements ParamMRI2 {

	public ListDicomParam(String noSeq, ArrayList<String[]> listA, int off, String numberOfFrame, ArrayList<String> listOffset) {

//		System.out.println(this + "listA length :" + listA.size());
//		System.out.println(this + "numberOfFrame :" + numberOfFrame);
//		System.out.println(this + "listOffset :" + listOffset);
		
		HashMap<String, String> listValues = hmInfo.get(noSeq);
	
		String[] listOffsetImage = new String[listOffset.size()];
		listOffsetImage = listOffset.toArray(listOffsetImage);

		int[] listOrder = Arrays.stream(listOffsetImage).mapToInt(Integer::parseInt).toArray();
		int[] listIndex = new int[listOffsetImage.length];
		Arrays.sort(listOrder);

		for (int i = 0; i < listOffsetImage.length; i++)
			listIndex[i] = Arrays.asList(listOffsetImage).indexOf(String.valueOf(listOrder[i]));

		String listOff = "";
		for (int j = 0; j < listIndex.length; j++) {
			listOff += String.valueOf(listIndex[j] + off) + " ";
		}
//		System.out.println(this +" listOff : " + listOff);
		listValues.put("Offsets Image", listOff);
		

		String[] listSlice = new String[18];
		
		if (!numberOfFrame.isEmpty())
			if (Integer.valueOf(numberOfFrame) > listA.size())
				listValues.put("Images In Acquisition", numberOfFrame);

//		if (listA.size() > 1)
//			listValues.put("Images In Acquisition", String.valueOf(listA.size()));
//		else
//			listValues.put("Images In Acquisition", numberOfFrame);
//
//		if (listValues.get("Images In Acquisition").isEmpty())
//			listValues.put("Images In Acquisition", "1");

		String[] lastEl = listA.get(listA.size() - 1);

//		Collections.sort(listA, new Comparator<Object[]>() {
//			@Override
//			public int compare(Object[] strings, Object[] otherStrings) {
//				return ((Integer) Integer.parseInt(strings[1].toString()))
//						.compareTo(Integer.parseInt(otherStrings[1].toString()));
//			}
//		});

		for (int h = 0; h < listSlice.length; h++)
			listSlice[h] = "";

		List<String> listTE = new ArrayList<>();
		List<String> listTR = new ArrayList<>();
		List<String> listIT = new ArrayList<>();
		List<String> listSO = new ArrayList<>();
		List<String> listImOrient = new ArrayList<>();
		List<String> listImPosit = new ArrayList<>();
		List<String> listImType = new ArrayList<>();
		List<String> listScanSeq = new ArrayList<>();
		List<String> listSliceLocation = new ArrayList<>();
		List<String> listTemporalPosit = new ArrayList<>();
		List<String> listDiffusionValue = new ArrayList<>();
		List<String> listDiffusionDirection = new ArrayList<>();
		List<String> listLabelType = new ArrayList<>();
		List<String> listRI = new ArrayList<>();
		List<String> listRS = new ArrayList<>();
		List<String> listSS = new ArrayList<>();

		String[] listFileDcm = new String[listA.size()];
		String tmp;

		for (int i = 0; i < listA.size(); i++) {
			if (!listA.get(i)[1].isEmpty()) {
				if (!listA.get(i)[5].isEmpty())
					listTE.add(listA.get(i)[5]);
				if (!listA.get(i)[4].isEmpty())
					listTR.add(listA.get(i)[4]);
				if (!listA.get(i)[6].isEmpty())
					listIT.add(listA.get(i)[6]);
				listImType.add(listA.get(i)[8]);
				listScanSeq.add(listA.get(i)[16]);
				listFileDcm[i] = listA.get(i)[0];
				listSliceLocation.add(listA.get(i)[7]);
				listTemporalPosit.add(listA.get(i)[9]);
				if (!listA.get(i)[13].isEmpty())
					listImPosit.add(listA.get(i)[13]);
				if (!listA.get(i)[14].isEmpty())
					listImOrient.add(listA.get(i)[14]);
				listDiffusionValue.add(listA.get(i)[15]);
				listDiffusionDirection.add(listA.get(i)[19]);
				listLabelType.add(listA.get(i)[17]);
				listRI.add(listA.get(i)[10]);
				listRS.add(listA.get(i)[11]);
				listSS.add(listA.get(i)[18]);
				tmp = listA.get(i)[14];
			} else {
				if (!lastEl[5].isEmpty())
					listTE.add(lastEl[5]);
				if (!lastEl[4].isEmpty())
					listTR.add(lastEl[4]);
				if (!lastEl[6].isEmpty())
					listIT.add(lastEl[6]);
				listImType.add(lastEl[8]);
				listScanSeq.add(lastEl[16]);
				listFileDcm[i] = lastEl[0];
				listSliceLocation.add(lastEl[7]);
				listTemporalPosit.add(lastEl[9]);
				if (!lastEl[13].isEmpty())
					listImPosit.add(lastEl[13]);
				if (!lastEl[14].isEmpty())
					listImOrient.add(lastEl[14]);
				listDiffusionValue.add(lastEl[15]);
				listDiffusionDirection.add(lastEl[19]);
				listLabelType.add(lastEl[17]);
				listRI.add(lastEl[10]);
				listRS.add(lastEl[11]);
				listSS.add(lastEl[18]);
				tmp = lastEl[14];
			}

//			System.out.println(this+" : noSeq = "+noSeq+" , tmp = "+tmp);
			try {
				if (!tmp.isEmpty()) {
					tmp = new DicomOrientation(tmp).getOrientationDicom();
					listSO.add(tmp);
				}
			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}
		}

		listA.clear();

		listSlice[0] = String.join(" ", listTE);
		listSlice[1] = String.join(" ", listTR);
		listSlice[2] = String.join(" ", listIT);
		listSlice[3] = String.join(" ", listSO);
		listSlice[4] = String.join(" ", listImOrient);
		listSlice[5] = String.join(" ", listImPosit);
		listSlice[6] = String.join(" ", listDiffusionValue);
		listSlice[7] = String.join(" ", listImType);
		listSlice[8] = String.join(" ", listSliceLocation);
		listSlice[9] = String.join(" ", listImPosit);
		listSlice[10] = String.join(" ", listImOrient);
		listSlice[11] = String.join(" ", listTemporalPosit);
		listSlice[12] = String.join(" ", listScanSeq);
		listSlice[13] = String.join(" ", listLabelType);
		listSlice[14] = String.join(" ", listRI);
		listSlice[15] = String.join(" ", listRS);
		listSlice[16] = String.join(" ", listSS);
		listSlice[17] = String.join(" ", listDiffusionDirection);

		if (!listSlice[0].isEmpty())
			listValues.put("Echo Time", deleteDuplicate(listSlice[0], false));
		listSlice[0] = listValues.get("Echo Time");

		if (!listSlice[1].isEmpty())
			listValues.put("Repetition Time", deleteDuplicate(listSlice[1], false));
		listSlice[1] = listValues.get("Repetition Time");
		
		if (!listSlice[2].isEmpty())
			listValues.put("Inversion Time", deleteDuplicate(listSlice[2], false));
		listSlice[2] = listValues.get("Inversion Time");
		
//		System.out.println(this + "Image Position 1: " + listSlice[5]);
		listValues.put("Slice Orientation", deleteDuplicate(listSlice[3], false));
		listValues.put("Image Orientation", deleteDuplicate(listSlice[4], false));
		listValues.put("Image Position", deleteDuplicate(listSlice[5], false));
		listValues.put("Number Of Diffusion", deleteDuplicate(listSlice[6], true));
		listValues.put("Image Type", deleteDuplicate(listSlice[7], false));
		listValues.put("Slice Position", deleteDuplicate(listSlice[8], true));
		listValues.put("Image Position Patient", deleteDuplicate(listSlice[9], true));
		listValues.put("Image Orientation Patient", deleteDuplicate(listSlice[10], true));
		listValues.put("Number Of Repetition", deleteDuplicate(listSlice[11], true));
		listValues.put("Scanning Sequence", deleteDuplicate(listSlice[12], false));
		listValues.put("Label Type (ASL)", deleteDuplicate(listSlice[13], false));
		listValues.put("Rescale Intercept", listSlice[14]);
		listValues.put("Rescale Slope", listSlice[15]);
		listValues.put("Scale Slope", listSlice[16]);
		listValues.put("B-values effective", listSlice[6]);
//		System.out.println(this + "Bval: " + listValues.get("B-values effective"));

		try {
//			listValues.put("Direction Diffusion", deleteDuplicateBy3(listSlice[17]));
			listValues.put("Direction Diffusion", reductListDiffusion(listSlice[6], listSlice[17]));
		} catch (Exception e) {
			listValues.put("Direction Diffusion", "");
		}

//		reductListDiffusion(listSlice[6], listSlice[17]);

		File fileDcm = new File(hmSeq.get(noSeq)[0]);
		File fileDcmEnd = new File(hmSeq.get(noSeq)[hmSeq.get(noSeq).length - 1]);
		String path = fileDcm.getParent();
		String firstDcm = fileDcm.getName();
		String lastDcm = fileDcmEnd.getAbsolutePath();
		lastDcm = lastDcm.substring(lastDcm.indexOf(path) + path.length() + 1);
		if (!firstDcm.contentEquals(lastDcm))
			try {
				lastDcm = " - " + lastDcm;
			} catch (Exception e) {
				lastDcm = "";
			}
		else
			lastDcm = "";

		listValues.put("noSeq", noSeq);
		listValues.put("File path", path);
		listValues.put("File Name", firstDcm + lastDcm);
		listValues.put("File Size (Mo)", String.valueOf(fileDcm.length() / (1024 * 1024.0)));

		listValues = new DicomDictionaryAdjustement().valuesAdjustement(listValues);

		hmInfo.put(noSeq, listValues);
		
//		System.out.println(this);
//		System.out.println(Arrays.asList(listValues));
//		System.out.println(Collections.singletonList(listValues));

		new ListOrderImage(noSeq, listSlice);
	}

	private String deleteDuplicate(String elements, Boolean canZero) {

		String resul = "";
		String[] list = null;
		String car = (elements.contains(";")) ? " ; " : " ";

		list = elements.split(car);

		List<String> array = Arrays.asList(list);
		Set<String> hs = new LinkedHashSet<>(array);
		list = Arrays.copyOf(hs.toArray(), hs.toArray().length, String[].class);

		for (String hh : list) {
			if (canZero)
				resul += hh + car;
			else if (!hh.contentEquals("0"))
				resul += hh + car;
		}

		return resul.trim();
	}

//	private String deleteDuplicateBy4(String elements) {
//
//		String resul = "";
//
//		if (!elements.isEmpty()) {
//			String tmp = "";
//
//			String[] list = elements.split(" +");
//
//			resul = list[0] + " " + list[1] + " " + list[2] + " " + list[3];
//
//			for (int i = 1; i < list.length / 4; i++) {
//				tmp = list[4 * i] + " " + list[4 * i + 1] + " " + list[4 * i + 2] + " " + list[4 * i + 3];
//				if (!resul.contains(tmp))
//					resul += " " + tmp;
//			}
//		}
//		return resul;
//	}
//
//	private String deleteDuplicateBy3(String elements) {
//
//		String resul = "";
//
//		if (!elements.isEmpty()) {
//			String tmp = "";
//
//			String[] list = elements.split(" +");
//
//			resul = list[0] + " " + list[1] + " " + list[2];
//
//			for (int i = 1; i < list.length / 3; i++) {
//				tmp = list[3 * i] + " " + list[3 * i + 1] + " " + list[3 * i + 2];
//				if (!resul.contains(tmp))
//					resul += " " + tmp;
//			}
//		}
//		return resul;
//	}

	private String reductListDiffusion(String bvals, String diffdir) {
		String newlistdiff = "", newlistbvals = "", tmplist = "";
		if (!diffdir.isEmpty()) {
			String tmp = "";
			String[] listdiff = diffdir.split(" +");
			String[] listbvals = bvals.split(" +");
			newlistdiff = listdiff[0] + " " + listdiff[1] + " " + listdiff[2];
			newlistbvals = listbvals[0];
			tmplist = newlistdiff + " " + newlistbvals;

			for (int i = 1; i < listdiff.length / 3; i++) {
				tmp = listdiff[3 * i] + " " + listdiff[3 * i + 1] + " " + listdiff[3 * i + 2];
//				if (!(tmp+" "+listbvals[i]).contains(tmplist)) {
				if (!tmplist.contains(tmp+" "+listbvals[i])) {
					newlistdiff += " " + tmp;
					newlistbvals += " " + listbvals[i];
					tmplist += " " + tmp + " " + listbvals[i];
				}
			}
		}
		newlistdiff = newlistdiff.replace('E', 'e');
		return newlistdiff;
	}

	public int getArrayIndex(int[] arr, int value) {
		int k = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {
				k = i;
				break;
			}
		}
		return k;
	}
}