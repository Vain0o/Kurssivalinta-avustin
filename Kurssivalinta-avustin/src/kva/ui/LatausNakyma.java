/* Kurssivalinta-avustin  – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import kva.logiikka.Kurssitarjotin;
import kva.logiikka.PeriodinTunniste;
import kva.logiikka.Sovelluslogiikka.LatauksenTila;

/**
 *
 * @author Väinö Viinikka
 */
public abstract class LatausNakyma extends Nakyma {
    
    private ScrollPane pohja;
    
    public LatausNakyma(String otsikko, Kayttoliittyma kayttis) {
        super(otsikko, kayttis);
        kayttis.getLogiikka().tilaProperty().addListener((a, vanhaArvo, uusiArvo) -> {
            if(uusiArvo == LatauksenTila.PERIODIEN_NIMET_LADATTU) {
                luoPeriodienValinta(kayttis.getLogiikka().getPeriodienTunnisteet());
            }
        });
    }
    
    public abstract Node luoAlkuTila();
    
    public abstract Consumer<Throwable> tarjottimenLatausVirheenKasittely();

    /**
     *
     * @return
     */
    @Override
    public final Node luoSisalto() {
        pohja = new ScrollPane();
        pohja.setContent(luoAlkuTila());
        pohja.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pohja.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pohja.setMaxWidth(450);
        
        return new StackPane(pohja);
    }
    
    protected void setPohjanSisalto(Node uusiSisalto) {
        pohja.setContent(Objects.requireNonNull(uusiSisalto));
    }
    
    protected void naytaVirheviesti(String teksti) {
        Alert ikkuna = new Alert(Alert.AlertType.NONE, teksti, ButtonType.OK);
        ikkuna.setTitle("Kurssivalinta-avustin");
        ikkuna.showAndWait();
    }
    
    /**Muuttaa näkymän Wilma-palvelimen osoitteen syötöstä ladattavien periodien valintaan.
     * 
     * @param periodit lista {@code PeriodinTunnisteista}, joita kuvaavien periodien 
     *        lataaminen on mahdollista.
     */
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
            
            Consumer<Kurssitarjotin> tuloksenKasittely = (tarjotin) -> {};
            getKayttoliittyma().getLogiikka().lataaKurssitarjotin(mukaanOtettavat, 
                    tuloksenKasittely, tarjottimenLatausVirheenKasittely());
        });
        asettelu.getChildren().add(latausnappi);
        
        pohja.setContent(asettelu);
    }
}
