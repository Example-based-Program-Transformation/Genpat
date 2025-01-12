/**
 * Copyright (C) SEI, PKU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiajun.jiang@pku.edu.cn>.
 */

package mfix.core.locator;

import mfix.common.conf.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Jiajun
 * @date: 2018/9/25
 */
public class Location {

    private final String _relClazz;
    private final String _innerClazz;
    private final String _method;
    private final int _line;
    private final double _suspicious;
    private final List<Integer> _consideredLines = new ArrayList<>();

    public Location(String relClazz, int line, double suspicious) {
        this(relClazz, null, null, line, suspicious);
    }

    public Location(String relClazz, String innerClazz, String method, int line, double suspicious) {
        _relClazz = relClazz;
        _line = line;
        _suspicious = suspicious;
        _innerClazz = innerClazz;
        _method = method;
    }

    public String getRelClazz() {
        return _relClazz;
    }

    public String getRelClazzFile() {
        return getRelClazz().replace('.', Constant.SEP) + ".java";
    }

    public String getInnerClazz() {
        return _innerClazz;
    }

    public String getFaultyMethodName() {
        return _method;
    }

    public int getLine() {
        return _line;
    }

    public List<Integer> getConsideredLines() {
        return _consideredLines;
    }

    public void addConsideredLine(Integer line) {
        _consideredLines.add(line);
    }

    public void addConsideredLines(List<Integer> lines) {
        _consideredLines.addAll(lines);
    }

    public double getSuspicious() {
        return _suspicious;
    }

    @Override
    public String toString() {
        return _relClazz + (_innerClazz == null ? "" : "$" + _innerClazz) + (_method == null ? "" : "." + _method) +
                ":" + getConsideredLines() + "#" + _suspicious;
    }
}
