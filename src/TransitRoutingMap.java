package src;

public class TransitRoutingMap {
	
	public int[] backTrackingFromStop;
	public int[] backTrackingRoute;
	public double[] backTrackingTime;
	
	public TransitRoutingMap(int[] _backTrackingFromStop, int[] _backTrackingRoute, double[] _backTrackingTime){
		backTrackingFromStop = _backTrackingFromStop;
		backTrackingRoute = _backTrackingRoute;
		backTrackingTime = _backTrackingTime;
	}
	
}
