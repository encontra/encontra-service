package pt.inevo.encontra.service;

import java.awt.image.BufferedImage;

public interface VectorizationService {
	public String vectorize(BufferedImage image);
	public BufferedImage simplify(BufferedImage image);
}
