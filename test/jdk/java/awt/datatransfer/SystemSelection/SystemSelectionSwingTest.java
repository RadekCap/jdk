/*
 * Copyright (c) 1999, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.util.Properties;

/*
 * @test
 * @key headful
 * @summary To check the functionality of newly added API getSystemSelection & make sure
 *          that it's mapped to primary clipboard
 * @author Jitender(jitender.singh@eng.sun.com) area=AWT
 * @library /lib/client
 * @build ExtendedRobot
 * @run main SystemSelectionSwingTest
 */

public class SystemSelectionSwingTest {

    JFrame jframe;
    JTextField jtf1, jtf2;
    Clipboard clip;
    Transferable t;

    public static void main(String[] args) throws Exception {
        new SystemSelectionSwingTest().doTest();
    }

    SystemSelectionSwingTest() {
        jframe = new JFrame();
        jframe.setSize(200, 200);

        jtf1 = new JTextField();
        jtf1.addFocusListener( new FocusAdapter() {
            public void focusGained(FocusEvent fe) {
                fe.getSource();
            }
        });

        jtf2 = new JTextField();

        jframe.add(jtf2, BorderLayout.NORTH);
        jframe.add(jtf1, BorderLayout.CENTER);

        jframe.setVisible(true);
        jframe.toFront();
        jtf1.requestFocus();
        jtf1.setText("Selection Testing");
    }

    // Get the contents from the clipboard
    void getClipboardContent() throws Exception {
        t = clip.getContents(this);
        if ( (t != null) && (t.isDataFlavorSupported(DataFlavor.stringFlavor) )) {
            jtf2.setBackground(Color.red);
            jtf2.setForeground(Color.black);
            jtf2.setText((String) t.getTransferData(DataFlavor.stringFlavor));
        }
    }


    // Get System Selection i.e. Primary Clipboard
    private void getPrimaryClipboard() {
        Properties ps = System.getProperties();
        String operSys = ps.getProperty("os.name");
        clip = Toolkit.getDefaultToolkit().getSystemSelection();
        if (clip == null) {
            if ((operSys.substring(0,3)).equalsIgnoreCase("Win") ||
                    (operSys.substring(0,3)).equalsIgnoreCase("Mac"))
                System.out.println(operSys + " operating system does not support system selection ");
            else
                throw new RuntimeException("Method getSystemSelection() is returning null on X11 platform");
        }
    }

    // Compare the selected text with one pasted from the clipboard
    public void compareText() {
        if ((jtf2.getText()).equals(jtf1.getSelectedText()) &&
                System.getProperties().getProperty("os.name").substring(0,3) != "Win") {
            System.out.println("Selected text & clipboard contents are same\n");
        } else  {
            throw new RuntimeException("Selected text & clipboard contents differs\n");
        }
    }

    public void doTest() throws Exception {
        ExtendedRobot robot = new ExtendedRobot();

        jframe.setLocation(100, 100);
        robot.waitForIdle(2000);

        Point tf1Location = jtf1.getLocationOnScreen();
        Dimension tf1Size = jtf1.getSize();
        getPrimaryClipboard();

        if (clip != null) {
            robot.mouseMove(tf1Location.x + 5, tf1Location.y + tf1Size.height / 2);
            robot.waitForIdle(2000);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle(20);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle(20);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle(20);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle(2000);

            getClipboardContent();
            compareText();

            robot.mouseMove(tf1Location.x + tf1Size.width / 2, tf1Location.y + tf1Size.height / 2);
            robot.waitForIdle(2000);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle(20);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle(20);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle(20);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle(2000);

            getClipboardContent();
            compareText();
        }
    }
}
