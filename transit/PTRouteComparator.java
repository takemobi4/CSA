package transit;

/*

Copyright (c) 2013 Peng Yu, MIT.

This software may not be redistributed, and can only be retained
and used with the explicit written consent of the author, Peng Yu.

This software is made available as is; no guarantee is provided
with respect to performance or correct behavior.

This software may only be used for non-commercial, non-profit,
research activities.

*/

import java.util.Comparator;

public class PTRouteComparator implements Comparator<PTRoute>{

	@Override
	public int compare(PTRoute r1, PTRoute r2) {
		
       if (r1.time < r2.time){
    	   
            return -1;
            
        }else if (r1.time > r2.time){
        	
            return 1;
            
        }else{
        	
    		return 0;
	
        }        
	}
	
	
}
