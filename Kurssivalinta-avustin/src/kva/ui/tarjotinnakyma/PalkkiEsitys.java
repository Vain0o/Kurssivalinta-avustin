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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
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
        muualtaValitut = new ValintaNappiLista();
        kehikko.setRight(muualtaValitut.getNode());
        
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
            lisaaNakyviin(nappi);
        }
        
        ValintaKuuntelija kuuntelija = new ValintaKuuntelija() {
            
            @Override
            public void valittu(Ryhma ryhma) {
                nappi.setOnValittu(true);
            }

            @Override
            public void valintaPoistettu(Ryhma ryhma) {
                nappi.setOnValittu(false);
                if(asetukset.pitaisiPiilottaa(ryhma.getModuuli())) {
                    poistaNakyvista(nappi);
                }
            }

            @Override
            public void valittuMuualta(Ryhma ryhma) {
                if(!asetukset.pitaisiPiilottaa(ryhma.getModuuli())) {
                    olennaiset.poistaNappi(nappi);
                    muualtaValitut.lisaaNappi(nappi);
                }
            }

            @Override
            public void valintaPoistettuMuualta(Ryhma ryhma) {
                if(!asetukset.pitaisiPiilottaa(ryhma.getModuuli())) {
                    muualtaValitut.poistaNappi(nappi);
                    olennaiset.lisaaNappi(nappi);
                }
            }
        };
        ryhma.lisaaValintaKuuntelija(kuuntelija);
        
        nappi.setOnMouseClicked((me) -> {
            if(me.getButton() == MouseButton.PRIMARY) {
                if(ryhma.OnValittu()) {
                    ryhma.setOnValittu(false);
                } else if(!onkoValinnalleEstetta(ryhma)) {
                    ryhma.setOnValittu(true);
                }
            }
        });
    }
    
    private void alustaAsetustenKuuntelu() {
        SetChangeListener<String> kuuntelija = (muutos) -> {
            if(muutos.wasAdded()) {
                sisalto.stream()
                        .filter((nappi) -> nappi.getText().contains(muutos.getElementAdded()))
                        .filter((nappi) -> asetukset.pitaisiPiilottaa(nappi.getRyhma().getModuuli()))
                        .filter((nappi) -> !nappi.getRyhma().OnValittu())
                        .forEach((nappi) -> {
                            poistaNakyvista(nappi);
                        });
            } else {
                sisalto.stream()
                        .filter((nappi) -> nappi.getText().contains(muutos.getElementRemoved()))
                        .filter((nappi) -> !asetukset.pitaisiPiilottaa(nappi.getRyhma().getModuuli()))
                        .forEach((nappi) -> {
                            lisaaNakyviin(nappi);
                        });
            }
        };
        asetukset.piilotetutAineet.addListener(kuuntelija);
        asetukset.epakiinnostavatAineet.addListener(kuuntelija);
        
        asetukset.piilotetutModuulit.addListener((SetChangeListener.Change<? extends String> change) -> {
            if(change.wasAdded()) {
                sisalto.stream()
                        .filter((nappi) -> nappi.getRyhma().getModuuli().getKoodi().equals(change.getElementAdded()))
                        .forEach((nappi) -> {
                            poistaNakyvista(nappi);
                        });
            } else {
                sisalto.stream()
                        .filter((nappi) -> nappi.getRyhma().getModuuli().getKoodi().equals(change.getElementRemoved()))
                        .filter((nappi) -> !asetukset.pitaisiPiilottaa(nappi.getRyhma().getModuuli()))
                        .forEach((nappi) -> {
                            lisaaNakyviin(nappi);
                        });
            }
        });
    }
    
    private boolean onkoValinnalleEstetta(Ryhma tarkistettava) {
        ArrayList<Ryhma> paallekkaiset = tarkistettava.getTarjotin().getValitutRyhmat().stream()
                .filter((ryhma) -> {
                    return ryhma.getSijainnit().stream()
                            .anyMatch((sijainti) -> tarkistettava.getSijainnit().contains(sijainti));
                })
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
        if(!paallekkaiset.isEmpty()) {
            StringBuilder viesti = new StringBuilder("Valitsemasi ryhmä ")
                    .append(tarkistettava.getKoodi())
                    .append(" on päällekkäin ");
            if(paallekkaiset.size() == 1) {
                viesti.append("ryhmän ")
                        .append(paallekkaiset.get(0).getKoodi())
                        .append(" kanssa. Poistetaanko päällekkäisen ryhmän valinta?");
            } else {
                viesti.append(" seuraavien ryhmien kanssa:\n\n");
                paallekkaiset.stream()
                        .map((ryhma) -> ryhma.getKoodi())
                        .forEach((koodi) -> viesti.append(koodi).append("\n"));
                viesti.append("\nPoistetaanko päällekkäisten ryhmien valinnat?");
            }
            if(!kysyKayttajalta(viesti.toString())) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean kysyKayttajalta(String kysymys) {
        ButtonType kylla = new ButtonType("Kyllä", ButtonData.YES);
        ButtonType ei = new ButtonType("Ei", ButtonData.NO);
        
        Alert ikkuna = new Alert(AlertType.NONE, kysymys, kylla, ei);
        ikkuna.setTitle("Kurssivalinta-avustin");
        
        Optional<ButtonType> tulos = ikkuna.showAndWait();
        return tulos.isPresent() && tulos.get().equals(kylla);
    }
    
    private void lisaaNakyviin(ValintaNappi nappi) {
        if(!nappi.getRyhma().onValittuMuualta()) {
            olennaiset.lisaaNappi(nappi);
        } else {
            muualtaValitut.lisaaNappi(nappi);
        }
    }
    
    private void poistaNakyvista(ValintaNappi nappi) {
        olennaiset.poistaNappi(nappi);
        muualtaValitut.poistaNappi(nappi);
    }
}
