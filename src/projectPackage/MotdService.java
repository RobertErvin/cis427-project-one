package projectPackage;
import java.io.BufferedReader;
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

    private final String FILEPATH = "motds.txt";

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
        lastMotdIndex = motds.size() > lastMotdIndex + 1 ? lastMotdIndex + 1: -1;
        return motds.get(lastMotdIndex + 1);
    }

    // Append a motd to the motd file
    private void writeToFile(String motd) throws IOException {
        Files.write(Paths.get(FILEPATH), motd.getBytes(), StandardOpenOption.APPEND);
    }

    // Get all stored motds
    private ArrayList<String> readFromFile() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(FILEPATH));
        ArrayList<String> motds = new ArrayList<>();

        try {
            String motd = br.readLine();
            motds.add(motd);

            while (motd != null) {
                motd = br.readLine();
                motds.add(motd);
            }
        } finally {
            br.close();
        }

        return motds;
    }
}
