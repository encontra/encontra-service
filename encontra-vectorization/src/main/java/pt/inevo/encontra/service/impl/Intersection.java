package pt.inevo.encontra.service.impl;

import pt.inevo.encontra.geometry.Point;

import java.util.ArrayList;


public class Intersection {
	private ArrayList<Point> _points;
	private ArrayList<Integer> _pointIDs;

	public Intersection(){
		_points=new ArrayList<Point>();
		_pointIDs=new ArrayList<Integer>();
	}

	void AddPoint(Integer id)
	{

	}
	/***
	* @desc adds a point to this intersection
	* @note these points are not deleted in this object
	*/
	void AddPoint(Point point)
	{
		if (point!=null) {
			_pointIDs.add(point.GetID());
			_points.add(point);
		}
	}

	/***
	* @desc changes the points coordinates, in order that all points became coincident
     * @return
	*/
	boolean ForceCoincidency()
	{
		if (_points.size()>0) {
			int i;
			Point p = null;
			Point mass_center=new Point();
			double x_sum=0, y_sum=0;

			// calculate the centre of mass
			for (i=0; i<_points.size(); i++) {
				p = _points.get(i);
				if (p!=null) {
					x_sum+=p.GetX();
					y_sum+=p.GetY();
				}
			}

			if (i>0) {
				mass_center.SetX(x_sum/i);
				mass_center.SetY(y_sum/i);
			}

			// updates the points with the new coordinates
			for (i=0; i<_points.size(); i++) {
				p=_points.get(i);
				p.SetX(mass_center.GetX());
				p.SetY(mass_center.GetY());
			}

			return true;
		}

		return false;
	}

}

