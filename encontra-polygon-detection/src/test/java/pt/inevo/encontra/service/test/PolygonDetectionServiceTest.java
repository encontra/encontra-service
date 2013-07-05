package pt.inevo.encontra.service.test;

import junit.framework.TestCase;
import pt.inevo.encontra.geometry.Polygon;
import pt.inevo.encontra.service.impl.PolygonDetectionServiceImpl;

import java.io.IOException;

public class PolygonDetectionServiceTest extends TestCase {

	private PolygonDetectionServiceImpl service;
	private String _svg;
	private String _svg2;
	private String _svg3;
	private String _svg4;

	protected void setUp() throws Exception {
		super.setUp();

		service=new PolygonDetectionServiceImpl();

		/*
		Polygon poly = new Polygon();
		poly.AddVertex(new Point(0,0));
		poly.AddVertex(new Point(0,100));
		poly.AddVertex(new Point(100,100));
		PolygonSet polySet=new PolygonSet();
		polySet.add(poly);
		_svg=polySet.AsSVG(false);

		log.debug(String.format("SVG=%s",_svg));*/

		_svg="<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><g stroke=\"#000000\">";
		_svg+="<path  d=\"M 10 0 L 10 120\"/>";
		_svg+="<path d=\"M 0 110 L 120 110\"/>";
		_svg+="<path d=\"M 110 120 L 110 0\"/>";
		_svg+="<path d=\"M 120 10 L 0 10\"/>";
		_svg+="<path d=\"M 60 0 L 60 120\"/>";
		_svg+="<path d=\"M 0 60 L 120 60\"/>";
		_svg+="</g></svg>";

		_svg3="<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><g stroke=\"#000000\">";
		_svg3+="<path  d=\"M 10 10 L 10 110\"/>";
		_svg3+="<path d=\"M 10 110 L 110 110\"/>";
		_svg3+="<path d=\"M 110 110 L 110 10\"/>";
		_svg3+="<path d=\"M 110 10 L 10 10\"/>";
		_svg3+="<path d=\"M 10 60 L 110 60\"/>";
		_svg3+="</g></svg>";

		_svg4="<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><g stroke=\"#000000\">";
		_svg4+="<path  d=\"M 10 10 L 30 30 L 20 60 L 30 90 L 10 110\"/>";

		_svg4+="<path d=\"M 10 110 L 110 110\"/>";
		_svg4+="<path d=\"M 110 110 L 110 10\"/>";
		_svg4+="<path d=\"M 110 10 L 10 10\"/>";
		_svg4+="</g></svg>";

        _svg2="<svg contentScriptType='text/ecmascript' ";
        _svg2+="xmlns:kabeja='http://kabeja.org/xml/1.0' ";
        _svg2+="xmlns:xlink='http://www.w3.org/1999/xlink' zoomAndPan='magnify' ";
        _svg2+="contentStyleType='text/css' viewBox='0 -100.25  100.25 100.25' ";
        _svg2+="preserveAspectRatio='xMidYMid meet' xmlns='http://www.w3.org/2000/svg' ";
        _svg2+="overflow='visible' version='1.0'>";
        _svg2+="<g stroke-width='0.025062499567866325' id='draft' transform='matrix(1 0 0 -1 0 0)'>";
        _svg2+="<g fill='none' kabeja:layer-name='0' stroke-width='0.025062499567866325' color='rgb(0,0,0)' stroke='currentColor'>";
        _svg2+="<line y2=\"0\" id=\"ID43\" x1=\"20.25\" x2=\"20.25\" stroke=\"currentColor\" y1=\"100.25\"/>";
        _svg2+="<line y2=\"0\" id=\"ID44\" x1=\"80\" x2=\"80\" stroke=\"currentColor\" y1=\"100.25\"/>";
        _svg2+="<line y2=\"90\" id=\"ID45\" x1=\"0\" x2=\"100.25\" stroke=\"currentColor\" y1=\"89.75\"/>";
        _svg2+="<line y2=\"49.5\" id=\"ID46\" x1=\"0\" x2=\"100\" stroke=\"currentColor\" y1=\"49.5\"/>";
        _svg2+="<line y2=\"10\" id=\"ID47\" x1=\"0\" x2=\"100.25\" stroke=\"currentColor\" y1=\"9.75\"/>";
        _svg2+="</g>";
        _svg2+="</g></svg>";


	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

    /*
	public void testExampleSVG() {
		Drawing draw=new Drawing();
		draw.createFromSVG(new File("dxf/exemplo.svg"));
		//draw.initialize();
		//VectorizationService vectorizationService=new VectorizationServiceImpl();
		//BufferedImage discreteimage=vectorizationService.simplify(draw.getImage(500));
		//String polylinesSVG=vectorizationService.vectorize(discreteimage);
		service.detectPolygons(polylinesSVG);


		System.out.println("Detected Polygons:");
		for(Polygon p : service._polygon_set) {
			System.out.println(p.toString());
		}

		System.out.println("SVG:");
		System.out.println(service._polygon_set.AsSVG(false));


		Drawing drawing=new Drawing();
		drawing.createFromSVG(service._polygon_set.AsSVG(false));
		drawing.initialize();

		//SVGViewer viewer=new SVGViewer();
		//viewer.setSVG(drawing.getSVGDocument());

		while(true){}
	}*/

	public void testDetectPolygons() throws IOException {
		/*
		Point p1=new Point(10,0);
		//p1.SetID(1);

		Point p2=new Point(10,120);
		//p2.SetID(2);

		Point p3=new Point(0,110);
		//p3.SetID(3);

		Point p4=new Point(120,110);
		//p4.SetID(4);


		Point p5=new Point(110,120);
		//p5.SetID(5);

		Point p6=new Point(110,0);
		//p6.SetID(6);

		Point p7=new Point(120,10);
		//p7.SetID(7);

		Point p8=new Point(0,10);
		p8.SetID(8);

		service.AddLine(p1,p2);
		service.AddLine(p3,p4);
		service.AddLine(p5,p6);
		service.AddLine(p7,p8);
		*/


		//SVGViewer viewer1=new SVGViewer();
		//viewer1.setSVG(drawing1.getSVGDocument());

		service.detectPolygons(_svg);

		System.out.println("Detected Polygons:");
		for(Polygon p : service._polygon_set) {
			System.out.println(p.toString());
		}

		System.out.println("SVG:");
		System.out.println(service._polygon_set.AsSVG(false));

        assertEquals(service._polygon_set.size(), 4);

		//Drawing drawing = DrawingFactory.getInstance().drawingFromSVG(new StringReader(polygonSetSVG));

		//SVGViewer viewer=new SVGViewer();
		//viewer.setSVG(drawing.getSVGDocument());

		//while(true){}
	}

}

