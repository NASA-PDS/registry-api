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

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


/**
 * The main class with its main entry point.
 * 
 * Note that the static initializer for this class loads resources which must be present in the classpath
 * (i.e., in the jar in which this class is packaged).
 * 
 * @author The Planetary Data System
 * @since 2021
 */
public final class Main {
    /** The greeting to display when the entry point is entered */
    private static String greeting;

    /**
     * The entry point.
     * 
     * @param argv Argument vector; these command-line arguments are ignored.
     * @throws Throwable if any error occurs.
     */
    public static void main(String[] argv) throws Throwable {
        System.out.println(greeting);
    }

    // Static initializers don't get doc comments.
    static {
        try {
            Properties props = new Properties();
            BufferedInputStream in = new BufferedInputStream(Main.class.getResourceAsStream("example.properties"));
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            props.load(reader);
            in.close();
            greeting = props.getProperty("example.greeting");
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
