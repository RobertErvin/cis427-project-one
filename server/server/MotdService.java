package server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/*
 * Responsible for handling Message-of-the-Day-specific business logic
 */
public class MotdService {
    public ArrayList<String> motds = new ArrayList<>();
    public int lastMotdIndex = -1;

    private final String FILEPATH = "server/motds.txt";

    // Get all stored motds on initialization
    public MotdService() throws Exception {
        this.motds = readFromFile();
    }

    // Store new motd
    public void add(String motd) throws Exception {
        writeToFile(motd);
        motds.add(motd);
    }

    // Get the next motd
    public String getNext() {
    	System.out.println("Motd Size: " + motds.size());
    	System.out.println("lastIndexBefore: " + lastMotdIndex);
    	
        lastMotdIndex = motds.size() > lastMotdIndex + 1 ? lastMotdIndex + 1: 0;
        
        System.out.println("lastIndexAfter: " + lastMotdIndex);
        System.out.println("Motd: " + motds.get(lastMotdIndex));
        
        return motds.get(lastMotdIndex);
    }

    // Append a motd to the motd file
    private void writeToFile(String motd) throws IOException {
        motd = "\n" + motd;
        Files.write(Paths.get(FILEPATH), motd.getBytes(), StandardOpenOption.APPEND);
    }

    // Get all stored motds
    private ArrayList<String> readFromFile() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(FILEPATH));
        ArrayList<String> motds = new ArrayList<>();

        try {
            String motd = br.readLine();
            if (motd != null) {
	            motds.add(motd);
	
	            while (motd != null) {
	                motd = br.readLine();
	                
	                if (motd != null) {
	                	motds.add(motd);
	                }
	            }
            }
        } finally {
            br.close();
        }

        return motds;
    }
}
