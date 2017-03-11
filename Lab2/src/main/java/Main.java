import java.io.File;

public class Main {

    public static void main(String[] args) {
        String linesFile = Main.class.getClassLoader().getResource("lines-original.txt").getFile();
        Clusterizer.clusterize(new File(linesFile), "\n");
    }
}
