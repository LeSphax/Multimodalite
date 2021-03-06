/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementations;

import interfaces.Constants;
import interfaces.GestureRecognitionAPI;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

/**
 *
 * @author kerbrase
 */
public class Controller implements interfaces.IController {

    private State state;
    private Constants.Shape currentShape;
    private String currentColor;
    private Point currentPosition;
    private Point finalPosition;
    private Point temporaryPosition;
    private boolean validatedPosition;
    private VocalSynthesis synthesis;

    private Timer t;
    private PaletteController palette;

    private enum State {

        Init,
        Create,
        Delete,
        MoveWaitingForStart,
        MoveWaitingForEnd
    }

    public Controller(PaletteController palette) {
        this.palette = palette;
        synthesis = new VocalSynthesis();
        t = new Timer(3000, (ActionEvent e) -> {
            timerActionPerformed();
        });
        reset();
    }

    private void timerActionPerformed() throws AssertionError {
        switch (state) {
            case Init:
                break;
            case Create:
                palette.createObject(currentShape, currentPosition, currentColor);
                break;
            case Delete:
                if (currentShape == null) {
                    System.out.println("Erreur : Il faut dire la forme de l'objet à supprimer");
                } else if (currentPosition == null) {
                    System.out.println("Erreur : Il faut pointer la position de l'objet à supprimer");
                } else {
                    palette.deleteObject(currentShape, currentColor, currentPosition);
                }
                break;
            case MoveWaitingForStart:
                if (currentShape == null) {
                    System.out.println("Erreur : Il faut dire la forme de l'objet à déplacer");
                } else if (currentPosition == null) {
                    System.out.println("Erreur : Il faut pointer la position de l'objet à déplacer");
                }
                break;
            case MoveWaitingForEnd:
                if (finalPosition == null) {
                    System.out.println("Erreur : Il faut pointer la position où vous voulez déplacer l'objet");
                } else {
                    palette.moveObject(currentPosition, currentShape, currentColor, finalPosition);
                }
                break;
            default:
                throw new AssertionError(state.name());
        }
        reset();
    }

    private void reset() {
        state = State.Init;
        currentPosition = null;
        finalPosition = null;
        currentColor = null;
        currentShape = null;
        validatedPosition = false;
        t.stop();
    }

    @Override
    public void saidColor(String color) {

        if (color.equals("same") && temporaryPosition != null) {
            System.out.println("Couleur entendue: De la même couleur");
            palette.askColor(temporaryPosition, this);
            updateColor(null);
        } else if (color.equals("same") && currentPosition != null) {
            System.out.println("Couleur entendue: De la même couleur");
            palette.askColor(currentPosition, this);
            updateColor(null);
        } else if (color.equals("same") && temporaryPosition == null && currentPosition == null) {
            System.out.println("Erreur : Pointez un objet avant de demander sa couleur");
        } else {
            System.out.println("Couleur entendue: " + color);
            updateColor(color);
        }
    }

    private void updateColor(String color) throws AssertionError {
        switch (state) {
            case Init:
                state = State.Init;
                break;
            case Create:
                state = State.Create;
                t.restart();
                currentColor = color;
                break;
            case Delete:
                state = State.Delete;
                t.restart();
                currentColor = color;
                break;
            case MoveWaitingForStart:
                state = State.MoveWaitingForStart;
                t.restart();
                currentColor = color;
                break;
            case MoveWaitingForEnd:
                state = State.MoveWaitingForEnd;
                t.restart();
                currentColor = color;
                break;
            default:
                throw new AssertionError(state.name());
        }
    }

    @Override
    public void receiveColor(String color) {
        System.out.println("Couleur récupérée : " + color);
        updateColor(color);
    }

    @Override
    public void saidShape(Constants.Shape shape) {
        System.out.println("Forme entendue : " + shape);
        switch (state) {
            case Init:
                state = State.Init;
                break;
            case Create:
                break;
            case Delete:
                state = State.Delete;
                currentShape = shape;
                if (!areDeleteParametersSet()) {
                    t.restart();
                } else {
                    t.stop();
                    timerActionPerformed();
                }
                break;
            case MoveWaitingForStart:
                currentShape = shape;
                if (temporaryPosition == null) {
                    state = State.MoveWaitingForStart;
                } else {
                    currentPosition = temporaryPosition;
                    temporaryPosition = null;
                    state = State.MoveWaitingForEnd;
                }
                t.restart();
                break;
            case MoveWaitingForEnd:
                state = State.MoveWaitingForEnd;
                currentShape = shape;
                if (temporaryPosition != null) {
                    currentPosition = temporaryPosition;
                }
                temporaryPosition = null;
                break;
            default:
                throw new AssertionError(state.name());
        }
    }

    private boolean areDeleteParametersSet() {
        return !(currentColor == null || currentShape == null || currentPosition == null);
    }

    @Override
    public void saidPosition() {
        System.out.println("Position entendue");
        switch (state) {
            case Init:
                state = State.Init;
                break;
            case Create:
                state = State.Create;
                if (currentPosition == null) {
                    currentPosition = temporaryPosition;
                    temporaryPosition = null;
                    t.restart();
                } else {
                    validatedPosition = true;
                    t.restart();
                }
                break;
            case Delete:
                state = State.Delete;
                break;
            case MoveWaitingForStart:
                state = State.MoveWaitingForStart;
                break;
            case MoveWaitingForEnd:
                if (temporaryPosition != null) {
                    finalPosition = temporaryPosition;
                } else {
                    validatedPosition = true;
                }
                timerActionPerformed();
                break;
            default:
                throw new AssertionError(state.name());

        }
    }

    @Override
    public void receivePointerPosition(Point point
    ) {
        System.out.println("Click en :" + point);
        switch (state) {
            case Init:
                state = State.Init;
                break;
            case Create:
                state = State.Create;
                if (validatedPosition) {
                    currentPosition = point;
                    validatedPosition = false;
                } else {
                    temporaryPosition = point;
                }
                break;
            case Delete:
                state = State.Delete;
                currentPosition = point;
                t.restart();
                break;
            case MoveWaitingForStart:
                if (currentShape != null) {
                    state = State.MoveWaitingForEnd;
                    currentPosition = point;

                } else {
                    state = State.MoveWaitingForStart;
                    temporaryPosition = point;
                }
                t.restart();
                break;
            case MoveWaitingForEnd:
                state = State.MoveWaitingForEnd;
                if (validatedPosition) {
                    finalPosition = point;
                    validatedPosition = false;
                } else {
                    temporaryPosition = point;
                }
                t.restart();
                break;
            default:
                throw new AssertionError(state.name());

        }
    }

    @Override
    public void gestureDetected(GestureRecognitionAPI.Gesture gesture) {
        switch (state) {
            case Init:
                computeGesture(gesture);
                break;
            case Create:
                computeGesture(gesture);
                break;
            case Delete:
                computeGesture(gesture);
                break;
            case MoveWaitingForStart:
                computeGesture(gesture);
                break;
            case MoveWaitingForEnd:
                computeGesture(gesture);
                break;
            default:
                throw new AssertionError(state.name());

        }

    }

    private void computeGesture(GestureRecognitionAPI.Gesture gesture) throws AssertionError {
        switch (gesture) {
            case Rectangle:
                reset();
                state = State.Create;
                currentShape = Constants.Shape.RECTANGLE;
                System.out.println("Rectangle reconnu");
                t.start();
                break;
            case Ellipse:
                reset();
                state = State.Create;
                currentShape = Constants.Shape.ELLIPSE;
                System.out.println("Ellipse reconnue");
                t.start();
                break;
            case Supprimer:
                reset();
                state = State.Delete;
                System.out.println("Suppression reconnue");
                t.start();
                break;
            case Deplacer:
                reset();
                state = State.MoveWaitingForStart;
                System.out.println("Déplacement reconnu");
                t.start();
                break;
            default:
                throw new AssertionError(gesture.name());
        }
    }

}
