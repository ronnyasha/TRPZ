package ua.kpi.webcrawler.ui;

import javax.swing.*;
import java.awt.*;

public class SettingsFrame extends JFrame {

    public SettingsFrame() {
        super("Crawler Settings");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Start URL:"));
        panel.add(new JTextField("https://example.org/"));
        panel.add(new JLabel("Max depth:"));
        panel.add(new JTextField("1"));
        panel.add(new JLabel("Domain filter:"));
        panel.add(new JTextField("example.org"));
        panel.add(new JLabel("Keywords:"));
        panel.add(new JTextField("example,domain"));

        JButton btnSave = new JButton("Save profile");
        JButton btnRun = new JButton("Run crawl");

        JPanel buttons = new JPanel();
        buttons.add(btnSave);
        buttons.add(btnRun);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }
}
