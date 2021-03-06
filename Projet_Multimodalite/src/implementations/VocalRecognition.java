package implementations;

/**
 * Yet another Ivy java program example
 *
 * This is the example from the documentation
 *
 * @author Yannick Jestin <jestin@cena.fr>
 *
 * (c) CENA
 *
 * This program is distributed as is
 *
 */
import fr.dgac.ivy.*;
import interfaces.Constants;
import interfaces.VocalRecognitionAPI;
import java.util.HashMap;
import java.util.Map;

public class VocalRecognition implements IvyMessageListener, VocalRecognitionAPI {

    private static Ivy bus;
    private Controller controller;

    public static Map<String, String> mapColors;

    public VocalRecognition(Controller newController) throws IvyException {
        controller = newController;
        initMap();

        bus = new Ivy("IvyTranslater", "IvyTranslater Ready", null);
        bus.bindMsg("^sra5 Parsed=Type:(.*) Value:(.*) Confidence=(.*) NP", this);

        bus.start(null);
    }

    // callback associated to the "Hello" messages"
    @Override
    public void receive(IvyClient client, String[] commands) {
        String type = commands[0];
        String value = commands[1];
        float confidence = Float.parseFloat(commands[2].replace(",", "."));

        switch (type) {
            case VocalRecognitionAPI.COLOR:
                if (confidence > MINIMUM_CONFIDENCE) {
                    if (mapColors.get(value) == null) {
                        System.out.println("Couleur " + value);
                    } else {
                        controller.saidColor(mapColors.get(value));
                    }
                }
                break;
            case VocalRecognitionAPI.OBJECT:
                // Sra a plus de mal à reconnaitre les objets
                if (confidence > MINIMUM_CONFIDENCE - 0.2f) {
                    controller.saidShape(Constants.Shape.valueOf(value.toUpperCase()));
                }
                break;
            case VocalRecognitionAPI.POSITION:
                if (confidence > MINIMUM_CONFIDENCE) {
                    controller.saidPosition();
                }
                break;
        }
    }
    private static final float MINIMUM_CONFIDENCE = 0.7f;

    public static void initMap() {
        mapColors = new HashMap<>();
        mapColors.put("noir", "black");
        mapColors.put("bleu", "blue");
        mapColors.put("rouge", "red");
        mapColors.put("vert", "green");
        mapColors.put("jaune", "yellow");
        mapColors.put("gris", "gray");
        mapColors.put("magenta", "magenta");
        mapColors.put("orange", "orange");
        mapColors.put("rose", "pink");
        mapColors.put("blanc", "white");
        mapColors.put("de cette couleur", "same");
    }
}
