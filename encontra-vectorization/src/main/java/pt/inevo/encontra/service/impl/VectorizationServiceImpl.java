package pt.inevo.encontra.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import pt.inevo.encontra.geometry.Polyline;
import pt.inevo.encontra.service.VectorizationService;

import java.util.logging.Logger;


public class VectorizationServiceImpl implements VectorizationService {

	Logger _log=Logger.getLogger(VectorizationServiceImpl.class.getName());

	public String vectorize(BufferedImage image){

		PolylinesImageRepresentation imageRep=new PolylinesImageRepresentation(image);
		ArrayList<Polyline> polylines=imageRep.PerformPolylinization();

		// Add SVG Header
		String result="<svg xmlns=\"http://www.w3.org/2000/svg\"  version=\"1.2\" width=\"100%\" height=\"100%\">\n";

		// draw polylines
		int line_count=0;
		for (Polyline polyline:polylines) {
			line_count+=polyline.GetVertexCount()-1;
			result += polyline.AsString(true,"#000000","none");
		}

		result += "</svg>";

	    return result;
	}

	public BufferedImage simplify(BufferedImage image) {

		boolean show=false;

		String _destination_dir=".";

	    ColorProcessor colorProcessor=new ColorProcessor(image);
	    ImagePlus imagePlus=new ImagePlus(null, colorProcessor.createImage());
	    imagePlus.setTitle("Original");
	    if(show) imagePlus.show();

	    try {
	    	File originalFile=new File(_destination_dir+File.separator+"original.png");
			javax.imageio.ImageIO.write(colorProcessor.getBufferedImage(), "png", originalFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
	    colorProcessor.sharpen();
	    ImagePlus sharp=new ImagePlus(_destination_dir, colorProcessor.createImage());
	    sharp.setTitle("Sharpen");
	    sharp.show();
	    */

	    /*
	    colorProcessor.findEdges();
	    colorProcessor.invert();
	    ImagePlus edges=new ImagePlus(_destination_dir, colorProcessor.createImage());
	    edges.setTitle("Edges");
	    edges.show();*/

	    ByteProcessor byteProcessor=(ByteProcessor)colorProcessor.convertToByte(true);
	    BinaryProcessor binaryProcessor=new BinaryProcessor(byteProcessor);

	    ImagePlus byteImage=new ImagePlus(null, binaryProcessor.createImage());
	    byteImage.setTitle("Convert to byte");
	    if(show) byteImage.show();

	    //binaryProcessor.setAutoThreshold(ImageProcessor.ISODATA, ImageProcessor.BLACK_AND_WHITE_LUT);
	    //binaryProcessor.autoThreshold();
	    binaryProcessor.threshold(254);
	    ImagePlus threshold=new ImagePlus(null, binaryProcessor.createImage());
	    threshold.setTitle("Threshold");
	    if(show) threshold.show();


	    binaryProcessor.dilate();
	    ImagePlus dilate=new ImagePlus(null, binaryProcessor.createImage());
	    dilate.setTitle("Dilate");
	    if(show) dilate.show();
	    /*
	    binaryProcessor.medianFilter();
	    ImagePlus median=new ImagePlus(_destination_dir, binaryProcessor.createImage());
	    median.setTitle("Median");
	    median.show();
	    */

	    binaryProcessor.erode();
	    ImagePlus erode=new ImagePlus(null, binaryProcessor.createImage());
	    erode.setTitle("Erode");
	    if(show) erode.show();

	    binaryProcessor.skeletonize();
	    ImagePlus skeleton=new ImagePlus(null, binaryProcessor.createImage());
	    skeleton.setTitle("Skeleton");
	    if(show) skeleton.show();

	    return skeleton.getBufferedImage();
	}
}

