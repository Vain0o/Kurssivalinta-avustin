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
package kva.ui;

import java.util.Collection;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**Sisältää toiminnallisuuden, jolla kurssitarjottimen ryhmiä piilotetaan.
 * <p>
 * Myös ryhmien suosittelu tultanen toteuttamaan tänne aikanaan.
 *
 * @author Väinö Viinikka
 */
public class Asetukset {
    
    /**Listaus piilotettavaksi merkityistä aineista.
     * <p>
     * Kun aine on merkitty piilotettaviksi, sen kursseja ei näytetä käyttäjälle. 
     * Toteutus perustuu kurssikoodeihin: jos esimerkiksi kokoelmaan lisätään merkkijono 
     * "YH", käyttäjälle ei näytetä ryhmiä, joiden kurssikoodeissa on merkkijono 
     * "YH".
     * <p>
     * Kokoelmaan kannattaa lisätä sellaiset kielet, joita opiskelija ei opiskele, 
     * sekä muut aineet, joista opiskelija ei opiskele yhtään kurssia.
     */
    public final ObservableSet<String> piilotetutAineet;
    
    /**Listaus aineista, joiden ei-pakolliset kurssit piilotetaan.
     * <p>
     * Toteutus perustuu kurssikoodeihin: jos esimerkiksi kokoelmaan lisätään merkkijono 
     * "YH", käyttäjälle ei näytetä ryhmiä, joiden kurssikoodeissa on merkkijono 
     * "YH" ja jotka eivät ole pakollisia.
     * <p>
     * Kokoelmaan kannattaa lisätä sellaiset aineet, joista opiskelija haluaa opiskella 
     * vain pakolliset kurssit.
     */
    public final ObservableSet<String> epakiinnostavatAineet;
    
    /**Listaus kursseista, jotka merkitään piilotettavaksi
     * <p>
     * Toteutus perustuu kurssikoodeihin, mutta se poikkeaa kokoelmien  {@code piilotetutAineet} 
     * ja {@code epakiinnostavatAineet} toteutuksista: jos kokoelmaan lisätään merkkijono 
     * "HI04", käyttäjälle ei näytetä ryhmiä, joiden kurssin kurssikoodi on nimenomaan 
     * "HI04".
     * <p>
     * Kokoelmaan kannattaa lisätä sellaiset sellaisten kurssien kurssikoodit, joita 
     * opiskelija ei aio käydä, mutta jotka eivät sovellu lisättäväksi piilotettuihin 
     * tai epäkiinnostaviin aineisiin.
     */
    public final ObservableSet<String> piilotetutKurssit;
    
    /**Luo uuden {@code Asetukset}-olion, johon ei vielä ole merkitty piilotettavia 
     * aineita tai kursseja.
     * 
     */
    public Asetukset() {
        piilotetutAineet = FXCollections.observableSet(new HashSet<>());
        epakiinnostavatAineet = FXCollections.observableSet(new HashSet<>());
        piilotetutKurssit = FXCollections.observableSet(new HashSet<>());
    }
    
    /**Luo uuden {@code Asetukset}-olion, johon on lisätty valmiiksi piilotettavia 
     * aineita ja kursseja.
     * <p>
     * Jos jokin parametreinä ainnetuista {@code Collectioneista} on arvoltaan {@code null}, 
     * sen kuvaamaan kategoriaan ei lisätä yhtään kursseja, eikä poikkeuksia synny.
     * 
     * @param piilotetutAineet Sisältää kurssikoodit, jotka lisätään {@code piilotetutAineet}-listalle.
     * @param epakiinnostavatAineet Sisältää kurssikoodit, jotka lisätään {@code epakiinnostavatAineet}-listalle.
     * @param piilotetutKurssit Sisältää kurssikoodit, jotka lisätään {@code piilotetutKurssit}-listalle.
     */
    public Asetukset(Collection<String> piilotetutAineet, Collection<String> epakiinnostavatAineet, 
            Collection<String> piilotetutKurssit) {
        this();
        if (piilotetutAineet != null) {
            this.piilotetutAineet.addAll(piilotetutAineet);
        }
        if (epakiinnostavatAineet != null) {
            this.epakiinnostavatAineet.addAll(epakiinnostavatAineet);
        }
        if (piilotetutKurssit != null) {
            this.piilotetutKurssit.addAll(piilotetutKurssit);
        }
    }
}
