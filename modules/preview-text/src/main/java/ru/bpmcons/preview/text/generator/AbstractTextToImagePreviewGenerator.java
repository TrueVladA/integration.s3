package ru.bpmcons.preview.text.generator;

import lombok.SneakyThrows;
import ru.bpmcons.preview.base.PreviewGenerator;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public abstract class AbstractTextToImagePreviewGenerator implements PreviewGenerator {
    private static final int MAX_WIDTH = 500;
    private static final int INITIAL_WIDTH = 250;
    private static final int INITIAL_HEIGHT = 250;

    private int charHeight = 0;

    @PostConstruct
    public void initMetrics() {
        Font font = new Font("Arial", Font.PLAIN, 8);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        charHeight = fm.getHeight();
    }

    protected int getMaxLines() {
        return INITIAL_HEIGHT / Math.max(charHeight, 1);
    }

    @SneakyThrows
    protected ByteArrayOutputStream renderText(String text) {
        Font font = new Font("Arial", Font.PLAIN, 8);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        String[] split = text.split("\n");

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = INITIAL_WIDTH;
        int height = INITIAL_HEIGHT;
        for (String s : split) {
            int lineWidth = fm.stringWidth(s);
            if (width < lineWidth && lineWidth < MAX_WIDTH) {
                width = lineWidth;
            }
        }
        if (fm.getHeight() * split.length < height) {
            height = fm.getHeight() * split.length;
        }
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < split.length; i++) {
            g2d.drawString(split[i], 0, fm.getHeight() * i + fm.getHeight());
        }
        g2d.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "jpeg", out);
        return out;
    }
}
