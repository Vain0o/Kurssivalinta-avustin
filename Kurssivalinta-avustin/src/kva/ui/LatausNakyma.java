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

/**Yläluokka {@code Nakymille}, jotka vastaavat {@code Kurssitarjottimen} tietojen 
 * lataamisesta.
 * <p>
 * Näkymän toiminnan alkuvaiheessa alaluokka vastaa metodin 
 * {@link kva.logiikka.Sovelluslogiikka#lataaPeriodienNimet(java.util.function.Consumer, java.util.function.Consumer, java.lang.Object...)} 
 * kutsumisesta asiaankuuluvilla parametreillä, jotka kysytään tarpeen mukaan käyttäjältä. 
 * Kun {@code Sovelluslogiikka} on ladannut periodien nimet, {@code LatausNakyma} 
 * ottaa asiasta automaattisesti kopin, ja luo näkymän, jossa käyttäjä valitsee haluamansa 
 * periodit, ja käskee kurssitarjottimen latauksen.
 * <p>
 * Tämän toiminnallisuuden lisäksi luokka tarjoaa alaluokkien käyttöön metodit {@link #naytaVirheviesti(java.lang.String)} 
 * ja {@link #palaaAlkuun()}.
 * <p>
 * Alaluokkia kannattaa kirjoittaa omansa joka {@link kva.logiikka.lataus.KurssitarjottimenLataaja}lle.
 *
 * @author Väinö Viinikka
 */
public abstract class LatausNakyma extends Nakyma {
    
    private ScrollPane pohja;
    
    /**Luo uuden {@code LatausNakyman}, jonka {@code Tabillä} on annettu otsikko ja 
     * joka käyttää annettua {@code Kurssitarjotinta}.
     * <p>
     * {@code LatausNakymat} tulee muiden {@code Nakymien} tapaan luoda luokassa {@link kva.ui.Kayttoliittyma} 
     * ja parametriksi annetaan {@code Nakyman} luonut {@code Kayttoliittyma}.
     * 
     * @param otsikko {@code LatausNakyman} kuvastaman välilehden otsikko
     * @param kayttis {@code Kayttoliittyma}, johon {@code LatausNakyma} kuuluu 
     */
    public LatausNakyma(String otsikko, Kayttoliittyma kayttis) {
        super(otsikko, kayttis);
        kayttis.getLogiikka().tilaProperty().addListener((a, vanhaArvo, uusiArvo) -> {
            if(uusiArvo == LatauksenTila.PERIODIEN_NIMET_LADATTU) {
                luoPeriodienValinta(kayttis.getLogiikka().getPeriodienTunnisteet());
            }
        });
    }
    
    /**Luo käyttöliittymän {@code KurssitarjottimenLataajan} vaatimien tietojen kyselylle 
     * ja {@code PeriodinTunnisteiden} lataamiselle.
     * <p>
     * {@code LatausNakyma} luo näkymään valmiiksi {@code ScrollPanen}, jonka sisälle 
     * palautettu {@code Node} asetetaan.
     * 
     * @return sisältää luodut käyttöliittymäkomponentit
     */
    public abstract Node luoAlkuTila();
    
    /**Palauttaa {@code Consumer}-olion {@code Kurssitarjottimen} latauksessa mahdollisesti 
     * syntyvien virheiden käsittelyyn.
     * 
     * @return {@code Consumer}, jolle annetaan {@code Kurssitarjottimen} latauksessa 
     *         mahdollisesti syntyvät poikkeukset
     */
    public abstract Consumer<Throwable> tarjottimenLatausVirheenKasittely();

    /**{@inheritDoc}*/
    @Override
    public final Node luoSisalto() {
        pohja = new ScrollPane();
        pohja.setContent(luoAlkuTila());
        pohja.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pohja.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pohja.setMaxWidth(450);
        
        return new StackPane(pohja);
    }
    
    /**Korvaa {@code LatausNakyman} pohjana toimivan {@code ScrollPanen} sisällön 
     * uudella.
     * 
     * @param uusiSisalto {@code ScrollPanen} uusi sisältö
     * @throws java.lang.NullPointerException jos {@code uusiSisalto} on {@code null}
     */
    protected void setPohjanSisalto(Node uusiSisalto) {
        pohja.setContent(Objects.requireNonNull(uusiSisalto));
    }
    
    /**Esittää annetun virheviestin erillisessä ikkunassa.
     * 
     * @param teksti virheviestin teksti
     */
    protected void naytaVirheviesti(String teksti) {
        Alert ikkuna = new Alert(Alert.AlertType.NONE, teksti, ButtonType.OK);
        ikkuna.setTitle("Kurssivalinta-avustin");
        ikkuna.showAndWait();
    }
    
    /**Palauttaa {@code LatausNakyman} alkuperäiseen tilaansa.
     * <p>
     * Metodia voi kutsua esimerkiksi, jos lataus on virheen takia aloitettava alkusta.
     * Paluu toteutetaan kutsumalla uudelleen metodia {@link #luoAlkuTila()}.
     */
    protected final void palaaAlkuun() {
        setPohjanSisalto(luoAlkuTila());
    }
    
    /**Muuttaa näkymän Wilma-palvelimen osoitteen syötöstä ladattavien periodien valintaan.
     * 
     * @param periodit lista {@code PeriodinTunnisteista}, joita kuvaavien periodien 
     *        lataaminen on mahdollista
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
