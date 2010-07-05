package pt.inevo.encontra.service.impl;


import pt.inevo.encontra.drawing.Drawing;
import pt.inevo.encontra.drawing.Primitive;
import pt.inevo.encontra.geometry.Line;
import pt.inevo.encontra.geometry.LineSet;
import pt.inevo.encontra.geometry.Point;
import pt.inevo.encontra.geometry.PolygonSet;
import pt.inevo.encontra.service.PolygonDetectionService;
import pt.inevo.jcali.CIPoint;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PolygonDetectionServiceImpl implements PolygonDetectionService {

        static Logger log = Logger.getLogger(PolygonDetectionServiceImpl.class.getName());

	public static int MAX_PROCESSING_TIME_DEFAULT=240;
	public static boolean SILENT_POLYGON_DETECTION_BY_DEFAULT=false;

	boolean _interrupted = false;
	boolean _time_exceeded = false;
	long _max_processing_time = MAX_PROCESSING_TIME_DEFAULT;
	boolean _silent = SILENT_POLYGON_DETECTION_BY_DEFAULT;


	private LineSet _line_set=new LineSet();
	public PolygonSet _polygon_set=new PolygonSet();


	/**
	* @descr Reads a set of lines to the current line set
	*/
	boolean ReadSVG(String svg) {

		Drawing drawing=new Drawing();
		drawing.createFromSVG(svg);
		drawing.initialize();


		//double max_dist=drawing.getDiagonalLength()/100;
		//drawing.reduceVertexCount(0.01);

		//double max_ang=Math.PI/36;
		for(Primitive prim:drawing.getAllPrimitives()) {
			//double length=prim.getDiagonalLength();
			if(prim.getNumPoints()>0){// && length>max_dist) {
				Point lastPoint=prim.getPoint(0);
				Point newPoint;
				for(int i=1;i<prim.getNumPoints();i++)
				{
					newPoint=prim.getPoint(i);
                    /* Only add if distance greater than 0.5
					double dx=Math.abs(newPoint.x-lastPoint.x);
					double dy=Math.abs(newPoint.y-lastPoint.y);

					double ang=Math.atan2(dy, dx);

					double dist=Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
           			*/
                    //if( (i==(prim.getNumPoints()-1)) || ( (dist>max_dist)  && (ang>max_ang) )) {
                        //log.info(String.format("Line(%f,%f,%f,%f)",lastPoint.x,lastPoint.y,newPoint.x,newPoint.y));
                        AddLine(lastPoint.x,lastPoint.y,newPoint.x,newPoint.y);
                        lastPoint=newPoint;
                    //}
				}
			}
		}
		//_line_set.show();

		log.info(".. lineset created!");



        return true;
	}


	/**
	* @descr Adds a line to the current line set
	*/
	public void AddLine(double x1, double y1, double x2, double y2)
	{
		_line_set.add(new Line(x1, y1, x2, y2));
	}

	public void AddLine(Point p1, Point p2)
	{
		_line_set.add(new Line(p1,p2));
	}

	/**
	* @descr Resets line and polygon sets
	*/
	void Reset()
	{
		_line_set.clear();
		_polygon_set.clear();
	}

	public PolygonSet detectPolygons(String svg) {
		Reset();

		log.info("Reading SVG...");
		ReadSVG(svg);
        log.info("Detecting Polygons...");
		detectPolygons();
		return _polygon_set;
	}

	/**
	* @descr Performs polygon detection
	*/
	public boolean detectPolygons()
	{
		//STARTING_PROCESS_MESSAGE("Polygon detection");

		_interrupted = _time_exceeded = false;

		log.info(String.format("Line set contains %d lines.", _line_set.size()));


         log.info("Starting line normalization...");
		_line_set.Normalize();

		//_line_set.show("Normalized LineSet");

		log.info("Removing intersections...");
		if (!_line_set.RemoveIntersections()) {
			log.log(Level.SEVERE,"Could not successfully remove line intersections.");
			return false;
		}

		//_line_set.show("LineSet - Remove intersections");

		log.info(String.format("After removal, line set contains %d lines.", _line_set.size()));

         log.info("Sortings lines...");
		_line_set.Sort();

        log.info(String.format("Creating polygon set from %d lines ...",_line_set.size()));

		if (!_polygon_set.Construct(_line_set)) {
			log.log(Level.SEVERE,"Error constructing the polygon set.");
			return false;
		}

		log.info(String.format("Polygon set contains %d polygons.", _polygon_set.size()));

		//ENDING_PROCESS_MESSAGE();
		return true;
	}


	boolean CreateSVGwithPolygons(String filename) {

		/*
		FILE * fp = fopen(filename.mb_str(),"w");

		if (!fp) {
			wxLogError(wxT("Could not create SVG file '%s'!"),filename.c_str());
			return false;
		}

		if (!PolygonDetector::Silent())
			wxLogMessage(wxT("Creating SVG file '%s' with detected polygons."), filename.c_str());

		if (!fprintf(fp, _polygon_set.AsSVG().mb_str())) {
			wxLogError(wxT("Could not write SVG contents in specified file."));
			fclose(fp);
			return false;
		}

		fclose(fp);*/
		return true;
	}
}
