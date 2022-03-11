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
package kva.logiikka.lataus;

import java.util.ArrayList;
import kva.logiikka.PalkinTunniste;

/**Säiliöluokka, johon kerätään ryhmien tiedot niiden luontivaiheessa.
 * <p>
 * Luokka eroaa luokasta {@link kva.logiikka.Ryhma} siten, että siihen ei ole tallennettu 
 * {@link kva.logiikka.Moduuli}a ja sijainteja kuvaavia merkkijonoja on mahdollista 
 * lisätä. Luokkaan kerätään ryhmän sisältämät tiedot ryhmien luontivaiheessa, minkä 
 * jälkeen {@link kva.logiikka.Kurssitarjotin} luo varsinaiset {@code Ryhmat LuotavanRyhman} 
 * pohjalta.
 *
 * @author Väinö Viinikka
 * @see kva.logiikka.Ryhma
 * @see kva.logiikka.PalkinTunniste
 * @see kva.logiikka.lataus.KurssitarjottimenLataaja
 */
public class LuotavaRyhma {
    
    private final String ryhmakoodi;
    private ArrayList<PalkinTunniste> sijainnit;

    /**Luo uuden {@code LuotavanRyhman}, jolla ei ole toistaiseksi sijainteja.
     * <p>
     * {@code LuotavaanRyhmaan} tullaan lisäämään uusia ominaisuuksia (opettajan nimi, 
     * onko ryhmä täysi, jne.) samaan tahtiin, kuin {@code Ryhmaankin}. Tällaisia 
     * uusia ominaisuuksien lisäämistä varten tullaan luomaan uusi konstruktori, ja 
     * tämä merkitään vanhentuneeksi.
     * 
     * @param ryhmakoodi ryhmän koodi, esim. "HI02.3"
     * @throws java.lang.IllegalArgumentException jos ryhmäkoodi on virheellinen, 
     *         ts. jotakin muuta muotoa, kuin "[merkkijono].[merkkijono]".
     */
    public LuotavaRyhma(String ryhmakoodi) {
        if(ryhmakoodi.split("[.]").length != 2) {
            throw new IllegalArgumentException("Virheellinen ryhmäkoodi: " + ryhmakoodi);
        }
        this.ryhmakoodi = ryhmakoodi;
        sijainnit = new ArrayList<>();
    }

    /**Palauttaa konstruktorissa annetun ryhmäkoodin.
     * 
     * @return ryhmäkoodi, esim. "HI02.3"
     */
    public String getRyhmakoodi() {
        return ryhmakoodi;
    }
    
    /**Palauttaa ryhmäkoodin kurssia kuvaavan osan.
     * 
     * @return ryhmäkoodin pistettä edeltävä osa, esim. "HI02" ryhmäkoodilla "HI02.3"
     */
    public String getKurssikoodi() {
        String[] osat = ryhmakoodi.split("[.]");
        return osat[0];
    }
    
    /**Palauttaa ryhmäkoodin osan, joka yksilöi saman kurssin eri ryhmät.
     * <p>
     * Ryhmätunnistetta ei palauteta kokonaislukuna, sillä ainakin Otaniemen lukiossa 
     * on käytössä myös kirjaimia sisältäviä ryhmätunnisteita, kuten "MAA02.D1".
     * 
     * @return ryhmäkoodin pilkkua seuraava osa, esim. "3" ryhmäkoodilla "HI02.3"
     */
    public String getRyhmatunniste() {
        String[] osat = ryhmakoodi.split("[.]");
        return osat[osat.length - 1];
    }
    
    /**Lisää uuden merkkijonon ryhmän sijainteihin.
     * 
     * @param uusiSijainti lisättävä merkkijono
     */
    public void lisaaSijainti(PalkinTunniste uusiSijainti) {
        sijainnit.add(uusiSijainti);
    }
    
    /**Palauttaa listan ryhmälle annetuista sijainneista.
     * <p>
     * Sijainnit ovat siinä järjestyksessä, kuin missä ne on lisätty metodilla {@link #lisaaSijainti(kva.logiikka.PalkinTunniste) }.
     * Sijainnit palautetaan tätä tarkoitusta varten luodulla uudella listalla, eikä 
     * niitä voi poistaa kutsumalla {@code getSijainnit().remove()}.
     * 
     * @return ryhmän sijainnit
     */
    public ArrayList<PalkinTunniste> getSijainnit() {
        return new ArrayList<>(sijainnit);
    }
}
