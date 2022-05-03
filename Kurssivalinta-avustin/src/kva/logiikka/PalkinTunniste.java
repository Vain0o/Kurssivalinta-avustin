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

/**Esitys {@code Ryhman} sijainnista kurssitarjottimessa.
 * <p>
 * {@code PalkinTunniste} sisältää tiedot siitä oppilaitoksesta, periodista ja palkista, 
 * jossa ryhmään liittyvä opetus pidetään. Samalla {@link kva.logiikka.Ryhma}lla voi 
 * olla sijainteinaan useita {@code PalkinTunnisteita}, esimerkiksi yksi kummallekin 
 * periodinpuolikkaalle.
 * <p>
 * {@code PalkinTunnisteen} sisältämiä tietoja ei voi muuttaa luomisen jälkeen.
 *
 * @author Väinö Viinikka
 * @see kva.logiikka.Ryhma
 * @since Kurssivalinta-avustin 1.0
 */
public class PalkinTunniste {
    
    private final String oppilaitos;
    private final String periodi;
    private final String palkki;
    private final int jarjestysluku;
    
    /**Luo uuden {@code PalkinTunnisteen}.
     * 
     * @param oppilaitos sen oppilaitoksen nimi, jossa ryhmään liittyvä opetus pidetään. 
     *        Tieto oppilaitoksesta on mukana sekaannusten välttämiseksi.
     * @param periodi sen periodin tai periodinpuolikkaan nimi, jossa ryhmään liittyvä 
     *        opetus pidetään
     * @param palkki sen palkin nimi tai numero, jossa ryhmään liittyvä opetus pidetään
     * @param jarjestysluku määrittää järjestyksen, jossa {@code PalkinTunnisteet} 
     *        esitetään käyttöliittymässä
     */
    public PalkinTunniste(String oppilaitos, String periodi, String palkki, int jarjestysluku) {
        this.oppilaitos = oppilaitos;
        this.periodi = periodi;
        this.palkki = palkki;
        this.jarjestysluku = jarjestysluku;
    }
    
    /**Luo uuden {@code PalkinTunnisteen}.
     * 
     * @param periodi sisältää tiedot oppilaitoksesta ja periodista, joissa ryhmään 
     *        liittyvä opetus pidetään. Tieto oppilaitoksesta on mukana sekaannuksien 
     *        välttämiseksi.
     * @param palkki sen palkin nimi tai numero, jossa ryhmään liittyvä opetus pidetään
     * @param jarjestysluku määrittää järjestyksen, jossa {@code PalkinTunnisteet} 
     *        esitetään käyttöliittymässä
     */
    public PalkinTunniste(PeriodinTunniste periodi, String palkki, int jarjestysluku) {
        this(periodi.getOppilaitos(), periodi.getPeriodi(), palkki, jarjestysluku);
    }

    /**Luo uuden {@code PalkinTunnisteen} järjestysluvulla {@code Integer.MAX_VALUE}.
     * 
     * @param oppilaitos sen oppilaitoksen nimi, jossa ryhmään liittyvä opetus pidetään. 
     *        Tieto oppilaitoksesta on mukana sekaannusten välttämiseksi.
     * @param periodi sen periodin tai periodinpuolikkaan nimi, jossa ryhmään liittyvä 
     *        opetus pidetään
     * @param palkki sen palkin nimi tai numero, jossa ryhmään liittyvä opetus pidetään
     */
    public PalkinTunniste(String oppilaitos, String periodi, String palkki) {
        this(oppilaitos, periodi, palkki, Integer.MAX_VALUE);
    }
    
    /**Luo uuden {@code PalkinTunnisteen} järjestysluvulla {@code Integer.MAX_VALUE}.
     * 
     * @param periodi sisältää tiedot oppilaitoksesta ja periodista, joissa ryhmään 
     *        liittyvä opetus pidetään. Tieto oppilaitoksesta on mukana sekaannuksien 
     *        välttämiseksi.
     * @param palkki sen palkin nimi tai numero, jossa ryhmään liittyvä opetus pidetään
     */
    public PalkinTunniste(PeriodinTunniste periodi, String palkki) {
        this(periodi.getOppilaitos(), periodi.getPeriodi(), palkki);
    }

    /**Palauttaa sen oppilaitoksen nimen, jossa sijaitsevaa palkkia {@code PalkinTunniste} 
     * kuvaa.
     * 
     * @return oppilaitoksen nimi
     */
    public String getOppilaitos() {
        return oppilaitos;
    }

    /**Palauttaa sen periodin tai periodinpuolikkaan nimen, jossa sijaitsevaa palkkia 
     * {@code PalkinTunniste} kuvaa.
     * 
     * @return periodin tai periodinpuolikkaan nimi
     */
    public String getPeriodi() {
        return periodi;
    }

    /**Palauttaa sen palkin nimen, joka määrittää {@code Ryhmaan} liittyvän opetuksen 
     * ajankohdat.
     * 
     * @return palkin nimi tai numero
     */
    public String getPalkki() {
        return palkki;
    }
    
    /**Palauttaa {@code PeriodinTunnisteen}, joka sisältää tiedot {@code PalkinTunnisteen} 
     * oppilaitoksesta ja periodista.
     * 
     * @return {@code PeriodinTunniste}, joka kuvaa palkin sisältävää periodia.
     */
    public PeriodinTunniste getPeriodinTunniste() {
        return new PeriodinTunniste(oppilaitos, periodi);
    }
    
    /**Palauttaa {@code PalkinTunnisteen} järjestysluvun.
     * 
     * @return luku, joka määrittää {@code PalkinTunnisteiden} järjestyksen käyttöliitymässä
     */
    public int getJarjestysluku() {
        return jarjestysluku;
    }
    
    /**Kertoo, kuuluuko annettu {@code PalkinTunniste} samaan periodiin, kuin tämä.
     * <p>
     * {@code true} palautetaan, jos annetun {@code PalkinTunnisteen} oppilaitos ja 
     * periodi ovat samat, kuin tämän, palkeista riippumatta. Lisäksi jos annettu 
     * {@code PalkinTunniste} on {@code null}, palautetaan {@code false}.
     * 
     * @param toinen {@code PalkinTunniste}, jonka kuuluvuus samaan periodiin halutaan 
     *        selvittää
     * @return {@code true}, jos {@code toinen} kuuluu samaan periodiin, kuin tämä
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
