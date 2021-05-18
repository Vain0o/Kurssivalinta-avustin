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

import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import kva.domain.Kurssi;

/**Sisältää toiminnallisuuden, jolla kurssitarjottimen ryhmiä piilotetaan.
 * <p>
 * Myös ryhmien suosittelu tultanen toteuttamaan tänne.
 *
 * @author Väinö Viinikka
 */
public class Asetukset {
    
    public final ObservableSet<String> piilotetutAineet;
    public final ObservableSet<String> epakiinnostavatAineet;
    public final ObservableSet<Kurssi> piilotetutKurssit;
    
    public Asetukset() {
        piilotetutAineet = FXCollections.observableSet(new HashSet<>());
        epakiinnostavatAineet = FXCollections.observableSet(new HashSet<>());
        piilotetutKurssit = FXCollections.observableSet(new HashSet<>());
    }
}
