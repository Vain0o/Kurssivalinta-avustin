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
package kva.domain;

import java.util.Objects;

/**Kapseloi ne tiedot, jotka ovat samat kaikissa saman kurssin {@code Ryhmissä}.
 *
 * @author Väinö Viinikka
 */
public class Kurssi implements Comparable<Kurssi> {
    
    private String kurssikoodi;

    /**Luo uuden kurssin annettulla kurssikoodilla
     * 
     * @param kurssikoodi Kurssin koodi ilman ryhmän numeroa, esim. FY05.
     */
    public Kurssi(String kurssikoodi) {
        this.kurssikoodi = kurssikoodi;
    }

    public String getKurssikoodi() {
        return kurssikoodi;
    }
    
    public Tyyppi getTyyppi() {
        return Tyyppi.SOVELTAVA;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.kurssikoodi);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Kurssi other = (Kurssi) obj;
        if (!Objects.equals(this.kurssikoodi, other.kurssikoodi)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Kurssi t) {
        return kurssikoodi.compareTo(t.getKurssikoodi());
    }
    
    /**Esitys kurssin tyypeistä, joita on kolmea eri lajia: pakollisia, valtakunnallisia 
     * syventäviä ja koulukohtaisia soveltavia.
     */
    public enum Tyyppi {
        
        PAKOLLINEN, SYVENTÄVÄ, SOVELTAVA;
    }
}
