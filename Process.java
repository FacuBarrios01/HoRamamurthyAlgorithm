import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Process {
    private int id;
    private List<Integer> requestingResourceList;
    private List<Integer> alocatedRecourcesList;

    public Process(int id) {
        this.id = id;
        this.requestingResourceList = new ArrayList<>();
        this.alocatedRecourcesList = new ArrayList<>();
    }

    public int getId(){
        return this.id;
    }

    public List<Integer> getAlocatedResourcesList(){
        return alocatedRecourcesList;
    }

    public List<Integer> getRequestingResourcesList(){
        return requestingResourceList;
    }

    public boolean addRequestResource(int resourceId){
        requestingResourceList.add(resourceId);
        return true;
    }

    public boolean addAlocatedResources(int resourceId){
        alocatedRecourcesList.add(resourceId);
        return true;
    }
}
