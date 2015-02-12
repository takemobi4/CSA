package transit;

import java.util.LinkedList;
import java.util.UUID;

public class PTRoute {
	
	public String id = UUID.randomUUID().toString();
	public String description = "";
	
	public LinkedList<PTActivity> activities = new LinkedList<PTActivity>();

	public double cost = 0;
	public double time = 0;
	public double walkingTime = 0;

	public PTRoute(String routeDescription){
		description = routeDescription;
	}
	
	public void addActivity(PTActivity _activity){
		activities.add(_activity);
		cost += _activity.cost;
		time += _activity.lb;
		
		if (_activity.isWalk){
			walkingTime += _activity.lb;
		}
	}

}
