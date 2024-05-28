package com.valvrare.littlekai.valvraretranslation.utils;

import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.util.Comparator;

/**
 * Created by Kai on 10/5/2016.
 */

public class NovelNameComparator implements Comparator<Novel> {
    @Override
    public int compare(Novel left, Novel right) {

        return left.getNovelName().compareTo(right.getNovelName());
    }
}
