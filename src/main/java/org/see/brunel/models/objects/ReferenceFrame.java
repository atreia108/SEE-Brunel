/*-
 * Copyright (c) 2026 Hridyanshu Aatreya
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package org.see.brunel.models.objects;

import org.see.skf.annotations.Attribute;
import org.see.skf.annotations.ObjectClass;
import org.see.skf.impl.encoding.HLAunicodeStringCoder;
import org.see.skf.model.AccessLevel;
import org.see.brunel.encoding.SpaceTimeCoordinateStateCoder;
import org.see.brunel.types.SpaceTimeCoordinateState;
import org.see.skf.model.objects.UpdatableObjectInstance;

@ObjectClass(name = "HLAobjectRoot.ReferenceFrame")
public class ReferenceFrame extends UpdatableObjectInstance {
    @Attribute(name = "name", coder = HLAunicodeStringCoder.class, access = AccessLevel.SUBSCRIBE)
    private String name;

    @Attribute(name = "parent_name", coder = HLAunicodeStringCoder.class, access = AccessLevel.SUBSCRIBE)
    private String parentName;

    @Attribute(name = "state", coder = SpaceTimeCoordinateStateCoder.class, access = AccessLevel.SUBSCRIBE)
    private SpaceTimeCoordinateState state;

    public ReferenceFrame() {
        this.name = "";
        this.parentName = "";
        this.state = new SpaceTimeCoordinateState();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public SpaceTimeCoordinateState getState() {
        return state;
    }

    public void setState(SpaceTimeCoordinateState state) {
        this.state = state;
    }
}
