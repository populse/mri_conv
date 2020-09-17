package abstractClass;

import java.io.File;
import javax.swing.ImageIcon;

public class PrefParam {

	public static String[] nameLookAndFeel = { "System", "Nimbus", "Graphite", "HiFi"},
			
							urlLookAndFeel = { "default", // default
			"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", // Nimbus
//			"com.jtattoo.plaf.noire.NoireLookAndFeel", // Noire
//			"com.jtattoo.plaf.texture.TextureLookAndFeel", // Texture
//			"com.jtattoo.plaf.mcwin.McWinLookAndFeel", // McWin
//			"com.jtattoo.plaf.aluminium.AluminiumLookAndFeel", // Aluminium
//			"com.jtattoo.plaf.bernstein.BernsteinLookAndFeel", // Bernstein
			"com.jtattoo.plaf.graphite.GraphiteLookAndFeel", // Graphite
//			"com.jtattoo.plaf.luna.LunaLookAndFeel", // Luna
//			"com.jtattoo.plaf.mint.MintLookAndFeel", // Mint
			"com.jtattoo.plaf.hifi.HiFiLookAndFeel", // HiFi
//			"com.jtattoo.plaf.fast.FastLookAndFeel", // Fast
//			"com.jtattoo.plaf.aero.AeroLookAndFeel", // Aero
//			"com.jtattoo.plaf.acryl.AcrylLookAndFeel", // Acryl
//			"com.jtattoo.plaf.smart.SmartLookAndFeel", // Smart
	};

	public static String separator, LookFeelCurrent,
						lectBruker, lectDicom, lectParRec, lectNifTI, lectBids, outExport,
						formatCurrent, namingFileNiftiExport, namingRepNiftiExport, namingFileNiftiExportMIA,
						namingOptionsNiftiExport, namingOptionsNiftiExportMIA, projectsDir, formatPhilips,
						lectCurrent, labelButtonExport, pathDictionaryUser, listProtocolsForBids, DirectoryDataOnly;

	public static int widthScreen, heightScreen, formatCurrentInt, SeqDetail, returnCodeExit;

	public static ImageIcon iconBruker, iconDicom, iconNifTI, iconPhilips, iconBids;

	public static File FilestmpRep, FilestmpExportNifit;

	public static boolean MIA, hasMultiOrientationScanMode, is1d, hasJsonKnown, CloseAfterExport, LogExport,
			OptionLookAndFeel, ExitSystem, previewActived, simplifiedViewDicom, deidentify;

	public static int[] listWidthColumn;
	
}