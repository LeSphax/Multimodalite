/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementations;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import interfaces.Constants;
import static interfaces.Constants.Shape.ELLIPSE;
import static interfaces.Constants.Shape.RECTANGLE;
import interfaces.IController;
import java.awt.Color;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import interfaces.IPaletteController;

/**
 *
 * @author annia
 */
public class PaletteController implements IPaletteController {

    private Ivy bus;
    IvyMessageListener callback, callback2;
    private Point destination;
    private int action;
    private String shape;
    private String color;
    
    private IController controller;

    public PaletteController() {
        try {
            bus = new Ivy("PaletteController", "PaletteController ready", null);

            callback = new IvyMessageListener() {
                @Override
                public void receive(IvyClient ic, String[] strings) {
                    if (action !=3){
                        String nom;
                        nom = strings[2];
                        try {
                            if (action == 0) {
                                //for(String n:nom){
                                if (shape == null) {
                                    if (color != null) {
                                        bus.sendMsg("Palette:DemanderInfo nom=" + nom);
                                    } else {
                                        bus.sendMsg("Palette:SupprimerObjet nom=" + nom);
                                        //shape = null;
                                    }
                                } else if (shape.equals(nom.substring(0, MOVE_ACTION))){
                                   if (color != null) {
                                        bus.sendMsg("Palette:DemanderInfo nom=" + nom);
                                    } else {

                                        bus.sendMsg("Palette:SupprimerObjet nom=" + nom);
                                        //shape = null;
                                    }
                                }


                                //}
                            } else if (action == MOVE_ACTION) {
                                if (shape == null) {
                                    if (color != null) {
                                        bus.sendMsg("Palette:DemanderInfo nom=" + nom);
                                    } else {
                                         bus.sendMsg("Palette:DeplacerObjet nom=" + nom + " x=" + destination.x + " y=" + destination.y);
                                        //shape = null;
                                    }
                                } else if (shape.equals(nom.substring(0, 1))){
                                   if (color != null) {
                                        bus.sendMsg("Palette:DemanderInfo nom=" + nom);
                                    } else {
                                         bus.sendMsg("Palette:DeplacerObjet nom=" + nom + " x=" + destination.x + " y=" + destination.y);
                                        //shape = null;
                                    }
                                }
                                //for(String n:nom){

                                //}

                            }
                        } catch (IvyException ex) {
                            Logger.getLogger(PaletteController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        String nom = strings[2];
                        try {
                            bus.sendMsg("Palette:DemanderInfo nom=" + nom);
                        } catch (IvyException ex) {
                            Logger.getLogger(PaletteController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };
            callback2 = (IvyClient ic, String[] strings) -> {
                if (action != 3){
                    String couleur = strings[5];
                    String nom = strings[0];
                    try {
                        if (color.equals(couleur)) {
                            if (action == 0) {
                                bus.sendMsg("Palette:SupprimerObjet nom=" + nom);
                                //color = null;
                            } else if (action == MOVE_ACTION){
                                bus.sendMsg("Palette:DeplacerObjet nom=" + nom + " x=" + destination.x + " y=" + destination.y);
                            }
                        }
                    } catch (IvyException ex) {
                        Logger.getLogger(PaletteController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    color = strings[5];
                    controller.receiveColor(color);
                }
            };


            bus.bindMsg("Palette:ResultatTesterPoint x=(.*) y=(.*) nom=(.*)", callback);
            bus.bindMsg("Palette:Info nom=(.*) x=(.*) y=(.*) longueur(.*) hauteur(.*) couleurFond=(.*) couleurContour(.*)", callback2);
            bus.start(null);
        } catch (IvyException ex) {
            Logger.getLogger(PaletteController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void createObject(Constants.Shape shape, Point position, String stringColor) {
        try {
            Color color;
            if (position == null) {
                position = new Point(0, 0);
            }
            if (stringColor == null) {
                color = Color.white;
            } else {
                try {
                    Field field = Class.forName("java.awt.Color").getField(stringColor);
                    color = (Color) field.get(null);
                } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    color = null; // Not defined
                }
            }
            switch (shape) {
                case RECTANGLE: {

                    bus.sendMsg("Palette:CreerRectangle x=" + position.x + " y="
                            + position.y + " couleurFond=" + color.getRed() + ":"
                            + color.getGreen() + ":" + color.getBlue());

                    break;
                }
                case ELLIPSE: {
                    bus.sendMsg("Palette:CreerEllipse x=" + position.x + " y="
                            + position.y + " couleurFond=" + color.getRed() + ":"
                            + color.getGreen() + ":" + color.getBlue());
                    break;
                }
            }
        } catch (IvyException ie) {
            System.out.println("can't send message");
        }
    }

    public void moveObject(Point origin, Point destination) {
        try {
            this.destination = new Point(destination.x-origin.x,destination.y - origin.y);
            action = MOVE_ACTION;
            bus.sendMsg("Palette:TesterPoint x=" + origin.x + " y=" + origin.y);
        } catch (IvyException ie) {
            System.out.println("can't send message");
        }
    }
    private static final int MOVE_ACTION = 1;


    @Override
    public void deleteObject(Constants.Shape shape, String color, Point position) {
        try {
            if (null != shape) switch (shape) {
                case RECTANGLE:
                    this.shape = "R";
                    break;
                case ELLIPSE:
                    this.shape = "E";
                    break;
                default:
                    this.shape = null;
                    break;
            }
            this.color = color;
            action = 0;
            bus.sendMsg("Palette:TesterPoint x=" + position.x + " y=" + position.y);
        } catch (IvyException ie) {
            System.out.println("can't send message");
        }
    }

    @Override
    public void moveObject(Point origin, Constants.Shape shape, String color, Point destination) {
       try {
           if (null != shape) switch (shape) {
               case RECTANGLE:
                   this.shape = "R";
                   break;
               case ELLIPSE:
                   this.shape = "E";
                   break;
               default:
                   this.shape = null;
                   break;
           }
            this.color = color;
            this.destination = new Point(destination.x-origin.x,destination.y - origin.y);
            action = MOVE_ACTION;
            bus.sendMsg("Palette:TesterPoint x=" + origin.x + " y=" + origin.y);
        } catch (IvyException ie) {
            System.out.println("can't send message");
        }
    }

    @Override
    public void askColor(Point position, IController controller) {
        action = 3;
        this.controller = controller;
        try {
            bus.sendMsg("Palette:TesterPoint x=" + position.x + " y=" + position.y);
        } catch (IvyException ex) {
            Logger.getLogger(PaletteController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
