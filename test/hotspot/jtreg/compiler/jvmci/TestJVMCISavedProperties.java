/*
 * Copyright (c) 2023, 2024, Oracle and/or its affiliates. All rights reserved.
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

/*
 * @test TestJVMCISavedProperties
 * @bug 8309390
 * @summary Ensures Services.getSavedProperties() includes properties set on
 *          the command line as well some specified properties but not
 *          properties set programmatically.
 * @requires vm.flagless
 * @requires vm.jvmci
 * @library /test/lib
 * @run driver TestJVMCISavedProperties
 */

import jdk.test.lib.Asserts;
import jdk.test.lib.process.ProcessTools;
import jdk.test.lib.process.OutputAnalyzer;

public class TestJVMCISavedProperties {

    public static void main(String[] args) throws Exception {
        if (args.length != 0) {
            System.setProperty("app3.NotPresentInSavedProperties", "42");
            System.out.println("DONE IN MAIN");
            return;
        }
        ProcessBuilder pb = ProcessTools.createLimitedTestJavaProcessBuilder(
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+EagerJVMCI",
            "-XX:+EnableJVMCI",
            "-Ddebug.jvmci.PrintSavedProperties=true",
            "-Dapp1.propX=true",
            "-Dapp2.propY=SomeStringValue",
            "TestJVMCISavedProperties", "true");
        OutputAnalyzer output = new OutputAnalyzer(pb.start());
        output.stdoutShouldContain("debug.jvmci.PrintSavedProperties=true");
        output.stdoutShouldContain("app1.propX=true");
        output.stdoutShouldContain("app2.propY=SomeStringValue");
        output.stdoutShouldContain("java.specification.version=" + Runtime.version().feature());
        output.stdoutShouldContain("os.name=");
        output.stdoutShouldContain("os.arch=");
        output.stdoutShouldNotContain("NotPresentInSavedProperties");
    }
}
