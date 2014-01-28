package net.tbnr.gearz.chat.channels.irc;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.tbnr.gearz.GearzBungee;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Created by jake on 12/20/13.
 */
public class Connection {
    String name;
    String server;
    String login;
    String password;
    @Setter @Getter
    public boolean printInput = false;

    protected Socket socket;
    protected BufferedReader inputReader;
    protected OutputStreamWriter outputWriter;

    @Getter
    public boolean connected;


    public Connection(String name, String server, String login) {
        this.name = name;
        this.server = server;
        this.login = login;
        if (GearzBungee.getInstance().getConfig().isString("irc.password")) {
            this.password = GearzBungee.getInstance().getConfig().getString("irc.password");
        }
    }

    public void connect() throws IOException {
        socket = new Socket(server, 6667);
        this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outputWriter = new OutputStreamWriter(socket.getOutputStream());

        raw("NICK " + name);
        raw("USER " + login + " 8 * :1.0");

        ProxyServer.getInstance().getScheduler().schedule(GearzBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                String line = null;
                try {
                    line = inputReader.readLine();
                } catch (InterruptedIOException iioe) {
                    try {
                        outputWriter.write("PING " + (System.currentTimeMillis() / 1000));
                    } catch (IOException e) {
                        //ignore
                    }
                } catch (Exception e) {
                    line = null;
                }

                if (line != null) {
                    try {
                        if (printInput) System.out.println(line);
                        if (line.startsWith("PING")) {
                            raw("PONG " + line.split(":")[1]);
                        }
                        if (line.startsWith(":" + name)) {
                            connected = true;
                            connectionMethods();
                        }
                    } catch (Exception e) {
                        System.out.println("There was an exception while reading lines!");
                        e.printStackTrace();

                    }
                }
            }
        },0, 1, TimeUnit.MILLISECONDS);
    }

    public void raw(String line) {
        try {
            if (line.length() > 512 - 2)
                line = line.substring(0, 512 - 2);

            outputWriter.write(line + "\r\n");
            outputWriter.flush();
        } catch (IOException e) {
            if (printInput) System.out.println("Error parsing: " + line);

        }
    }

    //This method handles methods that need
    //to be called once the bot is logged in
    public void connectionMethods() {
        for (String channel : GearzBungee.getInstance().getChannelManager().getToJoin()) {
            joinChannel(channel);
        }
        if (this.password != null) {
            raw("PRIVMSG NickServ :identify " + this.name + " " + this.password);
        } else {
            raw("PRIVMSG NickServ :identify " + this.name);
        }
    }

    public void joinChannel(String channel) {
        raw("JOIN " + channel);
    }
}
