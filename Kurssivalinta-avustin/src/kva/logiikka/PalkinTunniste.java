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

import java.util.Objects;

/**Esitys {@code Ryhman} sijainnista kurssitarjottimessa.
 * <p>
 * {@code PalkinTunniste} sisältää tiedot siitä oppilaitoksesta, periodista ja palkista, 
 * jossa ryhmään liittyvä opetus pidetään. Samalla {@code Ryhmalla} voi olla useita 
 * sijainteja, esimerkiksi yksi kummallekin periodinpuolikkaalle.
 * <p>
 * {@code RyhmanSijainnin} sisältämiä tietoja ei voi muuttaa luomisen jälkeen.
 *
 * @author Väinö Viinikka
 * @see kva.logiikka.Ryhma
 */
public class PalkinTunniste {
    
    private final String oppilaitos;
    private final String periodi;
    private final String palkki;

    /**Luo uuden {@code RyhmanSijainnin}.
     * 
     * @param oppilaitos sen oppilaitoksen nimi, jossa ryhmään liittyvä opetus pidetään. 
     *        Tieto oppilaitoksesta on mukana sekaannusten välttämiseksi
     * @param periodi sen periodin tai periodinpuolikkaan nimi, jossa ryhmään liittyvä 
     *        opetus pidetään
     * @param palkki sen palkin nimi tai numero, jossa ryhmään liittyvä opetus pidetään.
     */
    public PalkinTunniste(String oppilaitos, String periodi, String palkki) {
        this.oppilaitos = oppilaitos;
        this.periodi = periodi;
        this.palkki = palkki;
    }

    /**Palauttaa sen oppilaitoksen nimen, jonka kurssitarjottimiin {@code Ryhma} kuuluu.
     * 
     * @return oppilaitoksen nimi
     */
    public String getOppilaitos() {
        return oppilaitos;
    }

    /**Palauttaa sen periodin tai periodinpuolikkaan nimen, jossa {@code Ryhma} on 
     * tarjolla.
     * 
     * @return periodin tai periodinpuolikkaan nimi
     */
    public String getPeriodi() {
        return periodi;
    }

    /**Palauttaa sen palkin nimen, joka määrää {@code Ryhmaan} liittyvän opetuksen 
     * ajankohdat.
     * 
     * @return palkin nimi tai numero
     */
    public String getPalkki() {
        return palkki;
    }
    
    public PeriodinTunniste getPeriodinTunniste() {
        return new PeriodinTunniste(oppilaitos, periodi);
    }
    
    /** * Kertoo, kuuluuko annettu {@code PalkinTunniste} samaan periodiin.
     * <p>
     * {@code true} palautetaan, jos annetun {@code toisen} oppilaitos ja 
     * periodi ovat samat, kuin tämän {@code RyhmanSijainnin}, riippumatta palkeista. 
     * Lisäksi jos {@code toinen} on {@code null}, palautetaan arvo {@code false}.
     * 
     * @param toinen {@code PalkinTunniste}, jonka kuuluvuus samaan periodiin halutaan 
     *               selvittää
     * @return {@code true}, jos {@code toinen} kuuluu samaan periodiin
     */
    public boolean onSamaPeriodi(PalkinTunniste toinen) {
        if(toinen == null) {
            return false;
        }
        return getPeriodinTunniste().equals(toinen.getPeriodinTunniste());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.oppilaitos);
        hash = 89 * hash + Objects.hashCode(this.periodi);
        hash = 89 * hash + Objects.hashCode(this.palkki);
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
        final PalkinTunniste other = (PalkinTunniste) obj;
        if (!Objects.equals(this.oppilaitos, other.oppilaitos)) {
            return false;
        }
        if (!Objects.equals(this.periodi, other.periodi)) {
            return false;
        }
        if (!Objects.equals(this.palkki, other.palkki)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PalkinTunniste{" + "oppilaitos=" + oppilaitos + ", periodi=" + periodi + ", palkki=" + palkki + '}';
    }
}
