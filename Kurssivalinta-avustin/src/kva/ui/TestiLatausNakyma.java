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

import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import kva.logiikka.PeriodinTunniste;

/**Toteuttaa {@code Nakyman} jossa käyttäjä antaa tarvittavat ohjeet kurssitarjottimen 
 * lataamiselle tekstitiedostosta.
 * <p>
 * Ensiksi käyttäjä antaa tarvittavan Wilma-palvelimen (tai testiversioissa tiedoston) 
 * osoitteen. Tämän jälkeen hän pääsee valitsemaan haluamansa periodit mahdollisten 
 * periodien listalta. Kun käyttäjä tämän jälkeen käskee lataamaan kurssitarjottimen, 
 * {@code TestiLatausNakyma} korvataan {@link kva.ui.KurssitarjotinNakyma}lla, 
 * jossa kurssitarjotin esitetään.
 *
 * @author Väinö Viinikka
 * @see kva.logiikka.lataus.KurssitarjottimenLataaja
 */
public class TestiLatausNakyma extends LatausNakyma {
    
    /**Luo uuden {@code KurssitarjottimenValintaNakyman}.
     * 
     * @param otsikko välilehden otsikko
     * @param kayttis {@code Kayttoliittyma}, johon {@code KurssitarjottimenValintaNakyma} 
     *        kuuluu
     */
    public TestiLatausNakyma(String otsikko, Kayttoliittyma kayttis) {
        super(otsikko, kayttis);
    }

    @Override
    public Node luoAlkuTila() {
        VBox asettelu = new VBox();
        asettelu.setSpacing(10);
        asettelu.setPadding(new Insets(10, 0, 10, 5));
        
        asettelu.getChildren().add(new Label("Syötä tähän tekstitiedoston sijainti ja paina \"Tuo periodit\"."));
        TextField tekstikentta = new TextField("testitarjotin.txt");
        Button nappi = new Button("Tuo periodit");
        
        EventHandler<ActionEvent> kasittelija = (e) -> {
            Consumer<List<PeriodinTunniste>> tuloksenKasittely = (lista) -> {};
            Consumer<Throwable> virheenKasittely = (virhe) -> {
                naytaVirheviesti("Virhe periodien lataamisessa:\n\n" + virhe.getMessage());
                //Seuraavan rivin kommentointi voidaan poistaa testaamisen ajaksi.
                //virhe.printStackTrace();
            };
            getKayttoliittyma().getLogiikka().lataaPeriodienNimet(tuloksenKasittely, virheenKasittely, tekstikentta.getText());
        };
        
        tekstikentta.setOnAction(kasittelija);
        nappi.setOnAction(kasittelija);
        
        asettelu.getChildren().addAll(tekstikentta, nappi);
        return asettelu;
    }

    @Override
    public Consumer<Throwable> tarjottimenLatausVirheenKasittely() {
        return (ex) -> {
                naytaVirheviesti("Virhe kurssitarjottimen lataamisessa:\n\n" + ex.getMessage());
                //Seuraavan rivin kommentointi voidaan poistaa testaamisesn ajaksi.
                ex.printStackTrace();
            };
    }
}
