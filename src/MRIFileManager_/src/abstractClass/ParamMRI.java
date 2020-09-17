package abstractClass;

import java.util.HashMap;

import javax.swing.DefaultListModel;

public interface ParamMRI {
	
	public final String[][] List_Param_IRM = { 
			//MRI parameters			,	type		 ,	format
		{"Acquisition Date"				,	"string"	},//("yyyy.mm.dd - hh:mm:ss")
		{"Acquisition Time"				,	"string"	},//("hh:mm:ss")
		{"Bandwith"						,	"float"		},//(float)
		{"Byte Order"					,	"string"	},//(string)
		{"Coil"							,	"string"	},//(string)
		{"Creation Date"				,	"string"	},//(string)
		{"Data Type"					,	"string"	},//("8-bits-int-unsigned","8-bits-int-signed","16-bits-float-unsigned",.....)
		{"Echo Time"					,	"float"		},//(array of float)
		{"Flip Angle"					,	"float"		},//(float)
		{"FOV"							,	"float"		},//(float (x,y) if 2D, (x,y,z) if 3D)
		{"FOV Units"					,	"string"	},//(mm,mm)
		{"Images In Acquisition"		,	"int"		},//(integer, number total of image)
		{"Image Orientation Patient"	,	"float"		},//(float)
		{"Image Position Patient"		,	"float"		},//(float)
		{"Image Type"					,	"string"	},//(M,R,I,P,CR,T0,T1,T2,RHO,SPECTRO,DERIVED,ADC,RCBV,RCBF,MTT,TTP,......)
		{"Imaging Frequency"			,	"float"		},//(float)
		{"Imaged Nucleus"				,	"string"	},//("1H",...)
		{"Inversion Time"				,	"float"		},//(array of float)
		{"Manufacturer"					,	"string"	},//(string)
		{"Number Of Averages"			,	"int"		},//(integer)
		{"Number Of Diffusion"			,	"int"		},//(integer)
		{"Number Of Echo"				,	"int"		},//(integer)
		{"Number Of Image Type"			,	"int"		},//(integer)
		{"Number Of Repetition"			,	"int"		},//(integer)
		{"Number Of Slice"				,	"int"		},//(integer)
		{"Patient BirthDate"			,	"string"	},//("yyyy.mm.dd")
		{"Patient ID"					,	"string"	},//(string)
		{"Patient Name"					,	"string"	},//(string)
		{"Patient Position"				,	"string"	},//("HFS","HFP")
		{"Patient Sex"					,	"string"	},//(string)
		{"Patient Weight"				,	"float"		},//(float)
		{"Pcasl"						,	"float"		},//(array of float)
		{"Phase Direction"				,	"string"	},//("ap","fh",rl")
		{"Pixel Bandwidth"				,	"float"		},//(float)
		{"Pixel Spacing"				,	"float"		},//(float (x,y) if 2D (x,y,z) if 3D)
		{"Protocol"						,	"string"	},//(string)
		{"Read Direction"				,	"string"	},//("A-P","F-H",R-L")
		{"Repetition Time"				,	"float"		},//(array of float)
		{"Scan Mode 2D/3D"				,	"string"	},//("2D, 3D, MR ....")
		{"Scan Resolution"				,	"float"		},//(float (x,y))
		{"Sequence Name"				,	"string"	},//(string)
		{"Session"						,	"string"	},//(string)
		{"Slice Gap"					,	"float"		},//(float)
		{"Slice Orientation"			,	"string"	},//(axial coronal sagittal)
		{"Slice Position"				,	"float"		},//(array of float)
		{"Slice Separation"				,	"float"		},//(float)
		{"Slice Thickness"				,	"float"		},//(float)
		{"Software Version"				,	"string"	},//(string)
		{"Spatial Resolution"			,	"float"		},//(float (x,y) if 2D (x,y,z) if 3D)
		{"Station Name"					,	"string"	},//(string)
		{"Study Name"					,	"string"	},//(string)
			
};	
	
	public final String[][] List_Param_bruker = {
			{"Acquisition Date"				,	"##$VisuAcqDate="				,	"visu_pars"		},	// (1)
			{"Acquisition Time"				,	"##$VisuAcqScanTime="			,	"visu_pars"		},	// (2)
			{"Bandwith"						,	"##$SW_h="						,	"acqp"			},
			{"Byte Order"					,	"##$VisuCoreByteOrder="			,	"visu_pars"		},
			{"Coil"							,	"##$ACQ_operation_mode"			,	"acqp"			},
			{"Creation Date"				,	"##$SUBJECT_date="				,	"subject"		},	
			{"Data Type"					,	"##$VisuCoreWordType=" 			, 	"visu_pars"		},	// (5)
			{"Echo Time"					,	"##$ACQ_echo_time="				,	"acqp"			},  
			{"Flip Angle"					,	"##$ACQ_flip_angle="			,	"acqp"			},  
			{"FOV"							,	"##$RECO_fov="					,	"reco"			},  // or ##$VisuCoreExtent= in visu_pars ?
			{"FOV Units"					,	"##$VisuCoreUnits="				,	"visu_pars"		},	
			{"Images In Acquisition"		,	"##$VisuCoreFrameCount=" 		, 	"visu_pars"		}, 	
//			{"Image Orientation Patient"	,	"##$VisuCoreOrientation="		,	"visu_pars"		}, 	
//			{"Image Position Patient"		,	"##$VisuCorePosition="			,	"visu_pars"		},	
			{"Image Type"					,	"##$VisuCoreFrameType="			,	"visu_pars"		},  // (14)
			{"Imaging Frequency"			,	"##$VisuAcqImagingFrequency="	,	"visu_pars"		},  
			{"Imaged Nucleus"				,	"##$NUCLEUS="					,	"acqp"			},  
			{"Inversion Time"				,	"##$ACQ_inversion_time="		,	"visu_pars"		},  
			{"Manufacturer"					,	"##ORIGIN="						,	"method"		},	
			{"Number Of Averages"			,	"##$VisuAcqNumberOfAverages="	,	"visu_pars"		},  
			{"Number Of Echo"				,	"##$PVM_NEchoImages="			,	"method"		},	
			{"Number Of Repetition"			,	"##$PVM_NRepetitions="			,	"method"		},	
			{"Number Of Slice"				,	"##$NSLICES"					,	"acqp"			},  
			{"Patient BirthDate"			,	"##$SUBJECT_dbirth="			,	"subject"		}, 	
			{"Patient ID"					,	"##$SUBJECT_id="				,	"subject"		}, 	
			{"Patient Name"					,	"##$SUBJECT_name_string="		,	"subject"		},
			{"Patient Position"				,	"##$VisuSubjectPosition="		,	"visu_pars"		}, 	
			{"Patient Sex"					,	"##$SUBJECT_sex="				,	"subject"		}, 	
			{"Patient Weight"				,	"##$SUBJECT_weight="			,	"subject"		}, 	
//			{"Pcasl"						,	"##$PCASL_PhiSwList="			,	"method"		},
			{"Pixel Bandwidth"				,	"##$VisuAcqPixelBandwidth="		,	"visu_pars"		},  
			{"Pixel Spacing"				,	"##$PVM_SpatResol="				,	"method"		},  // (33)
			{"Protocol"						,	"##$ACQ_protocol_name="			,	"acqp"			},  
			{"Read Direction"				,	"##$PVM_SPackArrReadOrient="	,	"method"		},	// (35)
			{"Repetition Time"				,	"##$ACQ_repetition_time="		,	"acqp"			},
			{"Scan Mode"					,	"##$VisuCoreDim="				,	"visu_pars"		},  // (6)
			{"Scan Resolution"				,	"##$RECO_size="					,	"reco"			},  // or ##$VisuCoreSize in visu_pars ?
			{"Sequence Name"				,	"##$VisuAcqSequenceName="		,	"visu_pars"		},
			{"Session"						,	"##$ACQ_operator="				,	"acqp"			},
			{"Slice Orientation"			,	"##$PVM_SPackArrSliceOrient="	,	"method"		},	// (40)
			{"Slice Position"				,	"##$VisuCorePosition="			,	"visu_pars"		},
			{"Slice Separation"				,	"##$ACQ_slice_sepn="			,	"acqp"			},
			{"Slice Thickness"				,	"##$ACQ_slice_thick="			,	"acqp"			},
			{"Software Version"				,	"##$VisuCreatorVersion="		,	"visu_pars"		},
			{"Spatial Resolution"			,	"##$PVM_SpatResol="				,	"method"		},
			{"Station Name"					,	"##$ACQ_station="				,	"acqp"			},
			{"Study Name"					,	"##$SUBJECT_study_name="		,	"subject"		},
	};
	
public static final String[][] List_Param_philips = {
		{"Acquisition Time"				,	"Scan Duration [sec]"						,	"GENERAL INFORMATION"			,	"7"		,	"Scan Duration"				,	"Series_Info"	,	"8"		},
		{"Creation Date"				,	"Examination date/time"						,	"GENERAL INFORMATION"			,	"3"		,	"Examination date"			,	"Series_Info"	,	"3"		},
		{"Data Type"					,	"image pixel size (in bits)" 				, 	"IMAGE INFORMATION"				,	"7"		,	"Pixel Size" 				, 	"Image_info"	,	"10"	},
		{"Dimensions (2D/3D)"			,	"Scan mode"									,	"GENERAL INFORMATION"			,	"17"	,	"Scan mode"					,	"Series_Info"	,	"22"	},
		{"Echo Time"					,	"echo_time"									,	"IMAGE INFORMATION"				,	"30"	,	"Echo Time"					,	"Image_info"	,	"26"	},
		{"Flip Angle"					,	"image_flip_angle (in degrees)"				,	"IMAGE INFORMATION"				,	"35"	,	"Image Flip Angle"			,	"Image_info"	,	"31"	},
		{"FOV"							,	"FOV (ap,fh,rl) [mm]"						,	"GENERAL INFORMATION"			,	"19"	,	"FOV AP FH RL"				,	"Series_Info"	,	"24-26"	},
		{"Images In Acquisition"		,	"index in REC file (in images)" 			, 	"IMAGE INFORMATION"				,	"6"		,	"Index" 					, 	"Image_info"	,	"9"		},
		{"Image Orientation Patient"	,	"image angulation (ap,fh,rl in degrees )"	,	"IMAGE INFORMATION"				,	"16-18"	,	"Angulation AP FH RL"		,	"Image_info"	,	"42-44"	},
		{"Image Position Patient"		,	"image offcentre (ap,fh,rl in mm )"			,	"IMAGE INFORMATION"				,	"19-21" ,	"Offcenter AP FH RL"		,	"Image_info"	,	"45-47" },
		{"Image Type"					,	"image_type_mr"								,	"IMAGE INFORMATION"				,	"4"		,	"Type"						,	"Image_info"	,	"7"		},
		{"Inversion Time"				,	"Inversion delay (in ms)"					,	"IMAGE INFORMATION"				,	"40"	,	"Inversion Delay"			,	"Image_info"	,	"36"	},
		{"Number Of Averages"			,	"number of averages"						,	"IMAGE INFORMATION"				,	"34"	,	"No Averages "				,	"Image_info"	,	"30"	},
		{"Number Of Echo"				,	"echo number"								,	"IMAGE INFORMATION"				,	"1"		,	"Echo"						,	"Image_info"	,	"1"	},
		{"Number Of Diffusion"			,	"diffusion b value number"					,	"IMAGE INFORMATION"				,	"41"	,	"Diffusion B Factor"		,	"Image_info"	,	"29"	},
		{"Number Of Repetition"			,	"Max. number of dynamics"					,	"GENERAL INFORMATION"			,	"11"	,	"Max No Dynamics"			,	"Series_Info"	,	"12"	},
		{"Number Of Slice"				,	"Max. number of slices/locations"			,	"GENERAL INFORMATION"			,	"10"	,	"Max No Slices"				,	"Series_Info"	,	"11"	},
		{"Patient Name"					,	"Patient name"								,	"GENERAL INFORMATION"			,	"0"		,	"Patient Name"				,	"Series_Info"	,	"0"		},
		{"Patient Position"				,	"Patient position"							,	"GENERAL INFORMATION"			,	"13"	,	"Patient Position"			,	"Series_Info"	,	"17"	},
		{"Phase Direction"				,	"Preparation direction"						,	"GENERAL INFORMATION"			,	"14"	,	"Preparation Direction"		,	"Series_Info"	,	"18"	},
		{"Protocol"						,	"Protocol name"								,	"GENERAL INFORMATION"			,	"2"		,	"Protocol Name"				,	"Series_Info"	,	"2"		},
		{"Repetition Time"				,	"Repetition time [ms]"						,	"GENERAL INFORMATION"			,	"18"	,	"Repetition times"			,	"Series_Info"	,	"23"	},
		{"Scan Mode"					,	"Scan mode"									,	"GENERAL INFORMATION"			,	"17"	,	"Scan Mode"					,	"Series_Info"	,	"22"	},
		{"Scan Resolution"				,	"Scan resolution  (x, y)"					,	"IMAGE INFORMATION"				,	"9-10"	,	"Scan Resolution X Y"		,	"Image_info"	,	"12-13"	},
		{"Sequence Name"				,	"Technique"									,	"GENERAL INFORMATION"			,	"15"	,	"Technique"					,	"Series_Info"	,	"19"	},
		{"Slice Gap"					,	"slice gap (in mm )"						,	"IMAGE INFORMATION"				,	"23"	,	"Slice Gap"					,	"Image_info"	,	"20"	},
		{"Slice Orientation"			,	"slice orientation ( TRA/SAG/COR )"			,	"IMAGE INFORMATION"				,	"25"	,	"Slice Orientation"			,	"Image_info"	,	"48"	},
		{"Slice Thickness"				,	"slice thickness (in mm )"					,	"IMAGE INFORMATION"				,	"22"	,	"Slice Thickness"			,	"Image_info"	,	"19"	}, // must be added with Slice Gap
		{"Spatial Resolution"			,	"pixel spacing (x,y) (in mm)"				,	"IMAGE INFORMATION"				,	"28-29"	,	"Pixel Spacing"				,	"Image_info"	,	"24-25"	},
		{"Study Name"					,	"Examination name"							,	"GENERAL INFORMATION"			,	"1"		,	"Examination Name"			,	"Series_Info"	,	"1"		},
	};
		
	public final String[][] List_Param_Nifti = {
		{"Acquisition Date"				,	"json"	,	"SeriesDate"			},
		{"Acquisition Time"				,	"json"	,	"AcquisitionTime"		},
		{"Bandwith"						,	"json"	,	"Bandwith"				},
		{"Byte Order"					,	"nifti"	,	"Endianness"			},
		{"Coil"							,	"json"	,	"Coil"					},
		{"Creation Date"				,	"json"	,	"StudyDate"				},
		{"Data Type"					,	"nifti"	,	"Datatype"				},
		{"Echo Time"					,	"json"	,	"EchoTime"				},
		{"Flip Angle"					,	"json"	,	"FlipAngle"				},
		{"FOV"							,	"json"	,	"FOV"					},	
		{"Image Type"					,	"json"	,	"ImageType"				},
		{"Images In Acquisition"		,	"json"	,	"ImagesInAcquisition"	},
		{"Imaging Frequency"			,	"json"	,	"ImagingFrequency"		},
		{"Imaged Nucleus"				,	"json"	,	"ImagedNucleus"			},
		{"Inversion Time"				,	"json"	,	"InversionTime"			},
		{"Manufacturer"					,	"json"	,	"Manufacturer"			},
		{"Number Of Averages"			,	"json"	,	"NumberOfAverages"		},
		{"Number Of Echo"				,	"json"	,	"NumberOfEcho"			},	
		{"Number Of Repetition"			,	"json"	,	"NumberOfRepetition"	},	
		{"Number Of Slice"				,	"nifti"	,	"Dataset dimensions (Count, X,Y,Z,T...):"	},
		{"Patient BirthDate"			,	"json"	,	"PatientBirthDate"		},
		{"Patient ID"					,	"json"	,	"PatientID"       		},
		{"Patient Name"					,	"json"	,	"PatientName"     		},
		{"Patient Position"				,	"json"	,	"PatientPosition" 		},
		{"Patient Sex"					,	"json"	,	"PatientSex"      		},
		{"Patient Weight"				,	"json"	,	"PatientWeight"   		},
		{"Pcasl"						,	"json"	,	"Pcasl"					},
//		{"Phase Direction"				,	"json"	,	""				},
		{"Pixel Bandwidth"				,	"nifti"	,	"Bits per voxel"},
		{"Pixel Spacing"				,	"nifti"	,	"Grid spacings (X,Y,Z,T,...)"	},
		{"Protocol"						,	"json"	,	"ProtocolName"			},
		{"Read Direction"				,	"json"	,	"ReadDirection"			},
		{"Repetition Time"				,	"json"	,	"RepetitionTime"		},
		{"Scan Mode"					,	"json"	,	""				},
		{"Scan Resolution"				,	"nifti"	,	"Dataset dimensions (Count, X,Y,Z,T...):"	},
		{"Sequence Name"				,	"json"	,	"SequenceName"			},
		{"Session"						,	"json"	,	"Session"				},
//		{"Slice Gap"					,	"json"	,	""				},
		{"Slice Orientation"			,	"json"	,	"SliceOrientation"		},
//		{"Slice Position"				,	"json"	,	""				},
		{"Slice Separation"				,	"json"	,	"SpacingBetweenSlices"	},
		{"Slice Thickness"				,	"json"	,	"SliceThickness"		},
		{"Software Version"				,	"json"	,	"SoftwareVersions"				},
		{"Spatial Resolution"			,	"nifti"	,	"Grid spacings"	},
		{"Station Name"					,	"json"	,	"StationName"				},
//		{"Study Name"					,	"json"	,	""				},
	};
	
	public final String[][] List_Param_json = {
			{"Acquisition Date"				,	"SeriesDate"	            ,	"string"	},
			{"Acquisition Time"				,	"AcquisitionTime"           ,	"string"	},
			{"Anatomical Orientation Type" 	,	"AnatomicalOrientationType" ,	"string"	},
//			{"Byte Order"					,	                            },
			{"Creation Date"				,	"StudyDate"                 ,	"string"	},
//			{"Data Type"					,	                            },
//			{"Dimensions (2D/3D)"			,	                            },
			{"Echo Time"					,	"EchoTime"                  ,	"float"		},
			{"Echo Train Length"			,	"EchoTrainLength"	        ,	"int"		},
			{"Flip Angle"					,	"FlipAngle"                 ,	"float"		},
//			{"FOV"							,	                            },
//			{"FOV Units"					,	                            },
//			{"Images In Acquisition"		,	"ImagesInAcquisition"       ,	"int"		},
//			{"Image Orientation Patient"	,	"ImageOrientationPatient"	,	"float"     },
			{"Image Position Patient"		,	"ImagePositionPatient"      ,	"float"     },
//			{"Image Type"					,	"ImageType"                 },
			{"Imaging Frequency"			,	"ImagingFrequency"	        ,	"float"		},
			{"Imaged Nucleus"				,	"ImagedNucleus"             ,	"string"	},
//			{"Inversion Time"				,	                            },
			{"Manufacturer"					,	"Manufacturer"              ,	"string"	},
			{"Number Of Averages"			,	"NumberOfAverages"          ,	"int"		},
//			{"Number Of Echo"				,	                            },
//			{"Number Of Image Type"			,	                            },
//			{"Number Of Repetition"			,	                            },
//			{"Number Of Slice"				,	                            },
			{"Patient Birth Date"			,	"PatientBirthDate"          ,	"string"	},
			{"Patient ID"					,	"PatientID"                 ,	"string"	},
			{"Patient Name"					,	"PatientName"               ,	"string"	},
			{"Patient Position"				,	"PatientPosition"           ,	"string"	},
			{"Patient Sex"					,	"PatientSex"                ,	"string"	},
			{"Patient Weight"				,	"PatientWeight"             ,	"float"		},
//			{"Pcasl"						,	                            },
//			{"Phase Direction"				,	                            },
			{"Pixel Bandwidth"				,	"PixelBandwidth"            ,	"float"		},
//			{"Pixel Spacing"				,	                            },
			{"Protocol"						,	"ProtocolName" 		        ,	"string"	},
			{"Read Direction"				,   "ReadDirection"				,	"string"    },
			{"Repetition Time"				,	"RepetitionTime"            ,	"float"		},
//			{"Scan Resolution"				,	                            },
			{"Sequence Name"				,	"SequenceName"              ,	"string"	},
//			{"Slice Gap"					,	                            },
//			{"Slice Orientation"			,	                            },
//			{"Slice Position"				,	                            },
			{"Slice Separation"				,	"SpacingBetweenSlices"      ,	"float"		},
			{"Slice Thickness"				,	"SliceThickness"            ,	"float"		},
			{"Software Version"				,	"SoftwareVersions"          ,	"string"	},
			{"Station Name"					,	"StationName"               ,	"string"	},
//			{"Study Name"					,	                            },
			
	};
	
	/******************************* header table *******************************************************/
	
	public final String[] headerListData = {"Format","Directory/File","Patient Name","Study Name","Creation Date","Patient Sex","Patient Weight","Patient BirthDate"};
	public final String[] headerListSeq 	= {"Seq. n�","Protocol","Sequence Name","Acquisition Time","Acquisition Date","Scan Mode","Echo Time","Repetition Time","Inversion Time","Slice Orientation","Flip Angle"};

	/******************************* HashMap for each sequence ******************************************************/
	
	public HashMap<String, String[]> hmSeq = new HashMap<>(); // listSeq for data selected (key,value)=(n� Seq , pathFile)
	public HashMap<String, HashMap<String,String[]>> hmInfo = new HashMap<>(); // list of value corresponding to Info General and Info User Param(key,value)=(n� Seq ,listParamInfoGeneral)
	public HashMap<String, Object[]> hmOrderImage = new HashMap<>(); // order stack image (key,value)=(n� Seq , {xyczt,c,z,t,info})
		
	/******************************* Dictionnary MRI ****************************************************************/
	public HashMap<String, String[]> dictionaryParamMRI = new HashMap<>(); //
	
	/******************************* for Information Tree ***********************************************************/
	public HashMap<String,String[]> listParamInfoGeneral = new HashMap<String,String[]>(){
				private static final long serialVersionUID = 1L;
				{
				 put(	"File Info"			,	new String[] {	"File path","File Name","File Size (Mo)"});
					
				 put(	"Dimensions"		,	new String[] {	"Scan Mode","Scan Resolution","Images In Acquisition","Byte Order","Data Type",
						 										"Number Of Slice","Number Of Echo","Number Of Repetition","Number Of Diffusion"});
				 put(	"MRI parameters"	,	new String[] {	"Echo Time","Repetition Time","Inversion Time","Number Of Averages","FOV","Slice Thickness",
						 										"Slice Separation","Spatial Resolution","Slice Orientation","Read Direction","Image Type",
						 										"Patient Position","Bandwith","Protocol","Sequence Name","Acquisition Time","Flip Angle",
						 										"Imaged Nucleus"});
				 put(	"Equipment"			,	new String[] {	"Software Version","Acquisition Date","Station Name","Coil","Session"});
				}
	};
		
	public HashMap<String,String[]> listParamInfoUser = new HashMap<>();
				
	/******************************* List basket ********************************************************************/
	public DefaultListModel<String> listinBasket = new DefaultListModel<>();
	public HashMap<String, HashMap<String,String[]>> listBasket_hms= new HashMap<>(); //(key,value)=(listinBasket ,listParamInfo)
	public HashMap<String, Object[]> listBasket_hmo= new HashMap<>(); //(key,value)=(listinBasket ,listParamInfo)

	/******************************* List process ********************************************************************/
	
	public String[] listProcessLabel = {"T1map","T2map","TImap","T1map_FA","pCasl"};
	
}


/******************************* notes about Bruker parameters**************************************

(1) the date format must be changed to yyyy.mm.dd - hh:mm:ss
	ex : in Paravision 5.1,  <08:04:10 11 Feb 2014> 		must be changed in  2014.02.11 - 08:04:10
	 	 in Paravision 6  ,  <2014-04-22T09:07:38,694+0200> must be changed in	2014.04.22 - 09:07:38

(2) the time format must be changed in hh:mm:ss
	ex : 1228800	must be changed in	0:20:28.800
	
(5) the data type format must be changed in "8-bits-unsigned","8-bits-signed","16-bits-unsigned", ......
	ex : _16BIT_SGN_INT must be changed in	16-bits-int-signed
	
(6) the dimension format must be changed in 2D or 3D
	ex : 2 to 2D
	
(14) the image type format must be changed :
 		MAGNITUDE_IMAGE to M
 		REAL_IMAGE 		to R
 		PHASE_IMAGE 	to P
 		
(21) the number of type image can be calculate with the number of type found in (13)

(35) In Bruker parameters, SpatResol show read and phase (and slice in 3D). The correspondence with (x,y) depend on the ReadOrientation : 
		. if ReadOrientation = H_F or A_P (x,y) becomes (y,x) 
		. if ReadOrientation = L_R	(x,y) is still (x,y)
 	ex : (0.15625 , 0.234375) becomes (20 , 30) in H_F or A_P
 
(40) the read direction format must be changed in A-P","F-H",R-L"
 	  ex : 	L_R becomes R-L
 	   		H_F becomes F-H
 	   		A_P becomes A-P
  
******************************** end notes about Bruker parameters **********************************/



/***************************************************************************************************
0    Patient name                       :   OZEN^SUKRIYE
1    Examination name                   :   UNDEFINED
2    Protocol name                      :   SO2_3D_FFE SENSE
3    Examination date/time              :   2015.10.22 / 15:53:38
4    Series Type                        :   Image   MRSERIES
5    Acquisition nr                     :   6
6    Reconstruction nr                  :   1
7    Scan Duration [sec]                :   311
8    Max. number of cardiac phases      :   1
9    Max. number of echoes              :   9
10   Max. number of slices/locations    :   75
11   Max. number of dynamics            :   1
12   Max. number of mixes               :   1
13   Patient position                   :   Head First Supine
14   Preparation direction              :   Right-Left
15   Technique                          :   FFE
16   Scan resolution  (x, y)            :   224  224
17   Scan mode                          :   3D
18   Repetition time [ms]               :   66.524  
19   FOV (ap,fh,rl) [mm]                :   224.000  60.000  184.000
20   Water Fat shift [pixels]           :   2.002
21   Angulation midslice(ap,fh,rl)[degr]:   -10.490  -12.974  -15.314
22   Off Centre midslice(ap,fh,rl) [mm] :   7.220  14.117  -10.853
23   Flow compensation <0=no 1=yes> ?   :   1
24   Presaturation     <0=no 1=yes> ?   :   0
25   Phase encoding velocity [cm/sec]   :   0.000000  0.000000  0.000000
26   MTC               <0=no 1=yes> ?   :   0
27   SPIR              <0=no 1=yes> ?   :   0
28   EPI factor        <0,1=no EPI>     :   1
29   Dynamic scan      <0=no 1=yes> ?   :   0
30   Diffusion         <0=no 1=yes> ?   :   0
31   Diffusion echo time [ms]           :   0.0000
32   Max. number of diffusion values    :   1
33   Max. number of gradient orients    :   1
34   Number of label types   <0=no ASL> :   0


0  		slice number                             (integer)
1  		echo number                              (integer)
2  		dynamic scan number                      (integer)
3  		cardiac phase number                     (integer)
4  		image_type_mr                            (integer)
5  		scanning sequence                        (integer)
6  		index in REC file (in images)            (integer)
7  		image pixel size (in bits)               (integer)
8	 	scan percentage                          (integer)
9-10	recon resolution (x y)                   (2*integer)
11		rescale intercept                        (float)
12		rescale slope                            (float)
13		scale slope                              (float)
14		window center                            (integer)
15		window width                             (integer)
16-18	image angulation (ap,fh,rl in degrees )  (3*float)
19-21	image offcentre (ap,fh,rl in mm )        (3*float)
22		slice thickness (in mm )                 (float)
23		slice gap (in mm )                       (float)
24		image_display_orientation                (integer)
25		slice orientation ( TRA/SAG/COR )        (integer)
26		fmri_status_indication                   (integer)
27		image_type_ed_es  (end diast/end syst)   (integer)
28-29	pixel spacing (x,y) (in mm)              (2*float)
30		echo_time                                (float)
31		dyn_scan_begin_time                      (float)
32		trigger_time                             (float)
33		diffusion_b_factor                       (float)
34		number of averages                       (integer)
35		image_flip_angle (in degrees)            (float)
36		cardiac frequency   (bpm)                (integer)
37		minimum RR-interval (in ms)              (integer)
38		maximum RR-interval (in ms)              (integer)
39		TURBO factor  <0=no turbo>               (integer)
40		Inversion delay (in ms)                  (float)
41		diffusion b value number    (imagekey!)  (integer)
42		gradient orientation number (imagekey!)  (integer)
43		contrast type                            (string)
44		diffusion anisotropy type                (string)
45-47   diffusion (ap, fh, rl)                   (3*float)
48		label type (ASL)            (imagekey!)  (integer)
*********************************************************************************************************/

/********************************************************************************************************
=== PIXEL VALUES =============================================================
#  PV = pixel value in REC file, FP = floating point value, DV = displayed value on console
#  RS = rescale slope,           RI = rescale intercept,    SS = scale slope
#  DV = PV * RS + RI             FP = DV / (RS * SS)
#/
*********************************************************************************************************/


/***********************************************************************************************************
*  <Series_Info>
0        <Attribute Name="Patient Name" Tag="0x00100010" Level="Patient" Type="String">OZEN^SUKRIYE</Attribute>
1        <Attribute Name="Examination Name" Tag="0x00400254" Level="Examination" Type="String">UNDEFINED</Attribute>
2        <Attribute Name="Protocol Name" Tag="0x00181030" Level="MRSeries" Type="String">3D_T2_BrainView SENSE</Attribute>
3        <Attribute Name="Examination Date" Tag="0x00400244" Level="Examination" Type="Date">2015.10.22</Attribute>
4        <Attribute Name="Examination Time" Tag="0x00400245" Level="Examination" Type="Time">15:53:38</Attribute>
5        <Attribute Name="Series Data Type" Tag="0x20051035" Level="MRSeries" Type="String">PIXEL</Attribute>
6        <Attribute Name="Aquisition Number" Tag="0x2001107B" Level="MRSeries" Type="Int32">3</Attribute>
7        <Attribute Name="Reconstruction Number" Tag="0x2001101D" Level="MRSeries" Type="Int32">1</Attribute>
8        <Attribute Name="Scan Duration" Tag="0x20051033" Level="MRSeries" Type="Float">3.1250E+02</Attribute>
9        <Attribute Name="Max No Phases" Tag="0x20011017" Level="MRSeries" Type="Int32">1</Attribute>
10       <Attribute Name="Max No Echoes" Tag="0x20011014" Level="MRSeries" Type="Int32">1</Attribute>
11       <Attribute Name="Max No Slices" Tag="0x20011018" Level="MRSeries" Type="Int32">140</Attribute>
12       <Attribute Name="Max No Dynamics" Tag="0x20011081" Level="MRSeries" Type="Int32">1</Attribute>
13       <Attribute Name="Max No Mixes" Tag="0x20051021" Level="MRSeries" Type="Int16">1</Attribute>
14       <Attribute Name="Max No B Values" Tag="0x20051414" Level="MRSeries" Type="Int32">1</Attribute>
15       <Attribute Name="Max No Gradient Orients" Tag="0x20051415" Level="MRSeries" Type="Int32">1</Attribute>
16       <Attribute Name="No Label Types" Tag="0x20051428" Level="MRSeries" Type="Int32">0</Attribute>
17       <Attribute Name="Patient Position" Tag="0x00185100" Level="MRSeries" Type="String">HFS</Attribute>
18       <Attribute Name="Preparation Direction" Tag="0x2005107B" Level="MRStack" Type="String">RL</Attribute>
19       <Attribute Name="Technique" Tag="0x20011020" Level="MRSeries" Type="String">TSE</Attribute>
20       <Attribute Name="Scan Resolution X" Tag="0x2005101D" Level="MRSeries" Type="Int16">332</Attribute>
21       <Attribute Name="Scan Resolution Y" Tag="0x00180089" Level="MRImage" Type="Int32">336</Attribute>
22       <Attribute Name="Scan Mode" Tag="0x2005106F" Level="MRSeries" Type="String">3D</Attribute>
23       <Attribute Name="Repetition Times" Tag="0x20051030" Level="MRSeries" Type="Float" ArraySize="2">2.5000E+03  0.0000E+00</Attribute>
24       <Attribute Name="FOV AP" Tag="0x20051074" Level="MRStack" Type="Float">2.0000E+02</Attribute>
25       <Attribute Name="FOV FH" Tag="0x20051075" Level="MRStack" Type="Float">7.7000E+01</Attribute>
26       <Attribute Name="FOV RL" Tag="0x20051076" Level="MRStack" Type="Float">2.0000E+02</Attribute>
27       <Attribute Name="Water Fat Shift" Tag="0x20011022" Level="MRSeries" Type="Float">6.5929E-01</Attribute>
28       <Attribute Name="Angulation AP" Tag="0x20051071" Level="MRStack" Type="Float">-1.0490E+01</Attribute>
29       <Attribute Name="Angulation FH" Tag="0x20051072" Level="MRStack" Type="Float">-1.2974E+01</Attribute>
30       <Attribute Name="Angulation RL" Tag="0x20051073" Level="MRStack" Type="Float">-1.5314E+01</Attribute>
31       <Attribute Name="Off Center AP" Tag="0x20051078" Level="MRStack" Type="Float">1.7769E+00</Attribute>
32       <Attribute Name="Off Center FH" Tag="0x20051079" Level="MRStack" Type="Float">-6.3216E+00</Attribute>
33       <Attribute Name="Off Center RL" Tag="0x2005107A" Level="MRStack" Type="Float">-4.0895E+00</Attribute>
34       <Attribute Name="Flow Compensation" Tag="0x20051016" Level="MRSeries" Type="Boolean">N</Attribute>
35       <Attribute Name="Presaturation" Tag="0x2005102F" Level="MRSeries" Type="Boolean">N</Attribute>
36       <Attribute Name="Phase Encoding Velocity" Tag="0x2001101A" Level="MRSeries" Type="Float" ArraySize="3">0.0000E+00  0.0000E+00  0.0000E+00</Attribute>
37       <Attribute Name="MTC" Tag="0x2005101C" Level="MRSeries" Type="Boolean">N</Attribute>
38       <Attribute Name="SPIR" Tag="0x20011021" Level="MRSeries" Type="Boolean">Y</Attribute>
39       <Attribute Name="EPI factor" Tag="0x20011013" Level="MRSeries" Type="Int32">1</Attribute>
40       <Attribute Name="Dynamic Scan" Tag="0x20011012" Level="MRSeries" Type="Boolean">N</Attribute>
41       <Attribute Name="Diffusion" Tag="0x20051014" Level="MRSeries" Type="Boolean">N</Attribute>
42       <Attribute Name="Diffusion Echo Time" Tag="0x20011011" Level="MRSeries" Type="Float">0.0000E+00</Attribute>
43       <Attribute Name="PhotometricInterpretation" Tag="0x00280004" Level="MRImage" Type="String">MONOCHROME2</Attribute>
</Series_Info>

<Image_Info>
    <Key>
0                 <Attribute Name="Slice" Tag="0x2001100A" Type="Int32">71</Attribute>
1                 <Attribute Name="Echo" Tag="0x00180086" Type="Int32">1</Attribute>
2                 <Attribute Name="Dynamic" Tag="0x00200100" Type="Int32">1</Attribute>
3                 <Attribute Name="Phase" Tag="0x20011008" Type="Int32">1</Attribute>
4                 <Attribute Name="BValue" Tag="0x20051412" Type="Int32">1</Attribute>
5                 <Attribute Name="Grad Orient" Tag="0x20051413" Type="Int32">1</Attribute>
6                 <Attribute Name="Label Type" Tag="0x20051429" Type="Enumeration" EnumType="Label_Type">-</Attribute>
7                 <Attribute Name="Type" Tag="0x20051011" Type="Enumeration" EnumType="Image_Type">M</Attribute>
8                 <Attribute Name="Sequence" Tag="0x2005106E" Type="Enumeration" EnumType="Image_Sequence">SE</Attribute>
9                 <Attribute Name="Index" Type="Int32" Calc="Index">0</Attribute>
            </Key>
10            <Attribute Name="Pixel Size" Tag="0x00280100" Type="UInt16">16</Attribute>
11            <Attribute Name="Scan Percentage" Tag="0x00180093" Type="Double">7.972265E+001</Attribute>
12            <Attribute Name="Resolution X" Tag="0x00280011" Type="UInt16">352</Attribute>
13            <Attribute Name="Resolution Y" Tag="0x00280010" Type="UInt16">352</Attribute>
14            <Attribute Name="Rescale Intercept" Tag="0x00281052" Type="Double">0.000000E+000</Attribute>
15            <Attribute Name="Rescale Slope" Tag="0x00281053" Type="Double">1.186178E+001</Attribute>
16            <Attribute Name="Scale Slope" Tag="0x2005100E" Type="Float">3.8937E-04</Attribute>
17            <Attribute Name="Window Center" Tag="0x00281050" Type="Double">1.070000E+003</Attribute>
18            <Attribute Name="Window Width" Tag="0x00281051" Type="Double">1.860000E+003</Attribute>
19            <Attribute Name="Slice Thickness" Tag="0x00180050" Type="Double">1.100000E+000</Attribute>
20            <Attribute Name="Slice Gap" Type="Double" Calc="SliceGap">-5.500000E-001</Attribute>
21            <Attribute Name="Display Orientation" Tag="0x20051004" Type="Enumeration" EnumType="Display_Orientation">-</Attribute>
22            <Attribute Name="fMRI Status Indication" Tag="0x20051063" Type="Int16">0</Attribute>
23            <Attribute Name="Image Type Ed Es" Tag="0x20011007" Type="Enumeration" EnumType="Type_ed_es">U</Attribute>
24-25         <Attribute Name="Pixel Spacing" Tag="0x00280030" Type="Double" ArraySize="2">5.681818E-001  5.681818E-001</Attribute>
26            <Attribute Name="Echo Time" Tag="0x00180081" Type="Double">3.242758E+002</Attribute>
27            <Attribute Name="Dyn Scan Begin Time" Tag="0x200510A0" Type="Float">0.0000E+00</Attribute>
28            <Attribute Name="Trigger Time" Tag="0x00181060" Type="Double">0.000000E+000</Attribute>
29            <Attribute Name="Diffusion B Factor" Tag="0x20011003" Type="Float">0.0000E+00</Attribute>
30            <Attribute Name="No Averages" Tag="0x00180083" Type="Double">2.000000E+000</Attribute>
31            <Attribute Name="Image Flip Angle" Tag="0x00181314" Type="Double">9.000000E+001</Attribute>
32            <Attribute Name="Cardiac Frequency" Tag="0x00181088" Type="Int32">0</Attribute>
33            <Attribute Name="Min RR Interval" Tag="0x00181081" Type="Int32">0</Attribute>
34            <Attribute Name="Max RR Interval" Tag="0x00181082" Type="Int32">0</Attribute>
35            <Attribute Name="TURBO Factor" Tag="0x00180091" Type="Int32">133</Attribute>
36            <Attribute Name="Inversion Delay" Tag="0x00180082" Type="Double">0.000000E+000</Attribute>
37            <Attribute Name="Contrast Type" Tag="0x00089209" Type="String">T2</Attribute>
38            <Attribute Name="Diffusion Anisotropy Type" Tag="0x00189147" Type="String">-</Attribute>
39            <Attribute Name="Diffusion AP" Tag="0x200510B1" Type="Float">0.0000E+00</Attribute>
40            <Attribute Name="Diffusion FH" Tag="0x200510B2" Type="Float">0.0000E+00</Attribute>
41            <Attribute Name="Diffusion RL" Tag="0x200510B0" Type="Float">0.0000E+00</Attribute>
42            <Attribute Name="Angulation AP" Type="Double" Calc="AngulationAP">-1.048994E+001</Attribute>
43            <Attribute Name="Angulation FH" Type="Double" Calc="AngulationFH">-1.297418E+001</Attribute>
44            <Attribute Name="Angulation RL" Type="Double" Calc="AngulationRL">-1.531434E+001</Attribute>
45            <Attribute Name="Offcenter AP" Type="Double" Calc="OffcenterAP">1.848316E+000</Attribute>
46            <Attribute Name="Offcenter FH" Type="Double" Calc="OffcenterFH">-6.060802E+000</Attribute>
47            <Attribute Name="Offcenter RL" Type="Double" Calc="OffcenterRL">-4.139520E+000</Attribute>
48            <Attribute Name="Slice Orientation" Type="Enumeration" Calc="SliceOrient" EnumType="Slice_Orientation">Transversal</Attribute>
49            <Attribute Name="Image Planar Configuration" Tag="0x00280006" Type="UInt16">65535</Attribute>
50            <Attribute Name="Samples Per Pixel" Tag="0x00280002" Type="UInt16">1</Attribute>
</Image_Info>
**********************************************************************************************************************/