package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Currency;
import java.util.Scanner;

public class FileIO {
	
	public FileIO(){
	}
	
	// Write users username and password and returns if successful or not
	public void saveUserData(String username, String password){
		
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("../Mafia/src/user_save.txt", true));
			bw.write(username + " " + password);
			bw.newLine();
			
			bw.flush();	
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Method to check if the user already exists in the server
	public boolean doesUsrExist(String username){
		File file = new File("../Mafia/src/user_save.txt");
		boolean doesExist = false;
		
		System.out.println("doesExist before try: " + doesExist);
		
		try {
			Scanner scanner = new Scanner(file);
			int lineNum = 0;
			
			System.out.println("doesExist in try: " + doesExist);
			
			while(scanner.hasNextLine()){
				String currLine = scanner.nextLine();
				System.out.println(currLine);
				lineNum++;
				
				if(currLine.equals(username)){
					doesExist = true;
				}
			}
			
			scanner.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("doesExist after try: " + doesExist);
		
		return doesExist;
	}
	
	// Checks if the credentials entered are valid
	public boolean checkCredentials(String username, String password){
		File file = new File("../Mafia/src/user_save.txt");
		boolean isValid = false;
		
		try{
			Scanner scanner = new Scanner(file);
			int numLine = 0;
			
			while(scanner.hasNextLine()){
				String currLine = scanner.nextLine();
				String[] splitUp = currLine.split(" ");
				numLine++;
				
				System.out.println("username: " + username);
				System.out.println("password: " + password);
				
				if(splitUp[0].equals(username) && splitUp[1].equals(password)){
					isValid = true;
					break;
				}else{
					isValid = false;
				}
			}
			
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		return isValid;
	}
}
