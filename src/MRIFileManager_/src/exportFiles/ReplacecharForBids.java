package exportFiles;

public class ReplacecharForBids {

	public String charReplace(String tmp) {
		try {
			tmp = tmp.replace("-", "");
			tmp = tmp.replace("_", "");
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
			tmp = tmp.replace("+", "");
			tmp = tmp.replaceAll(" +", "");
		} catch (Exception e) {
			tmp = "";
		}

		return tmp;
	}
}