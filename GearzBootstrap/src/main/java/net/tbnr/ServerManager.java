package net.tbnr;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by rigor789 on 2014.01.18..
 */
public class ServerManager {

    @Getter
    public static ServerManager instance = new ServerManager();
    private ArrayList<Process> servers;

    private ServerManager(){
        servers = new ArrayList<>();
    }

    public void deployServer(Minigame minigame) {
        try {
            servers.add(startServer(minigame));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void attach(){
        try {
            Process process = servers.get(0);
            BufferedReader read = new BufferedReader(new InputStreamReader(process.getInputStream()));
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            while (read.ready()) {
                System.out.println(read.readLine());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private Process startServer(Minigame minigame) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "D:\\__TBNR\\Cogz\\Engine\\GearzBootstrap\\target\\", "start.bat" );
        pb.directory(new File("D:\\__TBNR\\Cogz\\Engine\\GearzBootstrap\\target\\"));
        System.out.println(pb.directory());
        return pb.start();
    }

    public void killServer(Process server){
        server.destroy();
    }

    public enum Minigame {
        SURVIVALGAMES("SurvivalGames", "sg");

        @Getter
        String name;
        @Getter
        String short_name;

        Minigame(String name, String short_name) {
            this.name = name;
            this.short_name = short_name;
        }
    }
}
