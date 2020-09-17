package dcm;

public class ChangeSyntax {
	
	public ChangeSyntax() {
		// TODO Auto-generated constructor stub
	}
	
	public String NewSyntaxType(String type){
		
		try {
			type=type.split("\\\\")[2];
			if (type.contains("_"))
				type=type.substring(0,type.indexOf("_"));
			type=type.replace(" ", "_");
		}
		catch (Exception e){
		}
		return type;
		
	}
	
	public String NewSyntaxScanSeq(String type) {
	
		type=type.split("\\\\")[0];
		
		return type;
		
	}
}