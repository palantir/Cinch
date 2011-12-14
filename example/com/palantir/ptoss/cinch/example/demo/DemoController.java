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

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.example.demo.DemoModel.DemoEnum;

public class DemoController {

    private final DemoModel model;

    public int demo1ActionCount = 0;
    public int demo2ActionCount = 0;

    public DemoController(final DemoModel model) {
        this.model = model;
    }

    public void demoAction() {
        demo1ActionCount++;
    }

    public void demoAction2() {
        demo2ActionCount++;
    }

    public void setToFoo() {
        model.setDemoEnum(DemoEnum.FOO);
    }

    int changeCount = 0;
    public void changeList() {
        final ImmutableList<String> one = ImmutableList.<String>of("The", "Fox", "Foxy", "Jumped", "Over", "Lazy", "Dog");
        final ImmutableList<String> two = ImmutableList.<String>of("Quick", "Quickly", "Quickest", "Brown", "Fox");
        if (++changeCount % 2 != 0) {
            model.setDemoList(one);
        } else {
            model.setDemoList(two);
        }
    }

    public void selectFox() {
        model.setSelectedItem("Fox");
    }

    public int duplicateCount = 0;
    public void duplicate() {
        duplicateCount++;
    }

    public void selectMulti() {
        model.setMultiSelectedItems(ImmutableList.of("bravo", "delta"));
    }

}
