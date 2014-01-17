package net.tbnr;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by rigor789 on 2014.01.17..
 */
public class GearzBootstrap {

    @Getter
    private static GearzBootstrap instance;

    private Scanner scanner;
    private ArrayList<Command> commands;

    @Getter @Setter
    private boolean exit;

    public static void main(String[] args) {
        new GearzBootstrap();
    }

    private GearzBootstrap() {
        instance = this;
        scanner = new Scanner(System.in);
        commands  = new ArrayList<>();
        exit = false;
        registerDefaultCommands();
        run();
    }

    private void run() {
        while(scanner.hasNext()){
            processInput(scanner.nextLine());
            if(isExit()){
                scanner.close();
                return;
            }
        }
        scanner.close();
    }

    private void registerDefaultCommands() {
        CommandExecutor defaultExecutor = new DefaultCommandExecutor();
        registerCommand("exit").setExecutor(defaultExecutor);
        registerCommand("about").setExecutor(defaultExecutor);
    }

    public void processInput(String input) {
        String[] args = input.split(" ");
        for(Command cmd : commands){
            if(args[0].equalsIgnoreCase(cmd.getName())){
                cmd.execute(args);
                return;
            }
        }
        System.out.println("Command not found.");
    }

    public Command registerCommand(String name) {
        Command cmd = new Command(name);
        commands.add(cmd);
        return cmd;
    }
}
