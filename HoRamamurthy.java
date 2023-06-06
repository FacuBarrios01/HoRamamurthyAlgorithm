import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HoRamamurthy {

    private int id;
    private static Process allProcesses[];
    // Theses two maps will work as my WFG
    // Global resource allocation table - First int = resource id, Second int =
    // owner process's id
    private static HashMap<Integer, Integer> gResourceAlloc = new HashMap();
    // Global resouce requests table - Fist int = resouce id, List<Integer> =
    // processes requesting resource
    private static HashMap<Integer, List<Integer>> gResourceRequests = new HashMap();
    // Stack for checking for cycles in the detectCycle method
    private static Set<Integer> processStack = new TreeSet<Integer>();

    public static void createWFG() {
        Process process;
        for (int i = 0; i < allProcesses.length; i++) {
            process = allProcesses[i];
            int processId = process.getId();
            for (int resourceId : process.getAlocatedResourcesList()) {
                gResourceAlloc.put(resourceId, processId);
            }
            for (int resourceId : process.getRequestingResourcesList()) {
                List<Integer> auxRequestList = new ArrayList<Integer>();
                if (gResourceRequests.containsKey(resourceId)) {
                    // if the key has already been used,
                    // we'll just grab the array list and add the value to it
                    auxRequestList = gResourceRequests.get(resourceId);
                    auxRequestList.add(processId);
                    gResourceRequests.put(resourceId, auxRequestList);
                } else {
                    // if the key hasn't been used yet,
                    // we'll create a new ArrayList<String> object, add the value
                    // and put it in the array list with the new key
                    auxRequestList.add(processId);
                    gResourceRequests.put(resourceId, auxRequestList);
                }
            }
        }
    }

    public static int getResourceOwner(int resourceId) {
        if(Objects.nonNull(gResourceAlloc.get(resourceId))) return gResourceAlloc.get(resourceId);
        else return -1;
    }

    // We check for a cycle in all processes
    public static boolean detectCycle(int processId) {
        // If the owner of the resource that the process is waiting for is in our stack
        // we have a cycle
        if (processStack.contains(processId))
            return true;
        else
            processStack.add(processId);

        // Look for resource requests for the process
        for (Entry<Integer, List<Integer>> entry : gResourceRequests.entrySet()) {
            if (entry.getValue().contains(processId)){
                int resourceOwner = getResourceOwner(entry.getKey());
                // If the process is waiting for a resource we will check if it is unasigned
                // Or in case it is asigned we will check who owns the resource and what
                // resources he is waiting for
                if (resourceOwner != processId && getResourceOwner(entry.getKey()) != -1)
                    return detectCycle(resourceOwner);
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        int numResources;
        int numProcesses;
        File inputFile = null;
        Scanner reader = null;
        String line;

        // Check for correct arguments
        if (args.length < 1) {
            System.out.println("No dataset provided");
            System.exit(0);
        } else if (args.length > 1) {
            System.out.println("Too many arguments");
            System.exit(0);
        }

        // Parse number of processes and resources
        inputFile = new File(args[0]);
        if (inputFile != null)
            reader = new Scanner(inputFile);

        // Get number of processes
        line = reader.nextLine();
        if (!line.contains("Number of processes:")) {
            System.out.println(
                    "First line must contain \"Number of processes:\" immediately followed by a integer value ex. \"Number of processes:5\"");
            System.exit(0);
        }
        numProcesses = Integer.parseInt(line.split(":")[1].trim());

        allProcesses = new Process[numProcesses];

        int processId;
        String resourcesStatus;
        String alocatedResources[];
        String requestingResources[];
        // Extract and format the data from out dataset & create and store the processes
        // from our system
        while (reader.hasNextLine()) {
            line = reader.nextLine();
            processId = Integer.parseInt(line.split(":")[0]);
            resourcesStatus = line.split(":")[1].trim();

            allProcesses[processId] = new Process(processId);

            alocatedResources = resourcesStatus.split("-")[0].trim().replace("[", "").replace("]", "").split(",");
            for (int i = 0; i < alocatedResources.length; i++) {
                if (!alocatedResources[i].isBlank())
                    allProcesses[processId].addAlocatedResources(Integer.parseInt(alocatedResources[i]));
            }
            requestingResources = resourcesStatus.split("-")[1].trim().replace("[", "").replace("]", "").split(",");
            for (int i = 0; i < requestingResources.length; i++) {
                if (!requestingResources[i].isBlank())
                    allProcesses[processId].addRequestResource(Integer.parseInt(requestingResources[i]));
            }
        }
        reader.close();

        createWFG();
        for (int i = 0; i < allProcesses.length; i++){
            boolean result =  detectCycle(i);
            processStack.clear();
            if(result) System.out.println("Procces " + i + " is locked due to a cycle");

        }
    }
}
