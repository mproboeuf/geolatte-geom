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
 * Copyright (C) 2010 - 2011 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee  (http://www.qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.geom;

import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.CrsRegistry;
import org.junit.Test;

import static org.geolatte.geom.PositionSequenceBuilders.variableSized;
import static org.geolatte.geom.builder.DSL.p;
import static org.geolatte.geom.builder.DSL.point;
import static org.junit.Assert.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 12/9/11
 */
public class PolygonTest {

    private static CoordinateReferenceSystem<P2D> crs = CoordinateReferenceSystems.PROJECTED_2D_METER;


    PositionSequence<P2D> shellPoints = variableSized(P2D.class).add(0, 0).add(10, 0).add(10, 10).add( 0, 10).add( 0, 0).toPositionSequence();
    PositionSequence<P2D> innerPoints = variableSized(P2D.class).add(1, 1).add( 9, 1).add(9, 9).add(1, 9).add( 1, 1).toPositionSequence();

    PositionSequence<P2D> shellPoints2 = variableSized(P2D.class).add(0, 0).add(10, 0).add(10, 10).add(0, 10).add(0, 0).toPositionSequence();
    PositionSequence<P2D> innerPoints2 = variableSized(P2D.class).add(1, 1).add( 9, 1).add( 9, 9).add( 1, 9).add( 1, 1).toPositionSequence();

    PositionSequence<P2D> shellPoints3 = variableSized(P2D.class).add(1, 1).add(10, 0).add(10, 10).add(0, 10).add(1, 1).toPositionSequence();


    @Test
    public void testEmptyRingsThrowIllegalArgumentException(){
        try {
            LinearRing<P2D> shell = new LinearRing<P2D>(crs);
            new Polygon<P2D>(new LinearRing[]{shell});
            fail("Polygon with empty shell should throw IllegalArgumentException.");
        } catch(IllegalArgumentException e){}

        try {
            LinearRing<P2D> shell = new LinearRing<P2D>(shellPoints, crs);
            LinearRing<P2D> emptyInner = new LinearRing<P2D>(crs);
            new Polygon<P2D>(new LinearRing[]{shell, emptyInner});
            fail("Polygon with empty inner ring should throw IllegalArgumentException.");
        } catch(IllegalArgumentException e){}
    }

    @Test
    public void testPolygonEquality() {
        LinearRing<P2D> shell = new LinearRing<P2D>(shellPoints, crs);
        LinearRing<P2D> inner = new LinearRing<P2D>(innerPoints, crs);
        Polygon<P2D> polygon1 = new Polygon<P2D>(new LinearRing[]{shell, inner});

        shell = new LinearRing<P2D>(shellPoints2, crs);
        inner = new LinearRing<P2D>(innerPoints2, crs);
        Polygon<P2D> polygon2 = new Polygon<P2D>(new LinearRing[]{shell, inner});

        shell = new LinearRing(shellPoints3, crs);
        Polygon<P2D> polygon3 = new Polygon<P2D>(new LinearRing[]{shell, inner});


        assertTrue(polygon1.equals(polygon1));
        assertTrue(new GeometryPointEquality().equals(null, null));
        assertFalse(polygon1.equals(null));

        assertTrue(polygon1.equals(polygon2));
        assertTrue(polygon2.equals(polygon1));

        assertFalse(polygon1.equals(polygon3));
        assertFalse(polygon3.equals(polygon1));

        assertFalse(polygon1.equals(point(crs, p(1, 2))));

    }


}
