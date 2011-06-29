package pt.inevo.encontra.service.impl;

import ij.ImagePlus;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Logger;

import pt.inevo.encontra.geometry.*;


public class PolylinesImageRepresentation {

	Logger _log=Logger.getLogger(PolylinesImageRepresentation.class.getName());

	/* default thresholding value for douglas-peuker tolerance*/
	public static double DEFAULT_POLYLINE_THRESHOLD_FACTOR=0.001; /* 0.05% of smaller image side */

	public static int NUM_NEIGHBOURS=8;
	public static int MAX_POLYLINE_TRACKING_RECURSION_LEVEL=512;

	public static int BINARY_PIXEL_OFF= 0x000000;
	public static int BINARY_PIXEL_ON = 0x0000FF;
	public static int BINARY_PIXEL_PON = BINARY_PIXEL_ON-1;
	public static int BINARY_PIXEL_IGNORE = BINARY_PIXEL_ON-2;
	public static int BINARY_PIXEL_USED = 0x02;
	public static int BINARY_PIXEL_ERASED = 0x01;

	private int _polyline_tracking_recursion_level;
	private int _maximum_recursion_level_reached;

	private int width;
	private int height;
	// Array with RGB values
	//private int [][] image_data;

	private ArrayList<Polyline> polylines;

	private Point[][] _image_points;

	private ArrayList<Intersection> _intersections;

	private BinaryProcessor binaryProcessor;

	public PolylinesImageRepresentation(BufferedImage image){
		ColorProcessor colorProcessor=new ColorProcessor(image);
		ByteProcessor byteProcessor=(ByteProcessor)colorProcessor.convertToByte(false);
		binaryProcessor=new BinaryProcessor(byteProcessor);

		width=binaryProcessor.getWidth();//img.getWidth();
		height=binaryProcessor.getHeight();//img.getHeight();

		_polyline_tracking_recursion_level = 0;
		_maximum_recursion_level_reached = 0;

		// Array with RGB values
		//image_data=CreateImageData();
	}

	/***
	* @desc peroforms a polylinization of loaded image
	* @return true if successful, false otherwise
	*/
	public ArrayList<Polyline> PerformPolylinization()
	{
		boolean show=false;
		// inverts image stored on _p_image_data
		// lines will be 0xFF and empty will be 0x00
		//InvertImageData();
		binaryProcessor.invert();
		ImagePlus invert=new ImagePlus(null, binaryProcessor.createImage());
		invert.setTitle("Invert");
		if(show) invert.show();

		// make a 3 pixel clear boundary on image
		ClearBorder();
		ImagePlus border=new ImagePlus(null, binaryProcessor.createImage());
		border.setTitle("Invert");
		if(show) border.show();

		// create the image points array
		//CreateImagePoints(widht,height,image_data);
		_image_points=new Point[width][height];

		// create the polyline array
		//_p_polylines_array = new PolylineArray();
		polylines=new ArrayList<Polyline>();

		_intersections=new ArrayList<Intersection>();

		// performs polylinization from upper-left point and ends
		Point p=new Point(1,1);

		while ( (p=NextOnPixel(p))!=null ) {
			if (!Polylinization(p, new Polyline(), null)) {
				_log.severe("Error tracking polyline on polylinization.");
				// in case of error we need to free used memory
				//FreeImagePoints();
				//wxDELETE(_p_polylines_array);
				//SHOW_MESSAGE_MAX_RECURSION_LEVEL_REACHED;
				return null;
			}
		}
		// frees image points
		//FreeImagePoints();

		int i=0;
		Polyline polyline;

		// adjusts polylines endpoints
		for (i=0; i<_intersections.size();i++)
			_intersections.get(i).ForceCoincidency();

		// remove polylines with only one vertex
		// note: after this step is not safe to use _intersections
		for (i=polylines.size();i>0;) {

			polyline = polylines.get(--i);
			polyline.Simplify(Math.min(width,height)*DEFAULT_POLYLINE_THRESHOLD_FACTOR);

			if (polyline.GetVertexCount()<2) {
				polylines.remove(i);
				//wxDELETE(p_polyline);
			}
		}

		//SHOW_MESSAGE_MAX_RECURSION_LEVEL_REACHED;
		return polylines;
	}


	public String toVRML(ArrayList<Polyline> polylines)
	{

		String s="#VRML V2.0 utf8\n#\n# File automatically generated from polylines\n\n ";

		for (Polyline polyline : polylines) {
			if (polyline!=null) s+= polyline.AsVRML();
		}
		return s;
	}

	/***
	* @desc inverts image stored on image_data
	* @note on pixels will be 0xFF and off ones will be 0x00
	*
	void InvertImageData()
	{

	    if (image_data!=null) {
	        int x, y;

	        for (y = 0; y < height; y++)
	            for (x = 0; x < width; x++)
	            	image_data[y][x] = 0xff - image_data[y][x];
	    }
	}*/

	/***
	* @returns a pointer to image data based on _p_binary_image
	* @note the image data created here MUST be deleted elsewhere
	**
	int [][] CreateImageData()
	{
	   int x, y;

	    // allocates memory for image data
	   int [][] _image_data=new int[binaryProcessor.getWidth()][binaryProcessor.getHeight()];

	    for (y=0; y<binaryProcessor.getHeight();y++)
	        for (x=0;x<binaryProcessor.getWidth();x++){
	        	int pixel;
	        	pixel=binaryProcessor.get(x, y);

	            _image_data[y][x] = pixel;
	        }

	    return _image_data;
	}*/



	/***
	* @desc creates (reserves memory) for image points array
	* @note the image points pointers inserted in this array are not
	*       deleted within this class. They MUST be deleted elsewhere
	*/
	void CreateImagePoints(int width, int height)
	{
	    int x, y;

	    // allocates memory for image data
	    Point [][] image_points=new Point[width][height];


	    //for (x=0; x<_width;x++)
	    //    for (y=0;y<_height;y++)
	    //        _p_image_points[x][y] = NULL;
	}


	/***
	* @desc sequentially search for next ON point
	* @return true if any found, false otherwise
	* @note start on point coordinates and if found the
	*     coordinates of next on pixel will be stored there
	*/
	Point NextOnPixel(Point point)
	{
		if (point==null)
			return null;

		int width_minus_1 = width - 1;
		int height_minus_1 = height - 1;

		double x, y;
		// finds next on pixel
	    for (y = point.getY(); y < height_minus_1; y++)
			for (x = (y==point.getY()?point.getX():1); x< width_minus_1;x++)
				if (binaryProcessor.get((int)x, (int)y) == BINARY_PIXEL_ON) {
					point.setX(x); point.setY(y);
					return point;
				}

		return null;

	}


	/***
	* @desc make a 3 pixel clear boundary on image
	*/
	void ClearBorder()
	{
		if (binaryProcessor!= null) {

			for (int y = 0; y < height; y++) {
				// image_data[y][0] = image_data[y][width-1] = BINARY_PIXEL_OFF;
				binaryProcessor.set(width-1, y, BINARY_PIXEL_OFF);
				binaryProcessor.set(0, y, BINARY_PIXEL_OFF);

				//image_data[y][1] = image_data[y][width-2] = BINARY_PIXEL_OFF;
				binaryProcessor.set(width-2, y, BINARY_PIXEL_OFF);
				binaryProcessor.set(1, y, BINARY_PIXEL_OFF);

				//image_data[y][2] = image_data[y][width-3] = BINARY_PIXEL_OFF;
				binaryProcessor.set(width-3, y, BINARY_PIXEL_OFF);
				binaryProcessor.set(2, y, BINARY_PIXEL_OFF);
			}
			for (int x = 1; x < width; x++) {
				//image_data[0][x] = image_data[height-1][x] = BINARY_PIXEL_OFF;
				binaryProcessor.set(x, 0, BINARY_PIXEL_OFF);
				binaryProcessor.set(x, height-1, BINARY_PIXEL_OFF);

				//image_data[1][x] = image_data[height-2][x] = BINARY_PIXEL_OFF;
				binaryProcessor.set(x, 1, BINARY_PIXEL_OFF);
				binaryProcessor.set(x, height-2, BINARY_PIXEL_OFF);

				//image_data[2][x] = image_data[height-3][x] = BINARY_PIXEL_OFF;
				binaryProcessor.set(x, 2, BINARY_PIXEL_OFF);
				binaryProcessor.set(x, height-3, BINARY_PIXEL_OFF);
			}
		}
	}

	/***
	* @return coordinates of neighbour point, based on freeman code
	* @see Michael Seul , Lawrence O'Gorman and Michael Sammon
	*      "Practical Algorithms for Image Analysis",
	*      Cambridge University Press, pp. 178 (2000)

	*/
	public Point Neighbour(Point point, int freeman_code)
	{
		// TODO - These deltas should be static
		int delta_x[] = {0,1,1,1,0,-1,-1,-1};
		int delta_y[] = {-1,-1,0,1,1,1,0,-1};

		if (freeman_code>=NUM_NEIGHBOURS) {
			_log.severe("Invalid Freeman Code when determining neighbor coordinates.");
			return new Point(0,0);
		}

		return new Point(point.getX()+delta_x[freeman_code], point.getY()+delta_y[freeman_code]);
	}



	/***
	* @desc determines which neighbors are ON
	* @return count of ON neighbours
	*/
	int NeighboursOn(Point point,Point [] neighbours)
	{
		if (binaryProcessor==null)
			return 0;

		Point p;
		int neighbours_on = 0;

		for (int neighbours_counter=0; neighbours_counter<NUM_NEIGHBOURS;neighbours_counter++) {
			neighbours[neighbours_counter]=new Point(0,0);
		}

		for (int code=0;code<NUM_NEIGHBOURS; code++) {
			p = Neighbour(point, code);
			int data=binaryProcessor.get((int)p.getX(),(int)p.getY());

			if ( data== BINARY_PIXEL_ON) {
				neighbours[neighbours_on] = p;
				neighbours_on++;
			}
		}

		return neighbours_on;
	}

	/***
	* @desc set the value of all neighbours
	*/
	void SetNeighboursValues(Point point, int value, int [] original_values)
	{
		if (binaryProcessor!=null) {
			Point p;
			for (int code=0;code<NUM_NEIGHBOURS;code++) {
				p = Neighbour(point, code);

				// store original value if requested
				if (original_values!=null)
					original_values[code] = binaryProcessor.get((int)p.getX(),(int)p.getY());

				// sets the new value
				binaryProcessor.set((int)p.getX(),(int)p.getY(), value);
			}
		}
	}

	/***
	* @desc restores the values on neighbours
	*/
	void RestoreNeighboursValues(Point point, int [] original_values)
	{
		if (binaryProcessor!=null) {
			Point p;
			for (int code=0;code<NUM_NEIGHBOURS;code++) {
				p = Neighbour(point, code);

				// store original value if given
				if (original_values!=null)
					binaryProcessor.set((int)p.getX(),(int)p.getY(), original_values[code]);
			}
		}
	}





	/***
	* @desc peroforms a polylinization of loaded image from point, using polyline
	* @return true if successful, false otherwise
	*/
	boolean Polylinization(Point point, Polyline polyline, Intersection p_intersection)
	{
		if (polylines==null || binaryProcessor==null) {
			_log.severe("Cannot find polylines array during polylinization.");
			return false;
		}

		if (!TrackPolyline(point, polyline, p_intersection)) {
			_log.severe(String.format("Error tracking polyline at point <%d,%d>",point.x, point.y));
			//wxDELETE(polyline);
			return false;
		}

		// simplification cannot be done here. Done in 'PerformPolylinization()'
		//polyline->Simplify(MIN(_width,_height)*DEFAULT_POLYLINE_THRESHOLD_FACTOR);

		polylines.add(polyline);

		return true;


//		unsigned char neighbours_values[NUM_NEIGHBOURS];
//		Point neighbours[NUM_NEIGHBOURS];
//		Point p = point;
//		Point current_neighbour;
//		size_t neighbours_on, i;
//		Polyline2D * p_branching_polyline = NULL;
	//
	//
//		// add current pixel to polyline
//		polyline->AddVertex(new Point2D(point.x, point.y));
//		// flag current pixel as OFF
//		_p_image_data[p.y][p.x] = BINARY_PIXEL_OFF;
	//
//		// let's see which neighbors are ON
//		neighbours_on = NeighboursOn(p, neighbours);
	//
	//
//		// cycle while there are neighbours
//		do {
//			// flag current pixel as OFF
//			_p_image_data[p.y][p.x] = BINARY_PIXEL_OFF;
	//
//			// let's see which neighbors are ON
//			neighbours_on = NeighboursOn(p, neighbours);
//
//			// in case of just one neighbour on
//			if (neighbours_on>=1) {
//				// add that point of polyline and mark it as off
//				p = neighbours[0];
//				polyline->AddVertex(new Point2D(p.x,p.y));
//				_p_image_data[p.y][p.x] = BINARY_PIXEL_OFF;
//			}
//			//} else {
//			if (neighbours_on>1) {
//				// sweep trough all neighbours on
//				//for (i=0; i<neighbours_on; i++){
//				while (neighbours_on>0) {
//					// i = --neighbours_on;
//					current_neighbour = neighbours[--neighbours_on];
//					p_branching_polyline = new Polyline2D();
	//
//					// p_branching_polyline->AddVertex(new Point2D(point.x, point.y));
//					// p_branching_polyline->AddVertex(new Point2D(p.x, p.y));
//					// p_branching_polyline->AddVertex(new Point2D(current_neighbour.x, current_neighbour.y));
	//
//					// lets put all neighbours as ignore
//					// SetNeighboursValues(p, BINARY_PIXEL_IGNORE, neighbours_values);
//					// neighbours_values[i] = BINARY_PIXEL_ON;
	//
//					// perform polylinization to this branch
//					if (!Polylinization(current_neighbour, p_branching_polyline)) {
//						wxDELETE(p_branching_polyline);
//						wxLogError("Branching failed on pixel <%d,%d>.", current_neighbour.x, current_neighbour.y);
//						return false;
//					}
	//
//					// marks current pixel as off
//					// neighbours_values[i] = BINARY_PIXEL_OFF;
//					// restore neihbours values
//					//RestoreNeighboursValues(p, neighbours_values);
//				}
//			}
//		} while (neighbours_on>0 );
	//
//		polyline->Simplify(MIN(_width,_height)*DEFAULT_POLYLINE_THRESHOLD_FACTOR);
	//
//		if (polyline->GetVertexCount()>1)
//			_p_polylines_array->Add(polyline);
//		else
//			wxDELETE(polyline);
	//
//		return true;
	}

	/***
	* @desc tracks the polyline
	*/
	boolean TrackPolyline(Point point, Polyline polyline, Intersection intersection)
	{
		_polyline_tracking_recursion_level++;

		if ( polylines==null || binaryProcessor==null) {
			_log.severe("Cannot find polylines array during polylinization.");
			_polyline_tracking_recursion_level--;
			return false;
		}

		Point [] neighbours=new Point[NUM_NEIGHBOURS];

		Point current_neighbour;
		int neighbours_on;
		Polyline branching_polyline = null;
		Point endpoint;

		// add current pixel to polyline
		polyline.AddVertex(endpoint = new Point(point.x, point.y));

		if (_image_points[(int)point.getX()][(int)point.getY()]!=null) {
			if (intersection!=null)
				intersection.AddPoint(_image_points[(int)point.getX()][(int)point.getY()]);
		}

		// fill the points array
		_image_points[(int)point.getX()][(int)point.getY()] = endpoint;

		// if this is an intersection
		if (intersection!=null) {
			// add this end point to intersection
			intersection.AddPoint(endpoint);
		}

		// lets see if maximum recursion level reached
		if (_polyline_tracking_recursion_level>MAX_POLYLINE_TRACKING_RECURSION_LEVEL) {
			_maximum_recursion_level_reached++;
			_polyline_tracking_recursion_level--;
			return true;
		}

		// flag current pixel as OFF
		binaryProcessor.set((int)point.getX(),(int)point.getY(), BINARY_PIXEL_OFF);


		// let's see which neighbors are ON
		neighbours_on=NeighboursOn(point,neighbours);

		// in case of just one neighbour on
		if (neighbours_on==1) {
			// add that point of polyline and mark it as off
			current_neighbour = neighbours[0];
			boolean result = TrackPolyline(current_neighbour, polyline, null);
			_polyline_tracking_recursion_level--;
			return result;
		}

		if (neighbours_on>1) {
			// if intersection not detected before
			if (intersection==null) {
				// create a new one and add current point
				intersection = new Intersection();
				intersection.AddPoint(endpoint);
				_intersections.add(intersection);
			}
			// sweep trough all neighbours on
			while (neighbours_on>0) {
				current_neighbour = neighbours[--neighbours_on];
				branching_polyline = new Polyline();
				//branching_polyline.AddVertex(new Point(point.x, point.y));

				// perform polylinization to this branch
				if (!Polylinization(current_neighbour, branching_polyline, intersection)) {
					//wxDELETE(p_branching_polyline);
					_log.severe(String.format("Branching failed on pixel <%d,%d>.", current_neighbour.x, current_neighbour.y));
					_polyline_tracking_recursion_level--;
					return false;
				}
			} /* while */
		} /* if */
		_polyline_tracking_recursion_level--;
		return true;

	}

	/***
	* @return pointer to a newly created table set (already normalized),
	*         containing lines detected in image
	* @note do not forget to DELETE THIS TABLE SET ELSEWHERE
	*/
	// TableSet * PolylinesImageRepresentation::GetTableSet()
	// {
//	 	if (!_p_polylines_array)
//	 		return NULL;
	//
//	 	Polyline2D * polyline;
//	 	TableSet * p_table_set = new TableSet();
//	 	EntitiesTable * p_entities_table = p_table_set->GetEntitiesTable();
	//
//	 	for (size_t i=0; i<_p_polylines_array->GetCount();i++) {
//	 		polyline = _p_polylines_array->Item(i);
//	 		p_entities_table->AddEntity(new Polyline2D(polyline));
//	 		// TODO: remove next line after debugging
//	 		// wxLogMessage(polyline->AsString());
//	 	}
//
//	 	p_entities_table->Normalize(p_entities_table->GetBoundingBox());
	//
//	 	return p_table_set;
	//
	// }
}

