package src;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import transit.PTActivity;
import transit.PTRoute;
import transit.PTRouteComparator;
import utils.Distance;
import utils.NumUtils;


public class CSA {

	static int routeNum;
	static int routeNumDouble; // two directions for each route
	static int tripNum;
	static int stopTimeNum;
	static int stopNum;
	static int connectNumDaily=0;
	static double walkTransferDist=500; //in meters;
	static double walkSpeed=1;//m/s
	public static String[] stopName = new String[]{};
	public static String[] stopId = new String[]{};
	public static double[] stopLat = new double[]{};
	public static double[] stopLon = new double[]{};
	public static String[] routeId = new String[]{};
	public static String[] routeName = new String[]{};

	public static TimeSequencePair[] connectDepTimePair = new TimeSequencePair[]{};
	public static double[] connectArrTime = new double[]{};
	public static double[] connectDepTime = new double[]{};
	public static String[] connectTripId = new String[]{};
	public static int[] connectFromStop = new int[]{};
	public static int[] connectToStop = new int[]{};
	public static int[] connectRoute = new int[]{};
	
	public static double[] connectArrTimeOrdered = new double[]{};
	public static String[] connectTripIdOrdered = new String[]{};
	public static int[] connectFromStopOrdered = new int[]{};
	public static int[] connectToStopOrdered = new int[]{};
	public static int[] connectRouteOrdered = new int[]{};
	
	
	//for checking if each trip in a route have the same number of stops
	public static int[][] tripStopCount = new int[][]{};
	

	//walking transfer between stops
	public static List<Integer> walkTransfer = new ArrayList<Integer>();
	public static List<Double> walkTransferTime = new ArrayList<Double>();
	public static int[] walkTransferArr = new int[]{};
	public static double[] walkTransferTimeArr = new double[]{};
	public static int[] walkTransferBeginIndex = new int[]{};
	public static int[] walkTransferEndIndex = new int[]{};
	
	
	//string and integer value of stop ID
	public static HashMap<String, Integer> stopIdMap = new HashMap<String, Integer>();
	//string and integer value of route ID
	public static HashMap<String, Integer> routeIdMap = new HashMap<String, Integer>();
	//(trip id, integer route id) pair
	public static HashMap<String, Integer> tripRouteMap = new HashMap<String, Integer>();
	//(trip id, trip headsign) pair
	public static HashMap<String, String> tripHeadSignMap = new HashMap<String, String>();
	//(trip id, trip direction) pair
	public static HashMap<String, String> tripDirectionMap = new HashMap<String, String>();
	//(trip id, service id) pair
	public static HashMap<String, String> tripServiceIdMap = new HashMap<String, String>();
	//which service IDs are running today
	public static Set<String> serviceId;
	public static int currentDay; //like 20140101
	public static int currentWeekDay; //1 to 7
	
	
	//to generate shapes
	public static HashMap<String, ArrayList<Double>> shapeMap = new HashMap<String, ArrayList<Double>>();
	public static HashMap<String, String> tripShapeMap = new HashMap<String, String>(); 
	public static Set<String> shapeSet = new HashSet<String>(); 
	
	static double busUncertainty = 0.05; // uncertainty in bus
	static double busArriveUncertainty = 1.5; // uncertainty in bus (in millisecond)
	static double walkingSpeed = 60.0; // meter per minute
	static double stop_transfer_time=0.0035; 
	
	public static int getCurrentDay(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter; 
		formatter = new SimpleDateFormat ("yyyyMMdd"); 
		return Integer.parseInt(formatter.format(cal.getTime())); 
	}
	
	public static int getCurrentWeekDay(){
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);//sunday is 1;
		dayOfWeek--;
		if (dayOfWeek==0){
			dayOfWeek=7;
		}
		return dayOfWeek; 
	}
	
	// distance between two points in meters;
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return dist * meterConversion;
	}
	
	//change this to be linear running time
	public static ArrayList<Integer> getCharacterIndex(String s, String c){
		ArrayList<Integer> indices = new ArrayList<Integer>();
		int index = s.indexOf(c);
		while (index >= 0) {
			indices.add(index);
		    index = s.indexOf(c, index + 1);
		}
		
		return indices;
	}
	
	public static double convertTimeToDouble(String stringTime){
		double hour=Double.parseDouble(stringTime.substring(0,2));
		double minute=Double.parseDouble(stringTime.substring(3,5));
		double second=Double.parseDouble(stringTime.substring(6,8));
		return hour/24+minute/1440+second/86400;
	}
	
	public static int countLines(String filename) throws FileNotFoundException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		int lines = 0;
		try {
			while (reader.readLine() != null) lines++;
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lines;
	}
	
	// do a binary search to find the begin index in 
	// the array of connection departure times
	public static int findBeginIndex(double t){
		int lo=0; 
		int hi=connectNumDaily;
		while (lo<hi){
			int mid=lo+(hi-lo)/2;
			if (connectDepTime[mid]>=t){
				hi=mid;
			}
			else{
				lo=mid+1;
			}
		}
		if (hi>0){
			return hi-1;
		}
		else{
			return hi;
		}
	}
	
	public static void parseStop(String filename) throws IOException{
		BufferedReader br;
		String line;
		int expNum;
		br = new BufferedReader(new FileReader(filename));
		line = br.readLine();
		for (int i=0;i<stopNum;i++){
			line = br.readLine();
			String[] elements = line.split("\"");
			stopId[i]=elements[1];
			ArrayList<Integer> indices=getCharacterIndex(line,",");
			expNum=0;
			for (int j=0;j<6;j++){
				if (!line.substring(indices.get(j)+1,indices.get(j)+2).equals(",")&&!line.substring(indices.get(j)+1,indices.get(j)+2).equals("\"")){
					expNum++;
				}
			}
			stopName[i]=line.substring(indices.get(1)+2,indices.get(2+expNum)-1);
			stopLat[i]=Double.parseDouble(line.substring(indices.get(3+expNum)+2,indices.get(4+expNum)-1));
			stopLon[i]=Double.parseDouble(line.substring(indices.get(4+expNum)+2,indices.get(5+expNum)-1));
			stopIdMap.put(stopId[i], i);
		}
		br.close();
		//System.out.println(stopId[6451]);
		//walking transfer
		
		walkTransferBeginIndex = new int[stopNum];
		walkTransferEndIndex = new int[stopNum];
		double dist;
		
		for (int i=0;i<stopNum;i++){
			if (i%100==0){
				System.out.println(i);
			}
			
			walkTransferBeginIndex[i]=walkTransfer.size();
			for (int j=0;j<stopNum;j++){
				dist=distFrom(stopLat[i],stopLon[i],stopLat[j],stopLon[j]);
				if (dist<walkTransferDist){
					walkTransfer.add(j);
					walkTransferTime.add(dist/walkSpeed);
				}
			}
			//not including the last one
			walkTransferEndIndex[i]=walkTransfer.size();
		}
		int walkTransferSize=walkTransfer.size();
		walkTransferArr = new int[walkTransferSize];
		walkTransferTimeArr = new double[walkTransferSize];
		for (int i=0;i<walkTransferSize;i++){
			walkTransferArr[i]=walkTransfer.get(i);
			walkTransferTimeArr[i]=walkTransferTime.get(i);
		}
		
	}
	
	public static void parseRoute(String filename) throws IOException{
		BufferedReader br;
		String line;
		br = new BufferedReader(new FileReader(filename));
		line = br.readLine();
		for (int i=0;i<routeNum;i++){
			line = br.readLine();
			//ArrayList<Integer> indices=getCharacterIndex(line,",");
			//routeId[i]=line.substring(1,indices.get(0)-1);
			String[] elements = line.split("\"");
			//distinguish between two directions
			routeId[2*i]=elements[1]+"D0";
			routeIdMap.put(routeId[2*i], 2*i);
			routeId[2*i+1]=elements[1]+"D1";
			routeIdMap.put(routeId[2*i+1], 2*i+1);
			
			elements = line.split(",");

			if (elements[2].length() > elements[3].length()){
				routeName[2*i] = elements[2].replace("\"", "");
				routeName[2*i+1] = elements[2].replace("\"", "");
			} else {
				routeName[2*i] = elements[3].replace("\"", "");
				routeName[2*i+1] = elements[3].replace("\"", "");
			}
		}
		br.close();
	}
	
	public static void parseTrip(String tripFileName, String calendarFileName, String calendarDateFileName) throws IOException{
		
		BufferedReader br;
		String line;
		int startDate;//for calendar
		int endDate;//for calendar
		
		//parse calendar to see which service ids are used
		br = new BufferedReader(new FileReader(calendarFileName));
		line = br.readLine();
		while ((line = br.readLine()) != null){
	        String[] elements = line.split("\"");
	        if (elements[2].substring(2*currentWeekDay-1,2*currentWeekDay).equals("1")){
	        	startDate=Integer.parseInt(elements[3]);
		        endDate=Integer.parseInt(elements[5]);
		        if (currentDay>=startDate&&currentDay<=endDate){
		        	serviceId.add(elements[1]);
		        }
	        }
		}
		br.close();
		
		//parse calendar dates to add/minus special cases
		br = new BufferedReader(new FileReader(calendarDateFileName));
		line = br.readLine();
		while ((line = br.readLine()) != null){
			String[] elements = line.split("\"");
			//System.out.println(Integer.parseInt(elements[3]));
			if (Integer.parseInt(elements[3])==currentDay){
				if (elements[4].equals(",2")){
					serviceId.remove(elements[1]);
					//System.out.println(elements[1]);
				}
				else{
					serviceId.add(elements[1]);
				}
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(tripFileName));
		line = br.readLine();
		for (int i=0;i<tripNum;i++){
			line = br.readLine();
		    //ArrayList<Integer> indices=getCharacterIndex(line,",");
		    //tripRouteMap.put(line.substring(indices.get(1)+2,indices.get(2)-1),routeIdMap.get(line.substring(1,indices.get(0)-1)));  
			String[] elements = line.split("\"");
			//for now don't consider boat etc
			if (elements.length<11){
				continue;
			}
			if (serviceId.contains(elements[3])){
				tripRouteMap.put(elements[5],routeIdMap.get(elements[1]+"D"+elements[10].substring(1,2)));
				tripHeadSignMap.put(elements[5], elements[7]);
				tripDirectionMap.put(elements[5],elements[10].substring(1,2));
				tripServiceIdMap.put(elements[5], elements[3]);
				//trip id to shape id map
				if (elements.length>=14){
					tripShapeMap.put(elements[5], elements[13]);
					shapeSet.add(elements[13]);
				}
			}
		}
		br.close();
	}
	
	public static void parseShape(String filename) throws IOException{
		String line;
		ArrayList<Double> lonlatArray=new ArrayList<Double>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		line = br.readLine();
		String currentShape="-1";
		while ((line = br.readLine())!= null){
			String[] elements = line.split("\"");
			if (!currentShape.equals(elements[1])){
				//process the previous shape
				if (shapeSet.contains(currentShape)){
					shapeMap.put(currentShape,lonlatArray);
				}
				
				if (shapeSet.contains(elements[1])){
					//start the new shape
					lonlatArray.clear();
					lonlatArray.add(Double.parseDouble(elements[5]));
					lonlatArray.add(Double.parseDouble(elements[3])); 
				}
				currentShape=elements[1];
			}
			else{
				if (shapeSet.contains(currentShape)){
					lonlatArray.add(Double.parseDouble(elements[5]));
					lonlatArray.add(Double.parseDouble(elements[3]));
				}
			}
			
		}
		//the last shape
		if (shapeSet.contains(currentShape)){
			shapeMap.put(currentShape,lonlatArray);
		}
		br.close();
	}
	
	private static List<Double> findPolyline(String tid, int stop1, int stop2){

		List<Double> route_array=shapeMap.get(tripShapeMap.get(tid));

		double min_dist1=999;
		double min_dist2=999;
		double dist1;
		double dist2;
		int num_points=route_array.size()/2;
		int point1=0;
		int point2=num_points*2;
		if (num_points<6){
			return route_array;
		}
		else{
			double lon1=stopLon[stop1];
			double lat1=stopLat[stop1];
			double lon2=stopLon[stop2];
			double lat2=stopLat[stop2];
			for (int i=0;i<num_points;i++){
				dist1=(lon1-route_array.get(2*i+1))*(lon1-route_array.get(2*i+1))+(lat1-route_array.get(2*i))*(lat1-route_array.get(2*i));
				dist2=(lon2-route_array.get(2*i+1))*(lon2-route_array.get(2*i+1))+(lat2-route_array.get(2*i))*(lat2-route_array.get(2*i));
				if (dist1<min_dist1){
					min_dist1=dist1;
					point1=2*i;
				}
				if (dist2<min_dist2){
					min_dist2=dist2;
					point2=2*i+2;
				}
			}


			//			return route_array.subList(point1, point2);

			if (point2 >= point1){
				return route_array.subList(point1, point2);
			} else {
				return route_array.subList(point1, point1);
			}
		}
	}
	
	public static void parseStopTime(String filename) throws IOException{
		BufferedReader br;
		String line;
		String currentTrip=null;
		double prevDepTime=-1;
		int prevStop=-1;
		//create stop route adjacency matrix
		br = new BufferedReader(new FileReader(filename));
		line = br.readLine();
		for (int i=0;i<stopTimeNum;i++){
			line = br.readLine();
			String[] elements = line.split("\"");
			if (serviceId.contains(tripServiceIdMap.get(elements[1]))){
				if (elements[1].equals(currentTrip)){
					connectNumDaily++;
				}
				else{
					currentTrip=elements[1];
				}
			}
		}
		br.close();
		
		// parse again to fill stop times
		connectDepTimePair = new TimeSequencePair[connectNumDaily];
		connectArrTime = new double[connectNumDaily];
		connectDepTime = new double[connectNumDaily];
		connectTripId = new String[connectNumDaily];
		connectFromStop = new int[connectNumDaily];
		connectToStop = new int[connectNumDaily];
		connectRoute = new int[connectNumDaily];
		connectNumDaily=0;
		br = new BufferedReader(new FileReader(filename));
		line = br.readLine();
		for (int i=0;i<stopTimeNum;i++){
			line = br.readLine();
			//if (i%100000==0){
				//System.out.println(i);
			//}
			String[] elements = line.split("\"");
			if (serviceId.contains(tripServiceIdMap.get(elements[1]))){
				if (elements[1].equals(currentTrip)){
					connectTripId[connectNumDaily] = elements[1];
					connectDepTimePair[connectNumDaily] =  new TimeSequencePair(prevDepTime,connectNumDaily);
					connectArrTime[connectNumDaily] = convertTimeToDouble(elements[3]);
					connectToStop[connectNumDaily] = stopIdMap.get(elements[7]);
					connectFromStop[connectNumDaily] = prevStop;
					connectRoute[connectNumDaily] = tripRouteMap.get(elements[1]);
					connectNumDaily++;
				}
				else{
					currentTrip=elements[1];
				}
				prevDepTime=convertTimeToDouble(elements[5]);
				prevStop=stopIdMap.get(elements[7]);
			}
		}
		br.close();
		
		//sort the array in the order of route id
		Arrays.sort(connectDepTimePair);
		connectArrTimeOrdered = new double[connectNumDaily];
		connectTripIdOrdered = new String[connectNumDaily];
		connectFromStopOrdered = new int[connectNumDaily];
		connectToStopOrdered = new int[connectNumDaily];
		connectRouteOrdered = new int[connectNumDaily];
	
		int currentRoute=-1;
		
		for (int i=0;i<connectNumDaily;i++){
			connectRouteOrdered[i] = connectRoute[connectDepTimePair[i].Sequence];
			connectArrTimeOrdered[i] = connectArrTime[connectDepTimePair[i].Sequence];
			connectTripIdOrdered[i] = connectTripId[connectDepTimePair[i].Sequence];
			connectFromStopOrdered[i] = connectFromStop[connectDepTimePair[i].Sequence];
			connectToStopOrdered[i] = connectToStop[connectDepTimePair[i].Sequence];
		}

		currentRoute=-1;
		currentTrip="-1";
		int tripStopCounter=0;
		int tripCount=0;
		tripStopCount = new int[tripNum][2];
		//deep copy to the original array
		for (int i=0;i<connectNumDaily;i++){
			connectRoute[i] =connectRouteOrdered[i];
			connectArrTime[i] = connectArrTimeOrdered[i];
			connectTripId[i] = connectTripIdOrdered[i];
			connectFromStop[i] = connectFromStopOrdered[i];
			connectToStop[i] = connectToStopOrdered[i];
			connectDepTime[i]=connectDepTimePair[i].time_;
		}
		
		//delete the copy
		connectRouteOrdered=null;
		connectArrTimeOrdered=null;
		connectTripIdOrdered=null;
		connectFromStopOrdered=null;
		connectToStopOrdered=null;
		
//		File writename = new File("G:\\M\\Bus\\MBTA\\GTFS\\MBTA_GTFS14Fall\\Connection.txt");
//		writename.createNewFile(); 
//        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
//		for (int i=0;i<connectNumDaily;i++){
//			out.write(connectFromStop[i]+" "+connectToStop[i]+"\r\n");
//			out.flush();
//		}
//		out.close();
		
		
	}
	
	public static TransitRoutingMap journeyGenerator(int fromStop, int toStop, double depTime){
		
		// Generate journey between fromStop and toStop
		
		// Initialize the queue for stops, routes and departure times
		int[] stopParent = new int[stopNum];
		int[] stopRoute = new int[stopNum]; //-1 is walking
		double[] stopDepTime = new double[stopNum]; // leaving time at the from stop
		
		// Initialize the minimum reachable time for each stop
		// to infinite, except for the fromStop, which is
		// the departure time
	
		double[] minArriveTime = new double[stopNum];
		for (int i=0;i<stopNum;i++){
			minArriveTime[i]=999; 
		}
		minArriveTime[fromStop]=depTime;
		
		
		// Implemented the CSA algorithm for fast transit planning
		
		// running variables
		
		// All available trips
		Set<String> availTrips = new HashSet<String>(); 
		int walkBeginIndex;
		int walkEndIndex;
		int beginIndex=findBeginIndex(depTime);
		int currStop;
		int walkToStop;
		
		// Iterate through all remaining connections in the day
		for (int index=beginIndex;index<connectNumDaily;index++){
			
			// First, exit the loop if the departure time at the index exceeds the 
			// current earliest arrival time at the toStop
			if (connectDepTime[index]>minArriveTime[toStop]){
				break;
			}
			
			// Next, check if the connection is reachable
			// if not, skip it
			if (availTrips.contains(connectTripId[index])){
				
			} else if(minArriveTime[connectFromStop[index]]<connectDepTime[index]){
				availTrips.add(connectTripId[index]);
			} else{
				continue;
			}
			
			// ok, then the connection is reachable, and has the 
			// potential to improve the arrival time at destination stop
			
			// get the to stop of this connection
			currStop=connectToStop[index];
			
			// we are interested only if the arrival time
			// of this connection improves the current earliest
			// arrival of its toStop
			if (connectArrTime[index]<minArriveTime[currStop]){
				
				// update the earliest arrival time for this stop
				minArriveTime[currStop]=connectArrTime[index];
				
				// update the parent stop and connection
				// that provides this arrival time
				// back tracking information
				stopParent[currStop]=connectFromStop[index];
				stopRoute[currStop]=connectRoute[index];
				stopDepTime[currStop]=connectDepTime[index];
				
				
				// check if any other stop can be reached 
				// with a walking transfer 
				walkBeginIndex=walkTransferBeginIndex[currStop];
				walkEndIndex=walkTransferEndIndex[currStop];
				
				// only if there are reachable stops
				// from currStop on foot
				if (walkBeginIndex<walkEndIndex){
					
					// iterate through all walking connections
					for(int j=walkBeginIndex; j<walkEndIndex; j++){
						
						// retrieve the walking connection
						walkToStop=walkTransferArr[j];
						
						// if the updated arrival time at currStop plus a walking transfer
						// provides a better arrival time walkToStop
						if (minArriveTime[currStop]+walkTransferTimeArr[j]<minArriveTime[walkToStop]){
							
							// update the earliest arrival time for walk to stop
							minArriveTime[walkToStop]=minArriveTime[currStop]+walkTransferTimeArr[j];
							
							// and the parent stop and connection pointers
							// back tracking information
							stopParent[walkToStop]=currStop;
							stopRoute[walkToStop]=-1;
							stopDepTime[walkToStop]=minArriveTime[currStop];
						}
					}
				}
			}
		}
		
		currStop = toStop;
		String prev_route = routeId[stopRoute[currStop]]; 
		String prev_route_name = routeName[stopRoute[currStop]];
		
		while(currStop != fromStop){
			
			String stop = stopName[stopParent[currStop]]; 
			String route = routeId[stopRoute[currStop]]; 
			String route_name = routeName[stopRoute[currStop]];
			double time = stopDepTime[currStop]; 


			System.out.println(stop+"("+stopParent[currStop]+")"+"\t"
							+route+"/"+route_name+"("+stopRoute[currStop]+")"
							+"\t"+time);
		
			currStop=stopParent[currStop];
			
			prev_route = routeId[stopRoute[currStop]]; 
			prev_route_name = routeName[stopRoute[currStop]];
			
			
			if (route != prev_route){
				System.out.println("Changing from " + prev_route_name + "(" + prev_route + ") to " 
						+ route_name + "(" + route + ") at " + stop);
			}
			
		}
		
		return new TransitRoutingMap(stopParent,stopRoute,stopDepTime);
	}
	
	public PriorityQueue<PTRoute> findPublicTransit(double precentOfDay,double lat1, double lon1, double lat2, double lon2,
			boolean hasCard, boolean hasPass, double walk_dist_thres){
		
		PriorityQueue<PTRoute> ptRoutes = new PriorityQueue<PTRoute>(10,new PTRouteComparator());
		
		// First, get the from and to stops using the 
		// origin and destination lat/lon
		
		List<Integer> from_stops = new ArrayList<Integer>();
		List<Integer> to_stops = new ArrayList<Integer>();
		
		for (int i=0;i<stopNum;i++){
			if (Distance.distBetween(lat1,lon1,stopLat[i],stopLon[i])<walk_dist_thres){
				from_stops.add(i);
			}
			
			if (Distance.distBetween(lat2,lon2,stopLat[i],stopLon[i])<walk_dist_thres){
				to_stops.add(i);
			}
		}

		// return an empty list if no stops can be found near the
		// origin or destination
		if (from_stops.isEmpty() || to_stops.isEmpty()){
			return ptRoutes;
		}
		
		// Next, iterate through pairs of stops and find routes
		// that connects them
		
		int routeCounter = 1;
		
		for (int from_stop_idx : from_stops){
			for (int to_stop_idx : to_stops){
				
				TransitRoutingMap map = journeyGenerator(from_stop_idx,to_stop_idx,precentOfDay);

				// Store it using a PTRoute object
				// The template for a pt route is:
				// walk from origin to stop1
				// wait at stop1
				// transit from stop1 to stop2
				// walk from stop2 to stop3
				// wait at stop3
				// transit from stop3 to stop4
				// walk from stop4 to destination
				
				PTRoute newRoute = new PTRoute("Public Transit " + routeCounter);
				routeCounter++;
				
				// First activity, walk to the departure stop
				double walkTime = NumUtils.roundTwoDecimals(1.414*Distance.distBetween(lat1,lon1,stopLat[from_stop_idx],stopLon[from_stop_idx])/walkingSpeed);
				PTActivity walkToFromStop = new PTActivity("Walk to "+stopName[from_stop_idx],
						walkTime,Double.POSITIVE_INFINITY,"Controllable;Activity;Walk","origin",stopName[from_stop_idx]
						,lat1,lon1,stopLat[from_stop_idx],stopLon[from_stop_idx]);
				walkToFromStop.isWalk = true;
				walkToFromStop.details.append("checkpoint:t;;");

				newRoute.addActivity(walkToFromStop);
				
				// extract the journey using the back pointers
				// from the toStop to fromStop
				// we get all stop, route and dep time sequences.

				LinkedList<PTActivity> activities = new LinkedList<PTActivity>();
				
				int currStop = to_stop_idx;
				int arrStop = -10;
				int currRoute = -10;
				int prevRoute = -10;
				
				while(currStop != from_stop_idx){

					currRoute = map.stopRoute[currStop]; 
					prevRoute = map.stopRoute[map.stopParent[currStop]];
					
					// if there is a route id change, it means we are starting a new route
					if (prevRoute != currRoute){												
						
						if (currRoute == -1){
							
							// switching to a walking transfer
							// finalize the walking activity
							
							walkTime = NumUtils.roundTwoDecimals(1.414*Distance.distBetween(
									stopLat[currStop],stopLon[currStop],stopLat[arrStop],stopLon[arrStop])/walkingSpeed);
							PTActivity walkBetweenStops = new PTActivity("Walk from "+stopName[currStop]+" to "+stopName[arrStop],
									walkTime,Double.POSITIVE_INFINITY,"Controllable;Activity;Walk",stopName[currStop],stopName[arrStop]
									,stopLat[currStop],stopLon[currStop],stopLat[arrStop],stopLon[arrStop]);
							
							walkBetweenStops.details.append("checkpoint:t;;");
							walkBetweenStops.isWalk = true;
							
							activities.add(walkBetweenStops);
							
						} else if (currRoute != -10) {
							
							// finalize the transit activity and associated waiting
							
							String route_name = routeName[currRoute];
							String route_id = routeId[currRoute]; 
							
							String routeType = "Bus";
							if (route_id.contains("CR-")){
								routeType = "Commuter Rail";
							} else if (route_name.contains("Line")){
								routeType = "Subway";
							} else if (route_id.contains("Boat")){
								routeType = "Ferry";
							}
							
							String direction = "0";
							if (route_id.contains("D1")){
								direction = "1";
							}
							
							double dep_time = map.stopDepTime[currStop]; 
							double arr_time = map.stopDepTime[arrStop]; 
							double duration = (arr_time-dep_time)*1440;
							
							double onTransitLB = NumUtils.roundTwoDecimals(duration*(1-busUncertainty));
							double onTransitUB = NumUtils.roundTwoDecimals(duration*(1+busUncertainty));
							
							PTActivity transit = new PTActivity("Take " + route_name+" from "+stopName[currStop]+" to "+stopName[arrStop],
									onTransitLB,onTransitUB,"Uncontrollable;Activity;"+routeType,
									route_name, route_id, routeType, stopName[currStop], stopName[arrStop],
									stopLat[currStop],stopLon[currStop],stopLat[arrStop],stopLon[arrStop],
									direction);	
							
							// need to wait for the bus here							
							PTActivity waitAtStop = new PTActivity("Wait at "+stopName[currStop],
									1.0,Double.POSITIVE_INFINITY,"Controllable;Activity;Wait",stopName[currStop],stopName[currStop]
									,stopLat[currStop],stopLon[currStop],stopLat[currStop],stopLon[currStop]);	
							
							transit.fromStopID = stopId[currStop];
							transit.toStopID = stopId[arrStop];
							
							transit.details.append("checkpoint:t;;");
							transit.polyline.addAll(findPolyline(route_id,currStop,arrStop));

							transit.isTransit = true;
							waitAtStop.isWait = true;
							
							transit.details.append("type:"+routeType+";;");
							transit.details.append("id:"+route_id+";;");
							transit.details.append("direction:"+direction+";;");
							transit.details.append("line:"+route_name+";;");
							transit.details.append("duration:"+duration+";;");
							transit.details.append("departure_stop:"+stopName[currStop]+";;");
							transit.details.append("arrival_stop:"+stopName[arrStop]+";;");
							
							
							transit.busArrivalTimes.add(new double[]{dep_time*1440,dep_time*1440-busArriveUncertainty,dep_time*1440+busArriveUncertainty});
							
							if (hasPass) {
								transit.cost = 0.0;
							} else if (hasCard) {
								transit.cost = -2.1;
							} else {
								transit.cost = -2.65;
							}
							
							activities.add(transit);
							activities.add(waitAtStop);
						}
						
						arrStop = currStop;
						
					} else {
						// else we do nothing
					}
					
					currStop=map.stopParent[currStop];
				}
								
				// Reverse the order and add activities to the route
				while (!activities.isEmpty()){
					newRoute.addActivity(activities.pollLast());
				}			
				
				// Finally, walk to the destination from the arrival stop
				walkTime = NumUtils.roundTwoDecimals(1.414*Distance.distBetween(stopLat[to_stop_idx],stopLon[to_stop_idx],lat2,lon2)/walkingSpeed);
				PTActivity walkToDestination = new PTActivity("Walk from "+stopName[to_stop_idx]+" to Destination",
						walkTime,Double.POSITIVE_INFINITY,"Controllable;Activity;Walk",stopName[to_stop_idx],"destination"
						,stopLat[to_stop_idx],stopLon[to_stop_idx],lat2,lon2);
				
				walkToDestination.isWalk = true;
				walkToDestination.details.append("checkpoint:t;;");

				newRoute.addActivity(walkToDestination);			
				
				ptRoutes.add(newRoute);
			}
		}

		return ptRoutes;
	}
	
	public static void main(String[] args) throws IOException {
		
		// Test the implementation of CSA for fast transit routing
		String mbta_folder = "/Users/yupeng/Dropbox/Code/Applications/Mobi/MBTA_GTFS/";
		//String mbta_folder = "G:\\M\\Bus\\MBTA\\GTFS\\MBTA_GTFS14Fall\\";

		// Load all required data
		
		// preprocess the data files
		// routes
		routeNum = countLines(mbta_folder+"routes.txt")-1;
		routeNumDouble=2*routeNum;
		routeId = new String[routeNumDouble];
		routeName = new String[routeNumDouble];

		// trips (bus lines)
		tripNum = countLines(mbta_folder+"trips.txt")-1;
		
		// stops
		stopTimeNum = countLines(mbta_folder+"stop_times.txt")-1;
		stopNum = countLines(mbta_folder+"stops.txt")-1;
		stopId = new String[stopNum];
		stopName = new String[stopNum];
		stopLat = new double[stopNum];
		stopLon = new double[stopNum];
		
		// get the current day of year and week
		currentDay=getCurrentDay();
		currentWeekDay=getCurrentWeekDay();
		
		// what is service id?
		serviceId = new HashSet<String>();
		
		// load and parse the data
		parseStop(mbta_folder+"stops.txt");
		parseRoute(mbta_folder+"routes.txt");
		parseTrip(mbta_folder+"trips.txt",
				mbta_folder+"calendar.txt",
				mbta_folder+"calendar_dates.txt");
		parseStopTime(mbta_folder+"stop_times.txt");
		
		System.out.println("Data loaded");
		System.out.println("Routing starts");
		long start = System.currentTimeMillis();
		// compute the routes between two stops given a 
		// departure time (percent time of day)
		journeyGenerator(5911,6451,0.5);
		System.out.println("Routing complete: " + (System.currentTimeMillis() - start));
		
		
		/*
		Random rand = new Random();
		int[] randFromStop = new int[10000];
		int[] randToStop = new int[10000];
		for (int i=0;i<10000;i++){
			randFromStop[i]=rand.nextInt(8000);
			randToStop[i]=rand.nextInt(8000);
		}
		long startTime = System.currentTimeMillis();
		for (int i=0;i<10000;i++){
			if(i%100==0){
				System.out.println(i);
			}
			journeyGenerator(randFromStop[i],randToStop[i],0.5);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Running time: " + (endTime - startTime) + "ms");
		*/
	}

}
