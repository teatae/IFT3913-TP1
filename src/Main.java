import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {
	
	public static Object[] countLine(String path) throws Exception {
		
		
		int classe_LOC	= 0;
		int classe_CLOC	= 0;
		double classe_DC = 0.0;
		
		
		
		if (path.contains(".java")) {
		
			File file = new File(path);
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String st;
			
			while ((st = br.readLine()) != null) {
				
				// Remove all spaces for each line and check if the length is not equal to 0.
				if(st.replaceAll("\\s+", "").length()!=0) {
					++classe_LOC;
					
					// and check whether it contains any hint of comments
					if(st.contains("//") || st.contains("/*") || st.contains("*") || st.contains("*/")) 
						++classe_CLOC;
					
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
	
	public static Object[] countPackage(String path) throws Exception {
		
		int paquet_LOC 	= 0;
		int paquet_CLOC	= 0;
		Double paquet_DC = 0.0;
		
		File file = new File(path);
		
		String[] f_lists = file.list();
		
		for(String st : f_lists) {
			
			if(st.contains(".java")) {
				
				Object[] countLine = countLine(path + "\\" + st);
				paquet_LOC += (int)countLine[0];
				paquet_CLOC += (int)countLine[1];
			}
			
		}
		
		Object[] res = new Object[3];
		
		paquet_DC = (paquet_CLOC / 1.0) / paquet_LOC;
		
		res[0] = paquet_LOC;
		res[1] = paquet_CLOC;
		res[2] = paquet_DC;
		
		
		
		return res;
		
	}
	

	public static void main(String[] path) throws Exception {
		
		Object[] results;

		
		if (path[0].contains(".java")) {
			
			System.out.println("Single Java File.");
			
			results = countLine(path[0]);
			
			int classe_LOC = (int)results[0];
			int classe_CLOC = (int)results[1];
			double classe_DC = (double)results[2];
			
			System.out.println("classe_LOC : " + classe_LOC);
			System.out.println("classe_CLOC : " + classe_CLOC);
			System.out.println("classe_DC : " + classe_DC);
			
			
		} else {
			
			System.out.println("Looks like a package.");
			
			results = countPackage(path[0]);
			
			int package_LOC = (int)results[0];
			int package_CLOC = (int)results[1];
			double package_DC = (double)results[2];
			
			System.out.println("paquet_LOC : " + package_LOC);
			System.out.println("paquet_CLOC : " + package_CLOC);
			System.out.println("paquet_DC : " + package_DC);
			
		}
		

		
	}

}
