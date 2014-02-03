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
import de.ls5.jlearn.algorithms.dhcmodular.DHCModular;
import de.ls5.jlearn.equivalenceoracles.RandomWalkEquivalenceOracle;
import de.ls5.jlearn.equivalenceoracles.WMethodEquivalenceTest;
import de.ls5.jlearn.exceptions.ObservationConflictException;
import de.ls5.jlearn.interfaces.Automaton;
import de.ls5.jlearn.interfaces.EquivalenceOracle;
import de.ls5.jlearn.interfaces.EquivalenceOracleOutput;
import de.ls5.jlearn.interfaces.Learner;
import de.ls5.jlearn.interfaces.Oracle;
import de.ls5.jlearn.logging.HtmlLoggingAppender;
import de.ls5.jlearn.logging.LearnLog;
import de.ls5.jlearn.logging.LogLevel;
import de.ls5.jlearn.logging.PrintStreamLoggingAppender;
import de.ls5.jlearn.util.DotUtil;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lucie Matusova <xmatus21@stud.fit.vutbr.cz>
 */
public class MyLearner {

    public static AutoTelnetClient my_telnet = new AutoTelnetClient("localhost", 2001);

    public static void main(String[] args) throws UnsupportedEncodingException, ObservationConflictException, LearningException {

        LearnLog.addAppender(new PrintStreamLoggingAppender(LogLevel.INFO, System.out));
        try {
            LearnLog.addAppender(new HtmlLoggingAppender(LogLevel.DEBUG, "/tmp/learn.html", false, false, false));
        } catch (IOException ex) {
            Logger.getLogger(MyLearner.class.getName()).log(Level.SEVERE, "addappender exception", ex);
        }

        // create oracle for mutex
        Oracle testOracle = new MyOracle();

        EquivalenceOracle eqtest = new WMethodEquivalenceTest(20); //new RandomWalkEquivalenceOracle();
        //((RandomWalkEquivalenceOracle) eqtest).setMaxTests(50);

        eqtest.setOracle(testOracle);

        Learner learner = new DHCModular();
        learner.setOracle(testOracle);  // set the Oracle to be queried during the learning process
        learner.setAlphabet(MyMapper.SIGMA);

        boolean equiv = false;
        while (!equiv) {
            try {
                // learn one round
                //  - starts the learning algorithm, returns once a hypothesis has been constructed
                learner.learn();
            } catch (LearningException ex) {
                Logger.getLogger(MyLearner.class.getName()).log(Level.SEVERE, "learner.learn() exception", ex);
            }
            Automaton hyp = learner.getResult();

            //logger.log(LogLevel.INFO, "hypothetical states: " + hyp.getAllStates().size());
            System.out.println("hypothetical states: " + hyp.getAllStates().size());

            EquivalenceOracleOutput o;
            // search for counterexample
            try {
                o = eqtest.findCounterExample(hyp);
                if (o == null) {
                    equiv = true;
                    continue;
                }
            } catch (NullPointerException ex) {
                equiv = true;
                continue;
            }

            try {
                learner.addCounterExample(o.getCounterExample(), o.getOracleOutput());
            } catch (ObservationConflictException ex) {
                Logger.getLogger(MyLearner.class.getName()).log(Level.SEVERE, "learner.addconterexample exception", ex);
            }
        }

        // close the connection with ModelSim by sending ESCAPE character
        byte[] bytes = new byte[]{0x1b};
        String esc = new String(bytes, "UTF-8");
        my_telnet.sendCommand(esc, false);
        my_telnet.disconnect();

        DotUtil.writeDot(learner.getResult(), new File("/tmp/learnresult_dhcm_wmethod_20.dot"));
    }

}
