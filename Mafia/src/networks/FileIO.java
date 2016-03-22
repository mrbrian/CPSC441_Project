package networks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileIO {
	
	public FileIO(){
	}
	
	// Write users username and passwor and returns if successful or not
	public void saveUserData(String username, String password){
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("../Mafia/src/save_file", true));
			bw.write(username + "\t" + password);
			bw.newLine();
			
			bw.flush();	
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Method to check if the user already exists in the server
	public boolean doesUsrExist(String username){
		File file = new File("../Mafia/src/save_file");
		boolean doesExist = false;
		
		try {
			Scanner scanner = new Scanner(file);
			int lineNum = 0;
			
			while(scanner.hasNextLine()){
				String currLine = scanner.nextLine();
				lineNum++;
				
				if(currLine.equals(username)){
					doesExist = true;
				}
			}
			
			scanner.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return doesExist;
	}
}
