/* Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
 * Copyright (C) 2021 Väinö viinikka
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
package kva.logiikka;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import kva.domain.Ryhma;

/**Säiliöluokka kurssitarjottimen {@link kva.domain.Ryhma}-olioille
 * <p>
 * Kurssitarjottimelle tehtävät operaatiot syötetään metodilla {@link #annaTehtava(java.util.function.Consumer)}
 * {@code Consumer<Stream<Ryhma>>}-tyyppisinä tehtävinä, joiden {@code accept()}-metodi 
 * saa parametrinaan {@code Ryhmat} sisältävän tietorakenteen {@code Streamin}. 
 * Toteutus mahdollistaa uusien tehtävien lisäämisen tehtävän suorittamisen aikana.
 *
 * @author Väinö Viinikka
 */
public class Kurssitarjotin {
    
    private Set<Ryhma> ryhmat;
    private volatile boolean suoritetaanTehtavaa;
    private Deque<Consumer<Stream<Ryhma>>> tehtavat;
    
    /**Luo uuden {@code Kurssitarjottimen}.
     * 
     */
    public Kurssitarjotin() {
        ryhmat = new HashSet<>();
        suoritetaanTehtavaa = false;
        tehtavat = new LinkedList<>();
    }
    
    /**Suorittaa annetun operaation kurssit sisältävällä {@code Streamilla}.
     * <p>
     * Jos metodia kutsutaan suoritettaessa jotakin muuta tehtävää, annettua tehtävää 
     * ei suoriteta välittömästi. Sen sijaan tehtävä sijoitetaan jonon päähän, ja 
     * suoritetaan käynnissä olevan tehtävän suorittamisen jälkeen.
     * 
     * @param tehtava annettava tehtävä
     * @throws Exception mikä tahansa poikkeus, joka syntyy suoritettaessa tehtävää 
     *         tai sen suorittamisen aikana annettuja muita tehtäviä
     * 
     */
    public void annaTehtava(Consumer<Stream<Ryhma>> tehtava) throws Exception {
        tehtavat.addLast(tehtava);
        suoritaTehtavat();
    }
    
    /**Suorittaa tehtävälistalla olevat tehtävät samassa järjestyksessä, kuin missä 
     * ne lisättiin, ellei metodia kutsuta tehtävän suorittamisen aikana.
     * 
     * @throws Exception mikä tahansa poikkeus, joka syntyy tehtävän suorittamisen 
     *         aikana
     */
    private void suoritaTehtavat() throws Exception {
        if(!suoritetaanTehtavaa) {
            suoritetaanTehtavaa = true;
            while(!tehtavat.isEmpty()) {
                tehtavat.removeFirst().accept(ryhmat.stream());
            }
            suoritetaanTehtavaa = false;
        }
    }
}
