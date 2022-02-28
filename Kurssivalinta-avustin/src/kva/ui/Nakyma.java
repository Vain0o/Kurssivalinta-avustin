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

/**Yläluokka käyttöliittymässä esiintyville välilehdille
 * <p>
 * Luokka hallinnoi alaluokkien puolesta {@link javafx.scene.control.Tab}-välilehteä 
 * ja tietoa käytetystä {@code Kayttoliittymasta}. Välilehden varsinaisen sisällön 
 * luonti jätetään alaluokan tehtäväksi {@link #luoSisalto()}-metodilla. Sisällön 
 * voi myöhemmin vaihtaa metodin {@link #setSisalto(javafx.scene.Node)} avulla.
 * <p>
 * Luokka kutsuu konstruktorissaan alaluokan metodia {@code luoSisalto()}. Näin ollen 
 * alaluokan konstruktorissa ei tulisi olla muuta koodia, kuin viittaus tämän luokan 
 * konstruktoriin. (Hyvä ohjelmointitapa ei välttämättä toteudu, mutta parempaa ratkaisua 
 * ei äkkiseltään tule mieleen.)
 *
 * @author Väinö Viinikka
 */
public abstract class Nakyma {
    
    /**{@code Nakyma}n hallinnoima välilehti */
    private Tab valilehti;
    
    /**Sen {@code Kayttoliittyma}n {@code Kurssitarjotin}, jossa {@code Nakyma}a 
     * käytetään
     */
    private final Kayttoliittyma kayttis;
    
    private String otsikko;
    
    /**Luo uuden Nakyman, jonka {@code Tabillä} on annettu otsikko ja joka käyttää 
     * annettua {@code Kurssitarjotinta}.
     * <p>
     * {@code Nakymat} tulisi luoda luokassa {@link kva.ui.Kayttoliittyma} ja parametriksi 
     * annetun {@code Kurssitarjottimen} tulisi olla {@code Kayttoliittyman} oma 
     * {@code Kurssitarjotin}.
     * 
     * @param otsikko {@code Nakyman} kuvastaman välilehden otsikko
     * @param kayttis {@code Kayttoliittyma}, johon {@code Nakyma} kuuluu
     */
    public Nakyma(String otsikko, Kayttoliittyma kayttis) {
        this.otsikko = otsikko;
        this.kayttis = kayttis;
    }
    
    /**Metodi, jota kutsutaan luokkaa luotaessa ja jonka paluuarvosta tulee {@code Nakyman} 
     * kuvaaman välilehden sisältö
     * <p>
     * Metodin suorittamisen jälkeen sisällön voi vaihtaa metodilla {@link #setSisalto(javafx.scene.Node)} 
     * 
     * @return luotavan välilehden ensimmäinen sisältö
     */
    public abstract Node luoSisalto();
    
    /**Palauttaa {@code Kayttoliittyman}, johon {@code Nakyma} kuuluu.
     *   
     * @return {@code Nakymalle} annettu {@code Kayttoliittyma}
     */
    public final Kayttoliittyma getKayttoliittyma(){
        return kayttis;
    }
    
    /**Palauttaa {@code Nakyman} kuvaaman {@code Tab}-välilehden.
     * 
     * @return {@code Nakyman} kuvaama välilehti, tai {@code null}, jos metodia 
     *         kutsutaan metodissa {@link #luoSisalto()}.
     */
    public final Tab getValilehti() {
        if(valilehti == null) {
            valilehti = new Tab(otsikko, luoSisalto());
        }
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
    protected final void setSisalto(Node sisalto) {
        valilehti.setContent(sisalto);
    }
}
