import com.sun.tools.jconsole.JConsoleContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	static List<String> all_java_paths = new ArrayList<String>();

	public static Object[] countLine(String path) throws Exception {


		int classe_LOC	= 0;
		int classe_CLOC	= 0;
		double classe_DC = 0.0;
		boolean is_commented = false;


		if (path.contains(".java")) {

			File file = new File(path);

			BufferedReader br = new BufferedReader(new FileReader(file));

			String st;

			while ((st = br.readLine()) != null) {

				// Remove all spaces for each line and check if the length is not equal to 0.
				if(st.replaceAll("\\s+", "").length()!=0) {
					++classe_LOC;

					// and check whether it contains any hint of comments
					if (st.contains("/*")) is_commented = true;
					if (is_commented || st.contains("//")) ++classe_CLOC;
					if (st.contains("*/")) is_commented = false;

				}



			}
		}
		classe_DC = (classe_CLOC / 1.0) / classe_LOC;

		// res[0] is for classe_LOC and res[1] is for classe_CLOC.
		Object[] res = new Object[3];

		res[0] = classe_LOC;
		res[1] = classe_CLOC;
		res[2] = classe_DC;

		return res;



	}

	public static void pathRecursion(String path) {
		File file = new File(path);
		String[] f_lists = file.list();

		if (f_lists != null) {
			for(String st : f_lists) {
				if (st.contains(".java")) {
					all_java_paths.add(path+"\\"+st);
				} else {
					pathRecursion(path+"\\"+st);
				}
			}
		}
	}

	public static Object[][] countPackage(String path) throws Exception {

		int paquet_LOC 	= 0;
		int paquet_CLOC	= 0;
		Double paquet_DC = 0.0;

		File file = new File(path);
		String[] f_lists = file.list();

		pathRecursion(path);
		System.out.println(all_java_paths);
		Object[][] all_res = new Object[all_java_paths.size()][3];

		for (int i = 0 ; i < all_java_paths.size(); i++) {
			String st = all_java_paths.get(i);

			if(st.contains(".java")) {
				Object[] countLine = countLine(st);
				paquet_LOC += (int)countLine[0];
				paquet_CLOC += (int)countLine[1];

				Object[] res = new Object[3];
				paquet_DC = (paquet_CLOC / 1.0) / paquet_LOC;
				res[0] = paquet_LOC;
				res[1] = paquet_CLOC;
				res[2] = paquet_DC;

				all_res[i] = res;
			}

		}
		return all_res;
	}


	public static void main(String[] path) throws Exception {

		if (path[0].contains(".java")) {
			Object[] results;

			System.out.println("Single Java File.");

			results = countLine(path[0]);

			int classe_LOC = (int)results[0];
			int classe_CLOC = (int)results[1];
			double classe_DC = (double)results[2];

			System.out.println("classe_LOC : " + classe_LOC);
			System.out.println("classe_CLOC : " + classe_CLOC);
			System.out.println("classe_DC : " + classe_DC);

		} else {
			Object[][] results;
			System.out.println("Looks like a package.");

			results = countPackage(path[0]);
			for (int i = 0 ; i < results.length; i++) {
				int package_LOC = (int) results[i][0];
				int package_CLOC = (int) results[i][1];
				double package_DC = (double) results[i][2];
				System.out.println("paquet_LOC : " + package_LOC);
				System.out.println("paquet_CLOC : " + package_CLOC);
				System.out.println("paquet_DC : " + package_DC);
			}


		}



	}

}