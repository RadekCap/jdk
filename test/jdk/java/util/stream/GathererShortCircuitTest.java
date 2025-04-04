/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * @test
 * @bug 8328316
 * @summary Testing Gatherer behavior under short circuiting
 * @run junit GathererShortCircuitTest
 */

public class GathererShortCircuitTest {
    @Test
    public void mustBeAbleToPushFromFinisher() {
        Integer expected = 8328316;
        List<Integer> source = List.of(1,2,3,4,5);

        Gatherer<Integer, ?, Integer> pushOneInFinisher =
                Gatherer.of(
                    (_, element, downstream) -> false,
                    (_, downstream) -> downstream.push(expected)
                );

        var usingCollect =
            source.stream().gather(pushOneInFinisher).collect(Collectors.toList());
        var usingBuiltin =
            source.stream().gather(pushOneInFinisher).toList();
        var usingCollectPar =
            source.stream().parallel().gather(pushOneInFinisher).collect(Collectors.toList());
        var usingBuiltinPar =
            source.stream().parallel().gather(pushOneInFinisher).toList();

        assertEquals(List.of(expected), usingCollect);
        assertEquals(List.of(expected), usingBuiltin);
        assertEquals(List.of(expected), usingCollectPar);
        assertEquals(List.of(expected), usingBuiltinPar);
    }
}
