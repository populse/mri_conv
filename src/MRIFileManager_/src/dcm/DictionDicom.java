package dcm;

public interface DictionDicom {

	String[] listType = { "M", "R", "I", "P", "PHASE_MAP", "PERFUSION", "OTHER", "DENSITY_MAP", "DIFFUSION_MAP",
			"IMAGE_ADDITION", "MODULUS_SUBTRACT", "MPR", "PHASE_SUBTRACT", "PROJECTION_IMAGE", "T1_MAP", "T2_MAP",
			"VELOCITY_MAP", "SUM" };

	String[] listScanSeq = { "SE", "IR", "GR", "EP", "RM", "B1" };

}

// list Type Image 

//Dicom Philips								|		Dicom Siemens					|		Dicom Bruker		
//---------------------------------------------------------------------------------------------------------------------------------
//ORIGINAL\PRIMARY\M_IR\M\IR    			|	ORIGINAL\PRIMARY\M\ND				|
//ORIGINAL\PRIMARY\I_IR\I\IR                |	ORIGINAL\PRIMARY\PERFUSION\NONE\ND 	|
//ORIGINAL\PRIMARY\R_IR\R\IR                |	ORIGINAL\PRIMARY\PERFUSION\TTP\ND	|
//ORIGINAL\PRIMARY\PHASE MAP\P\IR           |	ORIGINAL\PRIMARY\M\ND\NORM			|
//ORIGINAL\PRIMARY\M_FFE\M\FFE              |	ORIGINAL\PRIMARY\P\ND\ 				|
//ORIGINAL\PRIMARY\I_FFE\I\FFE              |	ORIGINAL\PRIMARY\M\NORM\DIS2D		|
//ORIGINAL\PRIMARY\R_FFE\R\FFE              |	ORIGINAL\PRIMARY\OTHER				|	
//ORIGINAL\PRIMARY\PHASE MAP\P\FFE          |										|
//ORIGINAL\PRIMARY\M_SE\M\SE                |										|
//ORIGINAL\PRIMARY\I_SE\I\SE                |										|
//ORIGINAL\PRIMARY\R_SE\R\SE                |										|
//ORIGINAL\PRIMARY\PHASE MAP\P\SE           |										|
//ORIGINAL\PRIMARY\T1 MAP\T1\UNSPECIFIED	|										|
//ORIGINAL\PRIMARY\T2 MAP\T2\UNSPECIFIED    |										|
//ORIGINAL\PRIMARY\M_B1\M\B1	            |										|
//ORIGINAL\PRIMARY\PHASE MAP\P\B1           |										|	
//ORIGINAL\PRIMARY\T1\MIXED					|										|
//ORIGINAL\PRIMARY\VELOCITY MAP\P\PCA		|										|	