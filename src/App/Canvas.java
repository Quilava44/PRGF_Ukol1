package App;

import Fills.ScanLine;
import Fills.SeedFill;
import Objects.Circle;
import Objects.Line;
import Render.CircleRenderer;
import Render.Clipper;
import Render.LineRenderer;
import Render.PolygonRenderer;
import Objects.Polygon;
import Objects.Point;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * trida pro kresleni na platno: zobrazeni pixelu, ovladani mysi
 *
 * @author PGRF FIM UHK
 * @version 2017
 */
public class Canvas {

    private JPanel panel;
    private BufferedImage img;
    private int x1,y1,x2,y2;

    private Polygon p = new Polygon();
    private Polygon cl = new Polygon();

    private LineRenderer lr;
    private PolygonRenderer pr;
    private CircleRenderer cr;
    private SeedFill sf;
    private ScanLine sl;
    private Clipper pcl;

    private JPanel menu;
    private JRadioButton usBtn;
    private JRadioButton kruzBtn;
    private JRadioButton polyBtn;
    private JRadioButton aaBtn;
    private JRadioButton vysBtn;
    private JRadioButton seedBtn;
    private JRadioButton seedMat4Btn;
    private JRadioButton scanBtn;
    private JRadioButton clipBtn;
    private ButtonGroup grpBtn;

    private JButton delBtn;


    public Canvas(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        lr = new LineRenderer(img);
        pr = new PolygonRenderer(img);
        cr = new CircleRenderer(img);
        sf = new SeedFill(img);
        sl = new ScanLine(img);
        pcl = new Clipper();


       panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        menu = new JPanel();
        usBtn = new JRadioButton("Úsečka");
        usBtn.setSelected(true);
        aaBtn = new JRadioButton("AA úsečka");
        polyBtn = new JRadioButton("Polygon");
        kruzBtn = new JRadioButton("Kružnice");
        vysBtn = new JRadioButton("Výseč kružnice");
        seedBtn = new JRadioButton("Seed fill");
        seedMat4Btn = new JRadioButton("Seed fill vzor");
        scanBtn = new JRadioButton("ScanLine fill");
        clipBtn = new JRadioButton("Ořez Polygonu");

        grpBtn = new ButtonGroup();
        grpBtn.add(usBtn);
        grpBtn.add(polyBtn);
        grpBtn.add(kruzBtn);
        grpBtn.add(aaBtn);
        grpBtn.add(vysBtn);
        grpBtn.add(seedBtn);
        grpBtn.add(seedMat4Btn);
        grpBtn.add(scanBtn);
        grpBtn.add(clipBtn);

        menu.add(usBtn);
        menu.add(aaBtn);
        menu.add(polyBtn);
        menu.add(kruzBtn);
        menu.add(vysBtn);
        menu.add(seedBtn);
        menu.add(seedMat4Btn);
        menu.add(scanBtn);
        menu.add(clipBtn);

        delBtn = new JButton("Smazat");

        delBtn.addActionListener(actionEvent -> {
            clear();
            if (polyBtn.isSelected())
                p.delPoints();
            present();
        });
        menu.add(delBtn);

        frame.add(menu, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    x1 = e.getX();
                    y1 = e.getY();

                    if (polyBtn.isSelected()) {

                        p.addPoint(new Point(x1,y1));
                        pr.drawPolygon(p);
                    }
                    else if(seedBtn.isSelected())
                        sf.fill(x1,y1,img.getRGB(x1,y1));
                    else if(seedMat4Btn.isSelected())
                        sf.fillPattern(x1,y1,img.getRGB(x1,y1));
                    else if(scanBtn.isSelected())
                        sl.fill(p);
                    else if(clipBtn.isSelected()){
                        clear();
                        pcl.setClipArea(cl);
                        pr.drawPolygon(pcl.clip(p));
                    }
                    panel.repaint();
                }
                if (e.getButton() == MouseEvent.BUTTON3){
                    x1 = e.getX();
                    y1 = e.getY();
                    if (clipBtn.isSelected()){
                        cl.addPoint(new Point(x1,y1));
                        pr.drawPolygon(cl);
                    }

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (usBtn.isSelected()) {
                    x2 = e.getX();
                    y2 = e.getY();
                    lr.drawLine(new Line(x1,y1,x2,y2, 0xffffff));
                }

                panel.repaint();
            }

        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                x2 = e.getX();
                y2 = e.getY();

                if (usBtn.isSelected()) {
                    clear();
                    lr.drawLine(new Line(x1,y1,x2,y2, 0xffffff));
                }
                else if (aaBtn.isSelected()) {
                    clear();
                    lr.drawLineAa(new Line(x1,y1,x2,y2, 0xffffff));
                }
                else if (kruzBtn.isSelected()) {
                    clear();
                    cr.drawCircle(new Circle(x1, y1, x2, y2, 0xffffff));
                }
                else if (vysBtn.isSelected()) {
                    clear();
                    x2 = e.getX();
                    y2 = e.getY();
                    cr.drawSec(new Circle(x1,y1,x2,y2, 0xffffff));
                }
                present();
            }
        });
    }

    public void clear() {
        Graphics gr = img.getGraphics();
        gr.setColor(new Color(0x2f2f2f));
        gr.fillRect(0, 0, img.getWidth(), img.getHeight());
    }

    public void present(Graphics graphics) {
        graphics.drawImage(img, 0, 0, null);
    }


    public void present() {
        if (panel.getGraphics() != null){
            panel.getGraphics().drawImage(img, 0, 0, null);
        }
    }

    public void start() {
        clear();
        img.getGraphics().drawString("Use mouse buttons", 5, img.getHeight() - 5);
        panel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
    }

}