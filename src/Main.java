import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class Main {
    static List<String> class_paths = new ArrayList<>();       // Contains all java classes paths within a path
    static List<String> package_paths = new ArrayList<>();     // Contains all package paths within a path
    static Object[][] class_paths_info;                        // Class info of class paths
    static Object[][] package_paths_info;                      // Package info of package paths
    public String path;                                        // First argument (args[0]) is the path

    /**
     * Constructor
     * @param args the main path
     * @throws Exception
     */
    public Main(String args) throws Exception {

        this.path = args;


        if (isJavaFile(path)) {
            System.out.println("Single Java File.");

            Object[] class_info = countLine(path);
            class_paths_info = new Object[1][];
            class_paths_info[0] = class_info;                  // Only one java class info because path is one class

            writeFile(class_paths_info, new Object[1][1]);
        } else if(isDirectory(path)) {
            System.out.println("Looks like a directory.");

            package_paths_info = countPackage(path);           // Contains all package info within a path
            class_paths_info = countClass(path);               // Contains all java class info within a path

            writeFile(class_paths_info, package_paths_info);
        }
    }

    /**
     *
     * @param path path string to verify
     * @return true if path is a directory
     */
    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.isDirectory();
    }
    
    public static boolean isJavaFile(String path) {
    	
    	if(path.length()<5) return false; 
    	return path.substring(path.length() - 5).equals(".java");
    }
    
    public static boolean hasJavaFile(String path) {
    	
    	File file = new File(path);
    	
    	File[] flist =  file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if(!isDirectory(name)) {
				if (isJavaFile(name)) return true;
				}
				return false;
				
			}
			

    	});
    	
    	for (File f : flist) {
			String fileName = f.getName();
			if(isJavaFile(fileName)) return true;
		}
		
		return false;
    }

    /**
     *
     * @param class_path the java class path
     * @return Object[] res
     * containing java class information such as
     * res[1] = classe_CLOC;
     * res[2] = classe_DC;
     * res[3] = class_path;
     * res[4] = WMC;
     * res[5] = classe_DC / WMC;
     * @throws Exception
     */
    public static Object[] countLine(String class_path) throws Exception {

        int classe_LOC = 0;
        int classe_CLOC = 0;
        int WMC = 0;
        boolean is_commented = false;

        if (isJavaFile(class_path)) {
            File file = new File(class_path);
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

                    if (st.contains("public")) {             // Is a method
                        ++WMC;
                    }
                    if (st.contains("if") && !is_commented) ++WMC;
                    if (st.contains("while") && !is_commented) ++WMC;
                    if (st.contains("case") && !is_commented) ++WMC;

                }
            }
        }

        double classe_DC = (double) classe_CLOC / classe_LOC;

        // res[0] is for classe_LOC and res[1] is for classe_CLOC.
        Object[] res = new Object[6];

        res[0] = classe_LOC;
        res[1] = classe_CLOC;
        res[2] = classe_DC;
        res[3] = class_path;
        res[4] = WMC;
        res[5] = classe_DC / WMC;

        return res;
    }

    /**
     * updates package_paths with all package paths within a path
     * @param package_path path of a package
     */
    public static void getPackagePathsRecursion(String package_path) {
        File file = new File(package_path);
        String[] f_lists = file.list();

        if (f_lists != null) {

            for (String st : f_lists) {
            	
            	String path = package_path + "\\" + st;

                if (isDirectory(path)) {
                	
                	if(hasJavaFile(path))
                    package_paths.add(package_path + "\\" + st);
                    getPackagePathsRecursion(package_path + "\\" + st);
                }
            }
        }
    }

    /**
     * updates class_paths with all java classes paths within a path
     * @param package_path path of a package
     */
    public static void getJavaPathsRecursion(String package_path) {
        File file = new File(package_path);
        String[] f_lists = file.list();

        if (f_lists != null) {

            for (String st : f_lists) {

                if (isJavaFile(st)) {
                    class_paths.add(package_path + "\\" + st);
                } else {
                    getJavaPathsRecursion(package_path + "\\" + st);
                }
            }
        }
    }

    /**
     * updates class_paths with all java classes paths within a path (no recursion/subfolders)
     * @param package_path path of a package
     */
    public static void getJavaPaths(String package_path) {
        File file = new File(package_path);
        String[] f_lists = file.list();

        if (f_lists != null) {

            for (String st : f_lists) {

                if (isJavaFile(st)) {
                    class_paths.add(package_path + "\\" + st);
                }
            }
        }
    }
    
    public String relativePath(String str) {
    	
    	File f = new File(path);
    	
    	String parent = f.getParent();
	
    	return str.substring(parent.length()+1);

    }
    
    public String packageNameMake(String path) {
    	
    	String rPath = relativePath(path);
    	String result = rPath.replace('\\', '.');
    	return result;
    	
    	
    }

    /**
     *
     * @param main_package_path the initial path from main
     * @return Object[][] package_paths_info
     * Object of all package info
     * @throws Exception
     */
    public static Object[][] countPackage(String main_package_path) throws Exception {
        package_paths.clear();
        getPackagePathsRecursion(main_package_path); // Get package paths within main_package_path

        Object[][] package_paths_info = new Object[package_paths.size() + 1][6]; // Results for each package

        for (int i = 0; i < package_paths_info.length - 1; i++) {                // We do a countLine for each package
            String current_package = package_paths.get(i);
            class_paths.clear();
            getJavaPaths(current_package);
            //System.out.println("Java paths in package " + current_package + " are " + class_paths);

            int package_LOC = 0;
            int package_CLOC = 0;
            int WCP = 0;

            for (String current_class : class_paths) {
                if (current_class.substring(current_class.length() -5).equals(".java")) {
                    Object[] result = countLine(current_class);
                    package_LOC += (int) result[0];
                    package_CLOC += (int) result[1];
                    WCP += (int) result[4];
                }
            }
            double package_DC = (double) package_CLOC / package_LOC;
            double package_BC = package_DC / WCP;

            package_paths_info[i][0] = package_LOC;
            package_paths_info[i][1] = package_CLOC;
            package_paths_info[i][2] = package_DC;
            package_paths_info[i][3] = current_package;
            package_paths_info[i][4] = WCP;
            package_paths_info[i][5] = package_BC;
        }

        class_paths.clear();
        // Get java paths within main_package_path, so we can do a final countLine
        getJavaPaths(main_package_path);
        //System.out.println("Java paths in main package " + main_package_path + " are " + class_paths);

        int package_LOC = 0;
        int package_CLOC = 0;
        int WCP = 0;

        for (String current_class : class_paths) {
            if (current_class.substring(current_class.length() -4).equals(".java")) {
                Object[] result = countLine(current_class);
                package_LOC += (int) result[0];
                package_CLOC += (int) result[1];
                WCP += (int) result[4];
            }
        }
        double package_DC = (double) package_CLOC / package_LOC;
        double package_BC = package_DC / WCP;

        package_paths_info[package_paths_info.length - 1][0] = package_LOC;
        package_paths_info[package_paths_info.length - 1][1] = package_CLOC;
        package_paths_info[package_paths_info.length - 1][2] = package_DC;
        package_paths_info[package_paths_info.length - 1][3] = main_package_path;
        package_paths_info[package_paths_info.length - 1][4] = WCP;
        package_paths_info[package_paths_info.length - 1][5] = package_BC;

        return package_paths_info;
    }

    /**
     *
     * @param main_package_path the initial path from main
     * @return Object[][] class_paths_info
     * Object of all java class info
     * @throws Exception
     */
    public static Object[][] countClass(String main_package_path) throws Exception {
        // Get java paths within package_path and returns their info
        class_paths.clear();
        getJavaPathsRecursion(main_package_path);

        Object[][] class_paths_info = new Object[class_paths.size()][6];

        for (int i = 0; i < class_paths.size(); i++) {
            String current_class = class_paths.get(i);

            if (isJavaFile(current_class)) {
                Object[] result = countLine(current_class);
                class_paths_info[i] = result;
            }
        }
        return class_paths_info;
    }


    /**
     * writes classes.csv and paquets.csv with
     * @param class_paths_info Object of all java class info
     * @param package_paths_info Object of all package info
     * @throws IOException
     */
    public void writeFile(Object[][] class_paths_info, Object[][] package_paths_info) throws IOException {
        Writer writer = null;
        String class_header = "chemin, class, classe_LOC, classe_CLOC, classe_DC, WMC, classe_BC" + System.lineSeparator();
        String package_header = "chemin, paquet, paquet_LOC, paquet_CLOC, paquet_DC, WCP, paquet_BC" + System.lineSeparator();

        try {
            if (class_paths_info.length > 0) {
                FileOutputStream outputStream = new FileOutputStream("classes.csv");
                OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
                writer = new BufferedWriter(outputWriter);
                writer.write(class_header);

                for (Object[] result : class_paths_info) { // writes into classes.csv all java class info
                    writer.write(relativePath((String)result[3]) + ",");
                    writer.write((result[3]).toString().substring((result[3]).toString().lastIndexOf('\\') + 1));
                    writer.write("," + result[0] + "," + result[1] + "," + new DecimalFormat("#.######").format(result[2]) + "," + result[4]);
                    writer.write("," + new DecimalFormat("#.######").format(result[5]) + System.lineSeparator());
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
                FileOutputStream outputStream = new FileOutputStream("paquets.csv");
                OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
                writer = new BufferedWriter(outputWriter);
                writer.write(package_header);
                for (Object[] result : package_paths_info) { // writes into classes.csv all java class info
                	if((int)result[0] == 0) continue;
                    writer.write(this.relativePath((String)result[3]) + ",");
                    writer.write(packageNameMake((String)result[3]));
                    writer.write("," + result[0] + "," + result[1] + "," + new DecimalFormat("#.######").format(result[2]) + "," + result[4]);
                    writer.write("," + new DecimalFormat("#.######").format(result[5]) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Output error");
            System.exit(0);
        }
        assert writer != null;
        writer.close();
    }

    /**
     *
     * @param args the main path, can be .java or package
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String path;
        if (args.length != 0)
            path = args[0];
        else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Type a path :");
            path = sc.next();
            sc.close();
        }

        new Main(path);

    }
}