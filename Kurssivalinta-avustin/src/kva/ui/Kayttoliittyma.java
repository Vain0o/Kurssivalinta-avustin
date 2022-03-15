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
import java.util.function.Consumer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import kva.logiikka.Kurssitarjotin;
import kva.logiikka.PeriodinTunniste;
import kva.logiikka.Sovelluslogiikka;

/**JavaFX-kirjastojen avulla luotu Kurssivalinta-avustimen käyttöliittymä.
 * <p>
 * Käyttöliittymä-ikkuna luodaan metodissa {@code luo} annettuun {@code Stage}-olioon. 
 * Ikkuna koostuu {@link kva.ui.Nakyma}-olioista, jotka näytetään {@link javafx.scene.control.TabPane}n 
 * välilehdillä. Aluksi näkyvissä ovat {@link kva.ui.AsetusNakyma} sekä {@link kva.ui.KurssitarjottimenValintaNakyma}, 
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
        AsetusNakyma asetusnakyma = new AsetusNakyma("Asetukset", this);
        KurssitarjottimenValintaNakyma valintanakyma = new KurssitarjottimenValintaNakyma("Kurssitarjotin", this);
        
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
    
    /**Käskee {@code Sovelluslogiikkaa} lataamaan {@code Kurssitarjottimen} ja luo 
     * sen perusteella {@link kva.ui.KurssitarjotinNakyma}n.
     * <p>
     * Metodin kutsuminen edellyttää, että {@code Sovelluslogiikasta} on jo ladattu 
     * periodien nimet, ja {@code valittavat} kuuluvat niihin.
     * 
     * @param valittavat niiden periodien tunnisteet, joiden halutaan kuuluvan {@code Kurssitarjottimeen}
     * @throws java.lang.IllegalArgumentException jos {@code valittavat} sisältää 
     *         {@code PeriodinTunnisteita}, joita ei löydy {@link kva.logiikka.Sovelluslogiikka#getPeriodienTunnisteet() }-listalta
     * @throws java.lang.IllegalStateException jos {@code Sovelluslogiikan Tila} on 
     *         {@code LUOTU}, {@code LADATAAN_PERIODIEN_NIMIA} tai {@code LADATAAN_KURSSITARJOTINTA}
     * @throws java.lang.NullPointerException jos {@code valittavat} on {@code null}
     */
    public void lataaKurssitarjotin(Collection<PeriodinTunniste> valittavat) {
        Consumer<Kurssitarjotin> tuloksenKasittely = (tarjotin) -> luoKurssitarjotinNakyma(tarjotin);
        Consumer<Throwable> virheenKasittely = (ex) -> {
            naytaVirheviesti("Virhe kurssitarjottimen lataamisessa:\n\n" + ex.getMessage());
            //Seuraavan rivin kommentointi voidaan poistaa testaamisesn ajaksi.
            //ex.printStackTrace();
        };
        getLogiikka().lataaKurssitarjotin(valittavat, tuloksenKasittely, virheenKasittely);
    }
    
    public void naytaVirheviesti(String teksti) {
        Alert ikkuna = new Alert(AlertType.NONE, teksti, ButtonType.OK);
        ikkuna.setTitle("Kurssivalinta-avustin");
        ikkuna.showAndWait();
    }
    
    /**Luo {@code Kayttoliittymalle KurssitarjotinNakyman} ja korvaa sillä {@code KurssitarjottimenValintaNakyman}. 
     * 
     * @param tarjotin {@code Sovelluslogiikan Kurssitarjotin} 
     */
    private void luoKurssitarjotinNakyma(Kurssitarjotin tarjotin) {
        valilehdet.getTabs().removeIf((tab) -> tab.getText().equals("Kurssitarjotin"));
        KurssitarjotinNakyma tarjotinNakyma = new KurssitarjotinNakyma("Kurssitarjotin", this, tarjotin);
        valilehdet.getTabs().add(tarjotinNakyma.getValilehti());
        valilehdet.getSelectionModel().select(tarjotinNakyma.getValilehti());
    }
}
