package pt.inevo.encontra.service.impl;

import ij.ImagePlus;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import pt.inevo.encontra.geometry.Polyline;
import pt.inevo.encontra.service.VectorizationService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;


public class VectorizationServiceImpl implements VectorizationService {

	Logger _log=Logger.getLogger(VectorizationServiceImpl.class.getName());

    public static boolean debug = false;

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
        
		String _destination_dir=".";

	    ColorProcessor colorProcessor=new ColorProcessor(image);
        debug(colorProcessor, "Original");

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
        debug(binaryProcessor, "Convert to byte");

	    //binaryProcessor.setAutoThreshold(ImageProcessor.ISODATA, ImageProcessor.BLACK_AND_WHITE_LUT);
	    //binaryProcessor.autoThreshold();
	    binaryProcessor.threshold(254);
        debug(binaryProcessor, "Threshold");

	    binaryProcessor.dilate();
        debug(binaryProcessor, "Dilate");

        /*
	    binaryProcessor.medianFilter();
	    ImagePlus median=new ImagePlus(_destination_dir, binaryProcessor.createImage());
	    median.setTitle("Median");
	    median.show();
	    */

	    binaryProcessor.erode();
        debug(binaryProcessor, "Erode");

	    binaryProcessor.skeletonize();
        ImagePlus skeleton=new ImagePlus("Skeleton", binaryProcessor.createImage());
        if (debug) skeleton.show();
	    return skeleton.getBufferedImage();
	}

    private void debug(ImageProcessor processor, String title) {
        if (!debug) return;
        ImagePlus image=new ImagePlus(title, processor.createImage());
        image.show();
    }
}

