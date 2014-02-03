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

import de.ls5.jlearn.abstractclasses.LearningException;
import de.ls5.jlearn.interfaces.Oracle;
import de.ls5.jlearn.interfaces.Symbol;
import de.ls5.jlearn.interfaces.Word;
import de.ls5.jlearn.shared.WordImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lucie Matusova <xmatus21@stud.fit.vutbr.cz>
 */

public class MyOracle implements Oracle {

    private final MyMapper testDriver;

    public MyOracle() {        
        testDriver = new MyMapper();
    }
    
    // go through the collection (inputs, outputs)
    //  - find input and add output to the trace
    // return trace at the end
    @Override
    public Word processQuery(Word query) throws LearningException {

        Word trace = new WordImpl() {};
        try {
            testDriver.reset();
        } catch (InterruptedException ex) {
            Logger.getLogger(MyOracle.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Symbol i : query.getSymbolList()) {
            Symbol o;
            try {
                o = testDriver.executeSymbol(i);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyOracle.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            trace.addSymbol(o);
        }
        return trace;
    }
}