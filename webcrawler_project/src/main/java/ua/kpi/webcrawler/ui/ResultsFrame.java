package ua.kpi.webcrawler.ui;

import javax.swing.*;
import java.awt.*;

public class ResultsFrame extends JFrame {

    private final JTable table;

    public ResultsFrame() {
        super("Crawler Results");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {"URL", "Keyword", "Count"};
        Object[][] data = {};
        table = new JTable(data, columns);

        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
