package fr.iambluedev.thelawyer;

import java.util.Scanner;

import fr.iambluedev.thelawyer.util.CliArgs;
import lombok.Getter;

public class App 
{
	@Getter
	private static Thelawyer instance;
	
    public static void main( String[] args ) throws InterruptedException
    {
    	System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF:%1$tT] [%2$s] %4$s %5$s%6$s%n");
    	
    	Scanner scanner = new Scanner(System.in);
    	CliArgs cliArgs = new CliArgs(args);
    	boolean rebuild = cliArgs.switchPresent("-rebuild");
    	
    	instance = new Thelawyer(rebuild);
        instance.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	instance.stop();
		    }
		});
        
        while(instance.isStarted()){
        	String command = scanner.nextLine();
        	instance.getLogger().info("Executed command : " + command);
        	if(command.equals("/stop")){
        		instance.stop();
        	}else if(command.equals("/refresh")){
        		instance.refresh();
        	}
        }
        
        scanner.close();
    }
}
