package MRIFileManager;

import java.io.File;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import abstractClass.PrefParam;

public class ProtocolsBidsYaml extends PrefParam{
	
	private HashMap<String, HashMap<String, HashMap<String , String[]>>> listPro, listPro2;
	private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	private String[] setProtocol = {"anat","unknow"};

	public ProtocolsBidsYaml(String fileYml) {
		try {
			User user = mapper.readValue(new File(fileYml), User.class);
			listPro2 = user.getlistProtocols();
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
		listProtocolsForBids = String.join(",", listPro2.keySet())+".datalad";
	}
	
	public ProtocolsBidsYaml(String fileYml, String constructor, String keyword) {
		
		try {
			User user = mapper.readValue(new File(fileYml), User.class);
			listPro = user.getlistProtocols();
			
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
		
		for (String hh : listPro.keySet()) {
			for (String ii : listPro.get(hh).keySet())
				for (String jj : listPro.get(hh).get(ii).get(constructor))
					if (keyword.matches(jj.replace("?", ".?").replace("*", ".*?"))) {
						setProtocol[0] = hh;
						setProtocol[1] = ii;
						return;
					}
		}
	}

	public String[] getSetProtocol() {
		return setProtocol;
	}
}