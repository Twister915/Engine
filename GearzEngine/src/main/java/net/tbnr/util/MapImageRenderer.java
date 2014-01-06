package net.tbnr.util;

import org.bukkit.entity.Player;
import org.bukkit.map.*;

import java.awt.image.BufferedImage;

public class MapImageRenderer extends MapRenderer {

    private boolean hasRendered;
    private Thread renderImageThread;

    private BufferedImage image;
    private String text;

    public MapImageRenderer(BufferedImage image, String text) {
        hasRendered = false;
        this.image = image;
        this.text = text;
    }

    @Override
    public void render(MapView view, final MapCanvas canvas, Player player) {
        if (!hasRendered && image != null && renderImageThread == null) {
            renderImageThread = new Thread() {
                @Override
                public void run() {
                    canvas.drawImage(0, 0, MapPalette.resizeImage(image));
                    canvas.drawText(0, 0, MinecraftFont.Font, text);
                }
            };
            renderImageThread.start();
            hasRendered = true;
        }
    }
}

