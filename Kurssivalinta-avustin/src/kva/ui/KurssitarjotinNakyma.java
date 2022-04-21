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

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import kva.logiikka.Kurssitarjotin;
import kva.logiikka.PalkinTunniste;
import kva.ui.tarjotinnakyma.PalkkiEsitys;

/**Toteuttaa {@code Nakyman}, jossa käyttäjä valitsee kurssit.
 * <p>
 * Näkymä sisältää {@link kva.logiikka.Kurssitarjotin}-olion mahdollisia palkkeja 
 * kuvaavat {@link kva.ui.tarjotinnakyma.PalkkiEsitys}-oliot {@code Kurssitarjottimen} 
 * {@code getMahdollisetPalkit()}-metodin kuvaamassa järjestyksessä. Ne puolestaan 
 * sisältävät yksittäisiä {@link kva.logiikka.Ryhma}-olioita kuvaavat {@link kva.ui.tarjotinnakyma.ValintaNappi}-napit.
 * Lisäksi näkymä sisältää oppilaitoksen ja periodin vaihtumista kuvaavat otsikot.
 *
 * @author Väinö Viinikka
 */
public class KurssitarjotinNakyma extends Nakyma {

    private final Kurssitarjotin tarjotin;
    
    /**Luo uuden kurssivalintanäkymän.
     * 
     * @param otsikko {@code Nakyman} kuvastaman välilehden otsikko
     * @param kayttis {@code Kayttoliittyma}, johon {@code Nakyma} kuuluu
     * @param tarjotin {@code Kurssitarjotin}, jonka {@code KurssivalintaNakyma} esittää
     */
    public KurssitarjotinNakyma(String otsikko, Kayttoliittyma kayttis, Kurssitarjotin tarjotin) {
        super(otsikko, kayttis);
        this.tarjotin = tarjotin;
    }

    @Override
    public Node luoSisalto() {
        BorderPane ylin = new BorderPane();
        VBox kehikko = new VBox();
        kehikko.setPadding(new Insets(10, 0, 10, 5));
        kehikko.setSpacing(10);
        
        String oppilaitos = null;
        String periodi = null;
        for(PalkinTunniste palkki : tarjotin.getMahdollisetPalkit()) {
            if(!palkki.getOppilaitos().equals(oppilaitos)) {
                oppilaitos = palkki.getOppilaitos();
                Label oppilaitosOtsikko = new Label(oppilaitos);
                oppilaitosOtsikko.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 20));
                kehikko.getChildren().add(oppilaitosOtsikko);
            }
            if(!palkki.getPeriodi().equals(periodi)) {
                periodi = palkki.getPeriodi();
                Label periodiOtsikko = new Label(periodi);
                periodiOtsikko.setFont(Font.font("System", FontWeight.BOLD, 15));
                kehikko.getChildren().add(periodiOtsikko);
            }
            
            PalkkiEsitys esitys = new PalkkiEsitys(tarjotin, palkki, getKayttoliittyma().getAsetukset());
            kehikko.getChildren().add(esitys.getEsitys());
        }
        
        
        ScrollPane pohja = new ScrollPane();
        pohja.setContent(kehikko);
        pohja.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pohja.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        ylin.setCenter(pohja);
        return ylin;
    }
}
