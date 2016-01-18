/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpvocal;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author kerbrase
 */
public class DessinCarré extends javax.swing.JPanel {

    Point position;
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;

    /**
     * Creates new form DessinCarré
     */
    public DessinCarré() {
        initComponents();
        position = new Point(100, 100);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.fillRect(position.x, position.y, WIDTH, HEIGHT);
    }

    public void changePosition(Point newPosition) {
        position.x += newPosition.x;
        position.y += newPosition.y;
        System.out.println("pos");
        if (position.x < 0 || position.y < 0 || position.x > getWidth() || position.y > getHeight()) {
            ivyTranslater.demiTour();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    void initSquare() {
        position.x = this.getWidth() / 2 - WIDTH / 2;
        position.y = this.getHeight() / 2 - HEIGHT / 2;
    }
}
