package abstractClass;

import java.io.IOException;
import java.util.HashMap;

public interface ListParam2 {

	public abstract HashMap<String, String> ListParamValueAcq(String it) throws IOException;

	public abstract Object[] ListOrderStackAcq(String dim, String nImage);
	
	public abstract HashMap<String, String> ListParamValueCal(String it) throws IOException;
	
	public abstract Object[] ListOrderStackCal(String dim, String nImage);

}
