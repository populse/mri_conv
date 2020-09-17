package abstractClass;

import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;

public interface ParamMRI2 {

	/*******************************
	 * header table
	 *******************************************************/
	public final String[] headerListData = { "Format", "Directory/File", "Patient Name", "Study Name", "Creation Date",
			"Patient Sex", "Patient Weight", "Patient BirthDate", "Note" };
	public final String[] headerListSeq = { "Seq. No.", "Serial Number", "Protocol", "Sequence Name",
			"Acquisition Time", "Acquisition Date", "Creation Date", "Scan Mode", "Echo Time", "Repetition Time",
			"Inversion Time", "Images In Acquisition", "Slice Orientation", "Flip Angle" };

	/*******************************
	 * Dictionnary MRI
	 ****************************************************************/
	public HashMap<String, HashMap<String, String>> dictionaryMRISystem = new HashMap<>();
	// (key#1 : value#1) = (labelMRI , (key#2 : value#2) = (tags 'where' yaml :
	// value))

	public HashMap<String, HashMap<String, String>> dictionaryMRIUser = new HashMap<>();
	// (key#1 : value#1) = (labelMRI , (key#2 : value#2) = (tags 'where' yaml :
	// value))

	public HashMap<String, HashMap<String, String>> dictionaryJsonSystem = new HashMap<>();
	// (key#1 : value#1) = (labelMRI , (key#2 : value#2) = (tag 'json' yaml :
	// value))

	public HashMap<String, HashMap<String, String>> dictionaryJsonUser = new HashMap<>();
	// (key#1 : value#1) = (labelMRI , (key#2 : value#2) = (tag 'json' yaml :
	// value))

	/*******************************
	 * for Information viewer
	 ***********************************************************/
	public HashMap<String, List<String>> listParamInfoSystem = new HashMap<>();
	// (key : value) = (category , list of label MRI) DictionaryMRI_system.yml

	public HashMap<String, List<String>> listParamInfoUser = new HashMap<>();
	// (key : value) = (category , list of label MRI) DictionaryMRI_user.yml

	/*******************************
	 * HashMap for each sequence
	 ******************************************************/
	public HashMap<String, String> hmData = new HashMap<>(); // only for Dicom , associate listData with path of
																// DicomDir or DirFile
	public HashMap<String, String> hmTagDicom = new HashMap<>(); // only for Dicom, record all header of Dicom
	public HashMap<String, String[]> hmSeq = new HashMap<>(); // listSeq for data selected (key,value)=(no. Seq ,
																// pathFile)
	public HashMap<String, HashMap<String, String>> hmInfo = new HashMap<>();
	// (key#1 : value#1) = (no seq , (key#2 : value#2) =(labelMRI : value))
	public HashMap<String, Object[]> hmOrderImage = new HashMap<>(); // order stack image (key,value)=(no Seq ,
																		// {xyczt,c,z,t,info})

	/*******************************
	 * List basket
	 ********************************************************************/
	public DefaultListModel<String> listinBasket = new DefaultListModel<>();
	public HashMap<String, String[]> listBasket_hmSeq = new HashMap<>(); // (key,value)=(listinBasket ,list hmSeq)
	public HashMap<String, HashMap<String, String>> listBasket_hmInfo = new HashMap<>(); // (key,value)=(listinBasket
																							// ,list#2 hmInfo)
	public HashMap<String, Object[]> listBasket_hmOrderImage = new HashMap<>(); // (key,value)=(listinBasket
																				// ,listParamInfo)

	/*******************************
	 * List process
	 ********************************************************************/
	public String[] listProcessLabel = { "T1map", "T2map", "TImap", "T1map_FA", "pCasl" };
}