package MRIFileManager;

import java.io.PrintWriter;
import java.io.StringWriter;

public class GetStackTrace {

	private static Throwable th;
	public static int numberOfError;
	private static String msg;

	public GetStackTrace(final Throwable th, String msg) {
		GetStackTrace.setTh(th);
		GetStackTrace.msg = msg;
		FileManagerFrame.getBugText().setText(FileManagerFrame.getBugText().getText()+"\n----------------\n"+getMessage());
	}

	public static String getMessage() {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		getTh().printStackTrace(pw);
		numberOfError++;
		return "\n Error No. " + numberOfError + "\n" + msg + "\n" + sw.getBuffer().toString();
	}

	public static Throwable getTh() {
		return th;
	}

	public static void setTh(Throwable th) {
		GetStackTrace.th = th;
	}
}