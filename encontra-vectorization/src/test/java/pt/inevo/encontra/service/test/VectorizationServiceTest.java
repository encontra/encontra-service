package pt.inevo.encontra.service.test;


import junit.framework.TestCase;
import pt.inevo.encontra.drawing.Drawing;
import pt.inevo.encontra.drawing.DrawingFactory;
import pt.inevo.encontra.service.VectorizationService;
import pt.inevo.encontra.service.impl.VectorizationServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VectorizationServiceTest extends TestCase {

	private VectorizationService service;
	private BufferedImage image;

	public VectorizationServiceTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		service=new VectorizationServiceImpl();
		image=ImageIO.read(new File(getClass().getResource("/simple.png").getFile()));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVectorize() {
		String svg=service.vectorize(image);
        /*
        Drawing drawing=new Drawing();
		drawing.createFromSVG(svg);
		drawing.initialize();

		SVGViewer viewer=new SVGViewer();
		viewer.setSVG(drawing.getSVGDocument());
		while(true){}*/
	}

	public void testSimplify() {
		BufferedImage simplified=service.simplify(image);
		//while(true){}
	}

	public void testDetectPolygons() throws IOException {
		BufferedImage simplified=service.simplify(image);
		String svg=service.vectorize(image);

        Drawing drawing = DrawingFactory.getInstance().drawingFromSVGContent(svg);

		//SVGViewer viewer=new SVGViewer();
		//viewer.setSVG(drawing.getSVGDocument());
		//while(true){}

	}
}

