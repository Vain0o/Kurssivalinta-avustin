/* Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
 * Copyright (C) 2022 Väinö Viinikka
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

import java.util.Objects;

/**Olio, jolla kurssitarjottimen periodit tunnistetaan.
 * <p>
 * {@link kva.logiikka.lataus.KurssitarjottimenLataaja} ilmoittaa mahdolliset periodit 
 * muodossa {@code List<PeriodinTunniste>}. Tieto siitä, mitkä periodit halutaan ladata, 
 * annetaan lataajalle niin ikään muodossa {@code Collection<PeriodinTunniste>}.
 * <p>
 * {@code PeriodinTunnistetta} ei voi muuttaa luomisensa jälkeen, ja se sisältää tiedot 
 * oppilaitoksen ja periodin nimistä.
 *
 * @author Väinö Viinikka
 * @see kva.logiikka.PalkinTunniste
 * @since Kurssivalinta-avustin 1.0
 */
public class PeriodinTunniste {
    
    private final String oppilaitos;
    private final String periodi;

    /**Luo uuden {@code PeriodinTunnisteen}.
     * 
     * @param oppilaitos sen oppilaitoksen nimi, jossa periodi käydään, esim. "Otaniemen 
     *        lukio"
     * @param periodi tarkoitetun periodin nimi, esimerkiksi "Periodi 1A" tai "1. 
     *        jakso"
     * @throws java.lang.NullPointerException jos jompikumpi parametreistä on {@code null}
     */
    public PeriodinTunniste(String oppilaitos, String periodi) {
        this.oppilaitos = Objects.requireNonNull(oppilaitos);
        this.periodi = Objects.requireNonNull(periodi);
    }

    /**Kertoo, minkä oppilaitoksen kurssitarjottimesta on kyse.
     * 
     * @return oppilaitoksen nimi
     */
    public String getOppilaitos() {
        return oppilaitos;
    }

    /**Kertoo periodin nimen.
     * 
     * @return periodin nimi
     */
    public String getPeriodi() {
        return periodi;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.oppilaitos);
        hash = 71 * hash + Objects.hashCode(this.periodi);
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
        final PeriodinTunniste other = (PeriodinTunniste) obj;
        if (!Objects.equals(this.oppilaitos, other.oppilaitos)) {
            return false;
        }
        if (!Objects.equals(this.periodi, other.periodi)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PeriodinTunniste{" + "oppilaitos=" + oppilaitos + ", periodi=" + periodi + '}';
    }
}
