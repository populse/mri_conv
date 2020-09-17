package exportFiles;

public interface DictionaryHeaderNifti {
	
	public static String[][] listTagHeaderNifti = {
			// key 												, type		, format	, units	, description
			{"Dataset header file"								,"string"	,""			,""		,"(from Nifti header)"},
			{"Dataset data file"								,"string"	,""			,""		,"(from Nifti header)"},
			{"Size of header"									,"int"		,""			,""		,"size of the header. Must be 348 (bytes) (from Nifti header)"},
			{"File offset to data blob"							,"float"	,""			,""		,"(from Nifti header)"},
			{"Endianness"										,"string"	,""			,""		,"(from Nifti header)"},
			{"Magic filetype string"							,"string"	,""			,""		,"(from Nifti header)"},
			{"Datatype"											,"string"	,""			,""		,"(from Nifti header)"},
			{"Bits per voxel"									,"int"		,""			,""		,"(from Nifti header)"},
			{"Scaling slope and intercept"						,"float"	,""			,""		,"(from Nifti header)"},
			{"Dataset dimensions (Count, X,Y,Z,T...)"			,"int"		,""			,""		,"data array dimensions (from Nifti header)"},
			{"Grid spacings (X,Y,Z,T,...)"						,"float"	,""			,""		,"(from Nifti header)"},
			{"XYZ  units"										,"string"	,""			,""		,"(from Nifti header)"},
			{"T units"											,"string"	,""			,""		,"(from Nifti header)"},
			{"T offset"											,"float"	,""			,""		,"(from Nifti header)"},
			{"Intent parameters"								,"float"	,""			,""		,"(from Nifti header)"},
			{"Intent code"										,"string"	,""			,""		,"(from Nifti header)"},
			{"Cal. (display) max/min"							,"float"	,""			,""		,"Maximum/minimum display intensity (from Nifti header)"},
			{"Slice timing code"								,"string"	,""			,""		,"(from Nifti header)"},
			{"MRI slice ordering (freq, phase, slice index)"	,"int"		,""			,""		,"(from Nifti header)"},
			{"Start/end slice"									,"int"		,""			,""		,"(from Nifti header)"},
			{"Slice duration"									,"float"	,""			,""		,"(from Nifti header)"},
			{"Q factor"											,"int"		,""			,""		,"(from Nifti header)"},
			{"Qform transform code"								,"string"	,""			,""		,"(from Nifti header)"},
			{"Quaternion b,c,d params"							,"float"	,""			,""		,"(from Nifti header)"},
			{"Quaternion x,y,z shifts"							,"float"	,""			,""		,"(from Nifti header)"},
			{"Affine transform code"							,"string"	,""			,""		,"(from Nifti header)"},
			{"1st row affine transform"							,"float"	,""			,""		,"(from Nifti header)"},
			{"2nd row affine transform"							,"float"	,""			,""		,"(from Nifti header)"},
			{"3rd row affine transform"							,"float"	,""			,""		,"(from Nifti header)"},
			{"Description"										,"string"	,""			,""		,"(from Nifti header)"},
			{"Intent name"										,"string"	,""			,""		,"(from Nifti header)"},
			{"Auxiliary file"									,"string"	,""			,""		,"(from Nifti header)"},		
			{"Extension byte 1"									,"int"		,""			,""		,"(from Nifti header)"}
	};
}