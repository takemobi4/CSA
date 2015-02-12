package transit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

public class PTActivity {	
	
	public String id = UUID.randomUUID().toString();
	public String description = "";
	public String type = "";

	public double lb = 0;
	public double ub = 0;
	
	public boolean isTransit = false;
	public boolean isBike = false;
	public boolean isWalk = false;
	public boolean isWait = false;
	
	public String route = "";
	public String routeID = "";
	public String routeDirection = "";
	public String routeType = "";
	public String initLoc = "";
	public String destLoc = "";
	public String headsign = "";
	public StringBuilder details = new StringBuilder();
	
	public String fromStopID = "";
	public String toStopID = "";
	
	public double startLat = 0;
	public double startLon = 0;
	public double endLat = 0;
	public double endLon = 0;
	
	public double cost = 0;
	
	public LinkedList<Double> polyline = new LinkedList<Double>();
	public StringBuilder instructions = new StringBuilder();

	
	// each arrival time has an uncertainty
	// 0: nominal/scheduled time; 1: earliest time; 2: latest time
	public ArrayList<double[]> busArrivalTimes = new ArrayList<double[]>();

	public PTActivity(String actDescription, double actLB, double actUB, String actType){
		description = actDescription;
		lb = actLB;
		ub = actUB;
		type = actType;
	}
	
	public PTActivity(String actDescription, double actLB, double actUB, String actType, String start, String end,
			double sLat, double sLon, double eLat, double eLon){
		description = actDescription;
		lb = actLB;
		ub = actUB;
		type = actType;
		
		initLoc = start;
		destLoc = end;
		
		startLat = sLat;
		startLon = sLon;
		endLat = eLat;
		endLon = eLon;
	}
	
	public PTActivity(String actDescription, double actLB, double actUB, String actType, String busRoute, String stop1, String stop2){
		description = actDescription;
		lb = actLB;
		ub = actUB;
		type = actType;
		route = busRoute;
		initLoc = stop1;
		destLoc = stop2;
	}
	
	public PTActivity(String actDescription, double actLB, double actUB, String actType, String _route, String _id, String _routeType,
			String stop1, String stop2, double sLat, double sLon, double eLat, double eLon, String _direction){
		
		description = actDescription;
		lb = actLB;
		ub = actUB;
		type = actType;
		
		route = _route;
		routeID = _id;
		routeType = _routeType;
		routeDirection = _direction;

		initLoc = stop1;
		destLoc = stop2;
		
		startLat = sLat;
		startLon = sLon;
		endLat = eLat;
		endLon = eLon;
	}

}
