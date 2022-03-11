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
package kva.ui;

import java.util.Collection;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import kva.logiikka.Moduuli;
import kva.logiikka.Moduuli.Tyyppi;

/**Sisältää tiedot siitä, mitä ryhmiä piilotetaan.
 * <p>
 * Tiedot piilotuksista ovat kolmessa {@link javafx.collections.ObservableSet}issä, 
 * joita kuuntelemalla on mahdollista pysyä kärryillä siitä, mitkä ryhmät käyttöliittymän 
 * tulisi piilottaa.
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
     * Kokoelmaan kannattaa lisätä sellaiset aineet, joista opiskelija ei ole kiinnostunut, 
     * ja joista hän haluaa opiskella vain pakolliset kurssit.
     */
    public final ObservableSet<String> epakiinnostavatAineet;
    
    /**Listaus kursseista, jotka merkitään piilotettavaksi
     * <p>
     * Toteutus perustuu kurssikoodeihin, mutta se poikkeaa kokoelmien  {@code piilotetutAineet} 
     * ja {@code epakiinnostavatAineet} toteutuksista: jos kokoelmaan lisätään merkkijono 
     * "HI04", käyttäjälle ei näytetä ryhmiä, joiden moduuli koodi on nimenomaan "HI04".
     * <p>
     * Kokoelmaan kannattaa lisätä sellaiset sellaisten kurssien kurssikoodit, joita 
     * opiskelija ei aio käydä, mutta jotka eivät sovellu lisättäväksi piilotettuihin 
     * tai epäkiinnostaviin aineisiin.
     */
    public final ObservableSet<String> piilotetutModuulit;
    
    /**Luo uuden {@code Asetukset}-olion, johon ei vielä ole merkitty piilotettavia 
     * aineita tai kursseja.
     * 
     */
    public Asetukset() {
        piilotetutAineet = FXCollections.observableSet(new HashSet<>());
        epakiinnostavatAineet = FXCollections.observableSet(new HashSet<>());
        piilotetutModuulit = FXCollections.observableSet(new HashSet<>());
    }
    
    /**Luo uuden {@code Asetukset}-olion, johon on lisätty valmiiksi piilotettavia 
     * aineita ja kursseja.
     * <p>
     * Jos jokin parametreinä ainnetuista {@code Collectioneista} on arvoltaan {@code null}, 
     * sen kuvaamaan kategoriaan ei lisätä yhtään kursseja, eikä poikkeuksia synny.
     * 
     * @param piilotetutAineet Sisältää kurssikoodit, jotka lisätään {@code piilotetutAineet}-listalle.
     * @param epakiinnostavatAineet Sisältää kurssikoodit, jotka lisätään {@code epakiinnostavatAineet}-listalle.
     * @param piilotetutModuulit Sisältää kurssikoodit, jotka lisätään {@code piilotetutModuulit}-listalle.
     */
    public Asetukset(Collection<String> piilotetutAineet, Collection<String> epakiinnostavatAineet, 
            Collection<String> piilotetutModuulit) {
        this();
        if (piilotetutAineet != null) {
            this.piilotetutAineet.addAll(piilotetutAineet);
        }
        if (epakiinnostavatAineet != null) {
            this.epakiinnostavatAineet.addAll(epakiinnostavatAineet);
        }
        if (piilotetutModuulit != null) {
            this.piilotetutModuulit.addAll(piilotetutModuulit);
        }
    }
    
    /**Kertoo, pitäisikö annettu {@code Moduuli} piilottaa asetusten mukaan.
     * <p>
     * Moduuli piilotetaan, mikäli 1) sen kurssikoodi on {@code piilotetutModuulit}-listalla, 
     * 2) osa sen kurssikoodista on {@code piilotetutAineet}-listalla tai 3) se ei 
     * ole pakollinen ja osa sen kurssikoodista on {@code epakiinnostavatAineet}-listalla.
     * 
     * @param moduuli {@code Moduuli}, josta halutaan selvittää, pitäisikö se piilottaa
     * @return {@code true}, jos {@code moduuli} on asetusten mukaan piilotettava.
     */
    public boolean pitaisiPiilottaa(Moduuli moduuli) {
        if(piilotetutModuulit.contains(moduuli.getKoodi())) {
            return true;
        }
        if(piilotetutAineet.stream()
                .anyMatch((koodi) -> moduuli.getKoodi().contains(koodi))) {
            return true;
        }
        if(moduuli.getTyyppi() != Tyyppi.PAKOLLINEN) {
            if(epakiinnostavatAineet.stream()
                    .anyMatch((koodi) -> moduuli.getKoodi().contains(koodi))) {
                return true;
            }
        }
        return false;
    }
}
