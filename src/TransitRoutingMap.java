package src;

public class TransitRoutingMap {
	
	public int[] stopParent;
	public int[] stopRoute;
	public double[] stopDepTime;
	
	public TransitRoutingMap(int[] _stopParent, int[] _stopRoute, double[] _stopDepTime){
		stopParent = _stopParent;
		stopRoute = _stopRoute;
		stopDepTime = _stopDepTime;
	}
	
}
