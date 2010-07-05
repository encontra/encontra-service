package pt.inevo.encontra.service.test;


import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;


import junit.framework.TestCase;
import pt.inevo.encontra.geometry.PolygonSet;
import pt.inevo.encontra.service.VectorizationService;
import pt.inevo.encontra.service.impl.VectorizationServiceImpl;

public class VectorizationServiceTest extends TestCase {

	private VectorizationService service;
	private BufferedImage image;

	public VectorizationServiceTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		service=new VectorizationServiceImpl();
		image=ImageIO.read(new File("test/simple.png"));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVectorize() {
		String svg=service.vectorize(image);
		//while(true){}
	}

	public void testSimplify() {
		BufferedImage simplified=service.simplify(image);
		//while(true){}
	}

	public void testDetectPolygons() {
		BufferedImage simplified=service.simplify(image);
		String svg=service.vectorize(image);

	}
}

