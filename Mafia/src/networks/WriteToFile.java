package networks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteToFile {
	
	public WriteToFile(){
	}
	
	// Write users username and passwor and returns if successful or not
	public void saveUserData(String username, String password){
		
		
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("save_file"));
			bw.write(username + "\t" + password);
			bw.newLine();
			
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			bw.close();
			bw.flush();
		}
	}
}
