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

package org.see.brunel.spaceport.ui;

public enum FederateMessageType {
    BRUNEL_SPACEPORT_BEACON_ACKNOWLEDGED,
    BRUNEL_SPACEPORT_CABLECAR_LANDER_TOUCHDOWN,
    BRUNEL_SPACEPORT_LANDER_ARRIVAL_ACKNOWLEDGED,
    BRUNEL_SPACEPORT_LANDER_TOUCHDOWN_ACKNOWLEDGED,
    BRUNEL_SPACEPORT_LANDER_REQUEST_DEPARTURE,

    FACENS_CABLECAR_SPACEPORT_ACKNOWLEDGED,
    BRUNEL_LANDER_SPACEPORT_REQUEST_LANDING,
    BRUNEL_LANDER_SPACEPORT_TOUCHDOWN,
    BRUNEL_LANDER_SPACEPORT_DEPARTURE_COMPLETED;
}
