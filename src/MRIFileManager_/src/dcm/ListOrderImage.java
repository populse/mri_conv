package dcm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import abstractClass.ParamMRI2;

public class ListOrderImage implements ParamMRI2 {

	public ListOrderImage(String noSeq, String[] listSlice) {

//		 System.out.println(noSeq);
//		 System.out.println("list TE : "+ listSlice[0].substring(0, listSlice[0].length()));
//		 System.out.println("list TR : "+ listSlice[1].substring(0, listSlice[1].length()));
//		 System.out.println("list IR : "+ listSlice[2].substring(0, listSlice[2].length()));
//		 System.out.println("list Diff : "+ listSlice[6].substring(0, listSlice[6].length()));
//		 System.out.println("list Type : "+ listSlice[7].substring(0, listSlice[7].length()));
//		 System.out.println("list Slice location : "+ listSlice[8].substring(0, listSlice[8].length()));
//		 System.out.println("list Temporal Posit : "+ listSlice[11].substring(0, listSlice[11].length()));
//		 System.out.println("list Scan seq : "+ listSlice[12].substring(0, listSlice[12].length()));
//		 System.out.println("list Label Type : "+ listSlice[13].substring(0, listSlice[13].length()));

		String[] s1 = new String[9];
		s1[0] = listSlice[0].toString().trim(); // TE
		s1[1] = listSlice[1].toString().trim(); // TR
		s1[2] = listSlice[2].toString().trim(); // IR
		s1[3] = listSlice[6].toString().trim(); // Diff
		s1[4] = listSlice[7].toString().trim(); // Type
		s1[5] = listSlice[8].toString().trim(); // Slice Location
		s1[6] = listSlice[11].toString().trim(); // Repetition
		s1[7] = listSlice[12].toString().trim(); // Scan seq
		s1[8] = listSlice[13].toString().trim(); // Label Type
		
		String[] s2 = s1.clone();

		Set<String> uniqueWords;
		int cnt = 0;
		for (int i = 0; i < s1.length; i++) {
			uniqueWords = new HashSet<>(Arrays.asList(s1[i].split(" +")));
			s1[i] = String.valueOf(uniqueWords.size());
			if (uniqueWords.size() > 1 || (i == 5 && uniqueWords.size() == 1))
				cnt++;
		}
		
//		System.out.println(this + " s1 : " + Arrays.toString(s1));

		String ord = "";
		try {
			for (int i = 0; i < s2[0].split(" +").length - 1; i++) {
				Boolean find = false;
				for (int j = 0; j < s2.length; j++) {

					String[] ll = s2[j].split(" +");
					if (ll.length > 1)
						if (!ll[i].trim().contentEquals(ll[i + 1].trim()))
							if (!ord.contains(String.valueOf(j))) {
								ord += String.valueOf(j) + " ";
								find = true;
							}
				}

				if (!find && !ord.contains("5"))
					ord += "5 ";

				if (ord.split(" +").length == cnt) {
					ord = ord.trim();
					break;
				}
			}
		} catch (Exception e) {
		}

//		System.out.println("ord = " + ord + ", cnt = " + cnt+" , s1[3] = "+s1[3]);

		if (s1[5].split(" +").length < 2)
			s1[5] = hmInfo.get(noSeq).get("Number Of Slice");

//		int NImage, NSlice, NEcho, NRepetition, NTR, NIR, Ndiff, Ntype, NLabel, NScanSeq;
//
//		NImage = Integer.parseInt(hmInfo.get(noSeq).get("Images In Acquisition"));
//		NSlice = Integer.parseInt(hmInfo.get(noSeq).get("Number Of Slice"));
//		NEcho = Integer.parseInt(hmInfo.get(noSeq).get("Number Of Echo"));
//		NRepetition = Integer.parseInt(hmInfo.get(noSeq).get("Number Of Repetition"));
//		NTR = hmInfo.get(noSeq).get("Repetition Time").split(" +").length;
//		NIR = hmInfo.get(noSeq).get("Inversion Time").split(" +").length;
//		Ndiff = Integer.parseInt(hmInfo.get(noSeq).get("Number Of Diffusion"));
//		Ntype = hmInfo.get(noSeq).get("Image Type").split(" ; ").length;
//		NLabel = hmInfo.get(noSeq).get("Label Type (ASL)").split(" +").length;
//		NScanSeq = hmInfo.get(noSeq).get("Scanning Sequence").split(" +").length;
//
//		if (NImage > NSlice * NEcho * NRepetition * NTR * NIR * Ndiff * Ntype * NLabel * NScanSeq)
//			NRepetition = NImage / (NSlice * NEcho * NTR * NIR * Ndiff * Ntype);

		// System.out.print("seq n :"+noSeq+" : ");
		// System.out.println(NImage + " , " + NSlice + " , " + NEcho + " , " +
		// NRepetition + " , " + NTR + " , " + NIR
		// + " , " + Ndiff + " , " + Ntype + " , " + NLabel + " , " + NScanSeq);

		int c, z, t;
		String order = "xyczt(Default)";

//		c = Ntype * Ndiff * NLabel * NScanSeq;
//		z = NSlice;
//		t = NEcho * NRepetition * NTR * NIR;

		int Nimage = Integer.parseInt(hmInfo.get(noSeq).get("Images In Acquisition"));

//		c = Integer.parseInt(s1[4]) * Integer.parseInt(s1[3]) * Integer.parseInt(s1[7]) * Integer.parseInt(s1[8]);
//		z = Integer.parseInt(s1[5]);
//		t = Integer.parseInt(s1[0]) * Integer.parseInt(s1[6]) * Integer.parseInt(s1[1]) * Integer.parseInt(s1[2]);
		
		c = Integer.parseInt(s1[3]) * Integer.parseInt(s1[4]) * Integer.parseInt(s1[8]);
		z = Integer.parseInt(s1[5]);
		t = Integer.parseInt(s1[0]) * Integer.parseInt(s1[1]) * Integer.parseInt(s1[2]) * Integer.parseInt(s1[6]) ;
		
//		System.out.println(this + " c, z, t :" + c + ", " + z + ", " + t);

//		if (Nimage > c*z*t) {
//			int Nrepetition = Nimage/(c*z*t);
//			t = Integer.parseInt(s1[0]) * Nrepetition * Integer.parseInt(s1[1]) * Integer.parseInt(s1[2]); 
//		}

		if (Nimage != c * z * t) {
			c = 1;
			t = Nimage / z;
		}

		String manuf = hmInfo.get(noSeq).get("Manufacturer");

		// order = "xyczt(Default)";
		// order = "xytzc";
		// order = "xyctz";
		// order = "xytcz";
		// order = "xyzct";
		// order = "xyztc";
		
		if (manuf.toLowerCase().contains("philips"))

			if (ord.contentEquals("0 5 5") || ord.contentEquals("0 5 4"))
				order = "xytzc";
			else if (hmInfo.get(noSeq).get("Scan Mode").contains("2"))
				order = "xyctz"; //??????
//				order = "xytzc"; //??????
			else if (c > 1 && z > 1 && t > 1)
				order = "xytzc";
			else
				order = "xytzc";
//				order = "xyztc";

		if (manuf.toLowerCase().contains("siemens")) {
			try {
				if (listSlice[8].split(" +")[0].contentEquals(listSlice[8].split(" +")[1]))
					order = "xyctz";
				else
					order = "xyczt";
			} catch (Exception e) {

			}
		}
		
		Object[] listIo = new Object[4];

		listIo[0] = order;
		listIo[1] = c;
		listIo[2] = z;
		listIo[3] = t;
		hmOrderImage.put(noSeq, listIo);
//		System.out.println(this + " order : " + Arrays.toString(listIo));
	}
}