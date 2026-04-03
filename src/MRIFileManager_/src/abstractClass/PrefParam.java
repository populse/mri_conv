package abstractClass;

import java.io.File;
import javax.swing.ImageIcon;

public class PrefParam {

	public static String[] nameLookAndFeel = { "System", "Nimbus", "Graphite", "HiFi"},
			
							urlLookAndFeel = { "default", // default
			"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", // Nimbus
			"com.jtattoo.plaf.graphite.GraphiteLookAndFeel", // Graphite
			"com.jtattoo.plaf.hifi.HiFiLookAndFeel", // HiFi
	};

//	public static String[] nameLookAndFeel = { "System", "FlatLafLight", "FlatLafDark", "FlatLafIntelliJ", "FlatLafDarcula",
//												"FlatLafmacOSLight", "FlatLafmacOSDark"},
//			
//			urlLookAndFeel = { "default", // default
//			"com.formdev.flatlaf.FlatLightLaf", // FlatLafLight
//			"com.formdev.flatlaf.FlatDarkLaf", // FlatLafDark
//			"com.formdev.flatlaf.FlatIntelliJLaf", // FlatLafIntelliJ
//			"com.formdev.flatlaf.FlatDarculaLaf", // FlatLafDarcula
//			"com.formdev.flatlaf.themes.FlatMacLightLaf", // FlatLafmacOSLight
//			"com.formdev.flatlaf.themes.FlatMacDarkLaf" // FlatLafmacOSDark
//			};

	public static String separator, LookFeelCurrent, versionSoft, SeqDetail,
						lectBruker, lectDicom, lectParRec, lectNifTI, lectBids, outExport,
						formatCurrent, namingFileNiftiExport, namingRepNiftiExport, namingFileNiftiExportMIA,
						namingOptionsNiftiExport, namingOptionsNiftiExportMIA, projectsDir, formatPhilips,
						lectCurrent, labelButtonExport, pathDictionaryUser, listProtocolsForBids, DirectoryDataOnly;

	public static int widthScreen, heightScreen, formatCurrentInt, returnCodeExit;

	public static ImageIcon iconBruker, iconDicom, iconNifTI, iconPhilips, iconBids;

	public static File FilestmpRep, FilestmpExportNifti;

	public static boolean MIA, hasMultiOrientationScanMode, is1d, hasJsonKnown, CloseAfterExport, LogExport,
			OptionLookAndFeel, ExitSystem, previewActived, simplifiedViewDicom, deidentify;

	public static int[] listWidthColumn;
	
}