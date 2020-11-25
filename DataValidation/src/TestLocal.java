
import java.io.File;
public class TestLocal {

	public static void Execute(String DirectoryName) {
		String workingDirectory = System.getProperty("user.dir");
        String  dir ; //= workingDirectory + File.separator + DirectoryName;
        dir = " C:\\Users";
        System.out.println("Final file directory : " + dir);
        File file = new File(dir);
        System.out.println("Final file directory : " + file);
        File[] files=file.listFiles();
        System.out.println("Final file directory : " + files);
        if(file.exists())
        	for (File a : files) {
        		
        		System.out.print(a.getName());
        	}
	}
}

