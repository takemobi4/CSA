package utils;

import java.util.Comparator;

public class LocationRecordComparator implements Comparator<LocationRecord>{

	@Override
	public int compare(LocationRecord l1, LocationRecord l2) {
		
        if (l1.time < l2.time){
    	   
            return -1;
            
        }else if (l1.time > l2.time){
        	
            return 1;
            
        }else{
        	
    		return 0;
	
        }      
	}

}
