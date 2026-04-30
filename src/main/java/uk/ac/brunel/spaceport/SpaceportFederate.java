/*-
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Brunel University of London
 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * 	  contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
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

package uk.ac.brunel.spaceport;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.interactions.*;
import uk.ac.brunel.core.PhysicalInterface;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

import java.io.File;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * An IEEE 1516-2025 High Level Architecture (HLA) federate that hosts the simulated models of spaceport platforms on
 * the surface of the Lunar South Pole.
 *
 * @author Hridyanshu
 */
public class SpaceportFederate extends SEELateJoinerFederate {
    private static final File confFile = new File("src/main/resources/spaceport.conf");

    public static final int SPACEPORT_COUNT = 3;

    private static final Vector3D[] SPAWN_POINTS = new Vector3D[] {
            Vector3D.of(-536.303710683329, 4219.126819522129, -5644.62109375),
            Vector3D.of(736.18, 4097.91 ,-5604.40),
            Vector3D.of(2008.6595593598631, 3976.691211771549, -5580.8349609375),
    };

    private final CopyOnWriteArraySet<Spaceport> spaceports;

    protected SpaceportFederate(SEEFederateAmbassador federateAmbassador, FederateConfiguration federateConfiguration) {
        super(federateAmbassador, federateConfiguration);
        spaceports = new CopyOnWriteArraySet<>();
    }

    @Override
    public void declareClasses() throws FederateNotExecutionMember, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, NameNotFound, NotConnected, RTIinternalError, InvalidObjectClassHandle, SaveInProgress, InvalidInteractionClassHandle, InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM {
        publishObjectClass(PhysicalEntity.class);
        publishObjectClass(PhysicalInterface.class);
        subscribeObjectClass(PhysicalEntity.class);

        publishInteractionClass(UCFPowerRequest.class);
        publishInteractionClass(MSGCargoPickupJob.class);
        publishInteractionClass(MSGCargoTransferComplete.class);
        publishInteractionClass(MSGLanderDepartureRequest.class);
        publishInteractionClass(MSGLandingPermission.class);

        subscribeInteractionClass(MSGCargoPickupJobAccepted.class);
        subscribeInteractionClass(MSGCargoPickupJobRejected.class);
        subscribeInteractionClass(MSGCargoTransferReady.class);
        subscribeInteractionClass(MSGLandingRequest.class);
        subscribeInteractionClass(MSGLanderTakeoff.class);
        subscribeInteractionClass(MSGLanderTouchdown.class);
        subscribeInteractionClass(UCFPowerAllocation.class);
        subscribeInteractionClass(UCFLoadSheddingEvent.class);
    }

    @Override
    public void declareObjectInstances() throws FederateNotExecutionMember, ObjectClassNotPublished, ObjectClassNotDefined, RestoreInProgress, ObjectInstanceNotKnown, IllegalName, ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, NotConnected, RTIinternalError, SaveInProgress {
        for (int i = 1; i < SPACEPORT_COUNT + 1; ++i) {
            SpaceTimeCoordinateState defaultState = new SpaceTimeCoordinateState();
            defaultState.setPosition(SPAWN_POINTS[i - 1]);

            String spaceportName = Spaceport.NAME_SEQUENCE + i;
            String spaceportArmName = SpaceportArm.NAME_SEQUENCE + i;

            Spaceport s = new Spaceport.Builder()
                    .federate(this)
                    .name(spaceportName)
                    .parentReferenceFrame("AitkenBasinLocalFixed")
                    .spawnPoint(defaultState)
                    .arm(spaceportArmName)
                    .build();

            registerObjectInstance(s, s.getName());

            SpaceportArm sArm = s.getArm();
            registerObjectInstance(sArm, sArm.getName());
            spaceports.add(s);
        }
    }

    @Override
    public void update() {
        spaceports.forEach(Spaceport::update);
    }

    public static void main(String[] args) {
        FederateConfiguration config = FederateConfiguration.Factory.create(confFile);
        SpaceportFederate federate = new SpaceportFederate(new SEEFederateAmbassador(), config);
        federate.configureAndStart();
    }
}
