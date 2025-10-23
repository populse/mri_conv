package MRIFileManager;

import javax.swing.*;
import java.awt.*;

public class SplashScreenApp extends JWindow {

    private static final long serialVersionUID = 1L;

	public SplashScreenApp(final FileManagerFrame wind) {
        // loading logo
		int logoWidth = 300;
        int logoHeight = 300;
        ImageIcon logo = new ImageIcon(getClass().getResource("/mri_conv.png"));
        Image scaled = logo.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaled));

        // text below
        JLabel textLabel = new JLabel("Loading...", SwingConstants.CENTER);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        textLabel.setForeground(Color.DARK_GRAY);

        // Disposition
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(imageLabel, BorderLayout.CENTER);
        content.add(textLabel, BorderLayout.SOUTH);
//        content.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        setContentPane(content);
        pack();
        setLocationRelativeTo(wind); // centrer à l'écran
        setBackground(new Color(0, 0, 0, 0));
    }

    public void displayTime(int millis) {
        setVisible(true);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setVisible(false);
        dispose();
    }
}
