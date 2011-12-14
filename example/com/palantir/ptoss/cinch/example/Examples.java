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
package com.palantir.ptoss.cinch.example;

import java.awt.Container;
import java.io.PrintWriter;

import javax.swing.JFrame;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Utility code used by the examples.
 */
public class Examples {
	
	public static void initializeLogging() {
		Logger.getRootLogger().setLevel(Level.INFO);
		ConsoleAppender console = new ConsoleAppender();
		console.setWriter(new PrintWriter(System.out));
		console.setLayout(new PatternLayout("%d{ISO8601} %d %p [%t] %c - %m%n"));
		Logger.getRootLogger().addAppender(console);
	}
	
	public static JFrame getFrameFor(String title, Container container) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(100, 100);
		frame.setSize(400, 600);
	
		frame.setContentPane(container);
		return frame;
	}
}
