import com.sun.tools.jconsole.JConsoleContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public String path;                                        // First argument (args[0]) is the path
    static List<String> class_paths = new ArrayList<>();       // Contains all java classes paths within a path
    static List<String> package_paths = new ArrayList<>();     // Contains all package paths within a path
    static Object[][] class_paths_info;                        // Class info of class paths
    static Object[][] package_paths_info;                      // Package info of package paths

    public Main (String[] args) throws Exception {
        this.path = args[0];

        if (path.contains(".java")) {
            System.out.println("Single Java File.");

            Object[] class_info = countLine(path);

            System.out.println("classe_LOC : " + class_info[0]);
            System.out.println("classe_CLOC : " + class_info[1]);
            System.out.println("classe_DC : " + class_info[2]);

            class_paths_info = new Object[1][];
            class_paths_info[0] = class_info;                  // Only one java class info because path is one class

            writeFile(class_paths_info, new Object[1][1]);
        } else {
            System.out.println("Looks like a package.");

            package_paths_info = countPackage(path);           // Contains all package info within a path
            class_paths_info = countClass(path);               // Contains all java class info within a path

            for (Object[] class_info : package_paths_info) {
                System.out.println("paquet_LOC : " + class_info[0]);
                System.out.println("paquet_CLOC : " + class_info[1]);
                System.out.println("paquet_DC : " + class_info[2]);
                System.out.println("path : " + class_info[3]);
            }

            writeFile(class_paths_info,package_paths_info);
        }
    }

    public static Object[] countLine(String classe_path) throws Exception {

        int classe_LOC = 0;
        int classe_CLOC = 0;
        boolean is_commented = false;

        if (classe_path.contains(".java")) {
            File file = new File(classe_path);
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
        res[3] = classe_path;

        return res;
    }

    public static void getPackagePathsRecursion(String package_path) {
        File file = new File(package_path);
        String[] f_lists = file.list();

        if (f_lists != null) {

            for (String st : f_lists) {

                if (!st.contains(".java")) {
                    package_paths.add(package_path + "\\" + st);
                    getPackagePathsRecursion(package_path + "\\" + st);
                }
            }
        }
    }

    public static void getJavaPathsRecursion(String package_path) {
        File file = new File(package_path);
        String[] f_lists = file.list();

        if (f_lists != null) {

            for (String st : f_lists) {

                if (st.contains(".java")) {
                    class_paths.add(package_path + "\\" + st);
                } else {
                    getJavaPathsRecursion(package_path + "\\" + st);
                }
            }
        }
    }

    public static Object[][] countPackage(String package_path) throws Exception {
        package_paths.clear();
        getPackagePathsRecursion(package_path);
        System.out.println(package_paths);

        Object[][] package_paths_info = new Object[package_paths.size() + 1][4];

        for (int i = 0; i < package_paths_info.length - 1; i++) {
            String current_package = package_paths.get(i);
            class_paths.clear();
            getJavaPathsRecursion(current_package);
            System.out.println("Java paths in package " + current_package + " are " + class_paths);

            int package_LOC = 0;
            int package_CLOC = 0;

            for (String current_class : class_paths) {
                if (current_class.contains(".java")) {
                    Object[] result = countLine(current_class);
                    package_LOC += (int) result[0];
                    package_CLOC += (int) result[1];
                }
            }
            double package_DC = (double) package_CLOC / package_LOC;

            package_paths_info[i][0] = package_LOC;
            package_paths_info[i][1] = package_CLOC;
            package_paths_info[i][2] = package_DC;
            package_paths_info[i][3] = current_package;
        }

        class_paths.clear();
        getJavaPathsRecursion(package_path);
        System.out.println("Java paths in main package " + package_path + " are " + class_paths);

        int package_LOC = 0;
        int package_CLOC = 0;

        for (String current_class : class_paths) {
            if (current_class.contains(".java")) {
                Object[] result = countLine(current_class);
                package_LOC += (int) result[0];
                package_CLOC += (int) result[1];
            }
        }
        double package_DC = (double) package_CLOC / package_LOC;

        package_paths_info[package_paths_info.length-1][0] = package_LOC;
        package_paths_info[package_paths_info.length-1][1] = package_CLOC;
        package_paths_info[package_paths_info.length-1][2] = package_DC;
        package_paths_info[package_paths_info.length-1][3] = package_path;

        return package_paths_info;
    }

    public static Object[][] countClass(String package_path) throws Exception {
        class_paths.clear();
        getJavaPathsRecursion(package_path);
        System.out.println(class_paths);

        Object[][] class_paths_info = new Object[class_paths.size()][4];

        for (int i = 0; i < class_paths.size(); i++) {
            String current_class = class_paths.get(i);

            if (current_class.contains(".java")) {
                Object[] result = countLine(current_class);
                class_paths_info[i] = result;
            }
        }
        return class_paths_info;
    }


    public void writeFile(Object[][] class_paths_info, Object[][] package_paths_info) throws IOException {
        Writer writer = null;
        String class_header = "chemin, class, classe_LOC, classe_CLOC, classe_DC" + System.lineSeparator();
        String package_header = "chemin, paquet, paquet_LOC, paquet_CLOC, paquet_DC" + System.lineSeparator();

        try {
            if (class_paths_info.length>0) {
                FileOutputStream outputStream = new FileOutputStream("classes.csv");
                OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
                writer = new BufferedWriter(outputWriter);
                writer.write(class_header);

                for (Object[] result : class_paths_info) { // writes into classes.csv all java class info
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
            if (package_paths_info[0] != null) {
                FileOutputStream outputStream = new FileOutputStream("package.csv");
                OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
                writer = new BufferedWriter(outputWriter);
                writer.write(package_header);
                for (Object[] result : package_paths_info) { // writes into classes.csv all java class info
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
    }

    public static void main(String[] args) throws Exception {
        Main hi = new Main(args);
    }
}