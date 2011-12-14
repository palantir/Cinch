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

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.core.ModelUpdate;


public class DemoModel extends DefaultBindableModel {

    public enum UpdateType implements ModelUpdate {
        OTHER, LIST, COMBO, MULTILIST;
    }

    public enum DemoEnum {
        FOO, BAR, BAZ;
    }

    private boolean demoBoolean;
    private boolean demoRadioBoolean;
    private DemoEnum demoEnum;
    private String demoString = "";
    private List<String> demoList = ImmutableList.<String>of("Quick", "Quickly", "Quickest", "Brown", "Fox");
    private List<String> demoFilteredList = demoList;
    private Object selectedItem;
    private String filterText;
    private String selectedComboItem;
    private Object selectedPrefItem;

    private List<String> demoMultiList = ImmutableList.<String>of("alpha", "bravo", "charlie", "delta", "echo");
    private List<String> multiSelectedItems = ImmutableList.of();

    private int sliderValue = 0;

    private final Predicate<String> filter = new Predicate<String>() {
        public boolean apply(String input) {
            if (filterText == null || filterText.trim().length() == 0) {
                return true;
            }
            return input.toLowerCase().startsWith(filterText.trim().toLowerCase());
        }
    };

    public void setDemoList(List<String> objects) {
        this.demoList = objects;
        applyFilter();
        modelUpdated(UpdateType.LIST);
    }

    public List<String> getDemoList() {
        return demoList;
    }

    public void setSelectedItem(Object item) {
        selectedItem = item;
        update();
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    public List<DemoEnum> getDemoEnumValues() {
        return ImmutableList.copyOf(DemoEnum.values());
    }

    public Object getSelectedPrefItem() {
        return selectedPrefItem;
    }

    public void setSelectedPrefItem(Object selectedPrefItem) {
        this.selectedPrefItem = selectedPrefItem;
        update();
    }

    public void setDemoBoolean(boolean demoBoolean) {
        this.demoBoolean = demoBoolean;
        update();
    }

    public boolean isDemoBoolean() {
        return demoBoolean;
    }

    public void setDemoEnum(DemoEnum demoEnum) {
        this.demoEnum = demoEnum;
        update();
    }

    public DemoEnum getDemoEnum() {
        return demoEnum;
    }

    public void setDemoString(String demoString) {
        this.demoString = demoString;
        update();
    }

    public String getDemoString() {
        return demoString;
    }

    public List<String> getComboList() {
        return ImmutableList.of("Alpha", "Bravo", "Charlie");
    }

    private void applyFilter() {
        demoFilteredList = Lists.newArrayList(Iterables.filter(demoList, filter));
    }

    public List<String> getFilteredList() {
        return demoFilteredList;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
        applyFilter();
        modelUpdated(UpdateType.LIST);
    }

    public String getFilterText() {
        return filterText;
    }

    public void setSelectedComboItem(String selectedComboItem) {
        this.selectedComboItem = selectedComboItem;
        update();
    }

    public String getSelectedComboItem() {
        return selectedComboItem;
    }

    public String getSelectedComboItemString() {
        return "selection: " + selectedComboItem;
    }

    public void setDemoRadioBoolean(boolean demoRadioBoolean) {
        this.demoRadioBoolean = demoRadioBoolean;
        update();
    }

    public boolean isDemoRadioBoolean() {
        return demoRadioBoolean;
    }

    public List<String> getDemoMultiList() {
        return demoMultiList;
    }

    public void setDemoMultiList(List<String> demoMultiList) {
        this.demoMultiList = demoMultiList;
        modelUpdated(UpdateType.MULTILIST);
    }

    public List<String> getMultiSelectedItems() {
        return multiSelectedItems;
    }

    public void setMultiSelectedItems(List<String> multiSelectedItems) {
        this.multiSelectedItems = multiSelectedItems;
        update();
    }

    public void setSliderValue(int sliderValue) {
        this.sliderValue = sliderValue;
        update();
    }

    public int getSliderValue() {
        return sliderValue;
    }
}
