/*
 * Copyright (C) 2013 Lucie Matusova <xmatus21@stud.fit.vutbr.cz>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package former_learnlib;

import de.ls5.jlearn.interfaces.Alphabet;
import de.ls5.jlearn.interfaces.Symbol;
import de.ls5.jlearn.shared.AlphabetImpl;
import de.ls5.jlearn.shared.SymbolImpl;
import static former_learnlib.MyLearner.my_telnet;

/**
 *
 * @author Lucie Matusova <xmatus21@stud.fit.vutbr.cz>
 */
public class MyMapper_old {

    // input symbols
    private static final Symbol DI_0 = new SymbolImpl("DI 0");
    private static final Symbol DI_1 = new SymbolImpl("DI 1");

    // input alphabet used by learning algorithm
    public static final Alphabet SIGMA;

    // simulation parameters and signal names
    private static final String force = "force ";
    private static final String run = "run ";
    private static final String examine = "examine -value ";
    private static final String arch = "t/";
    private static final String data_out = "DO";
    private static final String clk_period = "40 ns";
    private static final String RST_0 = "RST 0";
    private static final String RST_1 = "RST 1";

    private static final String resultAnchor = "result: ";

    static {
        SIGMA = new AlphabetImpl();
        SIGMA.addSymbol(DI_0);
        SIGMA.addSymbol(DI_1);
    }

    // return values of the SUL
    private static final Symbol FALSE = new SymbolImpl("0");
    private static final Symbol TRUE = new SymbolImpl("1");

    /*
     * executeSymbol method returns a system response to a symbol
     */
    public Symbol executeSymbol(Symbol s) throws InterruptedException {
        String response = new String();

        if (s.equals(DI_0) || s.equals(DI_1)) {
            // send commands over telnet
            my_telnet.sendCommand(force + arch + s.toString());
            // there needs to be a delay otherwise we are encountering errors
            Thread.sleep(50);
            my_telnet.sendCommand(run + clk_period);  // depends on a clock period
            Thread.sleep(50);
            my_telnet.sendCommand(examine + arch + data_out);
            Thread.sleep(50);

            my_telnet.readUntil(resultAnchor, false);
            response = my_telnet.readResult(false);

            switch (response) {
                case "0":
                    return FALSE;
                case "1":
                    return TRUE;
            }
        }

        return null;
    }

    /*
     * Set and unset RST signal
     */
    public void reset() throws InterruptedException {
        my_telnet.sendCommand(force + arch + RST_1);
        Thread.sleep(50);
        my_telnet.sendCommand(run + clk_period);  // depends on a clock period
        Thread.sleep(50);
        my_telnet.sendCommand(force + arch + RST_0);
    }

}