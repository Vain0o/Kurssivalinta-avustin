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

import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import kva.logiikka.Sovelluslogiikka;
import kva.logiikka.Sovelluslogiikka.LatauksenTila;

/**JavaFX-kirjastojen avulla luotu Kurssivalinta-avustimen käyttöliittymä.
 * <p>
 * Käyttöliittymä-ikkuna luodaan metodissa {@code luo} annettuun {@code Stage}-olioon. 
 * Ikkuna koostuu {@link kva.ui.Nakyma}-olioista, jotka näytetään {@link javafx.scene.control.TabPane}n 
 * välilehdillä. Aluksi näkyvissä ovat {@link kva.ui.AsetusNakyma} sekä {@link kva.ui.TestiLatausNakyma}, 
 * joka korvataan {@link kva.ui.KurssitarjotinNakyma}lla kun kurssitarjotin luodaan. 
 * Näkymien varsinainen toteutus on kyseisissä luokissa.
 *
 * @author Väinö Viinikka
 */
public class Kayttoliittyma {
    
    private static final int IKKUNAN_LEVEYS = 800;
    private static final int IKKUNAN_KORKEUS = 600;
    
    /**Sisältää tiedon käyttöliittymän käyttämästä sovelluslogiikasta.*/
    private final Sovelluslogiikka logiikka;
    
    /**Sisältää kurssien piilottamista koskevat asetukset.*/
    private final Asetukset asetukset;
    
    /**Sisältää käyttöliittymän välilehdet*/
    private TabPane valilehdet;

    /**Luo uuden käyttöliittymän, joka käyttää parametrina annettua sovelluslogiikkaa.
     * <p>
     * Konstruktori ei vielä luo uutta, käyttäjälle näytettävää ikkunaa, vaan se tehdään 
     * vasta metodissa {@code luo}.
     * 
     * @param logiikka {@code Sovelluslogiikka}, joka vastaa ohjeman varsinaisesta 
     *        toteutuksesta
     */
    public Kayttoliittyma(Sovelluslogiikka logiikka) {
        this.logiikka = logiikka;
        asetukset = new Asetukset();
    }
    
    /**Luo käyttöliittymäikkunan ja sen sisällön annettuun {@link javafx.stage.Stage}en.
     * <p>
     * Metodia tulee kutsua JavaFX:n sovellussäikeessä. Suositeltava tapa käyttää
     * metodia on kutsua sitä luokan {@link javafx.application.Application} alaluokan
     * metodissa {@code start()}.
     * 
     * @param ikkuna {@code Stage}, johon käyttöliittymä luodaan
     */
    public void luo(Stage ikkuna) {
        logiikka.tilaProperty().addListener((a, vanhaArvo, uusiArvo) -> {
            if(uusiArvo == LatauksenTila.KURSSITARJOTIN_LADATTU) {
                luoKurssitarjotinNakyma();
            }
        });
        
        AsetusNakyma asetusnakyma = new AsetusNakyma("Asetukset", this);
        //LatausNakyma valintanakyma = new TestiLatausNakyma("Kurssitarjotin", this);
        LatausNakyma valintanakyma = new WebEngineLatausNakyma("Kurssitarjotin", this);
        
        valilehdet = new TabPane(asetusnakyma.getValilehti(), valintanakyma.getValilehti());
        valilehdet.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Scene scene = new Scene(valilehdet, IKKUNAN_LEVEYS, IKKUNAN_KORKEUS);
        ikkuna.setTitle("Kurssivalinta-avustin");
        ikkuna.setScene(scene);
        ikkuna.show();
    }
    
    /**Palauttaa {@code Kayttoliityman} käyttämän {@link kva.logiikka.Sovelluslogiikka}-olion.
     * 
     * @return käytössä oleva {@code Sovelluslogiikka}
     */
    public Sovelluslogiikka getLogiikka() {
        return logiikka;
    }
    
    /**Palauttaa {@code Kayttoliittyman} piilotettavat aineet ja kurssit sisältävän 
     * {@code Asetukset}-olion.
     * 
     * @return {@code Kayttoliittyman Asetukset}
     */
    public Asetukset getAsetukset() {
        return asetukset;
    }
    
    /**Luo {@code Kayttoliittymalle KurssitarjotinNakyman} ja korvaa sillä {@code KurssitarjottimenValintaNakyman}. 
     * 
     */
    private void luoKurssitarjotinNakyma() {
        valilehdet.getTabs().removeIf((tab) -> tab.getText().equals("Kurssitarjotin"));
        KurssitarjotinNakyma tarjotinNakyma = new KurssitarjotinNakyma("Kurssitarjotin", this, logiikka.getTarjotin());
        valilehdet.getTabs().add(tarjotinNakyma.getValilehti());
        valilehdet.getSelectionModel().select(tarjotinNakyma.getValilehti());
    }
}
