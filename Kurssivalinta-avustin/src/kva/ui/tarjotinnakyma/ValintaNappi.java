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

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import kva.logiikka.Ryhma;

/**
 *
 * @author Väinö Viinikka
 */
public class ValintaNappi extends Button implements Comparable<ValintaNappi> {
    
    private final Ryhma ryhma;

    public ValintaNappi(Ryhma ryhma) {
        super(ryhma.getKoodi());
        this.ryhma = ryhma;
        
        switch(ryhma.getModuuli().getTyyppi()) {
            case PAKOLLINEN:
                setStyle("-fx-background-color: greenyellow; ");
                break;
            case VALTAKUNNALLINEN_SYVENTAVA:
                setStyle("-fx-background-color: salmon; ");
                break;
            case KOULUKOHTAINEN_SYVENTAVA:
                setStyle("-fx-background-color: moccasin; ");
                break;
            case SOVELTAVA:
                setStyle("-fx-background-color: white; ");
                break;
        }
    }

    public void setOnValittu(boolean onValittu) {
        if(onValittu) {
            super.setFont(Font.font("System", FontWeight.BOLD, 12));
        } else {
            super.setFont(Font.font("System", FontWeight.NORMAL, 12));
        }
    }
    
    public Ryhma getRyhma() {
        return ryhma;
    }

    @Override
    public int compareTo(ValintaNappi t) {
        return getText().compareTo(t.getText());
    }
}
