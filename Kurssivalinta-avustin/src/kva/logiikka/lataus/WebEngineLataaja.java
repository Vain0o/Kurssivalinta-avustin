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
package kva.logiikka.lataus;

import java.util.List;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import kva.logiikka.Moduuli;
import kva.logiikka.PalkinTunniste;
import kva.logiikka.PeriodinTunniste;

/**
 *
 * @author vaino
 */
public class WebEngineLataaja implements KurssitarjottimenLataaja {
    
    private WebEngine moottori;

    @Override
    public void muodostaYhteys(Object... data) throws Exception {
        moottori = (WebEngine) data[0];
        lataaSivu(moottori.getLocation() + "selection/view?");
        if(moottori.getDocument() != null) {
            System.out.println("Jee!");
        }
        
        //https://helsinki.inschool.fi/!02617820/
        //https://helsinki.inschool.fi/!02617820/selection/view?
    }

    @Override
    public List<PeriodinTunniste> haePeriodienNimet() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void valmisteleLataus(List<PeriodinTunniste> periodit) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean onSeuraavaaRyhmaa() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String seuraavanRyhmanKoodi() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PalkinTunniste getNykyinenSijainti() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LuotavaRyhma haeRyhmanTiedot(String ryhmakoodi) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Moduuli haeModuulinTiedot(String kurssikoodi) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private void lataaSivu(String URL) throws Exception {
        moottori.load(URL);
        while(moottori.getLoadWorker().isRunning()) {
            Thread.sleep(50);
        } // seuraavaksi tutkitaan, miksi tämä ei toimi.
        if(moottori.getLoadWorker().getState() != Worker.State.SUCCEEDED) {
            String viesti;
            if(moottori.getLoadWorker().getException() != null) {
                viesti = moottori.getLoadWorker().getException().getMessage();
            } else {
                viesti = "Latauksessa tapahtui virhe.";
            }
            throw new LataajaPoikkeus(viesti);
        }
    }
}
