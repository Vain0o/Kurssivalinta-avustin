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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import kva.logiikka.PeriodinTunniste;

/**Toteuttaa {@code Nakyman}, jossa {@code Kurssitarjotin} ladataan {@code WebEngineLataajan} 
 * avulla.
 * <p>
 * Näkymässä käyttäjä pääsee aluksi syöttämään Wilma-palvelimensa osoitteen. Tämän 
 * jälkeen avautuu {@link javafx.scene.web.WebView}, jossa käyttäjä kirjautuu Wilmaan
 * tunnuksillaan ja sen jälkeen käskee napinpainalluksella {@code PeriodinTunnisteiden} 
 * latauksen. {@code WebView}'n {@link javafx.scene.web.WebEngine} annetaan 
 * {@link kva.logiikka.lataus.WebEngineLataaja}lle lataamista varten.
 * <p>
 * Kun {@code PeriodinTunnisteet} on ladattu, {@code LatausNakyma} huolehtii niiden 
 * valinnasta.
 *
 * @author Väinö Viinikka
 * @since Kurssivalinta-avustin 1.0
 */
public class WebEngineLatausNakyma extends LatausNakyma {
    
    private ScrollPane pohja;
    
    /**Luo uuden WebEngineLatausNakyman.
     * <p>
     * {@code WebEngineLatausNakyma} tulee tavalliseen luoda luokassa {@link kva.ui.Kayttoliittyma} 
     * ja parametriksi annetaan {@code Nakyman} luonut {@code Kayttoliittyma}.
     * 
     * @param otsikko {@code Nakyman} kuvastaman välilehden otsikko
     * @param kayttis {@code Kayttoliittyma}, johon {@code Nakyma} kuuluu
     */
    public WebEngineLatausNakyma(String otsikko, Kayttoliittyma kayttis) {
        super(otsikko, kayttis);
        pohja = new ScrollPane();
    }

    @Override
    public Node luoAlkuTila() {
        VBox alkuAsettelu = new VBox();
        alkuAsettelu.setSpacing(10);
        alkuAsettelu.setPadding(new Insets(10, 0, 10, 5));
        
        alkuAsettelu.getChildren().add(new Label("Syötä tähän Wilma-palvelimesi osoite, ja paina sitten \"Seuraava\"."));
        TextField tekstikentta = new TextField();
        HBox osoiteKentta = new HBox();
        osoiteKentta.getChildren().addAll(new Label("https://"), tekstikentta);
        alkuAsettelu.getChildren().add(osoiteKentta);
        
        Button nappi = new Button("Seuraava");
        EventHandler<ActionEvent> kasittelija = (e) -> luoKirjautumisNakyma(tekstikentta.getText());
        tekstikentta.setOnAction(kasittelija);
        nappi.setOnAction(kasittelija);
        alkuAsettelu.getChildren().add(nappi);
        
        return alkuAsettelu;
    }

    @Override
    public Consumer<Throwable> tarjottimenLatausVirheenKasittely() {
        return (ex) -> {
                naytaVirheviesti("Virhe kurssitarjottimen lataamisessa:\n\n" + ex.getMessage());
                palaaAlkuun();
                //Seuraavan rivin kommentointi voidaan poistaa testaamisesn ajaksi.
                //ex.printStackTrace();
            };
    }
    
    /**Luo käyttöliittymän, jossa käyttäjä kirjautuu Wilmaan.
     * 
     * @param osoite Wilma-palvelimen osoite
     */
    private void luoKirjautumisNakyma(String osoite) {
        VBox asettelu = new VBox();
        asettelu.setSpacing(10);
        asettelu.setPadding(new Insets(10, 0, 10, 5));
        asettelu.getChildren().add(new Label("Kirjaudu Wilma-tunnuksillasi ja paina \"Lataa periodit\"."));
        
        WebView selain = new WebView();
        selain.setMaxSize(430, 480);
        WebEngine moottori = selain.getEngine();
        moottori.load("https://" + osoite);
        asettelu.getChildren().add(selain);
        
        Button takaisinNappi = new Button("Takaisin");
        takaisinNappi.setOnAction((ev) -> palaaAlkuun());
        Button latausNappi = new Button("Lataa periodit");
        latausNappi.setOnAction((e) -> {
            Consumer<List<PeriodinTunniste>> tuloksenKasittely = (lista) -> {};
            Consumer<Throwable> virheenKasittely = (virhe) -> {
                naytaVirheviesti("Virhe periodien lataamisessa:\n\n" + virhe.getMessage());
                palaaAlkuun();
                //Seuraavan rivin kommentointi voidaan poistaa testaamisen ajaksi.
                //virhe.printStackTrace();
            };
            getKayttoliittyma().getLogiikka().lataaPeriodienNimet(tuloksenKasittely, 
                    virheenKasittely, moottori);
        });
        
        HBox nappilista = new HBox();
        nappilista.setSpacing(10);
        nappilista.getChildren().addAll(takaisinNappi, latausNappi);
        asettelu.getChildren().add(nappilista);
        
        setPohjanSisalto(asettelu);
    }
}
