/* Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
 * Copyright (C) 2021 Väinö Viinikka
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

import javafx.scene.Node;
import javafx.scene.control.Tab;
import kva.logiikka.Sovelluslogiikka;

/**Yläluokka käyttöliittymässä esiintyville välilehdille
 * <p>
 * Luokka hallinnoi alaluokkien puolesta {@link javafx.scene.control.Tab}-välilehteä 
 * ja {@link kva.logiikka.Sovelluslogiikka}a. Välilehden varsinaisen sisällön luonti 
 * jätetään alaluokan tehtäväksi {@link #luoSisalto()}-metodilla. Sisällön voi myöhemmin 
 * vaihtaa metodin {@link #setSisalto(javafx.scene.Node)} avulla.
 *
 * @author Väinö Viinikka
 */
public abstract class Nakyma {
    
    /**{@code Nakyma}n hallinnoima välilehti */
    private final Tab valilehti;
    
    /**Sen {@code Kayttoliittyma}n {@code Sovelluslogiikka}, jossa {@code Nakyma}a 
     * käytetään
     */
    private final Sovelluslogiikka logiikka;
    
    /**Luo uuden Nakyman, jonka {@code Tabillä} on annettu otsikko ja joka käyttää 
     * annettua {@code Sovelluslogiikkaa}.
     * <p>
     * {@code Nakyma}t tulisi luoda luokassa {@link kva.ui.Kayttoliittyma} ja parametriksi 
     * annetun {@Sovelluslogiikan} tulisi olla {@code Kayttoliittyman} oma {@code Sovelluslogiikka}
     * 
     * @param otsikko {@Nakyman} kuvastaman välilehden otsikko
     * @param logiikka {@Nakyman} käyttöön annettava {@Sovelluslogiikka}
     */
    public Nakyma(String otsikko, Sovelluslogiikka logiikka) {
        this.logiikka = logiikka;
        this.valilehti = new Tab(otsikko, luoSisalto());
    }
    
    /**Metodi, jota kutsutaan luokkaa luotaessa ja jonka paluuarvosta tulee {@code Nakyman} 
     * kuvaaman välilehden sisältö
     * <p>
     * Metodin suorittamisen jälkeen sisällön voi vaihtaa metodilla {@link #setSisalto(javafx.scene.Node) 
     * 
     * @return Luotavan välilehden ensimmäinen sisältö
     */
    public abstract Node luoSisalto();
    
    /**Palauttaa {@code Nakyman} käytössä olevan {@code Sovelluslogiikan}.
     *   
     * @return {@code Nakymalle} annettu {@code Sovelluslogiikka}
     */
    public final Sovelluslogiikka getLogiikka(){
        return logiikka;
    }
    
    /**Palauttaa {@code Nakyman} kuvaaman {@code Tab}-välilehden.
     * 
     * @return {@code Nakyman} kuvaama välilehti, tai {@code null}, jos metodia 
     *         kutsutaan metodissa {@link #luoSisalto()}.
     */
    public final Tab getValilehti() {
        return valilehti;
    }
    
    /**Palauttaa välilehden sisällön {@link javafx.scene.Node}-muodossa.
     * 
     * @return {@code Nakyman} kuvaaman välilehden sisältö
     * @exception java.lang.NullPointerException jos metodia kutsutaan metodista 
     *            {@link #luoSisalto()}
     */
    public final Node getSisalto() {
        return valilehti.getContent();
    }
    
    /**Vaihtaa {@code Nakyman} kuvaaman välilehden sisällön uuteen.
     * <p>
     * Metodia tulisi lähtökohtaisesti kutsua vain alaluokista.
     * 
     * @param sisalto välilehden uusi sisältö
     * @exception java.lang.NullPointerException jos metodia kutsutaan metodista 
     *            {@link #luoSisalto()}
     */
    public final void setSisalto(Node sisalto) {
        valilehti.setContent(sisalto);
    }
}
