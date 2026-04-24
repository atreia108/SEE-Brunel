/*****************************************************************
 SEE Baseplate - A starter project template for the SEE HLA
 Starter Kit Framework.
 Copyright (c) 2026, Hridyanshu Aatreya - Modelling & Simulation
 Group (MSG) at Brunel University of London. All rights reserved.

 GNU Lesser General Public License (GNU LGPL).

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3.0 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library.
 If not, see http://http://www.gnu.org/licenses/
 *****************************************************************/

package uk.ac.brunel.federates;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;
import uk.ac.brunel.listeners.LanderListener;
import uk.ac.brunel.interactions.*;
import uk.ac.brunel.models.DynamicalEntity;
import uk.ac.brunel.models.PhysicalEntity;
import uk.ac.brunel.models.Spaceport;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

import java.io.File;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Spaceport Federate
 * @author Hridyanshu Aatreya
 */
public class SpaceportFederate extends SEELateJoinerFederate {
    private static final File confFile = new File("src/main/resources/spaceport.conf");

    private final CopyOnWriteArraySet<Spaceport> spaceports;

    public SpaceportFederate(SEEFederateAmbassador federateAmbassador, FederateConfiguration federateConfiguration) {
        super(federateAmbassador, federateConfiguration);
        spaceports = new CopyOnWriteArraySet<>();
    }

    @Override
    public void declareClasses() throws FederateNotExecutionMember, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, NameNotFound, NotConnected, RTIinternalError, InvalidObjectClassHandle, SaveInProgress, InvalidInteractionClassHandle, InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM {
        // Publish/Subscribe object and interaction classes here using methods inherited from the late joiner class.
        // Register the appropriate event listeners just before or at this stage to be notified when a remote object
        // instance is created or a certain interaction is received.
        publishObjectClass(PhysicalEntity.class);
        subscribeObjectClass(DynamicalEntity.class);

        publishInteractionClass(MSGCargoPickupJob.class);
        publishInteractionClass(MSGCargoTransferComplete.class);
        publishInteractionClass(MSGLanderDepartureRequest.class);
        publishInteractionClass(MSGLandingPermission.class);
        publishInteractionClass(MSGLogisticsDeliveryRequest.class);

        subscribeInteractionClass(MSGCargoPickupJobAccepted.class);
        subscribeInteractionClass(MSGCargoPickupJobRejected.class);
        subscribeInteractionClass(MSGCargoTransferReady.class);
        subscribeInteractionClass(MSGLandingRequest.class);
        subscribeInteractionClass(MSGLanderTakeoff.class);
        subscribeInteractionClass(MSGLanderTouchdown.class);
        subscribeInteractionClass(MSGLogisticsDeliveryResponse.class);
    }

    @Override
    public void declareObjectInstances() throws FederateNotExecutionMember, ObjectClassNotPublished, ObjectClassNotDefined, RestoreInProgress, ObjectInstanceNotKnown, NotConnected, RTIinternalError, SaveInProgress, IllegalName, ObjectInstanceNameInUse, ObjectInstanceNameNotReserved {
        // Create all the object instances pertinent to your federate and the federation execution at large.

        String spaceportNameSequence = "brunel_spaceport_";
        for (int i = 1; i < 4; ++i) {
            Spaceport s = new Spaceport.Builder()
                    .federate(this)
                    .name(spaceportNameSequence + i)
                    .parentReferenceFrame("AitkenBasinLocalFixed")
                    .spaceTimeCoordinateState(new SpaceTimeCoordinateState()) // TODO - Set initial spawn points.
                    .build();

            registerObjectInstance(s, s.getName());
            spaceports.add(s);
        }

        registerEventListeners();
    }

    private void registerEventListeners() {
        addInteractionListener(new LanderListener(spaceports));
    }

    @Override
    public void update() {
        // This segment is run every time the simulation is updated. Any jobs this federate must perform while running
        // will go here.
    }

    public static void main(String[] args) {
        FederateConfiguration config = FederateConfiguration.Factory.create(confFile);
        SpaceportFederate federate = new SpaceportFederate(new SEEFederateAmbassador(), config);
        federate.configureAndStart();
    }
}
