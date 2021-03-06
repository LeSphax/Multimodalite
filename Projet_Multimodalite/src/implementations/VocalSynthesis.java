package implementations;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerbrase
 */
public class VocalSynthesis {

    private static Ivy bus;

    VocalSynthesis(){
        // initialization, name and ready message
        bus = new Ivy("VocalSynthesis", "VocalSynthesis Ready", null);
        try {
            // starts the bus on the default domain
            bus.start(null);
        } catch (IvyException ex) {
            Logger.getLogger(VocalSynthesis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void say(String string) {
        System.out.println(string);
        try {
            bus.sendMsg("Virginie Say=\"" + string + "\"");
        } catch (IvyException ex) {
            Logger.getLogger(VocalSynthesis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
