//   Copyright 2011 Palantir Technologies
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package com.palantir.ptoss.cinch.example.demo;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.core.ModelUpdate;

public class DrawingCanvasModel extends DefaultBindableModel {

    public enum UpdateType implements ModelUpdate {
        DATA;
    }

    public enum Mode {
        POINT, LINE;
    }

    private final List<Line2D> lines = Lists.newArrayList();
    private final List<Point2D> points = Lists.newArrayList();

    private Mode mode = Mode.POINT;
    private boolean allowDrag = true;
    private Point2D cursor;

    public void addLine(Line2D line) {
        lines.add(line);
        modelUpdated(UpdateType.DATA);
    }

    public List<Line2D> getLines() {
        return lines;
    }

    public void addPoint(Point2D point) {
        points.add(point);
        modelUpdated(UpdateType.DATA);
    }

    public List<Point2D> getPoints() {
        return points;
    }

    public void setMode(Mode mode) {
        setCursor(null);
        this.mode = mode;
        update();
    }

    public Mode getMode() {
        return mode;
    }

    public void clear() {
        lines.clear();
        points.clear();
        setCursor(null);
        modelUpdated(UpdateType.DATA);
    }

    public void setAllowDrag(boolean allowDrag) {
        this.allowDrag = allowDrag;
        update();
    }

    public boolean isAllowDrag() {
        return allowDrag;
    }

    public void setCursor(Point2D cursor) {
        this.cursor = cursor;
        update();
    }

    public Point2D getCursor() {
        return cursor;
    }
}
