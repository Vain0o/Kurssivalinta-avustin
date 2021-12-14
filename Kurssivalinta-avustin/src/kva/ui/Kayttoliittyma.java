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

import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import kva.logiikka.Sovelluslogiikka;

/**JavaFX-kirjastojen avulla luotu Kurssivalinta-avustimen käyttöliittymä.
 * <p>
 * Käyttöliittymä-ikkuna luodaan metodissa {@code luo} annettuun {@code Stage}-olioon. 
 * Ikkuna koostuu {@code Nakymista}, jotka näytetään {@link javafx.scene.control.TabPane}n 
 * välilehdillä. Aluksi näkyvissä ovat {@link kva.ui.AsetusNakyma} sekä {@link kva.ui.KurssitarjottimenValintaNakyma}, 
 * joka korvataan {@link kva.ui.KurssitarjotinNakyma}lla kun kurssitarjotin luodaan. 
 * Näkymien varsinainen toteutus on kyseisissä luokissa.
 * <p>
 * {@code KurssitarjottimenValintaNakyman} korvaamista ei ole vielä toteutettu.
 *
 * @author Väinö Viinikka
 * @see kva.ui.Nakyma
 */
public class Kayttoliittyma {
    
    private static final int IKKUNAN_LEVEYS = 800;
    private static final int IKKUNAN_KORKEUS = 600;
    
    /**Sisältää tiedon käyttöliittymän käyttämästä sovelluslogiikasta.*/
    private final Sovelluslogiikka logiikka;
    
    /**Sisältää kurssien piilottamista koskevat asetukset.*/
    private final Asetukset asetukset;

    /**Luo uuden käyttöliittymän, joka käyttää parametrina annettua sovelluslogiikkaa.
     * <p>
     * Konstruktori ei vielä luo uutta, käyttäjälle näytettävää ikkunaa, vaan se
     * tehdään vasta metodissa {@code luo}.
     * 
     * @param logiikka {@code Sovelluslogiikka}, joka vastaa ohjeman varsinaisesta 
     *                 toteutuksesta
     */
    public Kayttoliittyma(Sovelluslogiikka logiikka) {
        this.logiikka = logiikka;
        asetukset = new Asetukset();
    }
    
    /**Luo käyttöliittymäikkunan ja sen sisällön annettuun {@link javafx.stage.Stage}en.
     * <p>
     * Metodia tulisi kutsua JavaFX:n sovellussäikeessä. Suositeltava tapa käyttää
     * metodia on kutsua sitä luokan {@link javafx.application.Application} alaluokan
     * metodissa {@code Start}.
     * 
     * @param ikkuna {@code Stage}, johon käyttöliittymä luodaan
     */
    public void luo(Stage ikkuna) {
        AsetusNakyma asetusnakyma = new AsetusNakyma("Asetukset", this);
        KurssitarjottimenValintaNakyma valintanakyma = new KurssitarjottimenValintaNakyma("Kurssitarjotin", this);
        
        TabPane valilehdet = new TabPane(asetusnakyma.getValilehti(), valintanakyma.getValilehti());
        valilehdet.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Scene scene = new Scene(valilehdet, IKKUNAN_LEVEYS, IKKUNAN_KORKEUS);
        ikkuna.setTitle("Kurssivalinta-avustin");
        ikkuna.setScene(scene);
        ikkuna.show();
    }
    
    /**Palauttaa {@code Kayttoliityman} käyttämän {@link kva.logiikka.Sovelluslogiikka}-olion.
     * 
     * @return käytössä oleva {@code Kurssitarjotin}
     */
    public Sovelluslogiikka getLogiikka() {
        return logiikka;
    }
    
    /**Palauttaa {@code Kayttoliittyman} piilotettavat aineet ja kurssit sisältävän 
     * {@code Asetukset}-olion.
     * 
     * @return {@code Kayttoliittyman Asetukset}
     * @see kva.ui.Asetukset
     */
    public Asetukset getAsetukset() {
        return asetukset;
    }
}
