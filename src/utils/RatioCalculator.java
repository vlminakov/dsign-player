package utils;

public class RatioCalculator {
	
	public double	_COEF_X	= 1;
	public double	_COEF_Y	= 1;

	public int		shiftX, shiftY;

	public RatioCalculator(double width, double height, double _baseWidth, double _baseHeight) {
		double tempX = width / _baseWidth;
		double tempY = height / _baseHeight;
		if (tempX * _baseHeight <= height) {
			_COEF_X = tempX;
			_COEF_Y = tempX;
		}
		else {
			_COEF_X = tempY;
			_COEF_Y = tempY;
		}
		shiftX = (int) Math.abs((width - _COEF_X * _baseWidth) / 2);
		shiftY = (int) Math.abs((height - _COEF_Y * _baseHeight) / 2);
	}
}
