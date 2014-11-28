/*
 * This file is part of the GeoLatte project.
 *
 *     GeoLatte is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GeoLatte is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with GeoLatte.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010 - 2013 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee  (http://www.qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.geom.curve;

import org.geolatte.geom.*;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsRegistry;
import org.junit.Test;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//import org.geolatte.geom.codec.Wkt;

/**
 * Unit test for {@link MortonCode}
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 2/21/13
 */
public class MortonCodeTest {

    CoordinateReferenceSystem<P2D> crs = CrsRegistry.getProjectedCoordinateReferenceSystemForEPSG(31370);
    CoordinateReferenceSystem<P2D> crs2 = CrsRegistry.getProjectedCoordinateReferenceSystemForEPSG(31300);

    Envelope<P2D> extent = new Envelope<P2D>(new P2D(0.0, 0.0), new P2D(100.0, 100.0), crs);
    MortonContext<P2D> ctxtLvl1 = new MortonContext<P2D>(extent, 1);
    MortonContext<P2D> ctxtLvl2 = new MortonContext<P2D>(extent, 2);
    MortonCode<P2D> mcLevel1 = new MortonCode<P2D>(ctxtLvl1);
    MortonCode<P2D> mcLevel2 = new MortonCode<P2D>(ctxtLvl2);

    @Test
    public void testLevel1() {
        assertEquals("0", mcLevel1.ofPosition(new P2D(0, 0)));
        assertEquals("0", mcLevel1.ofPosition(new P2D(10, 49)));
        assertEquals("1", mcLevel1.ofPosition(new P2D(0, 50)));
        assertEquals("1", mcLevel1.ofPosition(new P2D(49, 50)));
        assertEquals("2", mcLevel1.ofPosition(new P2D(50, 0)));
        assertEquals("2", mcLevel1.ofPosition(new P2D(99, 49)));
        assertEquals("3", mcLevel1.ofPosition(new P2D(50, 50)));
        assertEquals("3", mcLevel1.ofPosition(new P2D(100, 100)));
    }

    @Test
    public void testLevel2() {
        assertEquals("00", mcLevel2.ofPosition(new P2D(0, 0)));
        assertEquals("01", mcLevel2.ofPosition(new P2D(10, 40)));
        assertEquals("02", mcLevel2.ofPosition(new P2D(30, 20)));
        assertEquals("03", mcLevel2.ofPosition(new P2D(45, 45)));

        assertEquals("10", mcLevel2.ofPosition(new P2D(10, 55)));
        assertEquals("11", mcLevel2.ofPosition(new P2D(10, 90)));
        assertEquals("12", mcLevel2.ofPosition(new P2D(49, 55)));
        assertEquals("13", mcLevel2.ofPosition(new P2D(49, 80)));

        assertEquals("20", mcLevel2.ofPosition(new P2D(50, 0)));
        assertEquals("21", mcLevel2.ofPosition(new P2D(74, 30)));
        assertEquals("22", mcLevel2.ofPosition(new P2D(76, 24)));
        assertEquals("23", mcLevel2.ofPosition(new P2D(76, 49)));

        assertEquals("30", mcLevel2.ofPosition(new P2D(50, 50)));
        assertEquals("31", mcLevel2.ofPosition(new P2D(74, 76)));
        assertEquals("32", mcLevel2.ofPosition(new P2D(76, 74)));
        assertEquals("33", mcLevel2.ofPosition(new P2D(76, 79)));

    }

    @Test
    public void testPolygonLevel2() {
        Polygon<P2D> pg = polygon(ring(crs, p(10, 10), p(10, 40), p(40, 40), p(40, 10), p(10, 10)));
        assertEquals("0", mcLevel2.ofGeometry(pg));

        pg = polygon(ring(crs, p(60, 60), p(60, 90), p(90, 90), p(90, 60), p(60, 60)));
        assertEquals("3", mcLevel2.ofGeometry(pg));

        pg = polygon(ring(crs, p(10, 10), p(10, 90), p(90, 90), p(90, 10), p(10, 10)));
        assertTrue(mcLevel2.ofGeometry(pg).isEmpty());

    }

    @Test
    public void testofPointandofGeometryGiveSameResultForPoints() {
        P2D pos = new P2D(0, 0);
        Point<P2D> pnt = new Point<P2D>(pos, crs);
        assertEquals(mcLevel2.ofGeometry(pnt), mcLevel2.ofPosition(pos));

        pos = new P2D(10, 55);
        pnt = new Point<P2D>(pos, crs);
        assertEquals(mcLevel2.ofGeometry(pnt), mcLevel2.ofPosition(pos));

        pos = new P2D(76, 24);
        pnt = new Point<P2D>(pos, crs);
        assertEquals(mcLevel2.ofGeometry(pnt), mcLevel2.ofPosition(pos));

        pos = new P2D(76, 79);
        pnt = new Point<P2D>(pos, crs);
        assertEquals(mcLevel2.ofGeometry(pnt), mcLevel2.ofPosition(pos));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCoordinateWrongCrsIdOfGeometry() {
        CoordinateReferenceSystem<P2D> lFrN = CrsRegistry.getProjectedCoordinateReferenceSystemForEPSG(27562);
        mcLevel1.ofGeometry(point(lFrN, p(1, 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCoordinateWrongCrsIdOfPosition() {
        mcLevel1.ofPosition(new P2DM(1, 1, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCoordinateToRightOfExtentPosition() {
        mcLevel1.ofPosition(new P2D(101, 101));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCoordinateToRightOfExtentGeometry() {
        mcLevel1.ofGeometry(point(crs, p(101, 101)));
    }

    @Test
    public void testMCReturnsMaxLength() {
        assertEquals(1, mcLevel1.getMaxLength());
        assertEquals(2, mcLevel2.getMaxLength());
    }

    @Test
    public void testEnvelopeOfMortonCodeLevel1() {
        assertEquals(new Envelope<P2D>(new P2D(0.0, 0.0), new P2D(100.0, 100.0), crs), mcLevel2.envelopeOf(""));
        assertEquals(new Envelope<P2D>(new P2D(0.0, 0.0), new P2D(50.0, 50.0),crs), mcLevel2.envelopeOf("0"));
        assertEquals(new Envelope<P2D>(new P2D(0.0, 50.0), new P2D(50.0, 100.0), crs), mcLevel2.envelopeOf("1"));
        assertEquals(new Envelope<P2D>(new P2D(50.0, 0.0), new P2D(100.0, 50.0), crs), mcLevel2.envelopeOf("2"));
        assertEquals(new Envelope<P2D>(new P2D(50.0, 50.0), new P2D(100.0, 100.0), crs), mcLevel2.envelopeOf("3"));
    }

    @Test
    public void testEnvelopeOfMortonCodeLevel2() {
        assertEquals(new Envelope<P2D>(new P2D(0.0, 0.0), new P2D(25.0, 25.0), crs), mcLevel2.envelopeOf("00"));
        assertEquals(new Envelope<P2D>(new P2D(25.0, 50.0), new P2D(50.0, 75.0), crs), mcLevel2.envelopeOf("12"));
        assertEquals(new Envelope<P2D>(new P2D(50.0, 25.0), new P2D(75.0, 50.0), crs), mcLevel2.envelopeOf("21"));
        assertEquals(new Envelope<P2D>(new P2D(75.0, 75.0), new P2D(100.0, 100.0), crs), mcLevel2.envelopeOf("33"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnvelopeOfMCOnNullParameter() {
        mcLevel2.envelopeOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnvelopeOfMCOnToLongMortonCode() {
        mcLevel2.envelopeOf("123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnvelopeOfMCInvalidMortonCode() {
        mcLevel2.envelopeOf("42");
    }

    // TODO -- uncomment after codec package is redone.
    //test code for bug
//    @Test
//    public void testMortonCodeShouldNotReturnEmptyString() {
//        String wkt = "SRID=4326;POINT(-87.064293 33.087386)";
//        Point geom = (Point) Wkt.fromWkt(wkt);
//        MortonCode mortonCode = new MortonCode(new MortonContext(new Envelope(-140.0, 15, -40.0, 50.0, CrsId.valueOf(4326)), 8));
//        assertEquals(mortonCode.ofGeometry(geom), mortonCode.ofPosition(geom));
//    }


}
