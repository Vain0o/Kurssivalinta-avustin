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
package kva.ui.tarjotinnakyma;

import java.util.Collection;
import java.util.HashSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import kva.logiikka.Kurssitarjotin;
import kva.logiikka.PalkinTunniste;
import kva.logiikka.Ryhma;
import kva.logiikka.tapahtumat.ValintaKuuntelija;
import kva.ui.Asetukset;

/**
 *
 * @author Väinö Viinikka
 */
public class PalkkiEsitys {
    
    private final Kurssitarjotin tarjotin;
    private final PalkinTunniste palkki;
    private final Node esitys;
    private ValintaNappiLista olennaiset;
    private ValintaNappiLista muualtaValitut;
    private Collection<ValintaNappi> sisalto;
    private final Asetukset asetukset;

    public PalkkiEsitys(Kurssitarjotin tarjotin, PalkinTunniste palkki, Asetukset asetukset) {
        this.tarjotin = tarjotin;
        this.palkki = palkki;
        this.asetukset = asetukset;
        
        BorderPane kehikko = new BorderPane();
        
        Label otsikko = new Label(palkki.getPalkki());
        otsikko.setFont(new Font(15));
        kehikko.setTop(otsikko);
        
        olennaiset = new ValintaNappiLista();
        kehikko.setLeft(olennaiset.getNode());
        
        sisalto = new HashSet<>();
        alustaAsetustenKuuntelu();
        
        
        tarjotin.getKaikkiRyhmat().stream()
                .filter((ryhma) -> ryhma.getSijainnit().contains(palkki))
                .forEach((ryhma) -> lisaaRyhma(ryhma));
        
        esitys = kehikko;
    }

    public PalkinTunniste getPalkki() {
        return palkki;
    }

    public Node getEsitys() {
        return esitys;
    }
    
    private void lisaaRyhma(Ryhma ryhma) {
        ValintaNappi nappi = new ValintaNappi(ryhma);
        sisalto.add(nappi);
        if(!asetukset.pitaisiPiilottaa(ryhma.getModuuli())) {
            olennaiset.lisaaNappi(nappi);
        }
        
        ValintaKuuntelija kuuntelija = new ValintaKuuntelija() {
            
            @Override
            public void valittu(Ryhma ryhma) {
                nappi.setOnValittu(true);
            }

            @Override
            public void valintaPoistettu(Ryhma ryhma) {
                nappi.setOnValittu(false);
            }

            @Override
            public void valittuMuualta(Ryhma ryhma) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void valintaPoistettuMuualta(Ryhma ryhma) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        ryhma.lisaaValintaKuuntelija(kuuntelija);
        
        nappi.setOnMouseClicked((me) -> {
            if(me.getButton() == MouseButton.PRIMARY) {
                if(ryhma.OnValittu()) {
                    ryhma.setOnValittu(false);
                } else {
                    ryhma.setOnValittu(true);
                }
            }
        });
    }
    
    private void alustaAsetustenKuuntelu() {
        //Seuraavaksi yritetään saada tämä toimimaan asetusten mukaan.
        asetukset.piilotetutAineet.addListener((SetChangeListener.Change<? extends String> change) -> {
            if(change.wasAdded()) {
                sisalto.stream()
                        .filter((nappi) -> nappi.getText().contains(change.getElementAdded()))
                        .filter((nappi) -> asetukset.pitaisiPiilottaa(nappi.getRyhma().getModuuli()))
                        .forEach((nappi) -> {
                            olennaiset.poistaNappi(nappi);
                        });
            } else {
                sisalto.stream()
                        .filter((nappi) -> nappi.getText().contains(change.getElementRemoved()))
                        .filter((nappi) -> !asetukset.pitaisiPiilottaa(nappi.getRyhma().getModuuli()))
                        .forEach((nappi) -> {
                            olennaiset.lisaaNappi(nappi);
                        });
            }
        });
        asetukset.epakiinnostavatAineet.addListener((SetChangeListener.Change<? extends String> change) -> {
            if(change.wasAdded()) {
                sisalto.stream()
                        .filter((nappi) -> nappi.getText().contains(change.getElementAdded()))
                        .filter((nappi) -> asetukset.pitaisiPiilottaa(nappi.getRyhma().getModuuli()))
                        .forEach((nappi) -> {
                            olennaiset.poistaNappi(nappi);
                        });
            } else {
                sisalto.stream()
                        .filter((nappi) -> nappi.getText().contains(change.getElementRemoved()))
                        .filter((nappi) -> !asetukset.pitaisiPiilottaa(nappi.getRyhma().getModuuli()))
                        .forEach((nappi) -> {
                            olennaiset.lisaaNappi(nappi);
                        });
            }
        });
        asetukset.piilotetutModuulit.addListener((SetChangeListener.Change<? extends String> change) -> {
            if(change.wasAdded()) {
                sisalto.stream()
                        .filter((nappi) -> nappi.getRyhma().getModuuli().getKoodi().equals(change.getElementAdded()))
                        .forEach((nappi) -> {
                            olennaiset.poistaNappi(nappi);
                        });
            } else {
                sisalto.stream()
                        .filter((nappi) -> nappi.getRyhma().getModuuli().getKoodi().equals(change.getElementRemoved()))
                        .filter((nappi) -> !asetukset.pitaisiPiilottaa(nappi.getRyhma().getModuuli()))
                        .forEach((nappi) -> {
                            olennaiset.lisaaNappi(nappi);
                        });
            }
        });
    }
}
