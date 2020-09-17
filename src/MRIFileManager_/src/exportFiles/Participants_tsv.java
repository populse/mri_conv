package exportFiles;

import java.io.File;

public class Participants_tsv {
	
	private String pathTSV;
	
	public Participants_tsv(String pathTSV) {
		this.pathTSV = pathTSV;
	}
	
	public boolean TSV_exists() {
		return new File(pathTSV).exists();
	}
}