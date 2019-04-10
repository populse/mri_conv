# Documentation

The documentation is available on mri_conv's website here: [https://populse.github.io/mri_conv](https://populse.github.io/mri_conv)

# Release history

    04/04/2019 : Version 19.2.3a
	Bugs fixed:
	  - DICOM : for some Dicomdir files, anonymization did not work.
	  - Nifti : anonymization did not work
	  - Bruker : "Sequence Name" tag more complete for parametric images
 
    03/04/2019 : Version 19.2.2a
	Bugs fixed:
	  - DICOM : for some Dicom files, anonymization did not work.
	  - DICOM : after the ImageJ upgrade to 1.52n, some DICOM images were not displayed in the 'thumbnails' area.
	Others:
	  - ImageJ update to 1.52n
     
    04/03/2019 : Version 19.2.1a
	Bugs fixed:
	  - DICOM : problems opening Dicom files which included the same serial number.
	  - PHILIPS : problem opening DwiSE images.
	Features added :
	  - DICOM : add in "Preference" menu the option "simplified view for DICOMDIR" for faster reading. 
	Others:
	  - a new column 'Note' added in the 'Data browser' table.
	  - a new column 'Serial Number' added in the 'MRI sequence' table.
     
    22/01/2019 : Version 19.0.0a
	Bugs fixed:
	  - DICOM : problem opening Dicom files which are located in a root directory of a disk.
     
    11/12/2018 : Version 18.2.9a
	Bugs fixed:
	  - PHILIPS : under Linux, display problem for Xml/Rec files.
	Others:
	  - 'DataAnonymized' tag modified in json file (add 'Description, 'format', ...).
  
    11/12/2018 : Version 18.2.8a
	Bugs fixed:
	  - better research of Nifti files.
	Features added :
	  - options to anonymize MRI datas ->  'DataAnonymized' tag added in json file (value = no or yes).
	  - BRUKER : possibility to export the parametric images separatly (see 'File'-> export -> Option export' menu).
 
    04/12/2018 : Version 18.1.8a
	Bugs fixed:
	  - problem with export to Nifti (Populse_MIA)
	  - problem with defaut directory for the exported data.
	Others:
	  - the software version is displayed at the top of the sofwtare window.
	  - DictionaryMRI_System.yml is modified ('Patient Weight', type = string instead float)
	  - PHILIPS: 'Patient Sex', 'Patient Weight', 'Patient BirthDate' tags added.
