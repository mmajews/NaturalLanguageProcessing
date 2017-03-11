import java.io.File;

public class Main {

    public static void main(String[] args) {
        String linesFile = Main.class.getClassLoader().getResource("lines.txt").getFile();
        Clusterizer.clusterize(new File(linesFile), "\n");
    }
}
