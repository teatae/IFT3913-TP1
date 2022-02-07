import com.sun.tools.jconsole.JConsoleContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public String path;                      //First argument, args[0]
    static List<String> all_java_paths = new ArrayList<>();

    public Main (String[] args) throws Exception {
        this.path = args[0];
        Object[][] results_in_package;       //Array of all java classes info
        Object[] results;                    //Result (either class or package type result)

        if (path.contains(".java")) {
            System.out.println("Single Java File.");

            results = countLine(path);

            System.out.println("classe_LOC : " + results[0]);
            System.out.println("classe_CLOC : " + results[1]);
            System.out.println("classe_DC : " + results[2]);
            results_in_package = new Object[1][];
            results_in_package[0] = results; //only one java result because path is java

            writeFile(results_in_package, new Object[4]);
        } else {
            System.out.println("Looks like a package.");

            int package_LOC = 0;
            int package_CLOC = 0;
            results = new Object[4];

            results_in_package = countPackage(path); //contains all java class within package

            for (Object[] result : results_in_package) {
                package_LOC += (int) result[0];
                package_CLOC += (int) result[1];
            }
            double package_DC = (double) package_CLOC / package_LOC;

            results[0] = package_LOC;
            results[1] = package_CLOC;
            results[2] = package_DC;
            results[3] = path;

            System.out.println("paquet_LOC : " + results[0]);
            System.out.println("paquet_CLOC : " + results[1]);
            System.out.println("paquet_DC : " + results[2]);

            writeFile(results_in_package,results);
        }
    }

    public static Object[] countLine(String java_path) throws Exception {

        int classe_LOC = 0;
        int classe_CLOC = 0;
        boolean is_commented = false;

        if (java_path.contains(".java")) {
            File file = new File(java_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;

            while ((st = br.readLine()) != null) {

                // Remove all spaces for each line and check if the length is not equal to 0.
                if (st.replaceAll("\\s+", "").length() != 0) {
                    ++classe_LOC;

                    // and check whether it contains any hint of comments
                    if (st.contains("/*")) is_commented = true;
                    if (is_commented || st.contains("//")) ++classe_CLOC;
                    if (st.contains("*/")) is_commented = false;

                }
            }
        }

        double classe_DC = (double) classe_CLOC/ classe_LOC;

        // res[0] is for classe_LOC and res[1] is for classe_CLOC.
        Object[] res = new Object[4];

        res[0] = classe_LOC;
        res[1] = classe_CLOC;
        res[2] = classe_DC;
        res[3] = java_path;

        return res;
    }

    public static void pathRecursion(String package_path) {
        File file = new File(package_path);
        String[] f_lists = file.list();

        if (f_lists != null) {

            for (String st : f_lists) {

                if (st.contains(".java")) {
                    all_java_paths.add(package_path + "\\" + st);
                } else {
                    pathRecursion(package_path + "\\" + st);
                }
            }
        }
    }

    public static Object[][] countPackage(String package_path) throws Exception {
        pathRecursion(package_path);
        System.out.println(all_java_paths);
        Object[][] all_res = new Object[all_java_paths.size()][4];

        for (int i = 0; i < all_java_paths.size(); i++) {
            String java_path = all_java_paths.get(i);

            if (java_path.contains(".java")) {
                Object[] results = countLine(java_path);
                all_res[i] = results;
            }
        }
        return all_res;
    }


    public void writeFile(Object[][] results_in_package, Object[] results) throws IOException {
        Writer writer = null;
        String class_header = "chemin, class, classe_LOC, classe_CLOC, classe_DC" + System.lineSeparator();
        String package_header = "chemin, paquet, paquet_LOC, paquet_CLOC, paquet_DC" + System.lineSeparator();

        try {
            if (results_in_package.length>0) {
                FileOutputStream outputStream = new FileOutputStream("classes.csv");
                OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
                writer = new BufferedWriter(outputWriter);
                writer.write(class_header);

                for (Object[] result : results_in_package) { // writes into classes.csv all java class info
                    writer.write(result[3] + ",");
                    writer.write((result[3]).toString().substring((result[3]).toString().lastIndexOf('\\') + 1));
                    writer.write("," + result[0] + "," + result[1] + "," + result[2] + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Output error");
            System.exit(0);
        }
        assert writer != null;
        writer.close();
        writer = null;
        try {

            if (results[0] != null) {
                FileOutputStream outputStream = new FileOutputStream("package.csv");
                OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
                writer = new BufferedWriter(outputWriter);
                writer.write(package_header);
                writer.write(results[3] + ",");
                writer.write((results[3]).toString().substring((results[3]).toString().lastIndexOf('\\') + 1));
                writer.write(","  + results[0] + ","  + results[1] + "," + results[2] + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Output error");
            System.exit(0);
        }
        assert writer != null;
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        Main hi = new Main(args);
    }
}