[![](https://img.shields.io/badge/java-8-yellow.svg)](https://www.java.com/fr/download/)
[![](https://img.shields.io/badge/platform-Linux%2C%20OSX%2C%20Windows-orange.svg)](#)

# MRI File Manager

 MRI File Manager allows the reading of some raw and processed data files from MRI Spectrometers :

	- Bruker Paravision PV5 & PV6 (raw data of magnitude type).
	- Dicom from Bruker, Philips, Siemens.
	- Philips Achieva (Par/Rec v4, v4.1 & v4.2, Xml/Rec v5).
	- Nifti-1 (with or without Json).
	- BIDS - Brain Imaging Data Structure.

 It also allows for converting MRI images to Nifti-1 or BIDS:

	- Export MRI data in Nifti-1 or BIDS format until 5 dimensions of the image (x, y, slice, frame, temporal).
	- Json files are created and associated with Nifti files in order to contain MRI parameters.
	- an option of anonymization allows to hide sensitive informations about the patient (name, age, sex, weight).
	- adaptation of orientation information in Nifti headers (tested on SPM, FSL and MRtrix).
	- option of customizing the Nifti file names.
	- possibility to create text files containing bvecs & bvals for MRtrix and FSL (Bruker and Philips).


# Documentation

The documentation is available on mri_conv's website here: [https://populse.github.io/mri_conv](https://populse.github.io/mri_conv).  
To download the latest 'development' version, click [here](https://github.com/populse/mri_conv/archive/devpt.zip) or 'Download ZIP' in 'Clone or download'.

# Release history

    10/09/2020 : Version 20.2.3c
	Modification :
	  - some graphics improvements
	Others:
	  - ImageJ update to 1.53d
	  - ImageJ : 3D plug-in added


<p></p>

    10/08/2020 : Version 20.2.3b
	Modification :
	  - ImageJ : absolute path of ImageJ (instead of relative)
	Others:
	  - JTattoo update to 1.6.13
	  - removal of some unnecessary plugins from ImageJ


<p></p>

    17/07/2020 : Version 20.2.3a
	Bugs fixed:
	  - Bruker : bug with normalization for some parametrics 3d sequence fixed.


<p></p>

    03/07/2020 : Version 20.2.2a
	Bugs fixed:
	  - Basket manager : bug with the combobox "Nifti-1 , BIDS" fixed.


<p></p>

    01/07/2020 : Version 20.2.1a
	Bugs fixed:
	  - Philips : bug with the 'history' field under the buttons fixed.
	Others:
	  - ImageJ update to 1.53c

<p></p>

    24/06/2020 : Version 20.2.0a
	Features added:
	  - Philips : now compatible with ParRec V4 and V4.1.
	  	      possibility to select a single directory containing Philips data.
	Bugs fixed:
	  - DICOM : problem reading some Dicom files (16 bits signed) fixed.
	Others:
	  - ImageJ update to 1.53b

<p></p>

    18/06/2020 : Version 20.1.5d
	Bugs fixed:
	  - Dicom Philips : image order problem in some sequences 4D fixed.


    12/06/2020 : Version 20.1.5c
	Bugs fixed:
	  - Bruker : bug with 'slice package' fixed

<p></p>

    14/05/2020 : Version 20.1.5b
	Bugs fixed:
	  - Bruker : bug when exporting to Nifti with number of slice = 1 fixed
	  - Philips xml/REC : display bug when opening xml file fixed

<p></p>

    17/04/2020 : Version 20.1.4b
	Modification:
	  - some graphic improvements
	Others:
	  - dependencies update

<p></p>

    15/04/2020 : Version 20.0.4b
	Bug fixed:
	  - Bruker: slice thickness in Nifti header for Localizer/Tripilot sequence corrected
	Modification:
	  - The combobox of file history is now under the buttons "Bruker","Dicom" etc...
	Others:
	  - ImageJ update to 1.52v

<p></p>

    09/04/2020 : Version 20.0.3b
	Modification:
	  - Option export window: modification of tab "Nifti naming"

<p></p>

    08/04/2020 : Version 20.0.2b
	Bug fixed:
	  - Basket manager : bug under Windows when filling the basket fixed

<p></p>

    08/04/2020 : Version 20.0.2a
	Features added:
	  - Basket manager : preview window added
	  - Export to BIDS : better compatibility with modalities

<p></p>

    30/03/2020 : Version 20.0.1a
	Features added:
	  - All : now possibility to export Bruker, Philips and Dicom in BIDS format (still in development)
	  - Bruker : affine transformation now compatible with MRTrix
	Bug fixed:
	  - Nifti: problem exporting Nifti files without JSON fixed

<p></p>

    06/09/2019 : Version 19.6.1a
	Features added:
	  - Philips : possibility to launch MRI File Manager by script in windowless mode (see section 'Launching of the software' in 'Installation' page).
	Bug fixed:
	  - Philips: image order problem in some sequences 5D fixed.

<p></p>

    29/08/2019 : Version 19.6.0a
	Features added:
	  - Bruker : possibility to launch MRI File Manager by script in windowless mode (see section 'Launching of the software'    in 'Installation' page).
	  - Bids : menus "see 'dataset_description.json' file" and "see 'participants.tsv' file" added in popup menu.
	Bug fixed:
	  - Dicom Philips: image order problem in some sequences 5D fixed.
<p></p>

    09/08/2019 : Version 19.5.6a
	Bug fixed:
	  - Bids : some bugs who crashed Populse_mia fixed.

<p></p>
 
    09/08/2019 : Version 19.5.5a
	Others:
	  - Bids : following the Bids development, now possibility to export Bids structures to Nifti .

<p></p>

    09/07/2019 : Version 19.5.4b
	Features added:
	  - Bids : following the Bids development(still in trial version).

<p></p>

    05/07/2019 : Version 19.5.3b
	Features added:
	  - Bids - Brain Imaging Data Structure: new MRI file format introduced in the software (still in trial version).

<p></p>

    02/07/2019 : Version 19.4.3b
	Bug fixed:
	  - DICOM: problem reading some Dicom files.

<p></p>	

    02/07/2019 : Version 19.4.3a
	Features added :
	  - DICOM (Philips): possibility to generate bvecs/bvals files for reading of diffusion in MRtrix or FSL.
	Others:
	  - best display of JSON text in log
	  - best display of no-isotropic images in thumbnail list

<p></p>	

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
