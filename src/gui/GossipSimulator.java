package gui;

import gossip.Cluster;
import gossip.Member;

import javax.swing.*;
import java.awt.*;

/**
 * Main program, contains GUI.
 * @author Katharina Koslowski, 561010
 */
public class GossipSimulator {
    /**
     * Initializes a label at given position.
     * @param id process id
     * @param xPos x position
     * @param frame frame
     * @return label
     */
    private static JTextArea initLabel(int id, int xPos, JFrame frame){
        JTextArea label = new JTextArea();
        label.setBounds(xPos,150,130, 100);
        label.setText(Cluster.getMember(id).toString());
        frame.add(label);
        return label;
    }

    /**
     * Initializes a button for a process.
     * @param id process id
     * @param xPos x position
     * @param frame frame
     */
    private static void initProcessButton(int id, int xPos, JFrame frame) {
        JButton p = new JButton("Process " + id);
        p.setBounds(xPos,100,130, 40); //x axis, y axis, width, height
        p.setBackground(Color.GREEN);
        p.addActionListener(e -> {
            Member m = Cluster.getMember(id);
            synchronized (m) {
                m.activate();
                m.notifyAll();
            }
            Color background = p.getBackground().equals(Color.GREEN) ? Color.LIGHT_GRAY : Color.GREEN;
            p.setBackground(background);
        });
        frame.add(p);
    }

    public static void main(String[] args) {
        Cluster.startMembers();

        JTextArea[] labels = new JTextArea[Cluster.memberCount];
        JFrame frame = new JFrame();

        int xPos = -125;
        for (int i = 0; i < Cluster.memberCount; i++) {
            xPos += 180;
            initProcessButton(i, xPos, frame);
            labels[i] = initLabel(i, xPos, frame);
            final int tmp = i;
            Cluster.getMember(i).addPropertyChangeListener(e ->
                    labels[tmp].setText((String) e.getNewValue())
            );
        }

        frame.setSize(1000,450);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}
