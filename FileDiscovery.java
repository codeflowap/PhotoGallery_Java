import java.io.File;
import java.util.ArrayList;


























public class FileDiscovery {
	
	ArrayList<String> files = new ArrayList<>();
	String path;
	String[] extensions;
	FileDiscovery(String path, String[] extensions) {
		this.path = path;
		this.extensions = extensions;
	}
	
	ArrayList<String> discover() {
		findFiles(path);
		return files;
	}
	
	boolean accepted(String name) {
		for (int i = 0; i < extensions.length; i++) {
			if (name.endsWith(extensions[i])) {
				return true;
			}
		}
		return false;
	}
	
	void findFiles(String currentPath) {
		File folder = new File(currentPath);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null) {
			System.out.println("Found a null " + currentPath);
			return;
		}
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (accepted(listOfFiles[i].getName())) {
					files.add(listOfFiles[i].getAbsolutePath());
				}
			}
			else if (listOfFiles[i].isDirectory()) {
				findFiles(listOfFiles[i].getAbsolutePath());
			}
		}
	}
}
