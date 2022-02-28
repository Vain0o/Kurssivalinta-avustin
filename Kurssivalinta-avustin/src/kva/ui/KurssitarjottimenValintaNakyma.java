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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import kva.logiikka.Kurssitarjotin;
import kva.logiikka.PeriodinTunniste;

/**Toteuttaan {@code Nakyman} jossa käyttäjä antaa tarvittavat ohjeet kurssitarjottimen 
 * lataamiselle.
 * <p>
 * Ensiksi käyttäjä antaa tarvittavan Wilma-palvelimen (tai testiversioissa tiedoston) 
 * osoitteen. Tämän jälkeen hän pääsee valitsemaan haluamansa periodit mahdollisten 
 * periodien listalta. Kun käyttäjä tämän jälkeen käskee lataamaan kurssitarjottimen, 
 * {@code KurssitarjottimenValintaNakyma} korvataan {@link kva.ui.KurssitarjotinNakyma}lla, 
 * jossa kurssitarjotin esitetään.
 *
 * @author Väinö Viinikka
 * @see kva.logiikka.lataus.KurssitarjottimenLataaja
 */
public class KurssitarjottimenValintaNakyma extends Nakyma {

    private ScrollPane pohja;
    
    public KurssitarjottimenValintaNakyma(String otsikko, Kayttoliittyma kayttis) {
        super(otsikko, kayttis);
    }

    @Override
    public Node luoSisalto() {
        pohja = new ScrollPane();
        
        VBox asettelu = new VBox();
        asettelu.setSpacing(10);
        asettelu.setPadding(new Insets(10, 0, 10, 5));
        
        asettelu.getChildren().add(new Label("Syötä tähän Wilma-palvelimesi osoite. Kirjaudu sitten Wilmaan selaimessa\nja paina \"Tuo periodit\"."));
        TextField tekstikentta = new TextField("testitarjotin.txt");
        Button nappi = new Button("Tuo periodit");
        
        EventHandler<ActionEvent> kasittelija = (e) -> {
            Consumer<List<PeriodinTunniste>> tuloksenKasittely = (lista) -> luoPeriodienValinta(lista);
            Consumer<Throwable> virheenKasittely = (virhe) -> virhe.printStackTrace();
            super.getKayttoliittyma().getLogiikka().lataaPeriodienNimet(tekstikentta.getText(), tuloksenKasittely, virheenKasittely);
        };
        
        tekstikentta.setOnAction(kasittelija);
        nappi.setOnAction(kasittelija);
        
        asettelu.getChildren().addAll(tekstikentta, nappi);
        
        pohja.setContent(asettelu);
        pohja.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pohja.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pohja.setMaxWidth(450);
        
        return new StackPane(pohja);
    }
    
    private void luoPeriodienValinta(List<PeriodinTunniste> periodit) {
        HashMap<CheckBox, PeriodinTunniste> tunnisteenLoytaja = new HashMap<>();
        VBox asettelu = new VBox();
        asettelu.setSpacing(10);
        asettelu.setPadding(new Insets(10, 0, 10, 5));
        asettelu.getChildren().add(new Label("Valitse haluamasi periodit ja paina " + 
                "sitten \"Lataa\"."));
        
        String oppilaitos = null;
        for(PeriodinTunniste tunniste : periodit) {
            if(!tunniste.getOppilaitos().equals(oppilaitos)) {
                asettelu.getChildren().add(new Separator());
                asettelu.getChildren().add(new Label(tunniste.getOppilaitos()));
                oppilaitos = tunniste.getOppilaitos();
            }
            CheckBox nappi = new CheckBox(tunniste.getPeriodi());
            nappi.setAllowIndeterminate(false);
            tunnisteenLoytaja.put(nappi, tunniste);
            asettelu.getChildren().add(nappi);
        }
        
        Button latausnappi = new Button("Lataa");
        latausnappi.setOnAction((ev) -> {
            HashSet<PeriodinTunniste> mukaanOtettavat = tunnisteenLoytaja.keySet().stream()
                    .filter((cb) -> cb.isSelected())
                    .map((cb) -> tunnisteenLoytaja.get(cb))
                    .collect(Collectors.toCollection(() -> new HashSet<>()));
            super.getKayttoliittyma().lataaKurssitarjotin(mukaanOtettavat);
        });
        asettelu.getChildren().add(latausnappi);
        
        pohja.setContent(asettelu);
    }
}
