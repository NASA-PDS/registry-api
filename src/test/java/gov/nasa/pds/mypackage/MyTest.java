// Copyright © 2021, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// • Redistributions of source code must retain the above copyright notice,
//   this list of conditions and the following disclaimer.
// • Redistributions must reproduce the above copyright notice, this list of
//   conditions and the following disclaimer in the documentation and/or other
//   materials provided with the distribution.
// • Neither the name of Caltech nor its operating division, the Jet Propulsion
//   Laboratory, nor the names of its contributors may be used to endorse or
//   promote products derived from this software without specific prior written
//   permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.mypackage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * My test.
 * 
 * This is an example test class, showing off an example test.
 */
public class MyTest {
    /**
     * Test something.
     * 
     * Checks if 2 plus 2 equals 4.
     */
    @Test
    @DisplayName("Testing something 🤔")
    public void testSomething() {
        assertEquals(4, 2 + 2, "🚨 2 + 2 should equal 4");
    }
}
