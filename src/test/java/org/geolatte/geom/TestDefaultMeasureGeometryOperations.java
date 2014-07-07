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
 * Copyright (C) 2010 - 2012 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee  (http://www.qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.geom;


import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.geom.crs.LengthUnit;
import org.junit.Test;

import static org.geolatte.geom.builder.DSL.linestring;
import static org.geolatte.geom.builder.DSL.p;
import static org.geolatte.geom.builder.DSL.point;
import static org.junit.Assert.*;


/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 4/4/12
 */
public class TestDefaultMeasureGeometryOperations {

    private static CoordinateReferenceSystem<P2D> crs = CrsRegistry.getUndefinedProjectedCoordinateReferenceSystem();
    private static CoordinateReferenceSystem<P3D> crsZ = crs.addVerticalAxis(LengthUnit.METER);
    private static CoordinateReferenceSystem<P2DM> crsM = crs.addMeasureAxis(LengthUnit.METER);
    private static CoordinateReferenceSystem<P3DM> crsZM = crsZ.addMeasureAxis(LengthUnit.METER);


    DefaultMeasureGeometryOperations measureOps = new DefaultMeasureGeometryOperations();
    DefaultMeasureGeometryOperations measureOps3D = new DefaultMeasureGeometryOperations();

    MeasuredTestCases tc = new MeasuredTestCases();

    @Test(expected = IllegalArgumentException.class)
    public void testCreateMeasureOnLengthOpThrowsExceptionOnNullParameter() {
        measureOps.measureOnLength(null, P2DM.class, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateMeasureOnLengthOpWithInconsistentTypeMarker() {
        measureOps.measureOnLength(linestring(crs, p(1, 2), p(3, 4)), P3DM.class, false);
    }

    @Test
    public void testCreateMeasureOnLengthOpReturnsParameterOnEmptyGeometry() {
        Geometry geometry = measureOps.measureOnLength(tc.emptyLineString, P2DM.class, false);
        assertEquals("MeasureOnLengthOp returns non-empty geometry on empty geometry.", tc.emptyMeasuredLineString, geometry);
    }

    @Test
    public void testCreateMeasureOnLengthOpOnRegularLineString() {
        assertEquals("MeasureOnLengthOp creates wrong result at point 0:", 0.0, tc.measuredLineString2D.getPositionN(0).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 1:", 1.0, tc.measuredLineString2D.getPositionN(1).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 2:", 2.0, tc.measuredLineString2D.getPositionN(2).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 3:", 3.0, tc.measuredLineString2D.getPositionN(3).getM(), Math.ulp(10));
    }

    @Test
    public void testCreateMeasureOnLengthOpOnRegularMLineString() {
        assertEquals("MeasureOnLengthOp creates wrong result at point 0:", 0.0, tc.measuredMultiLineString2D.getPositionN(0).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 1:", 1.0, tc.measuredMultiLineString2D.getPositionN(1).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 2:", 2.0, tc.measuredMultiLineString2D.getPositionN(2).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 3:", 3.0, tc.measuredMultiLineString2D.getPositionN(3).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 4:", 3.0, tc.measuredMultiLineString2D.getPositionN(4).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 5:", 4.0, tc.measuredMultiLineString2D.getPositionN(5).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 6:", 5.0, tc.measuredMultiLineString2D.getPositionN(6).getM(), Math.ulp(10));
    }

    @Test
    public void testCreateMeasureOnLengthOpOnRegularMLineStringWithKeepInitial() {
        Geometry<P3DM> measured = measureOps.measureOnLength(tc.lineString3DM, P3DM.class, true);
        assertEquals("MeasureOnLengthOp creates wrong result at point 0:", 5.0, measured.getPositionN(0).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 1:", 6.0, measured.getPositionN(1).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 2:", 7.0, measured.getPositionN(2).getM(), Math.ulp(10));
        assertEquals("MeasureOnLengthOp creates wrong result at point 3:", 8.0, measured.getPositionN(3).getM(), Math.ulp(10));
    }

    @Test
    public void testCreateMeasureOnLengthOpPreserversCrsId() {
        assertEquals("MeasureOnLenthOp does not preserve CrsId: ", -1, tc.measuredLineString2D.getSRID());
    }

    @Test
    public void testCreateMeasureOnLengthOpPreserves3D() {
        assertEquals("MeasureOnLenthOp does not preserve 3D status: ",
                tc.lineString2d.getCoordinateReferenceSystem().hasVerticalAxis(),
                tc.measuredLineString2D.getCoordinateReferenceSystem().hasVerticalAxis());
        assertEquals("MeasureOnLenthOp does not preserve 3D status: ",
                tc.lineString3DM.getCoordinateReferenceSystem().hasVerticalAxis(),
                tc.measuredLineString3DM.getCoordinateReferenceSystem().hasVerticalAxis());
    }

    @Test
    public void testCreateMeasureOpPreservesAllNonMvalues() {
        checkXYZcoordinateEquality(tc.lineString2d, tc.measuredLineString2D);
        checkXYZcoordinateEquality(tc.lineString3DM, tc.measuredLineString3DM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateMeasureOnLengthFailsForNonEmptyNonLinealGeometries() {
        Point point = point(crs, p(3, 4));
        measureOps.measureOnLength(point, P2DM.class, false);
    }

    private void checkXYZcoordinateEquality(Geometry<? extends Projected> expected, Geometry<? extends Projected> received) {
        for (int i = 0; i < expected.getNumPositions(); i++) {
            assertEquals("MeasureOnLenthOp does not preserve X-coordinate:", expected.getPositionN(i).getX(), received.getPositionN(i).getX(), Math.ulp(10));
            assertEquals("MeasureOnLenthOp does not preserve Y-coordinate:", expected.getPositionN(i).getY(), received.getPositionN(i).getY(), Math.ulp(10));
            if (expected.getCoordinateReferenceSystem().hasVerticalAxis()) {
                assertEquals("MeasureOnLenthOp does not preserve Z-coordinate:",
                        ((Vertical) expected.getPositionN(i)).getAltitude(),
                        ((Vertical) received.getPositionN(i)).getAltitude(), Math.ulp(10));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateGetMeasureOpThrowsExceptionOnNullGeometry() {
        measureOps.measureAt((Geometry<P2DM>) null, new P2DM(crsM, 1, 2, 0));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateGetMeasureOpThrowsExceptionOnNullPosition() {
        measureOps.measureAt(tc.measuredLineString2D, null);

    }

    @Test
    public void testGetMeasureOpReturnsNaNOnEmptyGeometry() {
        double m = measureOps.measureAt(new LineString<>(crsM), new P2DM(crsM, 1, 2, 0));
        assertTrue(Double.isNaN(m));
    }

    @Test
    public void testGetMeasureOpReturnsNaNWhenPointNotOnMeasuredGeometry() {
        double m = measureOps.measureAt(tc.measuredLineString2D, new P2DM(crsM, 5, 5, 0));
        assertTrue(Double.isNaN(m));
    }

    @Test
    public void testGetMeasureOpOnRegularLine() {
        double m = measureOps.measureAt(tc.measuredLineString2D, new P2DM(crsM, 1.5, 1, 0));
        assertEquals(2.5, m, Math.ulp(10));
    }

    @Test
    public void testGetMeasureOpOnRegularMultiLine() {
        double m = measureOps.measureAt(tc.measuredMultiLineString2D, new P2DM(crsM, 4.5, 1, 0));
        assertEquals(4.5, m, Math.ulp(10));

        m = measureOps.measureAt(tc.measuredMultiLineString2D, new P2DM(crsM, 2.5, 1, 0));
        assertTrue(Double.isNaN(m));

    }

    @Test
    public void testGetMeasureOpOnRegularLinearRing() {
        double m = measureOps.measureAt(tc.measuredLinearRing, new P2DM(crsM, 0, 0.5, 0));
        assertEquals(3.5, m, Math.ulp(10));
    }

    @Test
    public void testGetMeasureOpOnRegularMultiPoint() {
        double m = measureOps.measureAt(tc.measuredMultiPoint, new P2DM(crsM, 1, 2, 0));
        assertEquals(2d, m, Math.ulp(10));
    }

    @Test
    public void testGetMinimumMeasureOpOnRegularMultiLineString() {
        double m = measureOps.minimumMeasure(tc.lineString3DM);
        assertEquals(5d, m, Math.ulp(10));

        m = measureOps.minimumMeasure(tc.caseD1A);
        assertEquals(0d, m, Math.ulp(10));

        m = measureOps.minimumMeasure(tc.emptyMeasuredLineString);
        assertTrue(Double.isNaN(m));

    }

    @Test
    public void testGetMaximumMeasureOpOnRegularMultiLineString() {
        double m = measureOps.maximumMeasure(tc.lineString3DM);
        assertEquals(30d, m, Math.ulp(10));

        m = measureOps.maximumMeasure(tc.caseD1A);
        assertEquals(4d, m, Math.ulp(10));

        m = measureOps.maximumMeasure(tc.emptyMeasuredLineString);
        assertTrue(Double.isNaN(m));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMinimumMeasureOpOnNull() {
        double m = measureOps.minimumMeasure(null);
    }
}
