package MRIFileManager;

//import javax.swing.JOptionPane;

public class passMatlab {
	
	public passMatlab(String listFileExported) {
			
		try {
			MatlabControl mc = new MatlabControl();
			mc.eval(new String("sendList({'"+listFileExported+"'})"));

		}
		catch (Error e) {
//			JOptionPane.showMessageDialog(null, "Session Matlab not found ");
		}
	}
}