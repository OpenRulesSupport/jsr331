package cloud.balancing;

import java.util.Comparator;

public class ProcessComparator implements Comparator<CloudProcess> {
	   
    public int compare(CloudProcess p1, CloudProcess p2){
   
        int memory1 = p1.getRequiredMemory(); 
        int memory2 = p2.getRequiredMemory(); 
             
        if(memory1 < memory2)
            return 1;
        else if(memory1 > memory2)
            return -1;
        else {
            int cpu1 = p1.getRequiredCpuPower(); 
            int cpu2 = p2.getRequiredCpuPower(); 
            if(cpu1 < cpu2)
                return 1;
            else if(cpu1 > cpu2)
                return -1;
            else {
            	int band1 = p1.getRequiredNetworkBandwidth();
            	int band2 = p2.getRequiredNetworkBandwidth();
                if(band1 < band2)
                    return 1;
                else if(band1 > band2)
                    return -1;
                else
                	return 0;
            }
        }
        
    }
   
}