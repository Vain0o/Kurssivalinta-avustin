/* Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
 * Copyright (C) 2021 Väinö viinikka
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import kva.logiikka.Moduuli;
import kva.logiikka.RyhmanSijainti;

/**{@code KurssitarjottimenLataaja}, joka lataa tiedot tekstitiedostosta.
 * <p>
 * Luokka hakee kurssitarjottimen tiedot tekstitiedostosta, jonka sijainti annetaan 
 * metodin {@link #muodostaYhteys(java.lang.String)} parametrina. Luokka on tarkoitettu 
 * ohjelman testaamiseen silloin, kun tietoja ei haluta tai voida hakea Wilmasta. 
 * Lopullinen Kurssivalinta-avustimen julkaisu ei sisällä viitteitä tähän luokkaan, 
 * vaan siinä käytetään toista {@code KurssitarjottimenLataajaa}, joka hakee tiedot 
 * suoraan Wilmasta.
 * 
 * @author Väinö Viinikka
 * @see kva.logiikka.lataus.KurssitarjottimenLataaja
 */
public class TestiLataaja implements KurssitarjottimenLataaja {
    
    private Scanner lukija;
    private String URL;

    @Override
    public void muodostaYhteys(String URL) throws Exception {
        Scanner uusi = new Scanner(new FileInputStream(URL));
        if((!uusi.hasNext()) || (!uusi.nextLine().equals("PERIODIEN_TUNNISTEET"))) {
            throw new IOException("Tiedosto \"" + URL + "\" ei noudata syntaksia.");
        }
        
        lukija = uusi;
        this.URL = URL;
    }

    @Override
    public List<PeriodinTunniste> haePeriodienNimet() throws Exception {
        ArrayList<PeriodinTunniste> tunnisteet = new ArrayList<>();
        String oppilaitos = "";
        
        while(true) {
            if(!lukija.hasNext()) {
                throw new IOException("Tiedosto \"" + URL + "\" ei noudata syntaksia.");
            }
            String syote = lukija.nextLine();
            if(syote.equals("MODUULIEN_TIEDOT")) {
                break;
            } else if(syote.startsWith("#")) {
                oppilaitos = syote.substring(1);
            } else {
                tunnisteet.add(new PeriodinTunniste(oppilaitos, syote));
            }
        }
        
        return tunnisteet;
    }

    @Override
    public void valmisteleLataus(List<PeriodinTunniste> periodit) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onSeuraavaaRyhmaa() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String seuraavanRyhmanKoodi() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RyhmanSijainti getNykyinenSijainti() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LuotavaRyhma haeRyhmanTiedot(String ryhmakoodi) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Moduuli haeModuulinTiedot(String kurssikoodi) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
