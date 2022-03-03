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
import java.util.Comparator;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

/**Hallinnoi {@code FlowPanea}, johon voi lisätä {@code ValintaNappeja}.
 * <p>
 * {@link javafx.scene.layout.FlowPane}n saa {@code Node}-muodossa käyttöön metodilla 
 * {@link #getNode()}. Luokka pitää {@code FlowPaneen} lisätyt komponentit järjestyksessä, 
 * mutta tämä edellyttää, että elementtien lisäys hoidetaan yksinomaan metodin {@link #lisaaNappi(kva.ui.tarjotinnakyma.ValintaNappi)} 
 * kautta. Elementtien lisäämistä kutsulla 
 * <p>
 * {@code ((FlowPane) valintaNappiLista.getNode()).getChildren().add(el)}
 * <p>
 * EI PIDÄ YRITTÄÄ MISSÄÄN NIMESSÄ, SILLÄ SE TULEE AIHEUTTAMAAN VIRHEITÄ.
 *
 * @author Väinö Viinikka
 */
public class ValintaNappiLista {
    
    private FlowPane lista;
    
    public ValintaNappiLista() {
        lista = new FlowPane();
        lista.setHgap(5);
        lista.setVgap(5);
        lista.setPrefWidth(300);
    }
    
    public ValintaNappiLista(Collection<ValintaNappi> jasenet) {
        this();
        ArrayList<ValintaNappi> lisays = new ArrayList<>(jasenet);
        lisays.sort(Comparator.naturalOrder());
        lista.getChildren().addAll(lisays);
    }

    public void lisaaNappi(ValintaNappi nappi) {
        if(lista.getChildren().contains(nappi)) {
            return;
        }
        
        int pieninIndeksi = 0;
        int suurinIndeksi = lista.getChildren().size();
        while(pieninIndeksi < suurinIndeksi) {
            int tarkastusIndeksi = (pieninIndeksi + suurinIndeksi) / 2;
            ValintaNappi tarkastus = (ValintaNappi) lista.getChildren().get(tarkastusIndeksi);
            int vertailu = tarkastus.compareTo(nappi);
            if(vertailu < 0) {
                pieninIndeksi = tarkastusIndeksi + 1;
            } else if(vertailu > 0) {
                suurinIndeksi = tarkastusIndeksi;
            } else {
                pieninIndeksi = tarkastusIndeksi + 1;
                suurinIndeksi = tarkastusIndeksi + 1;
            }
        }
        lista.getChildren().add(pieninIndeksi, nappi);
    }
    
    public void poistaNappi(ValintaNappi nappi) {
        lista.getChildren().remove(nappi);
    }
    
    public Node getNode() {
        return lista;
    }
}
