package philips;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;

import MRIFileManager.GetStackTrace;
import abstractClass.ImageThumb;
import abstractClass.ParamMRI2;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;

public class ImageThumbPhilips extends ImageThumb implements ParamMRI2 {

	private Image img;
	private ImagePlus imp;
	
	private URL imgURL = getClass().getResource("/BlackScreen.jpg");

	@Override
	public Image ImageThumbShow(String noSeq, String note) {
		String[] listParamToFind = { "Scan Resolution", "Images In Acquisition", "Byte Order", "Data Type", "Spatial Resolution"};
		String[] values = new String[listParamToFind.length];
		
		for (int i=0;i<listParamToFind.length;i++) {
			values[i]=hmInfo.get(noSeq).get(listParamToFind[i]);
			if (values[i] == "") {
				img = new ImageIcon(imgURL).getImage();
				return img;
			}
		}
		
		int w = Integer.parseInt(values[0].split(" +")[0]);
		int h = Integer.parseInt(values[0].split(" +")[1]);
		
		float resolX = Float.parseFloat(values[4].split(" +")[0]);
		float resolY = Float.parseFloat(values[4].split(" +")[1]);
		
		int intValue = Integer.parseInt(hmOrderImage.get(noSeq)[15].toString());
		
		String tmp = hmSeq.get(noSeq)[0];
		FileInputStream input = null;
		ByteArrayOutputStream output;
		ByteArrayInputStream inputStream = null;
		
		try {
			input = new FileInputStream(tmp);
			output = new ByteArrayOutputStream();
			tmp = values[3];
			byte[] buffer = new byte[w * h * Integer.parseInt(tmp)/8];
			int l = input.read(buffer);
			input.getChannel().position(intValue * l);
			l = input.read(buffer);
			output.write(buffer,0,l);
			input.close();
			output.close();
			byte[] data = output.toByteArray();
			inputStream = new ByteArrayInputStream(data);
			data = null;
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
	
		FileInfo fi = new FileInfo();
		
		if (values[3].contains("16")) {
			fi.fileType = FileInfo.GRAY16_UNSIGNED;
		} else if (values[3].contains("32")) {
			fi.fileType = FileInfo.GRAY32_FLOAT;
		} else {
			fi.fileType = FileInfo.GRAY8;
		}

		fi.inputStream = inputStream;
		fi.width = w;
		fi.height = h;
		fi.offset = 0;
		fi.nImages = 1;
		fi.intelByteOrder = true;

		FileOpener fo = new FileOpener(fi);
		fi = null;

		imp = fo.open(false);
		fo = null;
		imp.resetDisplayRange();

		img = imp.getImage();
		imp.close();
		imp=null;
		
		int scalX=120,scalY=120;
		
		if (Math.round(w*resolX)!=Math.round(h*resolY)) {
			if (Math.round(w*resolX)>Math.round(h*resolY)) {
				scalY=scalY*Math.round(h*resolY)/Math.round(w*resolX);
			}
			else {
				scalX=scalX*Math.round(w*resolX)/Math.round(h*resolY);
			}
		}

		img = getScaledImage(img, scalX, scalY);
		
		try {
			inputStream.close();
			inputStream.reset();
			output=null;
			inputStream=null;
			
			fi = null;

		} catch (IOException e) {
			new GetStackTrace(e, this.getClass().toString());
		}

		return img;
	}

	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(120, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 60-(w/2), 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}
}