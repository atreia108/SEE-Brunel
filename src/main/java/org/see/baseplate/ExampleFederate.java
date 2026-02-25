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

package org.see.baseplate;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;

import java.io.File;

public class ExampleFederate extends SEELateJoinerFederate {
    private static final File confFile = new File("src/main/resources/example_federate.conf");

    public ExampleFederate(SEEFederateAmbassador federateAmbassador, FederateConfiguration federateConfiguration) {
        super(federateAmbassador, federateConfiguration);
    }

    @Override
    public void declareClasses() throws FederateNotExecutionMember, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, NameNotFound, NotConnected, RTIinternalError, InvalidObjectClassHandle, SaveInProgress {
        // Publish/Subscribe object and interaction classes here using methods inherited from the late joiner class.
        // Register the appropriate event listeners just before or at this stage to be notified when a remote object
        // instance is created or a certain interaction is received.
    }

    @Override
    public void declareObjectInstances() {
        // Create all the object instances pertinent to your federate and the federation execution at large.
    }

    @Override
    public void update() {
        // This segment is run every time the simulation is updated. Any jobs this federate must perform while running
        // will go here.
    }

    public static void main(String[] args) {
        FederateConfiguration config = FederateConfiguration.Factory.create(confFile);
        ExampleFederate federate = new ExampleFederate(new SEEFederateAmbassador(), config);
        federate.configureAndStart();
    }
}
