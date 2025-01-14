import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class MeltGifApp extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage meltingImage;
    private int[] lineOffsets;
    private Timer meltTimer;
    private JLabel imageLabel;

    public MeltGifApp() {
        setTitle("Melt GIF Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Add File Chooser Button
        JButton loadButton = new JButton("Load GIF");
        loadButton.addActionListener(e -> loadGif());
        add(loadButton, BorderLayout.NORTH);

        // Image Display Label
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        // Add Melt Button
        JButton meltButton = new JButton("Melt Image");
        meltButton.addActionListener(e -> startMelting());
        add(meltButton, BorderLayout.SOUTH);
    }

    private void loadGif() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("GIF Images", "gif"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                originalImage = ImageIO.read(file);
                meltingImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics g = meltingImage.getGraphics();
                g.drawImage(originalImage, 0, 0, null);
                g.dispose();

                lineOffsets = new int[originalImage.getWidth()];
                imageLabel.setIcon(new ImageIcon(meltingImage));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startMelting() {
        if (meltingImage == null) {
            JOptionPane.showMessageDialog(this, "Please load an image first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (meltTimer != null && meltTimer.isRunning()) {
            meltTimer.stop();
        }

        meltTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean allMelted = true;
                for (int x = 0; x < meltingImage.getWidth(); x++) {
                    if (lineOffsets[x] < meltingImage.getHeight()) {
                        allMelted = false;
                        int shrink = 1 + (int) (Math.random() * 10);
                        shrink = Math.min(shrink, meltingImage.getHeight() - lineOffsets[x]);
                        for (int y = lineOffsets[x]; y < lineOffsets[x] + shrink; y++) {
                            meltingImage.setRGB(x, y, new Color(255, 255, 255, 0).getRGB());
                        }
                        lineOffsets[x] += shrink;
                    }
                }

                imageLabel.repaint();

                if (allMelted) {
                    meltTimer.stop();
                }
            }
        });

        meltTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MeltGifApp app = new MeltGifApp();
            app.setVisible(true);
        });
    }
}
