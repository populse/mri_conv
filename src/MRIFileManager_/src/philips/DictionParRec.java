package philips;

public interface DictionParRec {
	
	String[] listType = { "M", "R", "I", "P", "CR", "T0", "T1", "T2", "RHO", "SPECTRO", "DERIVED", "ADC",
			"RCBV", "RCBF", "MTT", "TTP", "NC1", "NC2", "NC3", "ASLCBF" }; // "list of possible values for image_type_mr "
	// = 0  "M"
	// = 1  "R"
	// = 2  "I"
	// = 3  "P"
	// = 4  "CR"
	// = 5  "T0"
	// = 6  "T1"
	// = 7  "T2"
	// = 8  "RHO"
	// = 9  "SPECTRO"
	// = 10 "DERIVED"
	// = 11 "ADC"
	// = 12 "RCBV"
	// = 13 "RCBF"
	// = 14 "MTT"
	// = 15 "TTP"
	// = 16 "NC1"
	// = 17 "NC2"
	// = 18 "NC3"
	
	String[] listScanSeq = {"IR","SE","FFE","DERIVED","PCA","UNSPECIFIED","SPECTRO","SI","Unknow1","Unknow2"}; // "list of possible values for scanning sequence "
	// = 0   "IR"
	// = 1   "SE"
	// = 2   "FFE"
	// = 3   "DERIVED"
	// = 4   "PCA"
	// = 5   "UNSPECIFIED"
	// = 6   "SPECTRO"
	// = 7   "SI"
	// = 8	 "unknow"
	// = 9   "unknow"
	
	String[] listLabelPar = {"CONTROL","LABEL"};
	
	String[] listLabelXml = {"-","CONTROL","LABEL"}; // list of possible values for Label Type Xml/Rec
	
	// acquired Sequences = [0,1,2,6,7];
	// calculated Sequences [all others]
	
	String[][] imageInformationParRec = {
			// since V4
			{"slice number" 							, "0"},
			{"echo number" 								, "1"},
			{"dynamic scan number" 						, "2"},
			{"cardiac phase number" 					, "3"},
			{"image_type_mr" 							, "4"},
			{"scanning sequence" 						, "5"},
			{"index in REC file" 						, "6"},
			{"image pixel size"       					, "7"},
			{"scan percentage"                  		, "8"},
			{"recon resolution"           				, "9"}, //up 10 (ind = 9)
			{"rescale intercept"                		, "11"},
			{"rescale slope"                    		, "12"},
			{"scale slope"                      		, "13"},
			{"window center"                   			, "14"},
			{"window width"                     		, "15"},
			{"image angulation"							, "16"}, // up 18 (ind = 15)
			{"image offcentre"							, "19"}, // up 21 (ind = 16)
			{"slice thickness" 							, "22"},
			{"slice gap" 								, "23"},
			{"image_display_orientation" 				, "24"},
			{"slice orientation" 						, "25"},
			{"fmri_status_indication" 					, "26"},
			{"image_type_ed_es" 						, "27"},
			{"pixel spacing" 							, "28"}, // up 29 (ind = 23)
			{"echo_time" 								, "30"},
			{"dyn_scan_begin_time" 						, "31"},
			{"trigger_time" 							, "32"},
			{"diffusion_b_factor" 						, "33"},
			{"number of averages" 						, "34"},
			{"image_flip_angle" 						, "35"},
			{"cardiac frequency" 						, "36"},
			{"minimum RR-interval" 						, "37"},
			{"maximum RR-interval" 						, "38"},
			{"TURBO factor" 							, "39"},
			{"Inversion delay" 							, "40"},
			//since V4.1
			{"diffusion b value number" 				, "41"},
			{"gradient orientation number" 				, "42"},
			{"contrast type" 							, "43"},
			{"diffusion anisotropy type" 				, "44"},
			{"diffusion" 								, "45"}, //up 47 (ind = 39)
			//since v4.2
			{"label type" 								, "48"},
	};
	
	String[][] imageInformationXmlRec = {
			{"Slice"                      , "0"}, 
			{"Echo"                       , "1"},
			{"Dynamic"                    , "2"},
			{"Phase"                      , "3"},
			{"BValue"                     , "4"},
			{"Grad Orient"                , "5"},
			{"Label Type"                 , "6"},
			{"Type"                       , "7"},
			{"Sequence"                   , "8"},
			{"Index"                      , "9"}, 
			{"Pixel Size"                 , "10"},
			{"Scan Percentage"            , "11"},
			{"Resolution X"               , "12"},
			{"Resolution Y"               , "13"},
			{"Rescale Intercept"          , "14"},
			{"Rescale Slope"              , "15"},
			{"Scale Slope"                , "16"},
			{"Window Center"              , "17"},
			{"Window Width"               , "18"},
			{"Slice Thickness"            , "19"},
			{"Slice Gap"                  , "20"},
			{"Display Orientation"        , "21"},
			{"fMRI Status Indication"     , "22"},
			{"Image Type Ed Es"           , "23"},
			{"Pixel Spacing"	          , "24"},
			{"Pixel Spacing (y)"          , "25"},
			{"Echo Time"                  , "26"},
			{"Dyn Scan Begin Time"        , "27"},
			{"Trigger Time"               , "28"},
			{"Diffusion B Factor"         , "29"},
			{"No Averages"                , "30"},
			{"Image Flip Angle"           , "31"},
			{"Cardiac Frequency"          , "32"},
			{"Min RR Interval"            , "33"},
			{"Max RR Interval"            , "34"},
			{"TURBO Factor"               , "35"},
			{"Inversion Delay"            , "36"},
			{"Contrast Type"              , "37"},
			{"Diffusion Anisotropy Type"  , "38"},
			{"Diffusion AP"               , "39"},
			{"Diffusion FH"               , "40"},
			{"Diffusion RL"               , "41"},
			{"Angulation AP (Image_Info)" , "42"},
			{"Angulation FH (Image_Info)" , "43"},
			{"Angulation RL (Image_Info)" , "44"}, 
			{"Offcenter AP"               , "45"}, 
			{"Offcenter FH"               , "46"},
			{"Offcenter RL"               , "47"},
			{"Slice Orientation"          , "48"},
			{"Image Planar Configuration" , "49"},
			{"Samples Per Pixel"          , "50"},
	};
}