package com.ficuno.creature;

import com.badlogic.gdx.math.Polygon;

import java.util.ArrayList;
import java.util.List;

public class TouchRegion {
    Main main;
    List<Polygon> cardTouchRegionPoly = new ArrayList<>();
    List<Polygon> uiTouchRegionPoly = new ArrayList<>();
    public TouchRegion(Main main){
        this.main = main;

        for (int x = 0; x < 6; x++){
            cardTouchRegionPoly.add(new Polygon(new float[]{0, 0, 112, 0, 112, 192, 0, 192}));
        }

        for (int x = 0; x < 4; x++){
            uiTouchRegionPoly.add(new Polygon(new float[]{0, 0, 96, 0, 96, 96, 0, 96}));

        }
    }
}