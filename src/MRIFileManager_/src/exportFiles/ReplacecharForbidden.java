package exportFiles;

public class ReplacecharForbidden {

	public String charReplace(String tmp) {

		try {
			tmp = tmp.replace("<", "");
			tmp = tmp.replace(">", "");
			tmp = tmp.replace(":", "");
			tmp = tmp.replace("/", "");
			tmp = tmp.replace("\\", "");
			tmp = tmp.replace("*", "");
			tmp = tmp.replace("?", "");
			tmp = tmp.replace("|", "");
			tmp = tmp.replace("\"", "");
			tmp = tmp.replace("(", "");
			tmp = tmp.replace(")", "");
			tmp = tmp.replace('.', '_');
			tmp = tmp.replaceAll(" +", "");
		} catch (Exception e) {
			tmp = "";
		}

		return tmp;
	}
}