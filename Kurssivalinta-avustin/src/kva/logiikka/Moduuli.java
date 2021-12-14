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

/**Kapseloi ne tiedot, jotka ovat samat kaikissa saman moduulin {@code Ryhmissä}.
 *
 * @author Väinö Viinikka
 */
public class Moduuli implements Comparable<Moduuli> {
    
    private String koodi;
    private Tyyppi tyyppi;

    /**Luo uuden kurssin annettulla kurssikoodilla.
     * 
     * @param koodi Kurssin koodi ilman ryhmän numeroa, esim. FY05
     * @param tyyppi Kertoo kurssin pakollisuuden
     */
    public Moduuli(String koodi, Tyyppi tyyppi) {
        this.koodi = koodi;
        this.tyyppi = tyyppi;
    }

    /**Palauttaa moduulin kurssikoodin.
     * 
     * @return koodi ilman ryhmän numeroa, esim. FY05
     */
    public String getKoodi() {
        return koodi;
    }
    
    /**Palauttaa {@code Tyyppi}-olion, joka kertoo, onko kurssi pakollinen, syventävä 
     * vai soveltava.
     * 
     * @return {@code Kurssin Tyyppi}
     */
    public Tyyppi getTyyppi() {
        return tyyppi;
    }

    @Override
    public int compareTo(Moduuli t) {
        return koodi.compareTo(t.getKoodi());
    }
    
    /**Esitys kurssin tyypeistä, joita on neljää eri lajia: pakollisia, valtakunnallisia 
     * syventäviä, koulukohtaisia syventäviä ja koulukohtaisia soveltavia.
     */
    public enum Tyyppi {
        
        /**Kertoo, että kyseessä on pakollinen kurssi.
         * <p>
         * Pakolliset kurssit ovat tarjolla kaikissa lukioissa ja jokaisen opiskelijan 
         * on käytävä ne. Poikkeuksena pitkien kielien pakollisia kursseja ei ole 
         * tarjolla niissä lukioissa, joissa kyseisen kielen opiskelu ei ole mahdollista. 
         * Lisäksi eräiden lukiolinjojen opiskelijoilla on huojennusoikeus, eli 
         * heidän ei tarvitse käydä kaikkia pakollisia kursseja.
         * <p>
         * Opiskelijan pitää olla käynyt kaikki aineen pakolliset kurssit ennen 
         * kuin hän voi osallistua kyseisen aineen ylioppilaskokeeseen. Ylioppilaskokeen 
         * kysymyksen perustuvat kunkin aineen pakollisten ja syventävien kurssien oppimäärään.
         */
        PAKOLLINEN, 
        
        /**Kertoo, että kyseessä on valtakunnallinen syventävä kurssi.
         * <p>
         * Valtakunnalliset syventävät kurssit ovat tarjolla kaikissa lukioissa, 
         * mutta niiden käyminen ei ole pakollista.
         * <p>
         * Ylioppilaskokeen kysymykset perustuvat kunkin aineen pakollisiin ja syventäviin 
         * kursseihin. Lyhyissä kielissä ei ole pakollisia kursseja, ja niiden ylioppilaskokeeseen 
         * voi osallistua käytyään vähintään kolme kurssia kyseisestä kielestä.
         */
        VALTAKUNNALLINEN_SYVENTAVA, 
        
        /**Kertoo, että kyseessä on koulukohtainen syventävä kurssi.
         * <p>
         * Koulukohtaiset syventävät kurssit eivät ole tarjolla kaikissa lukioissa, 
         * eikä niiden käyminen ole pakollista. Ylioppilaskokeen kysymykset perustuvat 
         * kunkin aineen pakollisiin ja syventäviin kursseihin.
         */
        KOULUKOHTAINEN_SYVENTAVA, 
        
        /**Kertoo, että kyseessä on koulukohtainen soveltava kurssi.
         * <p>
         * Koulukohtaiset soveltavat kurssit eivät ole pakollisia. Kyseessä ovat 
         * usein kunkin lukion omat erikoisuudet, kuten projekit ja opintomatkat. 
         * Myös ylioppilastutkintoon valmistavat kertauskurssit ovat pääsääntöisesti 
         * koulukohtaisia soveltavia kursseja.
         */
        SOVELTAVA;
    }
}