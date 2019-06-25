# Documentation

The documentation is available on mri_conv's website here: [https://populse.github.io/mri_conv](https://populse.github.io/mri_conv)

# Release history

    25/06/2019 : Version 19.3.3a
	Bugs fixed:
	  - Bruker : affine transformation problem of some Bruker sequences fixed.
	  - NIFTI : problem reading some Nifti files (64 bits float) fixed.
	  - Philips : problem reading when sequences had same number serial, protocol, acquisition time ... fixed.

<p></p>	

    21/06/2019 : Version 19.3.2a
    	Bugs fixed:
	  - NIFTI : problem reading some Nifti files (16 bits unsigned) fixed.
	  - Philips : problem reading when files had lowercase extensions (*.rec, *.par) fixed.
	Features added :
	  - Philips : possibility to generate bvecs/bvals files for reading of diffusion by MrTrix and FSL.

<p></p>	

    12/06/2019 : Version 19.3.1a
	Bugs fixed:
	  - DICOM : problem reading Dicom files without rescale slope and intercept

<p></p>	

    06/06/2019 : Version 19.3.0a	
	Bugs fixed:
	  - DICOM : dimension reversed for some Dicom.
	  - problem of the dialogue window 'Loading thumbnail ...' that did not always close resolved
	Features added :
	  - Bruker : possibility to generate bvecs/bvals files for reading of diffusion by MrTrix 
		(see page 'Documentation', section 'Set options')
	  - add option [ProjectsDir] for the script (see page 'Installation', section 'Launching of the software')
	Others:
	  - Json Irmage : now, all parameters that have floating or integer values (same single) are presented as an array (list of list)
	  - better management of items in the basket
	  - exit code = 100 (for populse_mia project only)

<p></p>	

    04/04/2019 : Version 19.2.3a
	Bugs fixed:
	  - DICOM : for some Dicomdir files, anonymization did not work.
	  - Nifti : anonymization did not work
	  - Bruker : "Sequence Name" tag more complete for parametric images
<p></p>	
  
    03/04/2019 : Version 19.2.2a
	Bugs fixed:
	  - DICOM : for some Dicom files, anonymization did not work.
	  - DICOM : after the ImageJ upgrade to 1.52n, some DICOM images were not displayed in the 'thumbnails' area.
	Others:
	  - ImageJ update to 1.52n
<p></p>	
 
    04/03/2019 : Version 19.2.1a
	Bugs fixed:
	  - DICOM : problems opening Dicom files which included the same serial number.
	  - PHILIPS : problem opening DwiSE images.
	Features added :
	  - DICOM : add in "Preference" menu the option "simplified view for DICOMDIR" for faster reading. 
	Others:
	  - a new column 'Note' added in the 'Data browser' table.
	  - a new column 'Serial Number' added in the 'MRI sequence' table.
 <p></p>	
 
    22/01/2019 : Version 19.0.0a
	Bugs fixed:
	  - DICOM : problem opening Dicom files which are located in a root directory of a disk.
 <p></p>	
 
    11/12/2018 : Version 18.2.9a
	Bugs fixed:
	  - PHILIPS : under Linux, display problem for Xml/Rec files.
	Others:
	  - 'DataAnonymized' tag modified in json file (add 'Description, 'format', ...).
 <p></p>	
 
    11/12/2018 : Version 18.2.8a
	Bugs fixed:
	  - better research of Nifti files.
	Features added :
	  - options to anonymize MRI datas ->  'DataAnonymized' tag added in json file (value = no or yes).
	  - BRUKER : possibility to export the parametric images separatly (see 'File'-> export -> Option export' menu).
 <p></p>	
 
    04/12/2018 : Version 18.1.8a
	Bugs fixed:
	  - problem with export to Nifti (Populse_MIA)
	  - problem with defaut directory for the exported data.
	Others:
	  - the software version is displayed at the top of the sofwtare window.
	  - DictionaryMRI_System.yml is modified ('Patient Weight', type = string instead float)
	  - PHILIPS: 'Patient Sex', 'Patient Weight', 'Patient BirthDate' tags added.
