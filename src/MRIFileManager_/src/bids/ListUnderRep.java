package bids;

import java.io.File;
import java.io.FilenameFilter;

import abstractClass.PrefParam;

public class ListUnderRep extends PrefParam{
	
	public ListUnderRep() {

	}
	
	public String[] listesousrep(String repertory) {
		
//		String listProtocol = "anat,fmap,tmap,func,dti,dwi,meg,beh,pcasl,derivatives,.datalad";

		boolean rootDisk = false;

		if (!repertory.contains(PrefParam.separator)) {
			repertory += PrefParam.separator;
			rootDisk = true;
		}

		File listeSousrepertory = new File(repertory);

		String[] listrep = null;

		File[] matchingFiles = listeSousrepertory.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.contains("dataset_description.json"))
					return true;
				return false;
			}
		});

		if (matchingFiles.length > 0)
			listrep = listeSousrepertory.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					return (new File(dir, name).isDirectory() && !listProtocolsForBids.contains(name));
				}
			});

		if (rootDisk) {
			repertory = repertory.substring(0, repertory.length() - 1);
			rootDisk = false;
		}

		return listrep;
	}
}