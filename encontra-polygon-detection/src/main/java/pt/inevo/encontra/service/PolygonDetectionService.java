package pt.inevo.encontra.service;

import pt.inevo.encontra.geometry.PolygonSet;

public interface PolygonDetectionService {

	public PolygonSet detectPolygons(String svg);
}
