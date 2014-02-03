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
public class MyMapper {

    // input alphabet used by learning algorithm
    public static final Alphabet SIGMA;

    // simulation parameters and signal names
    private static final String force = "force ";
    private static final String run = "run ";
    private static final String examine = "examine -value ";
    private static final String resultAnchor = "value: ";

    // input symbols
    // symbol s = XYZ, where X, Y, and Z is a value of WE_I, CYC_I, 
    //   and STB_I, respectively
    private static final Symbol D_0 = new SymbolImpl("000");
    private static final Symbol D_1 = new SymbolImpl("001");
    private static final Symbol D_2 = new SymbolImpl("010");
    private static final Symbol D_3 = new SymbolImpl("011");
    private static final Symbol D_4 = new SymbolImpl("100");
    private static final Symbol D_5 = new SymbolImpl("101");
    private static final Symbol D_6 = new SymbolImpl("110");
    private static final Symbol D_7 = new SymbolImpl("111");

    private static final String arch = "t/";
    // input signals
    private static final String WE_I = "WE_I ";
    private static final String CYC_I = "CYC_I ";
    private static final String STB_I = "STB_I ";
    // output signals
    private static final String ACK_O = "ACK_O";
    private static final String NOT_CYC_WRITE = "NOT_CYC_WRITE";
    private static final String NOT_CYC_READ = "NOT_CYC_READ";

    private static final String clk_half_period = "20 ns";
    private static final String CLK_0 = "CLK 0";
    private static final String CLK_1 = "CLK 1";
    private static final String RST_0 = "RST 0";
    private static final String RST_1 = "RST 1";

    static {
        SIGMA = new AlphabetImpl();
        SIGMA.addSymbol(D_0);
        SIGMA.addSymbol(D_1);
        SIGMA.addSymbol(D_2);
        SIGMA.addSymbol(D_3);
        SIGMA.addSymbol(D_4);
        SIGMA.addSymbol(D_5);
        SIGMA.addSymbol(D_6);
        SIGMA.addSymbol(D_7);
    }

    /*
     * executeSymbol method returns a system response to a symbol
     */
    public Symbol executeSymbol(Symbol s) throws InterruptedException {
        StringBuilder sb = new StringBuilder();

        if (s.equals(D_0) || s.equals(D_1) || s.equals(D_2) || s.equals(D_3) || s.equals(D_4) || s.equals(D_5) || s.equals(D_6) || s.equals(D_7)) {
            // send commands over telnet
            // set input signals WE_I, CYC_I and STB_I according to symbol values            
            my_telnet.sendCommand(force + arch + WE_I + s.toString().charAt(0));
            my_telnet.sendCommand(force + arch + CYC_I + s.toString().charAt(1));
            my_telnet.sendCommand(force + arch + STB_I + s.toString().charAt(2));
            my_telnet.sendCommand(force + arch + CLK_1);
            my_telnet.sendCommand(run + clk_half_period);
            my_telnet.sendCommand(force + arch + CLK_0);
            my_telnet.sendCommand(run + clk_half_period);

            // examine output signals
            my_telnet.sendCommand(examine + arch + ACK_O);
            my_telnet.readUntil(resultAnchor, false);
            sb.append(my_telnet.readResult(false));

            my_telnet.sendCommand(examine + arch + NOT_CYC_WRITE);
            my_telnet.readUntil(resultAnchor, false);
            sb.append(my_telnet.readResult(false));

            my_telnet.sendCommand(examine + arch + NOT_CYC_READ);
            my_telnet.readUntil(resultAnchor, false);
            sb.append(my_telnet.readResult(false));

            String response = sb.toString();

            if (response.equals(D_0.toString())) {
                return D_0;
            } else if (response.equals(D_1.toString())) {
                return D_1;
            } else if (response.equals(D_2.toString())) {
                return D_2;
            } else if (response.equals(D_3.toString())) {
                return D_3;
            } else if (response.equals(D_4.toString())) {
                return D_4;
            } else if (response.equals(D_5.toString())) {
                return D_5;
            } else if (response.equals(D_6.toString())) {
                return D_6;
            } else if (response.equals(D_7.toString())) {
                return D_7;
            }
        }

        return null;
    }

    /*
     * Set and unset RST signal
     */
    public void reset() throws InterruptedException {
        my_telnet.sendCommand(force + arch + RST_1);
        my_telnet.sendCommand(force + arch + CLK_1);
        my_telnet.sendCommand(run + clk_half_period);
        my_telnet.sendCommand(force + arch + RST_0);
        my_telnet.sendCommand(force + arch + CLK_0);
        my_telnet.sendCommand(run + clk_half_period);
    }

}
